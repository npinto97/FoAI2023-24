package export;

import java.util.Collections;
import java.util.Vector;

//import javax.faces.context.FacesContext;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.ClientException;

import connection.GraphDB;

/**
 * The Neo4jToProlog class provides methods to convert data from Neo4j database to Prolog format.
 * This class cannot be instantiated.
 * All methods of this class require {@link GraphDB} to be instantiated and the connection to not be closed before calling.
 */
public class Neo4jToProlog {
	//public enum outputStyle {STRUCTURE, CONTENT}; 
	//public enum outputFormat {PROLOG, GIE}; 

	/**
	 * Retrieves the list of properties for a given entity type.
	 * 
	 * @param entityType the type of the entity
	 * @return a vector containing the list of properties
	 * @throws ClientException if the Neo4j database is not connected
	 */
	private static Vector<String> listEntityProp(String entityType) {
		Session session = GraphDB.getSession();
		if(session == null) {
			throw new ClientException("Error: neo4j database is not connected.");
		}
		Result result = session.run( "MATCH (n:" + entityType + ") RETURN properties(n)" );
		Vector<String> labels = new Vector<String>();
		
		while(result.hasNext()) {
			Record rec = result.next();
			String[] prop = rec.get(0).toString().replaceAll("\"|}|\\{", "").replaceAll(" ", "").toLowerCase().split(",");
			
			for(int i = 0; i < prop.length; i++) {
				String[] temp = prop[i].split(":");
				if(!labels.contains(temp[0]))
					labels.addElement(temp[0]);	
			}
		}		
		Collections.sort(labels);
		
		return labels;
	}
	
	/**
	 * Retrieves the list of properties for a given relation type.
	 *
	 * @param relationType the type of the relation
	 * @return a vector containing the list of properties
	 * @throws ClientException if the Neo4j database is not connected
	 */
	private static Vector<String> listRelationProp(String relationType) {
		Session session = GraphDB.getSession();
		
		Result result = session.run( "MATCH ()-[r:" + relationType + "]->() RETURN properties(r)" );
		Vector<String> labels = new Vector<String>();
		
		while(result.hasNext()) {
			Record rec = result.next();
			String[] prop = rec.get(0).toString().replaceAll("\"|}|\\{", "").replaceAll(" ", "").toLowerCase().split(",");
			
			for(int i = 0; i < prop.length; i++) {
				String[] temp = prop[i].split(":");
				if(!labels.contains(temp[0]))
					labels.addElement(temp[0]);	
			}
		}
		Collections.sort(labels);
		return labels;
	}

	/**
	 * Retrieves the common attributes to all elements with a given name and type.
	 *
	 * @param name the element name
	 * @param type the type of the element (E for entity, R for relation)
	 * @return a vector containing the schema of common attributes
	 * @throws ClientException if the Neo4j database is not connected
	 */
	private static Vector<String> attributes(String name, String type) { // crea lo schema degli attributi comuni a tutti i nodi
		//new Neo4jConnection();

		Session session = GraphDB.getSession();
		if(session == null) {
			throw new ClientException("Error: neo4j database is not connected.");
		}
		Vector<String> labels;
		if(type == "E") 
			labels = listEntityProp(name);
		else // type == "R"
			labels = listRelationProp(name);
		int[] counter = new int[labels.size()];
		int max = 0;
		for(int i = 0; i < counter.length; i++)
			counter[i] = 0;
		
		Result result;
		if(type == "E") 
			result = session.run( "MATCH (n:" + name + ") RETURN properties(n)" );
		else
			result = session.run( "MATCH ()-[r:" + name + "]->() RETURN properties(r)" );
			
		while(result.hasNext()) {
			max++;
			Record rec = result.next();
			String[] prop = rec.get(0).toString().replaceAll("\"|}|\\{", "").replaceAll(" ", "").toLowerCase().split(",");
			
			for(int i = 0; i < prop.length; i++) {
				String[] temp = prop[i].split(":");
				counter[labels.indexOf(temp[0])]++;				
			}
		}
		
		Vector<String> schema = new Vector<String> ();
		for(int i = 0; i < counter.length; i++) {
			if(counter[i] == max)
				schema.addElement(labels.elementAt(i).toLowerCase());
		}		
		Collections.sort(schema); // schema in ordine alfabetico
		//graphdb.Connect.close();
		return schema;
	}
	
