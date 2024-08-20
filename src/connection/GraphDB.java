package connection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import domain.Attribute;
import domain.DomainData;
import domain.Entity;
import domain.Instance;
import domain.RelationTriple;
import domain.Relationship;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ClientException;
import org.neo4j.driver.util.Pair;
//import dominus.web.common.io.RelationalDB;

/**
 * The GraphDB class represents a connection to the graph database neo4j.
 * It provides methods for interacting with the graph database, such as querying and modifying data.
 * The class implements the AutoCloseable interface, allowing it to be used in try-with-resources statements.
 */
public final class GraphDB implements AutoCloseable {
	private final Driver driver;
	private static Session session;

	public final String ENTITY = "e";  // DA RIVEDERE SU DB
	public final String RELATION = "r";
	public final String YEAR = "Year";
	public final String MONTH = "Month";
	public final String DAY = "Day";

//	public GraphDB() {
//		Config noSSL = Config.builder().withoutEncryption().build();
////		driver = GraphDatabase.driver( "bolt://localhost", AuthTokens.basic("neo4j", "neo") ); // locale
////		driver = GraphDatabase.driver( "bolt://193.204.187.73:8090", AuthTokens.basic("neo4j", "memoria") ); // remoto
////		driver = GraphDatabase.driver( "bolt://193.204.187.178:7687", AuthTokens.basic("graphbrain", "6r4ph8r41n") );
//
//		
////		driver = GraphDatabase.driver( "bolt://localhost", AuthTokens.basic("neo4j", "neo") ); // locale
////		driver = GraphDatabase.driver( "bolt://193.204.187.178:7687", AuthTokens.basic("--user--", "--password--") ,noSSL); // remoto
////		session = driver.session();
//	}
	
	public GraphDB(String url, String username, String password) {
		Config noSSL = Config.builder().withoutEncryption().build();
		//driver = GraphDatabase.driver( "bolt://193.204.187.178:7687", AuthTokens.basic("graphbrain", "6r4ph8r41n") ); // LVCVM
		System.out.println(username + " and " + password);
		
		driver = GraphDatabase.driver(url, AuthTokens.basic(username, password), noSSL); 
		//driver = GraphDatabase.driver( "bolt://193.204.187.73:8090", AuthTokens.basic("graphbrain", "6r4ph8r41n") ); // remoto
		session = driver.session();
	}

	public static Session getSession() {
		return session;
	}

	public Driver getDriver() {
		return driver;
	}

	@Override
	public void close() throws Exception {
		session.close();
		driver.close();
	}

	public List<Instance> listInstancesEntity(String domain, String entityType, List<String> subClasses, DomainData domainSchema) {
		return listInstancesEntity(domain, subClasses, domainSchema.properties(entityType));
	}

	public List<Instance> listInstancesEntity(String domain, List<String> subClasses, List<Attribute> attributes) {
		String selectClause = " WHERE " + subClassRestriction("n",subClasses);
		return listInstancesEntity(domain, selectClause, attributes);
	}

	private List<Instance> listInstancesEntity(String query, List<Attribute> attributes) {
		if(session != null) {
			Result result = session.run(query);
			LinkedList<Instance> instances = populateInstancesMenu(result, attributes);
			return instances; 
		} else
			throw new ClientException("Graph database disconnected!");
	}

	//    	Davide: forse da rimuovere

	public List<Instance> listInstancesEntity(String domain, String selectClause, List<Attribute> attributes) {
		String domainRestriction;
		if(domain != null)
			domainRestriction = ":" + domain;
		else
			domainRestriction = "";
		if(session != null) {
			String query = "MATCH (n" + domainRestriction + ")" + selectClause + " RETURN id(n),properties(n),labels(n)";
			query += buildOrderClause("n", attributes); // + " LIMIT 4000";
			System.out.println(query);
			Result result = session.run(query);
			LinkedList<Instance> instances = populateInstancesMenu(result, attributes);
			return instances; 
		} else
			throw new ClientException("Graph database disconnected!");
	}


	public void updateRelationInstance(DomainData domain, String instanceRelId, String subject, String subjectInstance, String selectedRelationship, String label, String instanceId, List<Attribute> fullAttributes, HashMap<String,String> entityForm, String relationshipDescription) throws Exception {
		//comprende gia' l'attack per modifica e la modifica dell'autore
		Relationship relationship = domain.getRelationship(selectedRelationship);
		Entity subjEntity = domain.getEntity(subject);
		Entity objEntity = domain.getEntity(label);
		if (subjEntity.getAllSubclassesToString().contains(subject) && objEntity.getAllSubclassesToString().contains(label)) {
			for (Attribute attr : relationship.getMandatoryAttributes()) {
				if (entityForm.containsKey(attr.getName())) {
					throw new Exception("Missing mandatory attributes");
				}
			}
			editRelation(instanceRelId, subject, selectedRelationship, label, fullAttributes, entityForm, relationshipDescription);
			setDomainLabel(subject, subjectInstance, domain.getDomain());
			setDomainLabel(label, instanceId, domain.getDomain());
		}
		throw new Exception("Subject or object does not match with relationship");
	}

	public List<Instance> searchEntity(String domainRestriction, List<String> subClasses, List<Attribute> fields, HashMap<String,String> valuesForm) {
		List<String> values = compileAttributes(valuesForm, fields);
		String selectClause = " WHERE " + subClassRestriction("n",subClasses);
		for(int i = 0; i < fields.size(); i++) {
			if(!values.get(i).isEmpty()) {
				selectClause += " AND toLower(n." + fields.get(i).getName() + ") CONTAINS toLower('" + values.get(i) + "')";
			}
		}
		return listInstancesEntity(domainRestriction, selectClause, fields);
	}

	public void deleteRelationInstance(String instanceRelId, String relationshipDescription) throws SQLException {
		removeRelation(instanceRelId);
	}

	public LinkedList<Instance> listInstancesRelation(String instance1, String relationType, String instance2, List<Attribute> fields) {
		if(session != null) {
			Result result = session.run( "MATCH (x)-[r:" + relationType + "]->(x1) WHERE id(x)=" + instance1 
					+ " AND id(x1)=" + instance2 + " RETURN id(r),properties(r),type(r)" );
			LinkedList<Instance> instances = populateInstancesMenuRel(result, fields);
			return instances;
		} else
			throw new ClientException("Graph database disconnected!");
	}

	private String subClassRestriction(String nodeVar, List<String> subClasses) {
		String restriction = "(";
		Boolean first = true;
		for(String s : subClasses) {
			if(!first)
				restriction += " OR ";
			else
				first = false;
			restriction += nodeVar + ":`" + s + "`";
		}
		return restriction + ")";
	}

	//ste class Management ?
	private TreeSet<String> populateClassesMenu(Result result) {
		TreeSet<String> classes = new TreeSet<String>();
		while (result.hasNext()) {
			Record record = result.next();
			//gson.toJson(record.asMap());
			classes.add(extractTypeLabel(record.get(0).asList()));
		}
		return classes;
	}

