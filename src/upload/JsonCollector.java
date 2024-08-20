package upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonCollector {
	private Map<String, JsonNode> nodes = new HashMap<>();
	private Map<String, List<JsonArc>> arcs = new HashMap<>();
	
	public JsonCollector(Map<String, JsonNode> nodes, Map<String, List<JsonArc>> arcs) {
		this.nodes = nodes;
		this.arcs = arcs;
	}
	
	public Map<String, JsonNode> getNodes() {
		return nodes;
	}
	
	public void setNodes(Map<String, JsonNode> nodes) {
		this.nodes = nodes;
	}
	
	public Map<String, List<JsonArc>> getArcs() {
		return arcs;
	}
	
	public void setArcs(Map<String, List<JsonArc>> arcs) {
		this.arcs = arcs;
	}
}
