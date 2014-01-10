(function() {
  window.draw = function(data) {
    var edges, layout, oldDrawNodes, renderer, services;
    console.log(data);
    services = data.services.map(function(s) {
      return {
        id: s,
        value: {
          label: s
        }
      };
    });
    edges = data.edges;
    renderer = new dagreD3.Renderer();
    oldDrawNodes = renderer.drawNodes();
    renderer.drawNodes(function(graph, root) {
      var svgNodes;
      svgNodes = oldDrawNodes(graph, root);
      svgNodes.attr("id", function(u) {
        return "node-" + u;
      });
      return svgNodes;
    });
    layout = renderer.run(dagreD3.json.decode(services, edges), d3.select("svg g"));
    return d3.select("svg").attr("width", layout.graph().width + 40).attr("height", layout.graph().height + 40);
  };

  window.start = function() {
    return d3.json("/services", function(error, json) {
      console.log("error:" + error + " json:" + json);
      if (!error) {
        return draw(json);
      }
    });
  };

}).call(this);
