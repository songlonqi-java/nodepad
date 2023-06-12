json(_,index)
json(_,k8s_pod)
json(_,k8s_pod_namespace)
json(_,logger)
# json(_,k8s_container_name)
json(_,k8s_node_name)
json(_,fields)
json(fields,env)
json(fields,module)
json(fields,productline)
json(_,prospector)
json(prospector,type)
json(_,docker_container)
# json(_,source)
json(_,logInfo)
json(logInfo,errorMessage)
json(logInfo,errorCode)
json(logInfo,success)
json(logInfo,status)
json(_,logTime)
json(_,traceId)
json(_,logLevel)
json(_,beat)
json(beat,name)
json(beat,hostname)
json(beat,version)
json(_,threadName)
json(_,offset)

if productline {

}else{
add_key(productline,"default")
}

json(_, .[0].x_request_id, x_request_id)

