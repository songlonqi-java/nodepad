package main

import (
	"flag"
	"fmt"
	"net"
	"strconv"
)

var (
	maxRead = 1100
)

func main() {

	var network string
	var port int

	// &user：保存命令行中输入 -u 后面的参数值
	// "用户名，默认为root" : 说明
	flag.StringVar(&network, "t", "tcp", "协议，默认为tcp")
	flag.IntVar(&port, "p", 3306, "端口号，默认是3306")

	// 这里有一个非常中的操作，转换，必须调用该方法
	flag.Parse()

	if network == "tcp" {
		fmt.Printf("open tcp port is:%d \n", port)
		dotcp(strconv.Itoa(port))
	} else if network == "udp" {
		fmt.Printf("open udp port is:%d \n", port)
		doudp(strconv.Itoa(port))
	} else {
		fmt.Println("mast tcp and udp!!")
	}

}

func doudp(port string) {
	laddr, err := net.ResolveUDPAddr("udp", ":"+port)
	if err != nil {
		fmt.Println("ResolveUDPAddr err:", err)
		return
	}
	listen, err := net.ListenUDP("udp", laddr)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer listen.Close()
	for {
		var buf [1024]byte
		n, _, err := listen.ReadFromUDP(buf[:]) // 接收数据
		if err != nil {
			fmt.Printf("read from udp failed,err:%v\n", err)
			return
		}
		fmt.Println("接收到的数据：", string(buf[:n]))

	}
}

func dotcp(port string) {
	listen, err := net.Listen("tcp", ":"+port)
	if err != nil {
		checkError(err, "Listen")
		return
	}
	for {
		conn, err := listen.Accept()
		if err != nil {
			checkError(err, "Accept")
			return
		}
		go connectionHandler(conn)
	}
}

func connectionHandler(conn net.Conn) {
	connFrom := conn.RemoteAddr().String()
	fmt.Println("Connection from: ", connFrom)
	for {
		var ibuf []byte = make([]byte, maxRead+1)
		length, err := conn.Read(ibuf[0:maxRead])
		ibuf[maxRead] = 0 // to prevent overflow
		switch err {
		case nil:
			handleMsg(length, err, ibuf)
		default:
			goto DISCONNECT
		}
	}
DISCONNECT:
	err := conn.Close()
	fmt.Println("Closed connection:", connFrom)
	checkError(err, "Close:")
}

func handleMsg(length int, err error, msg []byte) {
	if length > 0 {

		for i := 0; ; i++ {
			if msg[i] == 0 {
				break
			}
		}
		fmt.Printf("Received data: %v", string(msg[0:length]))
		fmt.Println("   length:", length)
	}
}
func checkError(error error, info string) {
	if error != nil {
		panic("ERROR: " + info + " " + error.Error()) // terminate program
	}
}
