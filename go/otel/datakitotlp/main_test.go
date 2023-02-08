package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
	"testing"
)

func Test_testPost(t *testing.T) {
	postbody := `{"resourceSpans":[{"resource":{"attributes":[{"key":"service.name","value":{"stringValue":"front-app"}},{"key":"telemetry.sdk.language","value":{"stringValue":"webjs"}},{"key":"telemetry.sdk.name","value":{"stringValue":"opentelemetry"}},{"key":"telemetry.sdk.version","value":{"stringValue":"1.2.0"}}],"droppedAttributesCount":0},"instrumentationLibrarySpans":[{"spans":[{"traceId":"b974aa3f8e95387f959024e0472c62d5","spanId":"bd1b8a16de09d8fe","name":"files-series-info-0","kind":1,"startTimeUnixNano":1653030257075199700,"endTimeUnixNano":1653030257141699800,"attributes":[],"droppedAttributesCount":0,"events":[{"timeUnixNano":1653030257141599700,"name":"fetching-span1-completed","attributes":[],"droppedAttributesCount":0}],"droppedEventsCount":0,"status":{"code":0},"links":[],"droppedLinksCount":0}],"instrumentationLibrary":{"name":"example-tracer-web"}}]}]}`
	resp, err := http.Post("http://49.232.153.84:9529/otel/v1/traces",
		"application/json",
		strings.NewReader(postbody))
	if err != nil {
		fmt.Println(err)
	}

	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		// handle error
		fmt.Println(err)
	}

	fmt.Println(string(body))
	fmt.Println(resp.StatusCode)
}
