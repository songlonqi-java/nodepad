package jaeger

import (
	"context"
	"net"
	"net/http"
	"strconv"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	itrace "gitlab.jiagouyun.com/cloudcare-tools/datakit/internal/trace"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	jaeger "go.opentelemetry.io/otel/exporters/jaeger"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/sdk/resource"
	traceSdk "go.opentelemetry.io/otel/sdk/trace"
	semConv "go.opentelemetry.io/otel/semconv/v1.21.0"
	"go.opentelemetry.io/otel/trace"
)

var endpoint = "http://localhost:14000/apis/traces"

var out = make(chan int, 1)

func TestJaegerOTELExporter(t *testing.T) {
	afterGatherRun = &afterGather{t: t}
	go func() {
		HTTPServer()
	}()
	time.Sleep(time.Second * 3)
	Client(t)
	<-out
	lis.Close()
}

func Client(t *testing.T) error {
	t.Helper()
	TPctx := context.Background()
	exp, err := jaeger.New(jaeger.WithCollectorEndpoint(jaeger.WithEndpoint(endpoint)))
	if err != nil {
		return err
	}

	tp := traceSdk.NewTracerProvider(
		traceSdk.WithBatcher(exp),
		traceSdk.WithResource(resource.NewSchemaless(
			semConv.ServiceNameKey.String("jaeger_test"))),
	)
	otel.SetTracerProvider(tp)

	// set global propagator to tracecontext (the default is no-op).
	otel.SetTextMapPropagator(propagation.TraceContext{})
	commonLabels := []attribute.KeyValue{attribute.String("key1", "val1")}
	// work begins
	tracer := otel.Tracer("tracer_user_login")
	ctx, span := tracer.Start(
		context.Background(),
		"span-Example",
		trace.WithAttributes(commonLabels...))
	for i := 0; i < 20; i++ {
		_, spanC := tracer.Start(ctx, "span"+strconv.Itoa(i))

		spanC.End()
	}

	time.Sleep(time.Second)
	span.End()
	t.Logf("span end")
	ctx.Done()
	time.Sleep(time.Second * 2)
	tp.Shutdown(TPctx)
	t.Logf("out and shutdown")
	return nil
}

type afterGather struct {
	t *testing.T
}

func (af *afterGather) Run(inputName string, dktraces itrace.DatakitTraces) {
	for _, dktrace := range dktraces {
		for _, span := range dktrace {
			assert.Equal(af.t, len(span.GetFiledToString(itrace.FieldTraceID)), 32)
		}
	}
	out <- 1 // out.
}

type Handel struct {
}

func (h *Handel) ServeHTTP(w http.ResponseWriter, req *http.Request) {
	handleJaegerTrace(w, req)
}

var lis net.Listener

func HTTPServer() error {
	var err error
	lis, err = net.Listen("tcp", "localhost:14000")
	if err != nil {
		return err
	}
	h := &Handel{}
	return http.Serve(lis, h)
}