	private TreeSet<String> populateRelationshipsMenu(Result result) {
		TreeSet<String> rels = new TreeSet<String>();
		while (result.hasNext()) {
			rels.add(result.next().get(0).toString().replace("\"", ""));
		}
		return rels;
	}

	private LinkedList<Instance> populateInstancesMenu(Result result, List<Attribute> fields) {
		LinkedList<Instance> myInstances = new LinkedList<Instance>();
		while (result.hasNext()) {
			Record record = result.next();
			//gson.toJson(record.asMap());
			myInstances.add(new Instance(extractTypeLabel(record.get(2).asList()), "" + record.get(0).asInt(), toAttributeValuesMap(record.get(1).asMap()), fields));
			//ste PRIMA MenuItemInstance NON METTEVA GLI ATTRIBUTI NELL'ISTANZA, METTERE UN FLAG PER RENDERLO PIU' LEGGERO?
		}
		return myInstances;
	}

	public String insertRelationInstance(DomainData domain, String subject, String subjectInstance, String selectedRelationship, String label, String instanceId, List<Attribute> fullAttributes, HashMap<String,String> entityForm, String relationshipDescription) throws Exception {
		//comprende l'inserimento dell'autore della relazione e vari campi valorizzati
		Relationship relationship = domain.getRelationship(selectedRelationship);
		Entity subjEntity = domain.getEntity(subject);
		Entity objEntity = domain.getEntity(label);
		if (subjEntity.getAllSubclassesToString().contains(subject) && objEntity.getAllSubclassesToString().contains(label)) {
			for (Attribute attr : relationship.getMandatoryAttributes()) {
				if (entityForm.containsKey(attr.getName())) {
					throw new Exception("Missing mandatory attributes");
				}
			}
			String instanceRelId = createRelation(subject, selectedRelationship, label, subjectInstance, instanceId, fullAttributes, entityForm, relationshipDescription);
			List<String> values = compileAttributes(entityForm, fullAttributes);
			setDomainLabel(subject, subjectInstance, domain.getDomain());
			setDomainLabel(label, instanceId, domain.getDomain());
			return instanceRelId;
		}
		throw new Exception("Subject and object do not match with the relationship");
//		String instanceRelId = createRelation(subject, selectedRelationship, label, subjectInstance, instanceId, fullAttributes, entityForm, relationshipDescription);
//		List<String> values = compileAttributes(entityForm, fullAttributes);
//		setDomainLabel(subject, subjectInstance, domain.getDomain());
//		setDomainLabel(label, instanceId, domain.getDomain());
//		return instanceRelId;
	}

	public List<RelationTriple> searchRelationTriples(List<String> subjClasses, String subjectInstance, String selectedRelationship, List<String> objClasses, String instanceId, DomainData domainSchema, List<Attribute> fullAttributes, HashMap<String,String> partialEntityForm) {
		List<RelationTriple> relSearchRes;
		if(partialEntityForm != null) { // || isVoidSearch()) {
			relSearchRes = searchRelation(subjClasses, subjectInstance, selectedRelationship, objClasses, instanceId, domainSchema, fullAttributes, partialEntityForm);
		} else
			relSearchRes = searchRelation(subjClasses, subjectInstance, selectedRelationship, objClasses, instanceId, domainSchema, null, null);
		return relSearchRes;
	}

	public List<Instance> getInstanceEntities(String domainRestriction, String entityType, Entity subclass, boolean subclassRestriction, DomainData domainSchema) {
		return listInstancesEntity(domainRestriction, entityType, subclass.getAllSubclassNames(subclassRestriction), domainSchema); //ste aggiunto domainSchema,fullAttributes
	}

	private String extractTypeLabel(List<Object> labels) { //ste unificare con Management.extractTypeLabel? Mettere nel costruttore di Instance?
		String label = null;
		for(Object l : labels)
			if(Character.isUpperCase(l.toString().charAt(0))) {
				label = l.toString();
				break;
			}
		return label;
	}

	private LinkedList<Instance> populateInstancesMenuRel(Result result, List<Attribute> fields) {
		LinkedList<Instance> myInstances = new LinkedList<Instance>();
		while (result.hasNext()) {
			Record record = result.next();
			//gson.toJson(record.asMap());
			myInstances.add(new Instance(record.get(2).asString(), "" + record.get(0).asInt(), toAttributeValuesMap(record.get(1).asMap()), fields));
			//ste PRIMA MenuItemInstance NON METTEVA GLI ATTRIBUTI NELL'ISTANZA, METTERE UN FLAG PER RENDERLO PIU' LEGGERO?
		}
		return myInstances;
	}

	public List<String> compileAttributes(HashMap<String,String> valuesMap, List<Attribute> fields) {
		List<String> valuesList = new LinkedList<String>();
		for(int i=0; i<fields.size(); i++) {
			String value = valuesMap.get(fields.get(i).getName()); //ste per i non compilati e' "" non null!
			if(value == null) //ste per i menu a tendina non selezionati e' null
				value = "";
			//    			formattedEntity.add(value.replace(",", " ").replace(":", " - ").replace("\"", "''")); //ste caratteri , : ' " non ammessi
			valuesList.add(value.replace("\"", "\\\""));
		}
		return valuesList;
	}

	private String composeObjectToQuery(List<Attribute> attributes, List<String> values) {
		//ste passare anche i vecchi valori ed aggiungere quelli non previsti dal dominio attuale
		String listing = "{";
		for(int i = 0; i < attributes.size(); i++) {
			if(!values.get(i).equals("")) {
				if(attributes.get(i).getDataType().equals("entity"))
					System.out.println("Nodo con cui stabilire la relazione: " + values.get(i));
				listing += attributes.get(i).getName() + ":\"" + values.get(i) + "\",";
			}
		}
		try { //ste if(attributes.size() > 0)
			listing = listing.substring(0, listing.lastIndexOf(",")) + "}";
		} catch(Exception e) { //ste else
			listing = "{}";
		}
		return listing;
	}

	public void changeLabelKind(String oldLabel, String newLabel, String kind) {
		setKind(oldLabel, kind);
		changeLabel(oldLabel, newLabel);
	}

	public void changeLabel(String oldLabel, String newLabel) {
		System.out.println("MATCH (n:" + oldLabel + ") REMOVE n:" + oldLabel + " SET n:" + newLabel);
		if(session != null) {
			session.run( "MATCH (n:" + oldLabel + ") REMOVE n:" + oldLabel + " SET n:" + newLabel);
		}
	}

	public void setKind(String label, String kind) {
		List<Record> res = getAllNodesWithLabel(label).list();
		for(Record record : res) {
			List<Pair<String,Value>> values = record.fields();
			for (Pair<String,Value> nameValue: values) {
				if ("n".equals(nameValue.key())) {  // you named your node "p"
					Value value = nameValue.value();
					if (value.get("kind").asString().equals("") || 
							value.get("kind").asString().equals("null")) {
						Long id = record.get("n").asNode().id();
						//String idString = Long.toString(id);
						addPropertyNode(id, "kind", label);
					} 
				}
			}
		}	
	}
	
