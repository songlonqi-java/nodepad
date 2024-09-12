package main

import (
	"crypto/tls"
	"crypto/x509"
	"fmt"
	"io/ioutil"
	"net/http"
)

func handler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("URL:", r.URL.String())
	fmt.Println("URL:", r.URL.Path)

	for s, strings := range r.URL.Query() {
		fmt.Printf("key=%s  strings=%+v \n", s, strings)
	}
	for k, v := range r.Header {
		fmt.Printf("%s: %s\n", k, v)
	}
	fmt.Println("-----------body-----------")
	bts, err := ioutil.ReadAll(r.Body)
	if err != nil {
		fmt.Println(err)
		return
	}
	r.Body.Close()
	fmt.Printf("%s \n", string(bts))
	fmt.Println()
	w.WriteHeader(200)
	w.Write([]byte("ok"))
}

func main() {
	cf, err := tls.LoadX509KeyPair("./Nginx/guance.com_cert_chain.pem", "./Nginx/guance.com_key.key")
	if err != nil {
		fmt.Println(err)
		return
	}
	for _, bytes := range cf.Certificate {
		xc, err := x509.ParseCertificate(bytes)
		if err != nil {
			fmt.Println(err)
		} else {
			fmt.Println(xc.DNSNames)
		}
	}

	http.HandleFunc("/", handler)
	err = http.ListenAndServeTLS(":443", "./Nginx/guance.com_cert_chain.pem", "./Nginx/guance.com_key.key", nil)
	if err != nil {
		fmt.Println("Error starting server:", err)
		return
	}

}
