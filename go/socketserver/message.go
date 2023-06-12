package main

import (
	"bytes"
	"encoding/xml"
	"fmt"
	"time"
)

const (
	SUCCESS = "0"
	FAIL    = "-1"
)

type Flush func(m Messager)

type MessageGetter interface {
	GetData() *bytes.Buffer
	GetTime() time.Time
}

type Messager interface {
	MessageGetter
	AddData(k string, v ...string)
	SetStatus(status string)
	Complete()
}

type Message struct {
	Type   string
	Name   string
	Status string

	timestampInNano int64

	data *bytes.Buffer

	flush Flush
}

func NewMessage(mtype, name string, flush Flush) *Message {
	return &Message{
		Type:            mtype,
		Name:            name,
		Status:          SUCCESS,
		timestampInNano: time.Now().UnixNano(),
		data:            new(bytes.Buffer),
		flush:           flush,
	}
}

func (m *Message) Complete() {
	m.flush(m)
}

func (m *Message) GetData() *bytes.Buffer {
	return m.data
}

func (m *Message) GetTime() time.Time {
	return time.Unix(0, m.timestampInNano)
}

func (m *Message) SetTimestamp(timestampInNano int64) {
	m.timestampInNano = timestampInNano
}

func (m *Message) GetTimestamp() int64 {
	return m.timestampInNano
}

func (m *Message) AddData(k string, v ...string) {
	if m.data.Len() != 0 {
		m.data.WriteRune('&')
	}
	if len(v) == 0 {
		m.data.WriteString(k)
	} else {
		m.data.WriteString(k)
		m.data.WriteRune('=')
		m.data.WriteString(v[0])
	}
}

func (m *Message) SetStatus(status string) {
	m.Status = status
}

func (m *Message) toString() string {
	return fmt.Sprintf("Message Type:%s, Name: %s, Status: %s, timeNamo:%d data lens :%s", m.Type, m.Name, m.Status, m.timestampInNano/1e6, m.data)
}

func (m *Message) String() string {
	return m.toString()
}

// ---------------------------- heartbeat -------------

type Status struct {
	XMLName   xml.Name    `xml:"status"`
	Timestamp string      `xml:"timestamp,attr"`
	Runtime   Runtime     `xml:"runtime"`
	OS        OS          `xml:"os"`
	Disk      Disk        `xml:"disk"`
	Memory    Memory      `xml:"memory"`
	Thread    Thread      `xml:"thread"`
	Message   MessageXML  `xml:"message"`
	Extension []Extension `xml:"extension"`
}

type Runtime struct {
	StartTime     string `xml:"start-time,attr"`
	Uptime        string `xml:"up-time,attr"`
	JavaVersion   string `xml:"java-version,attr"`
	UserName      string `xml:"user-name,attr"`
	UserDir       string `xml:"user-dir"`
	JavaClasspath string `xml:"java-classpath"`
}

type OS struct {
	Name                   string  `xml:"name,attr"`
	Arch                   string  `xml:"arch,attr"`
	Version                string  `xml:"version,attr"`
	AvailableProcessors    int     `xml:"available-processors,attr"`
	SystemLoadAverage      float64 `xml:"system-load-average,attr"`
	ProcessTime            int64   `xml:"process-time,attr"`
	TotalPhysicalMemory    int64   `xml:"total-physical-memory,attr"`
	FreePhysicalMemory     int64   `xml:"free-physical-memory,attr"`
	CommittedVirtualMemory int64   `xml:"committed-virtual-memory,attr"`
	TotalSwapSpace         int64   `xml:"total-swap-space,attr"`
	FreeSwapSpace          int64   `xml:"free-swap-space,attr"`
}

type Disk struct {
	DiskVolumes []DiskVolume `xml:"disk-volume"`
}

type DiskVolume struct {
	ID     string `xml:"id,attr"`
	Total  int64  `xml:"total,attr"`
	Free   int64  `xml:"free,attr"`
	Usable int64  `xml:"usable,attr"`
}

type Memory struct {
	Max          int64 `xml:"max,attr"`
	Total        int64 `xml:"total,attr"`
	Free         int64 `xml:"free,attr"`
	HeapUsage    int64 `xml:"heap-usage,attr"`
	NonHeapUsage int64 `xml:"non-heap-usage,attr"`
	GC           []GC  `xml:"gc"`
}

type GC struct {
	Name  string `xml:"name,attr"`
	Count int    `xml:"count,attr"`
	Time  int    `xml:"time,attr"`
}

type Thread struct {
	Count             int `xml:"count,attr"`
	DaemonCount       int `xml:"daemon-count,attr"`
	PeekCount         int `xml:"peek-count,attr"`
	TotalStartedCount int `xml:"total-started-count,attr"`
	CatThreadCount    int `xml:"cat-thread-count,attr"`
	PigeonThreadCount int `xml:"pigeon-thread-count,attr"`
	HttpThreadCount   int `xml:"http-thread-count,attr"`
}

type MessageXML struct {
	Produced   int64 `xml:"produced,attr"`
	Overflowed int64 `xml:"overflowed,attr"`
	Bytes      int64 `xml:"bytes,attr"`
}

type Extension struct {
	ID               string            `xml:"id,attr"`
	ExtensionDetails []ExtensionDetail `xml:"extensionDetail"`
}

type ExtensionDetail struct {
	Id    string  `xml:"id,attr"`
	Value float64 `xml:"value,attr"`
}