	private ArrayList<String> toString(List<Attribute> attributes) {
		ArrayList<String> attributesString = new ArrayList<>();
		for (Attribute attr : attributes) {
			attributesString.add(attr.getName());
		}
		return attributesString;
	}

	public String createEntity(DomainData domainData, String entityType, List<Attribute> attributes, HashMap<String,String> entityForm) throws Exception {
		Entity entity = domainData.getEntity(entityType);
		if (entity != null) {
			for(Attribute attr : attributes) {
				if (!entity.getAllAttributesToString().contains(attr.getName())) {
					throw new Exception("Not existing attribute");
				}
			}
			for(Attribute attr : entity.getMandatoryAttributes()) {
				if (!toString(attributes).contains(attr.toString())) {
					throw new Exception("Not existing attribute");
				}
			}
		}
		List<String> values = compileAttributes(entityForm, attributes);
		System.out.println(attributes);
		String listing = composeObjectToQuery(attributes, values);
		if(session != null) {
			Result result = session.run( "CREATE (n:" + entityType + listing + ") RETURN n" );
			Record record = result.next();
			String id = record.get(0).toString().replaceAll("[^0-9]", ""); // la get restituisce "node<id>", vengono tenute solo le cifre
			handleSpecialAttributes(session, ENTITY, id, attributes, values);
			return id;
		} else 
			throw new ClientException("Graph database disconnected!");
	}
	
	public String mergeAttributesClause(String username, String domain, String reference, String targetId, String referenceType, String description, List<String> attributes, HashMap<String,String> target, HashMap<String,String> source) throws SQLException {
		RelationalDB rb = new RelationalDB();
		String setClause = " SET ";
		Boolean firstSet = true;
		for (String a : attributes) {
			String oldVal = target.get(a);
			String newVal = source.get(a);
			if(referenceType.equals(ENTITY) && oldVal != null) {
				session.run("MATCH (n)-[r:" + a + "]->() WHERE id(n) = " + targetId + " DELETE r");
			}
			rb.insertStatus(username, domain, targetId, referenceType, description, a, oldVal, newVal, "update");
			if(!firstSet)
				setClause += ",";
			else
				firstSet = false;
			setClause += reference + "." + a + "=\"" + newVal + "\"";
		}
		if (firstSet)
			return "";
		else
			return setClause;
	}
	
