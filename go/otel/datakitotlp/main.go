package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"os"
	"time"

	semconv "go.opentelemetry.io/otel/semconv/v1.7.0"

	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/trace"
)

type config struct {
	GrpcOpen     bool              `json:"grpc"`
	GrpcAddr     string            `json:"grpc_addr"`
	HTTPOpen     bool              `json:"http"`
	HttpEndpoint string            `json:"http_endpoint"`
	HttpAddr     string            `json:"http_addr"`
	ServerName   string            `json:"server_name"`
	TraceName    string            `json:"trace_name"`
	Count        int               `json:"count"`
	Tags         map[string]string `json:"tags"`
}

var globalConf *config

func initConfig() {
	bts, err := os.ReadFile("./tsconfig.json")
	if err != nil {
		handleErr(err, "read file err")
	}
	con := &config{}
	err = json.Unmarshal(bts, con)
	handleErr(err, "marshal err")
	globalConf = con

}

func main() {
	initConfig()
	log.Printf("Waiting for connection...")

	shutdown := initProvider()
	defer shutdown()

	metricSd := initMetric()
	defer metricSd()

	for j := 0; j < 1000; j++ {
		tracer := otel.Tracer(fmt.Sprintf("tracerName-%d", j))

		// labels represent additional key-value descriptors that can be bound to a
		// metric observer or recorder.
		commonLabels := []attribute.KeyValue{semconv.DBSystemMySQL}
		for key, val := range globalConf.Tags {
			commonLabels = append(commonLabels, attribute.String(key, val))
		}
		// work begins
		ctx, span := tracer.Start(
			context.Background(),
			"span-Example",
			trace.WithAttributes(commonLabels...))

		for i := 0; i < 2; i++ {
			_, iSpan := tracer.Start(ctx, fmt.Sprintf("Sample-child-%d", i))
			//iSpan.SetAttributes(semconv.OSTypeWindows)
			<-time.After(time.Second / 10)
			iSpan.End()
		}
		span.End()
		log.Printf("Doing really hard work (%d / 10)\n", j+1)
		<-time.After(time.Millisecond)
	}

	// ------------------------------------------ metric ---------------
	/*
		meter := global.Meter("test_meter")
		histogram := metric.Must(meter).NewFloat64Histogram("ex.com")
		commonLabels1 := []attribute.KeyValue{attribute.String("A", "1")}
		ctx := context.Background()
		meter.RecordBatch(ctx, commonLabels1, histogram.Measurement(12.0))
		// Recorder metric example
		counter := metric.Must(meter).
			NewFloat64Counter(
				"an_important_metric",
				metric.WithDescription("Measures the cumulative epicness of the app"),
			)

		for i := 0; i < 10; i++ {
			log.Printf("metric:Doing really hard work (%d / 10)\n", i+1)
			counter.Add(ctx, 1.0)
		}
	*/
	// ------------------------------------------ metric ---------------

	log.Printf("Done!")
}

func testPost() bool {
	return false
}

func handleErr(err error, message string) {
	if err != nil {
		log.Fatalf("%s: %v", message, err)
	}
}
