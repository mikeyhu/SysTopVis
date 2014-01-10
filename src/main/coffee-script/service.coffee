
window.draw = (data)->
	console.log data
	services = data.services.map (s)->
		{id: s, value :{label:s} }
	edges = data.edges

	renderer = new dagreD3.Renderer()
	oldDrawNodes = renderer.drawNodes()
	renderer.drawNodes (graph, root)->
		svgNodes = oldDrawNodes(graph, root)
		svgNodes.attr "id", (u)-> "node-" + u
		svgNodes

	layout = renderer.run dagreD3.json.decode(services, edges), d3.select("svg g")
	d3.select("svg")
		.attr("width", layout.graph().width + 40)
		.attr("height", layout.graph().height + 40)


window.start = ()->
	d3.json "/services",(error,json)->
		console.log "error:#{error} json:#{json}"
		draw(json) unless error