	public void mergeArcRelation(String relType, String targetId, String sourceId, List<String> attributes, HashMap<String,String> target, HashMap<String,String> source) throws SQLException, IOException {
		if(session != null) {
//			session.run("MATCH ()-[rt]->() WHERE id(rt)=" + targetId 
//					+ mergeAttributesClause("rt", targetId, RELATION, relType, attributes, target, source));
//			session.run("MATCH ()-[rs]->() WHERE id(rs)=" + sourceId + " DELETE rs");
//			RelationalDB.insertStatus(sourceId, GraphDB.RELATION, relType, "", null, null, RelationalDB.DELETE);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void createSimpleNode(String entityType, String attribute, String value) {
		if(session != null) {
			String query = "MERGE (n:" + entityType + " {" + attribute + ":'" + value + "'}) RETURN n"; 
			System.out.println(query);
			//Result result = session.run(query);
			//Record record = result.next();
			//String id = record.get(0).toString().replaceAll("[^0-9]", ""); // la get restituisce "node<id>", vengono tenute solo le cifre
			//RelationalDB.persistCreate(id, ENTITY, entityType, attributes, values);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void createSimpleAttribute(String uriNode, String attribute, String value) {
		if(session != null) {
			String query = "MATCH (n {uri:'" + uriNode + "'}) set a." + attribute + " = '" + value + "' RETURN n"; 
			System.out.println(query);
			//Result result = session.run(query);
			//Record record = result.next();
			//String id = record.get(0).toString().replaceAll("[^0-9]", ""); // la get restituisce "node<id>", vengono tenute solo le cifre
			//RelationalDB.persistCreate(id, ENTITY, entityType, attributes, values);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void createSimpleRelationship(String uriSubj, String rel, String uriObj) {
		if(session != null) {
			String query = "MATCH (n {uri:'" + uriSubj + "'}) MATCH (m {uri:'" + uriObj + "'}) CREATE n-[r:" + rel + "]-(m) RETURN r"; 
			System.out.println(query);
			//Result result = session.run(query);
			//Record record = result.next();
			//String id = record.get(0).toString().replaceAll("[^0-9]", ""); // la get restituisce "node<id>", vengono tenute solo le cifre
			//RelationalDB.persistCreate(id, ENTITY, entityType, attributes, values);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void createSimpleReifiedRelationship(String nameRel, String uriReification, String value) {
		String uriSubj = value.split(";")[0].split("::")[1];
		String uriObj = value.split(";")[1].split("::")[1];
		if(session != null) {
			String query = "MATCH (n {uri:'" + uriSubj + "'}) MATCH (m {uri:'" + uriObj + "'}) CREATE n-[r:" + nameRel + "]-(m) RETURN r"; 
			System.out.println(query);
			//Result result = session.run(query);
			query = "MATCH (n:{uri:'" + uriSubj + "'})-[r:" + nameRel + "]-(m:{uri:'" + uriObj + "'}), (e:{uri:'" + uriReification + "'}) set r=properties(e) return properties(r)";
			System.out.println(query);
			//Result result = session.run(query);
			query = "MATCH (n:{uri:'" + uriReification + "'}) DETACH DELETE n";
			System.out.println(query);
			//Result result = session.run(query);
			//RelationalDB.persistCreate(id, ENTITY, entityType, attributes, values);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void removeSimpleNode(String uri) {
		if(session != null) {
			String query = "MATCH (n: {uri: '" + uri + "'}) DELETE n"; 
			System.out.println(query);
			//Result result = session.run(query);
			//Record record = result.next();
			//String id = record.get(0).toString().replaceAll("[^0-9]", ""); // la get restituisce "node<id>", vengono tenute solo le cifre
			//RelationalDB.persistCreate(id, ENTITY, entityType, attributes, values);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public String createEntityAPI(String label, List<Attribute> attributes) throws SQLException {
		List<String> values = new ArrayList<String>();

		String attributesList = "";
		String valuesList = "";
		for (int i=0; i<attributes.size(); i++) {
			values.add(attributes.get(i).getValues().get(0));
			valuesList += attributes.get(i).getValues().get(0) + ",";
			attributesList += attributes.get(i).getName() + ":'" + attributes.get(i).getValues().get(0) + "',";
		}
		attributesList = attributesList.substring(0, attributesList.length()-2);
		String query = "CREATE (n:" + label + " {" + attributesList + "'}) RETURN n";
		if(session != null) {
			Result result = session.run(query);
			Record record = result.next();
			String id = record.get(0).toString().replaceAll("[^0-9]", ""); // la get restituisce "node<id>", vengono tenute solo le cifre
			handleSpecialAttributes(session, ENTITY, id, attributes, values);
			//RelationalDB.persistCreateAPI(id, ENTITY, label, attributes, values);
			return id;
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public String createRelation(String subject, String relationType, String object, String idEntity1, String idEntity2, List<Attribute> attributes, HashMap<String,String> entityForm, String description) throws SQLException {
		List<String> values = compileAttributes(entityForm, attributes);
		String listing = composeObjectToQuery(attributes, values);
		if(session != null) {
			Result result = session.run("MATCH (e1) WHERE id(e1)=" + idEntity1 + " OPTIONAL MATCH (e2) WHERE id(e2)= " + idEntity2 
					+ " CREATE (e1)-[r:" + relationType + listing + "]->(e2) RETURN r");		
			Record record = result.next();
			String id = record.get(0).toString().replaceAll("[^0-9]", ""); // la get restituisce "node<id>", vengono tenute solo le cifre
			return id;
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public Map<String, String> renameRelation(String oldRel, String newRel) {
		Map<String, String> ids = new HashMap<String, String>();
		if(session != null) {
			System.out.println("MATCH (n)-[rel:" + oldRel + "]->(m) WITH rel\r\n"
					+ "CALL apoc.refactor.setType(rel, '" + newRel + "') YIELD input, output RETURN id(rel), id(output)");
			Result result = session.run("MATCH (n)-[rel:" + oldRel + "]->(m) WITH rel\r\n"
					+ "CALL apoc.refactor.setType(rel, '" + newRel + "') YIELD input, output RETURN id(rel), id(output)");	
			while(result.hasNext()) {
				Record record = result.next();
				String oldId = record.get(0).toString();
				String newId = record.get(1).toString();
				System.out.println(oldId + "," + newId);
				ids.put(oldId, newId);
			}
		} else 
			throw new ClientException("Graph database disconnected!");

		return ids;
	}

	public void addDomainInstance(String instanceId, String domain) throws SQLException {
		addDomainLabel(instanceId, domain);
	}

	public void removeDomainInstance(String instanceId, String domain) throws SQLException {
		removeDomainLabel(instanceId, domain);
	}

	public void deleteEntityInstance(String label, String instanceId) throws SQLException { // usata in NodeBean.deleteEntity()
		removeEntity(label, instanceId);
	}

	public void editEntity(DomainData domainData, String entityType, List<Attribute> attributes, HashMap<String,String> entityForm, String id) throws Exception {
		List<String> values = compileAttributes(entityForm, attributes);
		for(Attribute attr : attributes) {
			if (!domainData.getEntity(entityType).getAllAttributesToString().contains(attr.getName())) {
				throw new Exception("Not existing attribute");
			}
		}
		if(session != null) {
			//    			HashMap<String,String> oldValsMap = getOldAttributeValues(session.run( "MATCH (e:" 
			//    					+ entityType + ") where id(e)=" + id + " return properties(e)"));
			HashMap<String,String> oldValsMap = getOldAttributeValues(session.run( "MATCH (e" 
					+ ") where id(e)=" + id + " return properties(e)"));
			String clause = compareOldNewAttributes("e", id, ENTITY, entityType, attributes, oldValsMap, values);
			//ste considerare anche il type, che prima era incluso in subClass
			session.run( "MATCH (e) where id(e)=" + id + clause);
			handleSpecialAttributes(session, ENTITY, id, attributes, values);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void editRelation(String instanceRelId, String subject, String relationType, String object, List<Attribute> fields, HashMap<String,String> valuesForm, String description) throws SQLException {
		List<String> values = compileAttributes(valuesForm, fields);
		if(session != null) {
			HashMap<String,String> oldValsMap = getOldAttributeValues(session.run("MATCH (e1)-[r:" 
					+ relationType + "]->(e2) WHERE id(r)=" + instanceRelId + " RETURN properties(r)"));
			String clause = compareOldNewAttributes("r", instanceRelId, RELATION, description, fields, oldValsMap, values);
			session.run("MATCH (e1)-[r:"+ relationType +"]->(e2) WHERE id(r)=" + instanceRelId 
					+ clause + " RETURN r");
		} else
			throw new ClientException("Graph database disconnected!");
	}

	public void createNode(String uri, ArrayList<String> labels) {
		if(session != null) {
			String query = "CREATE (n" + format(labels) + " {uri:'" + uri + "'});";
			session.run(query);
		} else
			throw new ClientException("Graph database disconnected!");
	}

	public void addPropertyNode(Long id, String property, String value) {
		if(session != null) {
			String query = "MATCH (n) WHERE id(n) = " + id +" set n." + property + " = '" + value + "';";
			//System.out.println(query);
			session.run(query);
		} else
			throw new ClientException("Graph database disconnected!");
	}

	public void addRelationship(String uri1, String relationship, String uri2, String inverse) {
		if(session != null) {
			String query = "MATCH "
					+ "(n1 {uri:'" + uri1 + "'}),\r\n"
					+ "(n2 {uri:'" + uri2 + "'})\r\n"
					+ "create (n1)-[r:" + relationship + "]->(n2) \r\n"
					+ "return r";
			session.run(query);
			query = "MATCH "
					+ "(n1 {uri:'" + uri2 + "'}),\r\n"
					+ "(n2 {uri:'" + uri1 + "'})\r\n"
					+ "create (n1)-[r:" + inverse + "]->(n2) \r\n"
					+ "return r";
			session.run(query);
		} else
			throw new ClientException("Graph database disconnected!");
	}

	private String format(ArrayList<String> labels) {
		String res = "";
		for (String label : labels) {
			res += ":" + label;
		}
		return res;
	}

	private String compareOldNewAttributes(String reference, String instanceId, String referenceType, String description, List<Attribute> attributes, HashMap<String,String> oldValsMap, List<String> values) throws SQLException {
		String setClause = "";
		Boolean firstSet = true;
		String removeClause = "";
		Boolean firstRemove = true;
		for(int i = 0; i < attributes.size(); i++) {
			String currentAttr = attributes.get(i).getName();
			String oldVal =(String) oldValsMap.get(currentAttr);
			String newVal = values.get(i).trim();
			if(oldVal == null && !values.get(i).isEmpty() || oldVal != null && !oldVal.trim().equals(values.get(i).trim())) {
				if (referenceType.equals(ENTITY)) { // altrimenti userebbe l'id di un arco per un nodo!
					session.run("MATCH (n)-[r:" + currentAttr + "]->() WHERE id(n) = " + instanceId + " DELETE r");
				}
				if(values.get(i).isEmpty()) {
					if(!firstRemove)
						removeClause += ",";
					else
						firstRemove = false;
					removeClause += reference + "." + currentAttr;
				} else {
					if(!firstSet)
						setClause += ",";
					else
						firstSet = false;
					setClause += reference + "." + currentAttr + "=\"" + values.get(i) + "\"";
				}
			}
		}
		String clause = "";
		if(!setClause.isEmpty())
			clause += " SET " + setClause;
		if(!removeClause.isEmpty())
			clause += " REMOVE " + removeClause;
		return clause;
	}

	private HashMap<String,String> getOldAttributeValues(Result result) {
		Map<String,Object> myAttributeValuesObject = new HashMap<String,Object>();
		HashMap<String,String> valMap = new HashMap<String, String>();
		if(result.hasNext()) { //ste era while, ma non dovrebbe essere solo uno???
			//valMap = new HashMap<String, String>();
			Record rec = result.next();
			myAttributeValuesObject = rec.get(0).asMap();
			Set<String> chiavi = myAttributeValuesObject.keySet();
			Iterator<String> chiaveIt = chiavi.iterator();
			while(chiaveIt.hasNext()) {
				String chiave = chiaveIt.next();
				valMap.put(chiave, myAttributeValuesObject.get(chiave).toString());
			}
		}
		return valMap;
	}

	public Map<String,String> getEntityAttributes(String id) {
		if(session != null) {
			Result result = session.run("MATCH (e) where id(e)=" + id + " RETURN labels(e),properties(e)");
			if (result.hasNext()) {
				Record record = result.next();
				//    				String types = record.get(0).toString().replaceAll("\"|]|\\[", "");
				return toAttributeValuesMap(record.get(1).asMap());
			}
			else
				return new HashMap<String,String>();
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public Instance getEntity(String id, DomainData domainSchema) {
		Instance instance = null;
		if(session != null) {
			Result result = session.run( "MATCH (e) where id(e)=" + id + " RETURN id(e),labels(e),properties(e)");
			if (result.hasNext()) {
				//newBuildInstance(result, fields);
				Record record = result.next();
				instance = buildInstanceEnt(record.get(0),record.get(1),record.get(2), domainSchema.getTopEntitiesToString(), domainSchema); 
			}
			return instance;
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public Map<String,String> getRelationAttributes(String id) {
		if(session != null) {
			Result result = session.run("MATCH ()-[r]->() where id(r)=" + id + " RETURN TYPE(r),properties(r)");
			if (result.hasNext()) {
				Record record = result.next();
				//    				String types = record.get(0).toString().replaceAll("\"|]|\\[", "");
				return toAttributeValuesMap(record.get(1).asMap());
			}
			else
				return new HashMap<String,String>();
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void removeEntity(String entityType, String id) {		
		if(session != null) {
			//    			session.run( "MATCH (e:" + entityType + ") where id(e)=" + id + " DELETE (e)"); // DETACH
			session.run( "MATCH (e) where id(e)=" + id + " DELETE (e)"); // DETACH
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void removeRelation(String instanceRelId) {		
		if(session != null) {
			session.run( "MATCH ()-[e]->() where id(e)=" + instanceRelId + " DELETE (e)");
			//graphdb.Connect.close();
		} else 
			throw new ClientException("Error: neo4j database not connected.");
	}

	//    	public List<RelationTriple> searchRelation(String subjType, String subjInst, String relType, String objType, String objInst, DomainData domainSchema, List<Attribute> fields, HashMap<String,String> valuesForm) {
	public List<RelationTriple> searchRelation(List<String> subjType, String subjInst, String relType, List<String> objType, String objInst, DomainData domainSchema, List<Attribute> fields, HashMap<String,String> valuesForm) {
		List<String> values = compileAttributes(valuesForm, fields);
		List<RelationTriple> triples = new LinkedList<RelationTriple>();
		if(session != null) {
			String query = searchRelationBaseQuery(subjType, subjInst, relType, objType, objInst, fields, values) 
					+ "return id(s),labels(s),properties(s),id(r),type(r),properties(r),id(o),labels(o),properties(o)";
			System.out.println("---\n" + subjType + "\n-\n" + objType + "\n-\n" + query + "\n---\n");
			Result result = session.run(query);
			//ste da qui preso da populateInstancesMenu -> mettere in un metodo le parti comuni
			List<Record> records = result.list();
			Iterator<Record> iterator = records.iterator();
			//List<String> entities = domainSchema.getEntitiesToString();
			List<String> entities = domainSchema.getAllEntitiesToString();
			while(iterator.hasNext()) {
				Record rec = iterator.next(); //System.out.println("---" + rec.toString() + "\n");
				Instance subjInstance = buildInstanceEnt(rec.get(0), rec.get(1), rec.get(2), entities, domainSchema);
				if(subjInstance != null) {
					Instance relInstance = buildInstanceRel(rec.get(3), rec.get(4), rec.get(5), domainSchema);
					if(relInstance != null) {
						Instance objInstance = buildInstanceEnt(rec.get(6), rec.get(7), rec.get(8), entities, domainSchema);
						if(objInstance != null) {
							triples.add(new RelationTriple(subjInstance,relInstance,objInstance));
						}
					}
				}
			}
		} else 
			throw new ClientException("Graph database disconnected!");
		return triples;
	}

	private String searchRelationBaseQuery(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst, List<Attribute> fields, List<String> values) {
		String query = "MATCH (s";
		if(subjTypes.size() == 1)
			query += ":" + subjTypes.get(0);
		query += ")-[r";
		if(relType != null && !relType.isEmpty())
			query += ":" + relType;
		query += "]->(o";		
		if(objTypes.size() == 1)
			query += ":" + objTypes.get(0);
		query += ") ";
		Boolean first = true;
		if (subjTypes.size() > 1 || subjInst != null) {
			query += "WHERE ";
			first = false;
			if(subjInst != null)
				query += "id(s)=" + subjInst;
			else // subjTypes.size() > 1
				query += subClassRestriction("s",subjTypes);
		}
		//ste qui andra' la selezione sottorelazioni
		if (objTypes.size() > 1 || objInst != null) {
			query += buildAndCondition(first);
			first = false;
			if(objInst != null)
				query += "id(o)=" + objInst;
			else // objTypes.size() > 1
				query += subClassRestriction("o",objTypes);
		}
		if(fields != null) { // <=> values != null
			for(int i = 0; i < fields.size(); i++) { // selezione sui campi
				if(!values.get(i).isEmpty()) {
					query += buildAndCondition(first);
					first = false;
					query += "r." + fields.get(i).getName() + " CONTAINS '" + values.get(i) + "'";
				}
			}
		}
		return query + " ";
	}

	private String buildAndCondition(boolean first) {
		if(!first)
			return " AND ";
		else
			return " WHERE ";
	}

	//MATCH (n) RETURN distinct labels(n), count(*)
	/* restituisce la lista di label di un nodo */
	public List<String> getInstanceLabels(String id) {
		List<String> labelsList = new LinkedList<String>();
		if(session != null) {
			Result result = session.run("MATCH (e) where id(e)=" + id + " RETURN distinct labels(e)");
			List<Record> records = result.list();
			Iterator<Record> iterator = records.iterator();
			if (iterator.hasNext()) {
				String[] labels = iterator.next().get(0).toString().replaceAll("\"|]|\\[", "").split(",");
				for (int i = 0; i < labels.length; i++)
					labelsList.add(labels[i].trim());
			}
			return labelsList;
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public Set<String> filterSubjects(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst) {
		return filterTypes(subjTypes, subjInst, relType, objTypes, objInst, ENTITY, "s");
	}

	public TreeSet<String> filterRelations(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst) {
		return filterTypes(subjTypes, subjInst, relType, objTypes, objInst, RELATION, "r");
	}

	public TreeSet<String> filterObjects(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst) {
		return filterTypes(subjTypes, subjInst, relType, objTypes, objInst, ENTITY, "o");
	}

	private TreeSet<String> filterTypes(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst, String what, String var) {
		if(session != null) {
			String query = searchRelationBaseQuery(subjTypes, subjInst, relType, objTypes, objInst, null, null);
			if (what.equals(ENTITY))
				query += " RETURN DISTINCT LABELS(" + var + ")";
			else // what.equals(RELATION)
				query += " RETURN DISTINCT TYPE(" + var + ")";
			Result result = session.run(query);
			if(what.equals(ENTITY))
				return populateLabelsMenu(result);
			else // what.equals(RELATION)
				return populateTypesMenu(result);
		} else 
			throw new ClientException("Graph database disconnected!");
	}
	//ste populateTypesMenu chiamata solo da filterTypes (ma ha cose in comune con getInstanceLabels)
	private TreeSet<String> populateTypesMenu(Result result) { //ste verificare se non e' sempre un solo elemento da splittare
		List<Record> records = result.list();
		TreeSet<String> types = new TreeSet<String>();
		for(Record rec : records)
			types.add(rec.get(0).asString());
		return types;
	}

	private TreeSet<String> populateLabelsMenu(Result result) { //ste verificare se non e' sempre un solo elemento da splittare
		List<Record> records = result.list();
		TreeSet<String> labels = new TreeSet<String>();
		for(Record rec : records)
			labels.add(extractTypeLabel(rec.get(0).asList()));
		return labels;
	}

	public TreeSet<String> filterClasses(List<String> subjTypes, String relType, List<String> objTypes, String returnClause) {
		if(session != null) {
			String query = searchRelationBaseQuery(subjTypes, null, relType, objTypes, null, null, null) + returnClause;
			System.out.println(query);
			Result result = session.run(query);
			return populateClassesMenu(result);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public TreeSet<String> filterRelationships(List<String> subjTypes, String relType, List<String> objTypes, String returnClause) {
		if(session != null) {
			String query = searchRelationBaseQuery(subjTypes, null, relType, objTypes, null, null, null) + returnClause;
			System.out.println(query);
			Result result = session.run(query);
			return populateRelationshipsMenu(result);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public LinkedList<Instance> filterInstances(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst, List<Attribute> fields, String returnClause) {
		if(session != null) {
			String query = searchRelationBaseQuery(subjTypes, subjInst, relType, objTypes, objInst, null, null) + returnClause;
			System.out.println(query);
			Result result = session.run(query);
			return populateInstancesMenu(result, fields);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public LinkedList<Instance> filterSubjectInstances(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst, List<Attribute> fields) {
		return filterInstances(subjTypes, subjInst, relType, objTypes, objInst, fields, " RETURN distinct id(s), properties(s), labels(s)" + buildOrderClause("properties(s)", fields));
	}

	//ste mai chiamata... sarebbe search?
	private LinkedList<Instance> filterRelationInstances(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst, List<Attribute> fields) {
		return filterInstances(subjTypes, subjInst, relType, objTypes, objInst, fields, " RETURN distinct id(r), properties(r), type(r)");
	} //ste chi usera' il type si aspetta una lista, quando si fa .asList funzionera'?

	public LinkedList<Instance> filterObjectInstances(List<String> subjTypes, String subjInst, String relType, List<String> objTypes, String objInst, List<Attribute> fields) {
		return filterInstances(subjTypes, subjInst, relType, objTypes, objInst, fields, " RETURN distinct id(o), properties(o), labels(o)" + buildOrderClause("properties(o)", fields));
	}

	public TreeSet<String> filterSubjectClasses(List<String> subjTypes, String relType, List<String> objTypes) {
		return filterClasses(subjTypes, relType, objTypes, " RETURN distinct labels(s)");
	}

	public TreeSet<String> filterObjectClasses(List<String> subjTypes, String relType, List<String> objTypes) {
		return filterClasses(subjTypes, relType, objTypes, " RETURN distinct labels(o)");
	}

	public TreeSet<String> filterRelationships(List<String> subjTypes, String relType, List<String> objTypes) {
		return filterRelationships(subjTypes, relType, objTypes, " RETURN distinct type(r)");
	}

	private String buildOrderClause(String fieldVar, List<Attribute> fields) {
		String orderClause = "";
		Boolean first = true;
		for(int i = 0; i < fields.size(); i++) {
			if(fields.get(i).isMandatory()) //ste se si ordina anche per distinguishing non mandatory sballa l'ordinamento
				if(first) {
					orderClause += " ORDER BY " + fieldVar + "." + fields.get(i).getName();
					first = false;
				} else
					orderClause += ", " + fieldVar + "." + fields.get(i).getName();
		}
		return orderClause;
	}

	//ste in Instance ? O in Management ?
	private Instance buildInstanceEnt(Value idValue, Value idType, Value propValue, List<String> labels, DomainData domainSchema) {
		String types = idType.toString().replaceAll("\"|]|\\[", "");
		String type = extractTypeLabel(types,labels);
		//ste verificare la possibilita' di usare type = extractTypeLabel(idType.asList())
		if(type != null) {
			List<Attribute> fields = domainSchema.properties(type);
			return buildInstance(idValue, type, propValue, fields);
		} else
			return null;
	}
	private Instance buildInstanceRel(Value idValue, Value idType, Value propValue, DomainData domainSchema) { //ste chiamata solo da searchRelation (inglobare?)
		String type = idType.toString().replaceAll("\"|]|\\[", "");
		List<Attribute> fields = domainSchema.propertiesRelation(type);
		return buildInstance(idValue, type, propValue, fields);
	}
	private Instance buildInstance(Value idValue, String type, Value propValue, List<Attribute> fields) {
		Map<String,String> myAttributeValues = toAttributeValuesMap(propValue.asMap());
		String id = "" + idValue.asInt();
		return new Instance(type, id, myAttributeValues, fields);
	}

	private Map<String,String> toAttributeValuesMap(Map<String,Object> attributeValuesObject) {
		Map<String,String> myAttributeValues = new HashMap<String,String>();
		Set<String> chiavi = attributeValuesObject.keySet();
		Iterator<String> chiaveIt = chiavi.iterator();
		while(chiaveIt.hasNext()) {
			String chiave = chiaveIt.next();
			myAttributeValues.put(chiave, attributeValuesObject.get(chiave).toString());
		}
		return myAttributeValues;
	}

	private String buildShortDescription(String id, String type, List<Attribute> fields, Map<String,String> myAttributeValues) { //ste mettere in Instance? crearla quando si fa la new?
		String shortDescription = "";
		for(Attribute a : fields)
			if(a.isDescriptive() && myAttributeValues.get(a.getName()) != null)
				shortDescription += myAttributeValues.get(a.getName()) + "  <" + id + ":" + type + ">";
		return shortDescription;
	}

	public String extractTypeLabel(String labelsString, List<String> domainEntities) {
		String label = null;
		//    		if(domainEntities == null) // relazione
		//    			label = labelsString;
		//    		else {
		String[] labels = labelsString.replace(" ", "").split(",");
		for(int i = 0; i < labels.length; i++)
			if(domainEntities.contains(labels[i]))
				label = labels[i];
		//    		}
		return label;
	}

	public void setDomainLabel(String entityType, String id, String domain) {		
		if(session != null) {
			session.run( "MATCH (e:" + entityType + ") where id(e)=" + id + " SET e:" + domain);
		} else 
			throw new ClientException("Graph database disconnected!");
	}
	public void addDomainLabel(String id, String domain) {		
		//new Neo4jConnection();
		if(session != null) {
			session.run( "MATCH (e) where id(e)=" + id + " SET e:" + domain);
			//Neo4jConnection.close();
		} else 
			throw new ClientException("Graph database disconnected!");
	}
	
	public void removeDomainLabel(String id, String domain) {		
		if(session != null) {
			session.run( "MATCH (e) where id(e)=" + id + " REMOVE e:" + domain);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public Result getAllNodesWithLabel(String label) {	
		Result res;
		if(session != null) {
			res = session.run( "MATCH (n:" + label + ") RETURN n");
		} else 
			throw new ClientException("Graph database disconnected!");
		return res;
	}

	//ste restituire l'informazione se tutto e' andato bene, in modo da consentire il rollback altrimenti
	private void handleSpecialAttributes(Session session, String referenceType, String instanceId, List<Attribute> attributes, List<String> values) {
		for(int i = 0; i < attributes.size(); i++) {
			String value = values.get(i);
			if(!value.equals("")) {
				Attribute a = attributes.get(i);
				String name = a.getName();
				String dataType = a.getDataType();
				if(referenceType.equals(ENTITY)) { //ste e se e' relation??
					if(dataType.equals("date")) {
						int date[] = extractDate(value);
						if(date != null) { //ste pero' se non era valida era gia' stato rimosso l'arco precedente!
							insertDate(session, "",instanceId,name,date[0],date[1],date[2]);
						}
					} else if(dataType.equals("entity")) {
						session.run("MATCH (n) WHERE id(n) = " + instanceId + " MATCH (m) WHERE id(m) = " + value
								+ " MERGE (n)-[:" + name + "]->(m)");
					}
				}
			}
		}
	}

	public int[] extractDate(String date) { // creare classe Date?
		int dateValues[] = {0, 0, 0}; // {anno, mese, giorno}
		String[] dateElements = date.split("/");
		if(dateElements.length > 3) {
			dateElements = date.split("-");
			if(dateElements.length > 3) {
				dateElements = date.split(".");
			}
		}
		int dateLength = dateElements.length;
		if(dateLength <= 3)
			for(int i=0; i < dateLength; i++)
				dateValues[i] = Integer.parseInt(dateElements[i]);
		if(!(dateLength == 1 || (dateLength == 2 && dateValues[1] >=1 && dateValues[1] <= 12) ||
				((dateValues[1] == 1 || dateValues[1] == 3 || dateValues[1] == 5 || dateValues[1] == 7 || dateValues[1] == 8 || dateValues[1] == 10 || dateValues[1] == 12)
						&& dateValues[2] >= 1 && dateValues[2] <= 31) ||
				((dateValues[1] == 4 || dateValues[1] == 6 || dateValues[1] == 9 || dateValues[1] == 11)
						&& dateValues[2] >= 1 && dateValues[2] <= 30) ||
				(dateValues[1] == 2 && (dateValues[2] <= 28 ||
				(dateValues[2] == 29 && (dateValues[0] % 4 == 0 && (dateValues[0] % 100 != 0 || dateValues[0] % 400 == 0))))))) {
			System.out.println("Data non valida");
			dateValues = null;
		}
		return dateValues;
	}

	//ste chiamato solo da handleSpecialAttributes, integrare? Pero' gestisce la timeline
	public void insertDate(Session session, String timeline, String node, String relationship, int year, int month, int day) {
		if(timeline.isEmpty() || timeline == null)
			timeline = "General";
		if(session != null) {
			String target;
			String query = "MATCH (node) where id(node) = " + node + " MERGE (timeline:Timeline {name:'" + timeline 
					+ "'}) MERGE (year:Year {value:'" + year + "'})";
			if(month != 0) {
				target = "month";
				query += " MERGE (month:Month {value:'" + year + "/" + month + "'})";
				if(day != 0) {
					target = "day";
					query += " MERGE (day:Day {value:'" + year + "/" + month + "/" + day + "'})"
							+ " MERGE (node)-[:" + relationship + "]->(day) MERGE (day)-[:belongsTo]->(month)";
				}
				query += " MERGE (month)-[:belongsTo]->(year)";
			} else
				target = "year";
			query += " MERGE (year)-[:belongsTo]->(timeline)"
					+ " MERGE (node)-[:" + relationship + "]->(" + target + ")";
			session.run(query);
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public Integer outDegree(String id) {
		int count = 0;
		if(session != null) {
			Result result = session.run("MATCH (e)-[r]->() where id(e)=" + id + " RETURN count(r) as count");
			if (result.hasNext())
				count =  result.next().get(0).asInt();
		} else 
			throw new ClientException("Graph database disconnected!");
		return count;
	}

	public Integer inDegree(String id) {
		int count = 0;
		if(session != null) {
			Result result = session.run("MATCH ()-[r]->(e) where id(e)=" + id + " RETURN count(r) as count");
			if (result.hasNext())
				count =  result.next().get(0).asInt();
		} else 
			throw new ClientException("Graph database disconnected!");
		return count;
	}

	public void mergeNode(String username, String domain, String entityType, String targetId, String sourceId, List<String> attributes, HashMap<String,String> target, HashMap<String,String> source) throws SQLException, IOException {
		if(session != null) {
			session.run("MATCH (e) where id(e)=" + targetId 
					+ mergeAttributesClause(username, domain, "e", targetId, ENTITY, entityType, attributes, target, source));
			session.run("MATCH (n) WHERE id(n)=" + targetId + " MATCH (m) WHERE id(m)=" + sourceId + " WITH head(collect([n,m])) as nodes"
					+ " CALL apoc.refactor.mergeNodes(nodes,{properties:\"discard\", mergeRels:false}) YIELD node RETURN node");
			// con apoc LASCIA GLI STESSI id RELAZIONI - verificare archi entranti e uso di "*"
			//    	oppure
			//    			CALL apoc.refactor.mergeNodes(nodes, {properties: {
			//    			    name:'discard',
			//    			    age:'overwrite',
			//    			    kids:'combine',
			//    			    `addr.*`: 'overwrite',
			//    			    `.*`: 'discard'
			//    			}})
			//    			discard : the property from the first node will remain if already set, otherwise the first property in list will be written
			//    			overwrite / override : last property in list wins
			//    			combine : if there is only one property in list, it will be set / kept as single property otherwise create an array, tries to coerce values
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void mergeArc(String username, String domain, String relType, String targetId, String sourceId, List<String> attributes, HashMap<String,String> target, HashMap<String,String> source) throws SQLException, IOException {
		// DA CONTROLLARE
		if(session != null) {
			session.run("MATCH ()-[rt]->() WHERE id(rt)=" + targetId 
					+ mergeAttributesClause(username, domain, "rt", targetId, RELATION, relType, attributes, target, source));
			session.run("MATCH ()-[rs]->() WHERE id(rs)=" + sourceId + " DELETE rs");
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	// DI PIERRO - Estrazione

	public void importOntology(final String uri) {
		String path = uri.replace("\\", "\\\\");
		String query = "call semantics.importRDF(\"file:/" + path + "\", \"RDF/XML\", { handleVocabUris: \"IGNORE\" })";
		session.run(query);
	}

	//String query = "call apoc.export.json.query(\"START nod=NODE(10483) MATCH (nod)-[r0]-(n0) return nod, r0, n0\", \"C:\\Users\\ddipi\\Documents\\neo4j-community-4.2.3\\bin\\subgraph.json\")";

	public void extractByDistance(final String id, final int k, final String fileName) {
		String node = "START n0=NODE(" + id + ") MATCH (n0)";

		String subgraph = node;
		for(int i=1; i<=k; i++) {
			subgraph += "-[r"+i+"]-(n"+i+")";
		}
		subgraph += " return n0";
		String query = "call apoc.export.json.query(\"" + subgraph;
		for(int i=1; i<=k; i++) {
			query += ", r"+i+", n"+i;
		}
		query += "\", \"" + fileName + "\");";
		session.run(query);
	}


	public void extractByNodeRel(final String individual, final String rel, final String fileName) {
		String id = individual.substring(individual.lastIndexOf("#")+1);
		String node = "START n0=NODE(" + id + ") MATCH (n0)";

		String subgraph = node;
		subgraph += "-[r1:"+ rel +"]-(n1)";           	
		subgraph += " return n0, r1, n1";
		String query = "call apoc.export.json.query(\"" + subgraph;
		query += "\", \"" + fileName + "\");";
		session.run(query);
	}


	//

	public void renameEnt(String oldLabel, String newLabel) {
		if(session != null) {
			session.run(String.format("MATCH (n:%s) REMOVE n:%s SET n:%s", oldLabel, oldLabel, newLabel));
		} else 
			throw new ClientException("Graph database disconnected!");
	}

	public void renameRel(String oldType, String newType) {
		//String.format("MATCH (s" + (!domain.isEmpty() ? ":" : "") + "%s)-[old:%s]->(d) CREATE (s)-[new:%s]->(d) SET new=old WITH old, id(old) as oldId DELETE old RETURN oldId", domain, oldValue, newValue)
		// CALL apoc.refactor.rename.type(oldType, newType, [rels])
		////    		MATCH (:Engineer {name: "Jim"})-[Rel]->(:Engineer {name: "Alistair"})
		//    		MATCH ()-[Rel]->()
		//    		WITH collect(Rel) AS arcs
		//    		CALL apoc.refactor.rename.type(oldType, newType, arcs)
		//    		YIELD committedOperations
		//    		RETURN committedOperations
	}

	//    	public void renameAttrEnt(String domain, String Ent, String oldName, String newName) {
	//    		Neo4jConnectionUnified conn = new Neo4jConnectionUnified();
	//    		Session session = conn.getSession();
	//    		if(session != null) {
	//    			session.run(String.format("MATCH (s:%s:%s) SET s.%s=s.%s REMOVE s.%s", domain, Ent, newName, oldName, oldName));
	//    			//ste ma se un nodo appartiene anche ad altri domini viene cambiato il nome anche per gli altri domini!
	//    			try {
	//    				conn.close();
	//    			} catch (Exception e) {
	//    				e.printStackTrace();
	//    			}
	//    			RelationalDB.renameAttrEnt(domain, Ent, oldName, newName);
	//    		} else 
	//    			throw new ClientException("Graph database disconnected!");
	//    	}

	public void renameAttrRel(String Ent, String oldName, String newName) {
		//String.format("MATCH (s:%s)-[rel:%s]->(d:%s) SET rel.%s=rel.%s REMOVE rel.%s", domain, relation, domain, newValue, oldValue, oldValue)
	}

	//    	public void qi() {
	//    		new Neo4jConnection();
	//    		Session session = Neo4jConnection.session();	
	//    		if(session != null) {
	////    			session.run("MATCH (n) WHERE id(n) = 336222 SET n.subClass = 'Decoder'");
	//    			session.run("CREATE (n:Document:lam { title: 'Gramatica della lingua latina', language: 'ita', format: '16x9', length: '398 pp', date: '1782', subClass: 'Book' })");
	//    			Neo4jConnection.close();
	//    		} else
	//    			throw new ClientException("Graph database disconnected!");
	//    	}

	//ste per autoComplete nei textbox degli attributi
	//    	Entity subclass, boolean subclassRestriction
	//    	subclass.getAllSubclassNames(subclassRestriction)
	public List<String> getAttributeValues(List<String> labels, String property) {
		List<String> values = new LinkedList<String>();
		if(session != null) {
			Result res = session.run("MATCH(n) WHERE " + subClassRestriction("n", labels) + " RETURN n." + property);
			for(Record r : res.list())
				values.add(r.get(0).asString());
		} else 
			throw new ClientException("Graph database disconnected!");
		Collections.sort(values);
		return values;
	}


	//ste da eliminare

	public Result getAllNodesWithSubclass() {	
		Result res;
		if(session != null) {
			res = session.run( "MATCH (n) where exists(n.subClass) RETURN n");
		} else 
			throw new ClientException("Graph database disconnected!");
		return res;
	}

	public Result getNodesWithoutSubClass() {
		Result res;
		if(session != null) {
			res = session.run( "match (n) where not exists(n.subClass) return n");
		} else 
			throw new ClientException("Graph database disconnected!");
		return res; 
	}

	public String getLabel(int id) {
		String label = null;
		if(session != null) {
			Result res = session.run(String.format("MATCH (n) where ID(n) = " + id + " return n.subClass"));
			List<Record> rec = res.list();
			label = rec.get(0).get(0).toString();
		} else 
			throw new ClientException("Graph database disconnected!");
		return label;
	}

	public Result getNodesWithKind() {
		Result res;
		if(session != null) {
			res = session.run("match (n) where exists(n.kind) return n");
		} else 
			throw new ClientException("Graph database disconnected!");
		return res; 
	}

	public List<Record> executeQuery(String query) {
		Result res;
		List<Record> recs;
		if(session != null) {
			res = session.run(query);
			recs = res.list();
			//System.out.println(recs.size());
		} else 
			throw new ClientException("Graph database disconnected!");
		return recs; 
	}

	public void createSimpleRelationship(String uriIndivAss, String relName, String className, String attrName,
			String uriValueAss) {
		if(session != null) {
			String query = "MATCH (n {uri:'" + uriIndivAss + "') "
					+ "MATCH (m:" + className + " {" + attrName + ":'" + uriValueAss + "'})" 
					+ " CREATE (n)-[r:" + relName + "]->(m) RETURN r";
			System.out.println(query);
			//    			Result result = session.run(query);		
			//    			Record record = result.next();
			//    			String id = record.get(0).toString().replaceAll("[^0-9]", ""); // la get restituisce "node<id>", vengono tenute solo le cifre
		} else 
			throw new ClientException("Graph database disconnected!");

	}

}