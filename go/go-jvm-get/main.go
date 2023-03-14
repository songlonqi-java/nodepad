package main

import (
	"context"
	"fmt"
	"github.com/newrelic/nrjmx/gojmx"
)

func main() {

	// JMX Client configuration.
	config := &gojmx.JMXConfig{
		Hostname:         "localhost",
		Port:             9008,
		RequestTimeoutMs: 10000,
	}

	// Connect to JMX endpoint.
	client, err := gojmx.NewClient(context.Background()).Open(config)
	handleError(err)

	// Get the mBean names.
	mBeanNames, err := client.QueryMBeanNames("java.lang:type=*")
	handleError(err)

	// Get the Attribute names for each mBeanName.
	for _, mBeanName := range mBeanNames {
		mBeanAttrNames, err := client.GetMBeanAttributeNames(mBeanName)
		handleError(err)

		// Get the attribute value for each mBeanName and mBeanAttributeName.
		jmxAttrs, err := client.GetMBeanAttributes(mBeanName, mBeanAttrNames...)
		if err != nil {
			fmt.Println(err)
			continue
		}
		for _, attr := range jmxAttrs {
			if attr.ResponseType == gojmx.ResponseTypeErr {
				fmt.Println(attr.StatusMsg)
				continue
			}
			printAttr(attr)
		}
	}

	// Or use QueryMBean call which wraps all the necessary requests to get the values for an MBeanNamePattern.
	// Optionally you can provide atributes to QueryMBeanAttributes in tha same way you provide for GetMBeanAttributes,
	// e.g.: response, err := client.QueryMBeanAttributes("java.lang:type=*", mBeanAttrNames...)
	response, err := client.QueryMBeanAttributes("java.lang:type=*")
	handleError(err)
	for _, attr := range response {
		if attr.ResponseType == gojmx.ResponseTypeErr {
			fmt.Println(attr.StatusMsg)
			continue
		}
		printAttr(attr)
	}
}

func handleError(err error) {
	fmt.Println(err)
}

func printAttr(attr *gojmx.AttributeResponse) {
	fmt.Println(attr.String())
}
