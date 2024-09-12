package com.zy.observable.server.otel;

/*
import com.guance.exporter.guance.trace.GuanceSpanExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class OpenTelemetryInitializer {
    public static OpenTelemetry initialize() {
        GuanceSpanExporter guanceExporter = new GuanceSpanExporter();
        guanceExporter.setEndpoint("https://openway.guance.com");
        guanceExporter.setToken("tkn_0d9ebb474d394xxxxxxxxx");

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(guanceExporter).build())
                .setResource(Resource.create(Attributes.builder()
                        .put(ResourceAttributes.SERVICE_NAME, "serviceForJAVA")
                        .build()))
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }
}
*/
