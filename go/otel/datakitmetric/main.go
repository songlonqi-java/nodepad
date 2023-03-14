package main

import (
	"context"
	"fmt"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/metric/instrument"
	"log"
	"time"

	"go.opentelemetry.io/otel/exporters/otlp/otlpmetric/otlpmetricgrpc"
	"go.opentelemetry.io/otel/metric/global"
	metricsdk "go.opentelemetry.io/otel/sdk/metric"
	"go.opentelemetry.io/otel/sdk/resource"
	semconv "go.opentelemetry.io/otel/semconv/v1.7.0"
)

func main() {
	metricSd := initMetric()
	defer metricSd()
	// 统计 每5秒 收到的用户请求数量以及成功失败的数量
	for i := 0; i < 5; i++ {
		userMeter := global.Meter("metricName_user_login")
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*10)

		//loginCount := metric.Must(userMeter).NewInt64Counter("user_login_five_second") // 计数器 递增
		//count := rand.Int63n(10)
		//loginCount.Add(ctx, count)
		//
		//loginSuccess := metric.Must(userMeter).NewInt64Histogram("user_login_success") // 柱状图
		//loginSuccess.Record(ctx, count)
		//attrs := []attribute.KeyValue{
		//	attribute.Key("A").String("B"),
		//	attribute.Key("C").String("D"),
		//}
		counter, err := userMeter.Float64Counter("user.login", instrument.WithDescription("a simple counter"))
		if err != nil {
			log.Fatal(err)
		}
		badCounet, err := userMeter.Int64Counter("user.login.bad", instrument.WithDescription("user login fatal"))
		if err != nil {
			log.Fatal(err)
		}

		if i%2 == 0 {
			counter.Add(ctx, 5, attribute.String("A.a", "B"), attribute.String("B", "b"))
			badCounet.Add(ctx, 5, attribute.String("A.a", "B"), attribute.String("B", "b"))

		} else {
			counter.Add(ctx, 6, attribute.String("A.a", "C"))
			badCounet.Add(ctx, 6, attribute.String("A.a", "C"))
		}

		/*		_, err = userMeter.Int64ObservableGauge(
					"DiskUsage",
					instrument.WithUnit(unit.Bytes),
					instrument.WithInt64Callback(func(_ context.Context, obsrv instrument.Int64Observer) error {
						// Do the real work here to get the real disk usage. For example,
						//
						//   usage, err := GetDiskUsage(diskID)
						//   if err != nil {
						//   	if retryable(err) {
						//   		// Retry the usage measurement.
						//   	} else {
						//   		return err
						//   	}
						//   }
						//
						// For demonstration purpose, a static value is used here.
						usage := 7 + i
						obsrv.Observe(int64(usage), attribute.Int("disk.id", i))
						return nil
					}),
				)
				if err != nil {
					fmt.Println("failed to register instrument")
					panic(err)
				}*/

		/*
			histogram, err := userMeter.Float64Histogram("baz", instrument.WithDescription("a very nice histogram"))
			if err != nil {
				log.Fatal(err)
			}

			histogram.Record(ctx, 23, attribute.String("AH", "B"), attribute.String("B", "b"))
			histogram.Record(ctx, 7, attribute.String("AH", "C"))
			histogram.Record(ctx, 101, attribute.String("AH", "D"))
			histogram.Record(ctx, 105, attribute.String("AH", "E"))
		*/
		cancel()
		time.Sleep(time.Second * 2)
		fmt.Println(time.Now().String())
		//	loginFail := metric.Must(userMeter).NewInt64Histogram("user_login_fail")
		//	loginFail.Record(ctx, rand.Int63n(3))

	}

	log.Printf("Done!")
}

func handleErr(err error, message string) {
	if err != nil {
		log.Fatalf("%s: %v", message, err)
	}
}

func initMetric() func() {
	ctxM := context.Background()
	opts := []otlpmetricgrpc.Option{
		otlpmetricgrpc.WithInsecure(),
		otlpmetricgrpc.WithEndpoint("10.200.14.226:4317"),
		otlpmetricgrpc.WithReconnectionPeriod(500 * time.Millisecond),
		//	otlpmetricgrpc.WithHeaders(map[string]string{"header": "1"}), // 开启校验 header
	}

	// opts = append(opts, additionalOpts...)
	//client := otlpmetricgrpc.NewClient(opts...)
	//exp, err := otlpmetric.New(ctxM, client)
	exp, err := otlpmetricgrpc.New(ctxM, opts...)
	handleErr(err, "otlpmetric.New")
	res, _ := resource.New(ctxM,
		resource.WithAttributes(
			semconv.ServiceNameKey.String("serviceNameForMetric"),
			// semconv.ProcessPIDKey.Int(os.Getpid()), // set pid
			// and so on ...
		),
	)

	//pusher := controller.New(
	//	processor.NewFactory(
	//		simple.NewWithHistogramDistribution(),
	//		exp,
	//	),
	//	controller.WithResource(res),
	//	controller.WithExporter(exp),
	//	controller.WithCollectPeriod(2*time.Second),
	//)
	meterProvider := metricsdk.NewMeterProvider(metricsdk.WithResource(res), metricsdk.WithReader(metricsdk.NewPeriodicReader(exp)))
	global.SetMeterProvider(meterProvider)

	//if err := pusher.Start(ctxM); err != nil {
	//	log.Fatalf("could not start metric controoler: %v", err)
	//}
	/*defer func() {
		ctx, cancel := context.WithTimeout(ctx, time.Second)
		defer cancel()
		// pushes any last exports to the receiver
		if err := pusher.Stop(ctx); err != nil {
			otel.Handle(err)
		}
	}()*/
	return func() {
		handleErr(meterProvider.Shutdown(ctxM), "failed to shutdown pusher")
	}
}
