package main

import (
	"bytes"
)

type Context struct {
	m_data []byte
	/*
		最终返回的 tree
			header: messageTree 填充string
			start : 初始化 dt，放到 parents 中，再放到 tree的transactions中。
			end: 从parent中取出第一个 填充end属性 也是最终返回的tree
			m: 放入tree 的metric数组中
			h:
	*/
	m_tree *MessageTree
	//
	m_parents []*Transaction // 暂存 transaction 的地方
}

func (mt *MessageTree) ToString() string {
	str := "domain: " + mt.domain + "\n" +
		"host name: " + mt.hostName + "\n" +
		"address: " + mt.addr + "\n" +
		"thread group name: " + mt.threadGroupName + "\n" +
		"thread ID: " + mt.ThreadID + "\n" +
		"thread name: " + mt.threadName + "\n" +
		"message ID: " + mt.MessageID + "\n" +
		"parent message ID: " + mt.parentMessageID + "\n" +
		"root message ID: " + mt.RootMessageID + "\n" +
		"session token: " + mt.SessionToken + "\n"

	if mt.message != nil {
		str += "message: " + mt.message.String() + "\n"
	}

	str += "events: [\n"
	for _, event := range mt.events {
		str += "\t" + event.ToString() + "\n"
	}
	str += "]\n"

	str += "transactions: [\n"
	for _, transaction := range mt.transactions {
		str += "\t" + transaction.ToString() + "\n"
	}
	str += "]\n"

	str += "heartbeats: [\n"
	for _, heartbeat := range mt.heartbeats {
		str += "\t" + heartbeat.ToString() + "\n"
	}
	str += "]\n"

	str += "metrics: [\n"
	for _, metric := range mt.metrics {
		str += "\t" + metric.ToString() + "\n"
	}
	str += "]\n"

	return str
}

type MessageTree struct {
	domain          string
	hostName        string
	addr            string
	threadGroupName string
	ThreadID        string
	threadName      string
	MessageID       string
	parentMessageID string
	RootMessageID   string
	SessionToken    string
	message         *Message
	events          []*Event
	transactions    []*Transaction
	heartbeats      []*Heartbeat
	metrics         []*metric
}

func (t *MessageTree) addEvent(e *Event) {
	if t.events == nil {
		t.events = make([]*Event, 0)
	}
	t.events = append(t.events, e)
}

func (t *MessageTree) addMetric(m *metric) {
	if t.metrics == nil {
		t.metrics = make([]*metric, 0)
	}
	t.metrics = append(t.metrics, m)
}

func (ctx *Context) addChild(msg *Message) {
	if len(ctx.m_parents) != 0 {
		ctx.m_parents[0].addChild(msg)
	} else {
		ctx.m_tree.message = msg
	}
}

func (c *Context) getMessageTree() *MessageTree {
	return c.m_tree
}

func (c *Context) getVersion(buf *bytes.Buffer) string {
	data := make([]byte, 3)
	buf.Read(data)
	return string(data)
}

func (c *Context) popTransaction() *Transaction {
	t := c.m_parents[len(c.m_parents)-1]
	c.m_parents = c.m_parents[:len(c.m_parents)-1]
	return t
}

func (c *Context) pushTransaction(t *Transaction) {
	if len(c.m_parents) > 0 {
		c.m_parents[len(c.m_parents)-1].addChild(&t.Message)
	}
	c.m_parents = append(c.m_parents, t)
}

// todo parse heartbeat 解析成指标 上报 com.dianping.cat.consumer.heartbeat.HeartbeatAnalyzer#buildHeartBeatInfo
