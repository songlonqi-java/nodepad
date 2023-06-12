package main

import (
	"bytes"
	"fmt"
	"time"
)

type Event struct {
	Message
}

func (e Event) ToString() string {
	return e.Message.toString()
}

type metric struct {
	Message
}

func (m metric) ToString() string {
	return m.Message.toString()
}

type Heartbeat struct {
	Message
}

func (h Heartbeat) ToString() string {
	return fmt.Sprintf("Heartbeat Type:%s, Name: %s, Status: %s, timeNamo:%d ", h.Type, h.Name, h.Status, h.timestampInNano/1e6)
}

type Transaction struct {
	Message

	durationInNano      int64
	durationStartInNano int64

	children []*Message
}

type defaultTransaction struct {
	children  []Message
	mtype     string
	name      string
	timestamp int64
	data      string
	status    string
	duration  int64
}

func NewTransaction(mtype, name string, flush Flush) *Transaction {
	return &Transaction{
		Message:             *NewMessage(mtype, name, flush),
		durationStartInNano: time.Now().UnixNano(),
	}
}

func (t *Transaction) Complete() {
	if t.durationInNano == 0 {
		durationNano := time.Now().UnixNano() - t.durationStartInNano
		t.durationInNano = durationNano
	}
	t.Message.flush(t)
}

func (t *Transaction) GetDuration() int64 {
	return t.durationInNano
}

func (t *Transaction) SetDuration(durationInNano int64) {
	t.durationInNano = durationInNano
}

func (t *Transaction) SetDurationStart(durationStartInNano int64) {
	t.durationStartInNano = durationStartInNano
}

func (t Transaction) addChild(message *Message) {
	if t.children == nil {
		t.children = make([]*Message, 0)
	}
	t.children = append(t.children, message)
}

func (t *Transaction) setData(bts []byte) {
	if t.data == nil {
		t.data = bytes.NewBuffer(bts)
	} else {
		// todo
	}
}

func (t *Transaction) ToString() string {

	return t.toString()
}
