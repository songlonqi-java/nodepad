package main

import (
	"context"
	"github.com/pinpoint-apm/pinpoint-go-agent"
	"log"
	"net/http"

	pphttp "github.com/pinpoint-apm/pinpoint-go-agent/plugin/http"
)

func outGoing(w http.ResponseWriter, r *http.Request) {
	tracer := pinpoint.FromContext(r.Context())
	ctx := pinpoint.NewContext(context.Background(), tracer)
	// or ctx := r.Context()

	client := pphttp.WrapClientWithContext(ctx, &http.Client{})
	//resp, err := client.Get("http://localhost:9000/async_wrapper?foo=bar&say=goodbye")
	client.Get("http://localhost:9000/async_wrapper?foo=bar&say=goodbye")
}

func main() {
	//setup agent
	opts := []pinpoint.ConfigOption{
		pinpoint.WithAppName("GoHTTPTest"),
		pinpoint.WithAgentId("GoHTTPTestAgent"),
		pinpoint.WithCollectorHost("localhost"),
		pinpoint.WithCollectorAgentPort(9991),
		pinpoint.WithCollectorSpanPort(9991),
		pinpoint.WithCollectorStatPort(9991),
		pinpoint.WithLogLevel("debug"),
		pinpoint.WithLogOutput("/usr/local/tmall/pp.log"),
	}
	cfg, _ := pinpoint.NewConfig(opts...)
	agent, err := pinpoint.NewAgent(cfg)
	if err != nil {
		log.Fatalf("pinpoint agent start fail: %v", err)
	}

	defer agent.Shutdown()

	mux := pphttp.NewServeMux()
	mux.HandleFunc("/bar", outGoing)
	http.ListenAndServe("localhost:8000", mux)
}