	/**
	 * Creates facts in Prolog format based on the data in the Neo4j database.
	 *
	 * @return a vector containing the created facts
	 * @throws ClientException if the Neo4j database is not connected
	 */
	public static Vector<String> createFacts() {
			Vector<String> facts = new Vector<String>();
//			int id_fact = 0; // GIE FORMALISM
			Session session = GraphDB.getSession();
			if(session == null) {
				throw new ClientException("Error: neo4j database is not connected.");
			}
				Record r;
				Result result = session.run("MATCH (n) RETURN id(n),labels(n),properties(n)");
				while(result.hasNext()) {
					r = result.next();
					int id = r.get(0).asInt();
					for (Object o : r.get(1).asList()) {
						facts.add("node(" + id + ", '" + o.toString() + "').");
					}
					facts.add("node_properties(" + id + ", '" + r.get(2).asMap().toString().replace("'", "''") + "').");
				}
				result = session.run("MATCH (n)-[r]->(m) RETURN id(r),type(r),id(n),id(m),properties(r)");
				while(result.hasNext()) {
					r = result.next();
					facts.add("arc(" + r.get(0).asInt() + ", '" + r.get(1).asString() + "', " + r.get(2).asInt() + ", " + r.get(3).asInt() + ").");
					String properties =  r.get(4).asMap().toString().replace("'", "''") ;
					if (!properties.equals("{}")) {
						facts.add("arc_properties(" + r.get(0).asInt() + ", '" + properties + "').");					
					}
				}			
			
//			// VECCHIO CODICE di if(session != null) {
//		Result result = session.run( "MATCH (n) RETURN DISTINCT labels(n)" );
//		Vector<String> labels = new Vector<String>();
//		Vector<String> arcs = new Vector<String>();
//		while(result.hasNext()) {
//			Record r = result.next();
//			labels.addElement(r.get(0).toString().replaceAll("\"|]|\\[", "")); //.toLowerCase();
//			System.out.println("--- " + r.get(0).toString().replaceAll("\"|]|\\[", ""));
//			System.out.println("+++ " + r.get(0).asString());
//		}
//		//facts.addElement("% ELENCO DEI FATTI CHE CARATTERIZZANO LE ENTITA'");
//		for(int i = 0; i < labels.size(); i++) {
//			Vector<String> schema = attributes(labels.elementAt(i),"E");  // CREAZIONE DELLO SCHEMA PER LABEL i-esima
//			result = session.run( "MATCH (x:"+ labels.elementAt(i) +") RETURN id(x)" );
//			while(result.hasNext()) {
//				String id = result.next().get(0).toString();
//				Result properties = session.run( "MATCH(x:" + labels.elementAt(i) + ") WHERE id(x) = " + id + " RETURN properties(x)" );
//				while(properties.hasNext()) {
//					String[] prop = properties.next().get(0).toString().replaceAll("\"|}|\\{", "").toLowerCase().split(",");		
//					for(int j = 0; j < prop.length; j++) { // ordinamento alfabetico degli attributi dello schema
//						String[] attrib_value = prop[j].split(": ");
//						prop[j] = attrib_value[0].trim() + ": " + attrib_value[1];
//					}
//					Arrays.sort(prop);
//					// riscrittura degli attributi 
//					Vector<String> others_values = new Vector<String>();
//					String temp = labels.elementAt(i).toLowerCase() + "(" + id +  ","; 
//					for(int j = 0; j < prop.length; j++) {
//						String[] attrib_value = prop[j].split(": ");
//						if(contains(schema,attrib_value[0].trim())) {
//							if(j == prop.length - 1)
//								temp += attrib_value[1].replaceAll(" ", "_").replaceAll("/", "_");			// modifica 27-08-2017
//							else
//								temp += attrib_value[1].replaceAll(" ", "_").replaceAll("/", "_") + ",";	// modifica 27-08-2017
//						} else {
//								if(j == prop.length - 1) 
//									temp = temp.subSequence(0, temp.lastIndexOf(",")).toString();							
//								others_values.addElement(prop[j]); 
//						}
//					}
//					facts.addElement(temp + ")");
//					
//					if (!others_values.isEmpty()) {
//						for(int j = 0; j < others_values.size(); j++) {
//							String[] other = others_values.elementAt(j).split(": ");	
//							facts.addElement(other[0].trim() + "(" + id + "," + other[1].replaceAll(" ", "_").replaceAll("/", "_") + ")"); // modifica 27-08-2017
//						}
//					}
//			   }
//			   Collections.sort(facts);
//			   Result relationships = session.run( "MATCH(x:" + labels.elementAt(i) + ")-[r]->(b) WHERE id(x) = " + id + " RETURN type(r),id(b),properties(r),id(r)" );  // ARCHI USCENTI
//			   while(relationships.hasNext()) {
//				   Record temp = relationships.next();
//				   Vector<String> schema1 = attributes(temp.get(0).toString().replaceAll("\"", ""),"R");
//				   String[] prop = temp.get(2).toString().replaceAll("\"|}|\\{", "").toLowerCase().split(",");
//				   for(int j = 0; j < prop.length; j++) { // ordinamento alfabetico degli attributi dello schema
//						String[] attrib_value = prop[j].split(": ");
//						if(attrib_value.length > 1)
//							prop[j] = attrib_value[0].trim() + ": " + attrib_value[1];
//					}
//				   Arrays.sort(prop);
//				   String propert = "";
//				   for(int j = 0; j < prop.length; j++) {
//						String[] attrib_value = prop[j].split(": ");	
//						if(attrib_value.length > 1 && contains(schema1,attrib_value[0].trim())) {	// da investigare					
//							if(j == prop.length - 1)
//								propert += attrib_value[1].replaceAll(" ", "_").replaceAll("/", "_");				// modifica 27-08-2017
//							else
//								propert += attrib_value[1].replaceAll(" ", "_").replaceAll("/", "_") + ",";			// modifica 27-08-2017
//						} else if(attrib_value.length > 1) {
//							if(j == prop.length - 1 && propert.endsWith(",")) 
//								propert = propert.subSequence(0, propert.lastIndexOf(",")).toString();
//							arcs.addElement(attrib_value[0].trim() + "(" + temp.get(3) + "," + attrib_value[1].replaceAll(" ", "_").replaceAll("/", "_") + "),1)."); // modifica 27-08-2017
//						}
//					}
//				   if(propert.isEmpty())
//					   arcs.addElement(temp.get(0).toString().replaceAll("\"", "").toLowerCase() + "(" 
//						   + temp.get(3) + "," + id + "," + temp.get(1).toString() + "),1).");
//				   else 
//					   arcs.addElement(temp.get(0).toString().replaceAll("\"", "").toLowerCase() + "(" 
//						   + temp.get(3) + "," + id + "," + temp.get(1).toString() + "," + propert + "),1).");
//			   }			   
//			}
//		} // end for
//		for(int j = 0; j < facts.size(); j++)
//			facts.set(j, "fact(" + (++id_fact) + "," + facts.elementAt(j) + ",1).");
//		//facts.addElement("% ELENCO DEI FATTI CHE ESPRIMONO LE RELAZIONI FRA ENTITA'");
//		Collections.sort(arcs);
//		for(int i = 0; i < arcs.size(); i++) {
//			facts.addElement("fact(" + (++id_fact) + "," + arcs.elementAt(i));
//		}
		//graphdb.Connect.close();

			// STAMPA SU FILE LOCALE SUL SERVER
//			String destination = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("spreadingFolder");
//			String fileName = "graph.pl";
//			try {
//	            FileWriter out = new FileWriter(new File(destination + "/" + fileName));
//	            for (String f : facts)
//	            	out.write(f + "\n");
//	            out.close();
//	        } catch (IOException e) {
//	            System.out.println("Errore nell'apertura del file: " + e.getMessage());
//	        }
		return facts;
	}
}
