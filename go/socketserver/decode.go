package main

import (
	"bytes"
	"fmt"
	"strings"
	"time"
)

/*
包信息：
	头部4字节表示包长度
	接着就是 version domain hostName 。。。。用 0x7f 分割每一个string字段 换行符
	最后是包体

	开始 RootMessageID	trace-id
	messageID			span id
	parenMessageID 		parentSpanID

一个头部信息包含 messageID... 如果一个包中有几个 transaction 那么是一样的 span id

*/

func handleMsg(buf *bytes.Buffer, ip string) {
	fmt.Printf("ip=%s  start \n", ip)
	version := readVersion(buf)
	fmt.Printf("ip=%s version=%s \n", ip, version)
	msgTree := &MessageTree{
		domain:          readString(buf),
		hostName:        readString(buf),
		addr:            readString(buf),
		threadGroupName: readString(buf),
		ThreadID:        readString(buf),
		threadName:      readString(buf),
		MessageID:       readString(buf),
		parentMessageID: readString(buf),
		RootMessageID:   readString(buf),
		SessionToken:    readString(buf),
	}
	fmt.Printf("ip=%s treeheader =%+v \n", ip, msgTree)
	ctx := &Context{m_tree: msgTree}
	// data t
	dt := ctx.decodeMessage(buf, ip)
	fmt.Printf("trancation= trace-id=%s\n parentSpan-id=%s \n span-id=%s \n service=%s resource=%s operation=%s source=%s status=%s \n", msgTree.RootMessageID,
		msgTree.parentMessageID, msgTree.MessageID, msgTree.domain, dt.Name, dt.Name, "cat", "ok")
	fmt.Println("ip:=" + ip + "   " + ctx.m_tree.ToString())
	fmt.Printf("ip=%s context=%+v \n", ip, ctx)
	fmt.Println("-------end print------" + ip)

}

func checkError(error error, info string) {
	if error != nil {
		panic("ERROR: " + info + " " + error.Error()) // terminate program
	}
}

func (ctx *Context) decodeMessage(buf *bytes.Buffer, ip string) *Transaction {
	var msg *Transaction
	for buf.Len() > 0 {
		b := readInt(buf)
		switch b {
		case 't': // 开始 RootMessageID --》 trace-id
			fmt.Printf("ip=%s t start \n", ip)
			timestamp := readInt64(buf)

			t := readString(buf)
			name := readString(buf)
			if "System" == t && strings.HasPrefix(name, "UploadMetric") {
				name = "UploadMetric"
			}
			dt := &Transaction{}
			dt.Type = t
			dt.Name = name
			//dt. = timestamp
			timeStart := time.UnixMilli(timestamp)
			fmt.Printf("ip=%s  time start %s \n", ip, timeStart.String())
			dt.SetDurationStart(timestamp * 1000) // todo  纳秒
			ctx.pushTransaction(dt)
		case 'T': // 结束
			fmt.Printf("ip=%s  t end \n", ip)
			status := readString(buf)
			data := readString(buf)
			duration := readInt64(buf)
			//	dt.Status = status

			//	dt.data = bytes.NewBuffer([]byte(data))
			//	dt.SetDuration(duration)
			// d := time.Duration(duration * 1000)

			dt := ctx.popTransaction()
			dt.SetStatus(status)
			dt.SetDuration(duration) // todo 纳秒
			dt.setData([]byte(data))
			ctx.m_tree.transactions = append(ctx.m_tree.transactions, dt)
			msg = dt
		case 'E':
			fmt.Printf("ip=%s  E case \n", ip)
			timestamp := readInt64(buf)
			t := readString(buf)
			name := readString(buf)
			status := readString(buf)
			data := readString(buf)
			e := &Event{
				Message{
					Type:            t,
					Name:            name,
					Status:          status,
					timestampInNano: timestamp,
					data:            bytes.NewBuffer([]byte(data)),
				},
			}
			fmt.Printf("metric=%s \n", e.toString())
			ctx.m_tree.addEvent(e)
		case 'M':
			fmt.Println("M case")
			timestamp := readInt64(buf)
			t := readString(buf)
			name := readString(buf)
			status := readString(buf)
			data := readString(buf)
			m := &metric{Message{
				Type:            t,
				Name:            name,
				Status:          status,
				timestampInNano: timestamp,
				data:            bytes.NewBuffer([]byte(data)),
			}}
			fmt.Printf("metric=%s \n", m.toString())
			ctx.m_tree.addMetric(m)
			ctx.addChild(&m.Message)
		case 'L':
			fmt.Println("L case")
			timestamp := readInt64(buf)
			t := readString(buf)
			name := readString(buf)
			status := readString(buf)
			data := readString(buf)
			h := &Message{
				Type:            t,
				Name:            name,
				Status:          status,
				timestampInNano: timestamp,
				data:            bytes.NewBuffer([]byte(data)),
			}
			fmt.Printf("metric=%s \n", h.toString())
			ctx.addChild(h)
		case 'H':
			fmt.Println("H case")
			timestamp := readInt64(buf)
			t := readString(buf)
			name := readString(buf)
			status := readString(buf)
			data := readString(buf)
			h := &Heartbeat{
				Message{
					Type:            t,
					Name:            name,
					Status:          status,
					timestampInNano: timestamp,
					data:            bytes.NewBuffer([]byte(data)),
				},
			}
			fmt.Printf("heartbase=%s \n", h.toString())
			// ctx.addChild(h)
			if ctx.m_tree.heartbeats == nil {
				ctx.m_tree.heartbeats = make([]*Heartbeat, 0)
			}
			ctx.m_tree.heartbeats = append(ctx.m_tree.heartbeats, h)
		default:
			fmt.Println("default case")
		}
	}
	if msg == nil {
		// msg = ctx.m_tree.message
	}
	return msg
}

var m_data = make([]byte, 256)

func readString(buf *bytes.Buffer) string {
	length := int(readVarInt(buf, 32))
	if length == 0 {
		return ""
	} else if length > len(m_data) {
		m_data = make([]byte, length)
	}
	buf.Read(m_data[0:length])

	return string(m_data[0:length])
}

func readInt64(buf *bytes.Buffer) int64 {
	return readVarInt(buf, 64)
}

func readVarInt(buf *bytes.Buffer, len int) int64 {
	shift, result := 0, int64(0)
	for shift < len {
		b, err := buf.ReadByte()
		if err != nil {
			fmt.Println(err)
		}
		result = result | (int64)(b&0x7F)<<shift
		if (b & 0x80) == 0 {
			return result
		}
		shift += 7
	}
	fmt.Printf("------- read len=%d err \n", len)
	return 0
}

func readInt(buf *bytes.Buffer) byte {
	b, err := buf.ReadByte()
	if err != nil {
		fmt.Println(err)
	}
	return b
}

func readVersion(buf *bytes.Buffer) string {
	bts := make([]byte, 3)
	buf.Read(bts)
	return string(bts)
}
