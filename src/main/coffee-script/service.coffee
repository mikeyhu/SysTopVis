
window.draw = (data)->
	console.log data
	services = data.services
	edges = data.edges

	isServiceHealthy = (serviceName)->
		h = (service.healthy for service in services when service.id is serviceName)[0]
		isHealthy = h == "true"
		res = if isHealthy then "HEALTHY" else "UNHEALTHY" 
		console.log "h:#{h} isHealthy:#{isHealthy} res:#{res}"
		res

	svg = d3.select("svg > g")
	svg.selectAll("*").remove()

	renderer = new dagreD3.Renderer()
	oldDrawNodes = renderer.drawNodes()
	renderer.drawNodes (graph, root)->
		svgNodes = oldDrawNodes(graph, root)
		svgNodes.attr "class", (u)-> "node-#{isServiceHealthy(u)}"
		svgNodes

	layout = renderer.run dagreD3.json.decode(services, edges), d3.select("svg g")
	d3.select("svg")
		.attr("width", layout.graph().width + 40)
		.attr("height", layout.graph().height + 40)


window.start = ()->
	cb = (error,json)->
		console.log "error:#{error} json:#{json}"
		draw(json) unless error

	task = ()->
		d3.json "/services", cb

	setInterval task,2000



