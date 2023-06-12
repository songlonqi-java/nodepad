package main

import (
	"bytes"
	"encoding/binary"
	"flag"
	"fmt"
	"io"
	"net"
	"net/http"
	"strconv"
)

var (
	maxRead = 1024 * 200 // 10KB
)

func main() {

	var network string
	var port int

	flag.StringVar(&network, "t", "tcp", "协议，默认为tcp")
	flag.IntVar(&port, "p", 2280, "端口号，默认是3306")

	// 这里有一个非常中的操作，转换，必须调用该方法
	flag.Parse()

	go dotcp(strconv.Itoa(port))
	http.HandleFunc("/cat/s/router", router)
	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		fmt.Println(err)
	}
}

var kvs = []byte(`{"kvs":{"startTransactionTypes":"Cache.;Squirrel.","block":"false","routers":"10.200.6.16:2280;","sample":"1.0","matchTransactionTypes":"SQL"}}`)

func router(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(200)
	fmt.Println("http handle")
	w.Write(kvs)
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
		go func() {
			defer conn.Close()

			// 持续读取请求并处理
			for {
				buf, err := readPacket(conn)
				if err != nil {
					fmt.Printf("failed to read packet: %v\n", err)
					break
				}

				// 处理数据包
				addr := conn.RemoteAddr()
				handleMsg(buf, addr.String())
			}
		}()
	}
}

func readPacket(r io.Reader) (*bytes.Buffer, error) {
	// 先读取 4 字节的包头，获取包体长度
	header := make([]byte, 4)
	if _, err := io.ReadFull(r, header); err != nil {
		return nil, err
	}
	length := binary.BigEndian.Uint32(header)

	if length > uint32(maxRead) {
		return nil, fmt.Errorf("packet too large: %d", length)
	}
	fmt.Printf("this body length is %d \n", length)
	// 读取包体
	body := make([]byte, length)
	if _, err := io.ReadFull(r, body); err != nil {
		return nil, err
	}

	// 封装成 Packet 对象返回
	return bytes.NewBuffer(body), nil
}

func connectionHandler(conn net.Conn) {
	connFrom := conn.RemoteAddr().String()
	fmt.Println("Connection from: ", connFrom)
	first, next := true, false
	cacheBuf := make([]byte, 0)
	cacheLen := 0
	for {
		var ibuf = make([]byte, maxRead)
		length, err := conn.Read(ibuf)
		// ibuf[maxRead] = 0 // to prevent overflow
		// 先取长度 没有读完 接着读下一个包
		if err != nil {
			goto DISCONNECT
		}
		if first {
			// 第一个包，或者下一个新包，检查长度
			buf := bytes.NewBuffer(ibuf[0:length])
			bts4 := make([]byte, 4)
			_, _ = buf.Read(bts4)
			fmt.Printf("---- header %v \n", bts4)
			bodyLen := ipv4ToInt32(bts4)
			if length == maxRead {
				first = false // 无脑下一个
				for _, b := range ibuf {
					cacheBuf = append(cacheBuf, b)
				}
				cacheLen = length
				continue
			}
			if bodyLen-4 != uint32(length) {
				// 第一个包或者新包，没有将buf读满，不合理。丢弃。
				continue
			} else {
				// 解包，清空缓存
				handleMsg(buf, "")
				cacheBuf = make([]byte, 0)
				continue
			}
		}

		if next {
			if length == maxRead {
				for _, b := range ibuf {
					cacheBuf = append(cacheBuf, b)
				}
				cacheLen += length
				continue
			}

		}

	}
DISCONNECT:
	err := conn.Close()
	fmt.Println("Closed connection:", connFrom)
	checkError(err, "Close:")
}

// ipv4ToInt32: 类似 ip 转 int。
func ipv4ToInt32(bts []byte) uint32 {
	return uint32(bts[0])>>24 + uint32(bts[1])>>16 + uint32(bts[2])>>8 + uint32(bts[3])
}
