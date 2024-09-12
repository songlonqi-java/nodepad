package com.zy.observable.server.controller;

/*
import com.zy.observable.server.otel.OpenTelemetryManager;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import java.util.concurrent.TimeUnit;
import static java.lang.Thread.sleep;
import com.zy.observable.server.bean.AjaxResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@Controller
public class GuanceExporter {

    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    private static final OpenTelemetry openTelemetry = OpenTelemetryManager.getOpenTelemetry();

    @RequestMapping("/guance")
    @ResponseBody
    public AjaxResult resource() {
        logger.info("this is resource");

        try {

            Tracer tracer = openTelemetry.getTracer("instrumentation-library-name", "1.0.0");
            Span parentSpan = tracer.spanBuilder("parent").startSpan();


            Span childSpan = tracer.spanBuilder("child")
                    .setParent(Context.current().with(parentSpan))
                    .startSpan();
            childSpan.setAttribute("tagsA", "vllelel");
            // do stuff
            sleep(500);    //延时 1 秒
            for (int i = 0; i < 10; i++) {
                Span childSpan1 = tracer.spanBuilder("child")
                        .setParent(Context.current().with(parentSpan))
                        .startSpan();
                sleep(1000);    //延时 1 秒
                System.out.println(i);
                childSpan1.end();
            }
            childSpan.end();
            childSpan.end(0, TimeUnit.NANOSECONDS);
            System.out.println("span end");
            sleep(1000);    // 延时 1 秒
            parentSpan.end();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("finally end");
        }

        return AjaxResult.success("this is resource");
    }
}
*/