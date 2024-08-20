package upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import connection.GB;
import domain.Attribute;
import domain.DomainData;
import domain.Entity;
import domain.Reference;
import domain.Relationship;

public class BatchUpload {

	
	
	private static JsonCollector fetchJson(String file) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, JsonNode> nodes = new HashMap<>();
		Map<String, List<JsonArc>> arcs = new HashMap<>();
		
		try {
	        List<String> lines = Files.readAllLines(Paths.get(file));
	        for (String line : lines) {
	        	if (line.contains("\"jtype\": \"node\"")) {
	        		JsonNode node = objectMapper.readValue(line, JsonNode.class);
	                nodes.put(node.getIdentity(), node);
	        	} else {
	        		JsonArc arc = objectMapper.readValue(line, JsonArc.class);
	        		List<JsonArc> oldArcs = new ArrayList<>();
	        		if(arcs.containsKey(arc.getSubject())) {
	        			oldArcs = arcs.get(arc.getSubject());
	        		}
	        		oldArcs.add(arc);
        			arcs.put(arc.getSubject(), oldArcs);
	        	}
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return new JsonCollector(nodes, arcs);
	}
	
	private static boolean checkConsistency(Map<String, JsonNode> nodes, Map<String, List<JsonArc>> arcs, DomainData domain) {
		Map<String, Map<String, String>> mandatoryRels = getMandatoryRelationships(domain);
		Map<String, Map<String, String>> mandatoryArcs = new HashMap<>();
		for (String nodekey: nodes.keySet()) {
			for(String mandatoryLabel : mandatoryRels.keySet()) {
				if(domain.getEntity(mandatoryLabel).getName().equals(nodes.get(nodekey).getLabel()) 
						//|| domain.getEntity(mandatoryLabel).hasChild(nodes.get(nodekey).getLabel())
						) {
					Map<String, String> oldArcs = new HashMap<>();
					if(mandatoryArcs.containsKey(nodekey)) {
						oldArcs = mandatoryArcs.get(nodekey);
					}
					oldArcs.putAll(mandatoryRels.get(mandatoryLabel));
					mandatoryArcs.put(nodekey, oldArcs);
				}
			}
        	if (!checkEntity(nodes.get(nodekey), domain)) {
        		return false;
        	}
        	if (!checkMandatoryAttributes(nodes.get(nodekey), domain)) {
        		return false;
        	}
        	if (!checkAttributes(nodes.get(nodekey), domain)) {
        		return false;
        	}
	    }
		for(String nodekey : arcs.keySet()) {
			for (JsonArc arc : arcs.get(nodekey)) { 
				boolean attributeRel = false;
				if(mandatoryArcs.containsKey(nodekey) && mandatoryArcs.get(nodekey).containsKey(arc.getName())) {
		        	if(mandatoryArcs.get(nodekey).get(arc.getName()).equals(nodes.get(arc.getObject()).getLabel()) || 
		        			domain.getEntity(mandatoryArcs.get(nodekey).get(arc.getName())).hasChild(nodes.get(nodekey).getLabel())) {
		        		mandatoryArcs.get(nodekey).remove(arc.getName());
		        		if(mandatoryArcs.get(nodekey).isEmpty()) {
		        			mandatoryArcs.remove(nodekey);
		        		}
		        		attributeRel = true;
		        	}
		        }
		    	if (!checkRelationship(arc, domain)) {
		        	return false;
		        }	
		        if (!checkMandatoryAttributesRelationship(arc, domain)) {
		        	return false;
		        }
		        if (!checkAttributesRelationship(arc, domain)) {
		        	return false;
		        }
		        if (!attributeRel && !checkSubjectObjectRelationship(nodes, arc, domain)) {
		        	return false;
		        }
		        
		    }
		}
	    if(!mandatoryArcs.isEmpty()) {
	    	for(String nodekey : mandatoryArcs.keySet()) {
	    		System.out.println("Attributes " + mandatoryArcs.get(nodekey).keySet() + " not available for node " + nodekey);
	    	}
	    	return false;
	    }
		return true;
	}
	
	private static Map<String, Map<String, String>> getMandatoryRelationships(DomainData domain) {
		Map<String, Map<String, String>> mandatoryRels = new HashMap<>();
		for(Entity e : domain.getAllEntities()) {
			for(Attribute a : e.getAllAttributes()) {
				if(a.getTarget()!=null && !a.getTarget().equals("") && a.getMandatory()) {
					Map<String, String> map = new HashMap<>();
					if(mandatoryRels.containsKey(e.getName())) {
						map = mandatoryRels.get(e.getName());
						map.put(a.getName(), a.getTarget());
					} else {
						map.put(a.getName(), a.getTarget());
					}
					mandatoryRels.put(e.getName(), map);
				}
			}
		}
		return mandatoryRels;
	}
	
	private static boolean checkEntity(JsonNode node, DomainData domain) {
		Entity e = domain.getEntity(node.getLabel());
		if (e == null) {
			System.out.println("Entity " + node.getLabel() + " does not exist");
			return false;
		}
		return true;
	}
	
	private static boolean checkMandatoryAttributes(JsonNode node, DomainData domain) {
		Entity e = domain.getEntity(node.getLabel());
		List<Attribute> mandatoryAttributes = e.getMandatoryAttributes();
		for (Attribute mandatoryAttribute : mandatoryAttributes) {
			if(!node.getProperties().containsKey(mandatoryAttribute.getName()) && !mandatoryAttribute.getDataType().equals("entity")) {
				System.out.println("Mandatory attribute " + mandatoryAttribute.getName() + " not available in node " + node.getIdentity());
				return false;
			}
		}
		return true;
	}
	
	private static boolean checkAttributes(JsonNode node, DomainData domain) {
		List<String> entityAttributes = domain.getEntity(node.getLabel()).getAllAttributesToString();
		for (String attribute : node.getProperties().keySet()) {
			if(!entityAttributes.contains(attribute)) {
				System.out.println("Unpredicted attribute " + attribute + " in entity " + node.getLabel() + " for node " + node.getIdentity());
				return false;
			}
		}
		return true;
	}
	
	private static boolean checkRelationship(JsonArc arc, DomainData domain) {
		Entity r = domain.getRelationship(arc.getName());
		if (r == null) {
			System.out.println("Relationship " + arc.getName() + " does not exist");
			return false;
		}
		return true;
	}
	
	private static boolean checkMandatoryAttributesRelationship(JsonArc arc, DomainData domain) {
		Entity r = domain.getRelationship(arc.getName());
		List<Attribute> mandatoryAttributes = r.getMandatoryAttributes();
		for (Attribute mandatoryAttribute : mandatoryAttributes) {
			if(!arc.getProperties().containsKey(mandatoryAttribute.getName())) {
				System.out.println("Mandatory attribute " + mandatoryAttribute.getName() + " not available in arc " + arc.getSubject() + "-[" + arc.getName() + "]->" + arc.getObject());
				return false;
			}
		}
		return true;
	}
	
	private static boolean checkAttributesRelationship(JsonArc arc, DomainData domain) {
		List<String> entityAttributes = domain.getRelationship(arc.getName()).getAllAttributesToString();
		for (String attribute : arc.getProperties().keySet()) {
			if(!entityAttributes.contains(attribute)) {
				System.out.println("Unpredicted attribute " + attribute + " in arc " + arc.getSubject() + "-[" + arc.getName() + "]->" + arc.getObject());
				return false;
			}
		}
		return true;
	}
	
	private static boolean checkSubjectObjectRelationship(Map<String, JsonNode> nodes, JsonArc arc, DomainData domain) {
		Relationship r = domain.getRelationship(arc.getName());
		if (nodes.get(arc.getSubject()) == null) {
			System.out.println("Subject of arc " + arc.getSubject() + "-[" + arc.getName() + "]->" + arc.getObject() + " does not exist");
			return false;
		}
		if (nodes.get(arc.getObject()) == null) {
			System.out.println("Object of arc " + arc.getSubject() + "-[" + arc.getName() + "]->" + arc.getObject() + " does not exist");
			return false;
		}
		String subjectLabel = nodes.get(arc.getSubject()).getLabel();
		String objectLabel = nodes.get(arc.getObject()).getLabel();
		for (Reference ref : r.getReferences()) {
			Entity subjentity = domain.getEntity(ref.getSubject());
			Entity objentity = domain.getEntity(ref.getObject());
			if ( (ref.getSubject().equals(subjectLabel) || subjentity.hasChild(subjectLabel)) && 
				 (ref.getObject().equals(objectLabel) || objentity.hasChild(objectLabel)) ) {
				return true;
			}
		}
		System.out.println("Domain and/or range do not match with arc " + arc.getSubject() + "-[" + arc.getName() + "]->" + arc.getObject());
		return false;
	}
	
	/*
	 * CREATE 
	 * (:Test {identity:'1', name: 'Alice'}),
       (:Test {identity:'2', name: 'Bob'}),
       (:Test {identity:'3', name: 'Charlie'});
       
       - - -
       
       WITH [
  		{a: '1', b: '2', props: {x: 100, y: "abc", z: 500}},
  		{a: '3', b: '2', props: {x: 123, y: "bar", z: 99}}
	   ] AS data

		UNWIND data AS d
		MATCH (a {identity: d.a}), (b {identity: d.b})
		CREATE (a)-[r:BAR]->(b)
		SET r = d.props;
	 */
	
	public static boolean upload(String file, DomainData domain, boolean consistency) {
		JsonCollector jsoncollector = fetchJson(file);
		Map<String, JsonNode> nodes = jsoncollector.getNodes();
		Map<String, List<JsonArc>> arcs = jsoncollector.getArcs();
		
		if (!consistency && !checkConsistency(nodes, arcs, domain)) {
			return false;
		}
		String createNodesQuery = "CREATE ";
		for(String nodekey : nodes.keySet()) {
			nodes.get(nodekey).setProperties(formatProperties(nodes.get(nodekey).getProperties()));
			String label = nodes.get(nodekey).getLabel();
			String identity = nodes.get(nodekey).getIdentity();
			String properties;
			if(nodes.get(nodekey).getProperties().isEmpty()) {
				properties = nodes.get(nodekey).getProperties().toString().replace("{", "{identity: '" + identity + "'").replace("=", ":");
			}else {
				properties = nodes.get(nodekey).getProperties().toString().replace("{", "{identity: '" + identity + "', ").replace("=", ":");
			}
			createNodesQuery += "(:" + label + " " + properties + "), ";
		}
		createNodesQuery = createNodesQuery.substring(0, createNodesQuery.length()-2);
		System.out.println(createNodesQuery);
		
		String createArcsQuery = "";
		int c = 0;
		for(String nodekey : arcs.keySet()) {
			for(JsonArc arc : arcs.get(nodekey)) {
				createArcsQuery += "MATCH (n" + c + " {identity: '" + nodekey + "'}), (m" + c + " {identity: '" + arc.getObject() + "'}) CREATE (n" + c + ")-[:" + arc.getName() + "]->(m" + c + ") WITH n" + c + ", m" + c + "\n";
				c++;
			}
		}
		createArcsQuery = createArcsQuery.substring(0, createArcsQuery.lastIndexOf(")") + 1);
		System.out.println(createArcsQuery);
		
		GB gb = new GB("default");
		gb.getGraphdb().executeQuery(createNodesQuery);
		gb.getGraphdb().executeQuery(createArcsQuery);
		return true;
	}
	
	private static Map<String, String> formatProperties(Map<String, String> properties) {
		Map<String, String> formattedProperties = new HashMap<String, String>();
		for(String key : properties.keySet()) {
			formattedProperties.put(key, "'" + properties.get(key) + "'");
		}
		return formattedProperties;
	}
}
