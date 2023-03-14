package main

import (
	"flag"
	"fmt"
	"net"
	"time"
)

func main() {

	var network string
	var addr string

	flag.StringVar(&network, "t", "tcp", "协议，默认为tcp")
	flag.StringVar(&addr, "a", "127.0.0.1：9530", "address ： 127.0.0.1：9530")

	// 这里有一个非常中的操作，转换，必须调用该方法
	flag.Parse()

	if network == "tcp" || network == "udp" {
		fmt.Printf("do %s add is:%s \n", network, addr)
		dotcp(network, addr)
	} else {
		fmt.Println("mast tcp and udp!!")
	}
}

func dotcp(network, addr string) {
	conn, err := net.Dial(network, addr)
	if err != nil {
		fmt.Printf("err=%v \n", err)
		return
	}
	defer conn.Close()
	for i := 0; i < 5; i++ {
		n, err := conn.Write([]byte("this is msg 001 \n this is msg 002"))
		if err != nil {
			fmt.Println(err)
			return
		}
		time.Sleep(time.Second)
		fmt.Printf("send len is %d \n", n)
	}
	fmt.Println("Done")
}
