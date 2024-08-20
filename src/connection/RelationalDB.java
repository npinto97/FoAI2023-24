package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import domain.Attachment;
import domain.Attribute;
import domain.Author;
import domain.HallComparator;
import domain.HallUser;
import connection.RelationalDB;

public class RelationalDB {
	public final String ENTITY = "e";  
	public final String RELATION = "r";

	public final String ATTACK = "attack";
	public final String SUPPORT = "support";
	public final String COMMENT = "comment";
	public final String DELETE = "delete";
	public final String UPDATE = "update";

	private final int weightInstances = 1;
	private final int weightAttributes = 1;
	private final int weightUpdates = 1;
	private final int weightDeletions = 1;
	private final int weightAttacks = 1;
	private final int weightSupports = 1;
	private final int weightComments = 1;
	private final int weightReceivedUpdates = 1;
	private final int weightReceivedDeletions = 1;
	private final int weightReceivedAttacks = 1;
	private final int weightReceivedSupports = 1;

	private static Connection connection;

	public RelationalDB() {
		try {
			if(connection == null) {
				Class.forName("org.postgresql.Driver");
				connection = DriverManager.getConnection("jdbc:postgresql://193.204.187.73:4366/graphbrain", "postgres", "P0576r35"); // remoto
			}
		} catch (ClassNotFoundException e1 ) {
			System.out.println("Error: Driver for PostgreSql not found.");
			e1.printStackTrace();
		} catch (SQLException e)  {
			//ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			//PrologBean pb= (PrologBean) ec.getSessionMap().get("prologBean");
			//pb.message = "Error: Connection failed to PostgreSql.";
			e.printStackTrace();
		}
		System.out.println(connection);
	}
	
	public String getDbInfo(String kgName) {
		System.out.println(connection);
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT url_db_graph, un_db_graph, pw_db_graph, location_ontologies FROM knowledge_graphs WHERE gbkg_name = '" + kgName + "'");
			if(result.next()) {
				return result.getString(1) + ";" + result.getString(2) + ";" + result.getString(3) + ";" + result.getString(4);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Connection getConnection() {
		return connection;
	}

//	public void close() throws SQLException {
//		try {
//            if (connection != null && !connection.isClosed()) {
//                connection.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//	}

	public String userRights(String username) {
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT rights FROM users WHERE username = '" + username + "'");
			if(result.next())
				return result.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "write";
	}

	public void newAccess(String domain, String username) {
		//			String query = "INSERT INTO users_domains (username,domain) VALUES ('" 
		//					+ lb.username + "', '" + domain 
		//					+ "') ON CONFLICT DO UPDATE SET last_login = now()";
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO logins (username) VALUES ('" 
					+ username + "')");
			statement.executeUpdate("INSERT INTO domain_logins (username,domain) VALUES ('" 
					+ username + "', '" + domain + "')");
			ResultSet result = statement.executeQuery("SELECT * FROM users_domains WHERE username = '" 
					+ username + "' AND domain = '" + domain + "'");
			if(result.next())
				statement.executeUpdate("UPDATE users_domains SET last_login = now() WHERE username = '" 
						+ username + "' AND domain = '" + domain + "'");
			else
				statement.executeUpdate("INSERT INTO users_domains (username,domain) VALUES ('" 
						+ username + "', '" + domain + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public TreeSet<String> getDomains() {
	    TreeSet<String> domains = new TreeSet<>();
	    String query = "SELECT name FROM domains";
	    try (Statement statement = connection.createStatement();
	         ResultSet resultSet = statement.executeQuery(query)) {
	        while (resultSet.next()) {
	            domains.add(resultSet.getString(1));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return domains;
	}


	public String[] getCredit(String username) {
		String[] usercredit = new String[2];
		String query = "SELECT credit,bonus FROM users WHERE username = '" + username + "'";
		try {
			Statement statement = connection.createStatement();
			ResultSet resultset = statement.executeQuery(query);
			if(resultset.next()) {
				usercredit[0] = resultset.getString(1);
				usercredit[1] = resultset.getString(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usercredit;
	}

	public List<String[]> getUserContributions(String username, String domain) {

		List<String[]> contributions = new ArrayList<String[]>();
		String contribution[] = new String[3];
		try {
			String queryPart1 = "SELECT count(*) FROM author WHERE username = '" + username;
			if (domain != null)
				queryPart1 += "' AND domain = '" + domain;
			queryPart1 += "' AND type ='";
			Statement statement = connection.createStatement();
			ResultSet resultset = statement.executeQuery(queryPart1 + "e" + "' AND attribute_key IS NULL");
			contribution[0] = "Entities";
			if(resultset.next())
				contribution[1] = resultset.getString(1);
			resultset = statement.executeQuery(queryPart1 + "e" + "' AND attribute_key IS NULL AND is_active = TRUE");
			if(resultset.next())
				contribution[2] = resultset.getString(1);
			contributions.add(contribution);
			contribution = new String[3];
			contribution[0] = "Entity Attributes";
			resultset = statement.executeQuery(queryPart1 + "e" + "' AND attribute_key IS NOT NULL");
			if(resultset.next())
				contribution[1] = resultset.getString(1);
			resultset = statement.executeQuery(queryPart1 + "e" + "' AND attribute_key IS NOT NULL AND is_active = TRUE");
			if(resultset.next())
				contribution[2] = resultset.getString(1);
			contributions.add(contribution);
			contribution = new String[3];
			contribution[0] = "Relationships";
			resultset = statement.executeQuery(queryPart1 + "r" + "' AND attribute_key IS NULL");
			if(resultset.next())
				contribution[1] = resultset.getString(1);
			resultset = statement.executeQuery(queryPart1 + "r" + "' AND attribute_key IS NULL AND is_active = TRUE");
			if(resultset.next())
				contribution[2] = resultset.getString(1);
			resultset = statement.executeQuery(queryPart1 + "r" + "' AND attribute_key IS NOT NULL");
			contributions.add(contribution);
			contribution = new String[3];
			contribution[0] = "Relationship Attributes";
			if(resultset.next())
				contribution[1] = resultset.getString(1);
			resultset = statement.executeQuery(queryPart1 + "r" + "' AND attribute_key IS NOT NULL AND is_active = TRUE");
			if(resultset.next())
				contribution[2] = resultset.getString(1);
			contributions.add(contribution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contributions;
	}

	public List<HallUser> getRanking(String domain) {
		String query;
		if(domain != null)
			query = "SELECT * FROM users_domains WHERE domain = '" + domain + "'";
		else
			query = "SELECT username, sum(instances) as instances, sum(attributes) as attributes, "
					+ "sum(updates) as updates, sum(deletions) as deletions, sum(attacks) as attacks, "
					+ "sum(supports) as supports, sum(comments) as comments, "
					+ "sum(updates_received) as updates_received, sum(deletions_received) as deletions_received, "
					+ "sum(attacks_received) as attacks_received, sum(supports_received) as supports_received "
					+ "FROM users_domains GROUP BY username";
		try {
			Statement statement = connection.createStatement();
			ResultSet resultset = statement.executeQuery(query);
			return buildRanking(resultset);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<HallUser>();
	}

	private List<HallUser> buildRanking(ResultSet resultset) {
		List<HallUser> ranking = new ArrayList<HallUser>();
		int contributions;
		int trustDenominator;
		double trust;
		try {
			while(resultset.next()) {
				contributions = weightInstances * resultset.getInt(resultset.findColumn("instances"))
						+ weightAttributes * resultset.getInt(resultset.findColumn("attributes"))
						+ weightUpdates * resultset.getInt(resultset.findColumn("updates"))
						+ weightDeletions * resultset.getInt(resultset.findColumn("deletions"))
						+ weightAttacks * resultset.getInt(resultset.findColumn("attacks"))
						+ weightSupports * resultset.getInt(resultset.findColumn("supports"))
						+ weightComments * resultset.getInt(resultset.findColumn("comments"))
						+ weightReceivedSupports * resultset.getInt(resultset.findColumn("supports_received"))
						- weightReceivedUpdates * resultset.getInt(resultset.findColumn("updates_received"))
						- weightReceivedDeletions * resultset.getInt(resultset.findColumn("deletions_received"))
						- weightReceivedAttacks * resultset.getInt(resultset.findColumn("attacks_received"));
				trustDenominator = weightReceivedSupports * resultset.getInt(resultset.findColumn("supports_received"))
						+ weightReceivedUpdates * resultset.getInt(resultset.findColumn("updates_received"))
						+ weightReceivedDeletions * resultset.getInt(resultset.findColumn("deletions_received"))
						+ weightReceivedAttacks * resultset.getInt(resultset.findColumn("attacks_received"));
				if(trustDenominator != 0)
					trust = weightReceivedSupports * resultset.getInt(resultset.findColumn("supports_received")) / trustDenominator;
				else
					trust = 0.0; //ste ma cosi' non distingue se ha avuto N attacchi
				ranking.add(new HallUser("", resultset.getString(1), contributions, trust));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ranking.sort(new HallComparator());
		for(int i = 1; i <= ranking.size(); i++) {
			ranking.get(i-1).id = i + "";
		}
		return ranking;
	}

	public void persistCreate(String username, String domain, String id, String referenceType, String description, List<Attribute> attributes, List<String> values) throws SQLException {
		persistAuthor(username, domain, id, referenceType, description); // inserimento della creazione della relazione
		for(int i = 0; i < attributes.size(); i++) { // inserimento della creazione dei campi della relazione
			if(!values.get(i).isEmpty()) { //ste inserire anche quelli vuoti per i successivi update e le valutazioni?
				persistAuthor(username, domain, null, id, referenceType, description, attributes.get(i).getName(), values.get(i));
			}
		}
	}

	public void persistCreateAPI(String id, String referenceType, String description, List<Attribute> attributes, List<String> values) throws SQLException {
		persistAuthorAPI(id, referenceType, description); // inserimento della creazione della relazione
		for(int i = 0; i < attributes.size(); i++) { // inserimento della creazione dei campi della relazione
			if(!values.get(i).isEmpty()) { //ste inserire anche quelli vuoti per i successivi update e le valutazioni?
				persistAuthorAPI(null, id, referenceType, description, attributes.get(i).getName(), values.get(i));
			}
		}
	}

	public int persistAuthor(String username, String domain, Integer oldTupleId, String id, String instance, String label, String attributeKey, String attributeValue) throws SQLException {
		int newFkAuthorId = 0;
		// MODIFICO LABEL
		//label = label.split("\\.")[1];

//		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//		WelcomeBean wb= (WelcomeBean) ec.getSessionMap().get("welcomeBean");
//		LoginBean lb= (LoginBean) ec.getSessionMap().get("loginBean");
		String query = "INSERT INTO author(updated_id,graph_id,type,username,domain,description,attribute_key,attribute_value)"
				+ " VALUES ("+ oldTupleId + "," + id + ",'" + instance
				+ "','" + username + "','" + domain + "', '" + label + "',";
		if(attributeKey == null)
			query += "null,";
		else 
			query += "'" + attributeKey + "',";

		if(attributeValue == null)
			query += "null)";
		else 
			query += "'" + attributeValue.replace("'", "''") + "')";
		Statement statement = connection.createStatement();
		//			statement.executeUpdate(query);
		System.out.println(query);
		/*
			statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = statement.getGeneratedKeys();
			if(rs != null && rs.next()) {
			    newFkAuthorId = rs.getInt(1);
			}
			if(attributeKey == null)
				updateInteractionStatistics(connection, lb.username, wb.domain, "instances");
			else
				updateInteractionStatistics(connection, lb.username, wb.domain, "attributes");
			return newFkAuthorId;
		 */
		return 0;
	}

	public int persistAuthorAPI(Connection connection, Integer oldTupleId, String id, String instance, String label, String attributeKey, String attributeValue) throws SQLException {
		int newFkAuthorId = 0;
		String query = "INSERT INTO author(updated_id,graph_id,type,username,domain,description,attribute_key,attribute_value)"
				+ " VALUES ("+ oldTupleId + "," + id + ",'" + instance
				+ "','admin','graphbrain', '" + label + "',";
		if(attributeKey == null)
			query += "null,";
		else 
			query += "'" + attributeKey + "',";

		if(attributeValue == null)
			query += "null)";
		else 
			query += "'" + attributeValue.replace("'", "''") + "')";
		//Statement statement = connection.createStatement();
		//			statement.executeUpdate(query);
		System.out.println(query);
		/*
			statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = statement.getGeneratedKeys();
			if(rs != null && rs.next()) {
			    newFkAuthorId = rs.getInt(1);
			}
			if(attributeKey == null)
				updateInteractionStatistics(connection, lb.username, wb.domain, "instances");
			else
				updateInteractionStatistics(connection, lb.username, wb.domain, "attributes");
			return newFkAuthorId;
		 */
		return 0;
	}

	public int persistAuthorAPI(Integer oldTupleId, String id, String instance, String label, String attributeKey, String attributeValue) throws SQLException {
		int newFkAuthorId = persistAuthorAPI(connection, oldTupleId, id, instance, label, attributeKey, attributeValue);
		return newFkAuthorId;
	}

	public ResultSet getAllNodesWithSubClass() {
		ResultSet res = null;
		String query = "select graph_id\r\n"
				+ "from author \r\n"
				+ "where attribute_key = 'subClass' and is_active='true'";
		try {
			Statement statement = connection.createStatement();
			res = statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public void persistAuthor(String username, String domain, String id, String instance, String label) throws SQLException {
		persistAuthor(username, domain, null, id, instance, label, null, null);
	}

	public void persistAuthorAPI(String id, String instance, String label) throws SQLException {
		persistAuthorAPI(null, id, instance, label, null, null);
	}

	private void updateInteractionStatistics(Connection connection, String username, String domain, String statistics) {
		String query = "UPDATE users_domains SET " + statistics + " = " + statistics 
				+ " + 1 WHERE username = '" + username + "' AND domain = '" + domain + "'";
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertUpdateEvaluations(String username, String domain, String instanceId, String referenceType, String description, LinkedHashMap<String,String> oldValsMap, List<String> changedAttrs, List<String> changedVals) throws SQLException {
		Iterator<String> iterAttr = changedAttrs.iterator();
		Iterator<String> iterVal = changedVals.iterator();
		String nextAttr, nextVal;
		while(iterAttr.hasNext()) {
			nextAttr = iterAttr.next();
			nextVal = iterVal.next();
			insertStatus(username, domain, instanceId, referenceType, description, nextAttr, oldValsMap.get(nextAttr), nextVal, UPDATE);
		}
	}

	public Integer insertStatus(String username, String domain, String id, String instanceType, String label, String attributeName, String attributeValue, String notes, String statusType) throws SQLException {
//		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//		WelcomeBean wb= (WelcomeBean) ec.getSessionMap().get("welcomeBean");
//		LoginBean lb= (LoginBean) ec.getSessionMap().get("loginBean");
		String attributeNamePlain = attributeName.replace("*", ""); //ste serve ancora? o ora gestisce JSF i *?
		String query = "SELECT id, username FROM author WHERE graph_id = " + id
				+ " AND type = '" + instanceType + "' AND is_active = true AND attribute_key ";
		if(!attributeNamePlain.equals(""))
			query += "= '" + attributeNamePlain + "'";
		else // si sta valutando l'intera entita' o relazione
			query += "IS NULL";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		Integer fkAuthorId = null;
		String targetUser = null;
		if(resultSet.next()) {
			fkAuthorId = resultSet.getInt(1);
			targetUser = resultSet.getString(2);
			if(statusType == UPDATE) // sicuramente attribute_key non era null, la tupla dell'attributo e' superata
				statement.executeUpdate("UPDATE author SET is_active = false WHERE id = " + fkAuthorId);
		}
		if(statusType == UPDATE) {
			persistAuthor(username, domain, fkAuthorId, id, instanceType, label, attributeName, notes); // se UPDATE le note contengono il nuovo valore
		} else if(statusType == DELETE) {
			statement.executeUpdate("UPDATE author SET is_active = false WHERE graph_id = " + id + " AND type = '" + instanceType + "'");
		}
		String attributeNameQuery = "";
		String attributeNameQueryValue = "";
		String attributeValueQuery = "";
		String attributeValueQueryValue = "";
		String notesQuery = "";
		String notesQueryValue = "";
		if(!attributeName.isEmpty()) {
			attributeNameQuery = ", attribute_key";
			attributeNameQueryValue = ", '" + attributeNamePlain + "'";
			if(attributeValue != null) {
				attributeValueQuery = ", attribute_value";
				attributeValueQueryValue = ", '" + attributeValue.replace("'", "''") + "'";
			}
		}
		if(notes != null && !notes.isEmpty()) {
			notesQuery = ", notes";
			notesQueryValue = ", '" + notes.replace("'", "''") + "'";
		}
		if(targetUser == null || !targetUser.equals(username)) {
			query = "INSERT INTO status(fk_author_id, graph_id, type, description" + attributeNameQuery + attributeValueQuery
					+ ", username, status_type" + notesQuery + ", domain) VALUES (" 
					+ fkAuthorId + "," + id + ",'" + instanceType + "','" + label + "'" + attributeNameQueryValue + attributeValueQueryValue
					+ ", '" + username + "', '" + statusType + "'" + notesQueryValue + ", '" + domain + "')";
			System.out.println(query);
			statement.executeUpdate(query);
			// gestione statistiche e stampa dei fatti su file
			String statistic;
			if(statusType != COMMENT) {
				String predicate, flag; //ste usare proprio le costanti di classe invece del flag?
				if(statusType == SUPPORT) {
					updateInteractionStatistics(connection, targetUser, domain, "supports_received");
					statistic = "supports";
					predicate = "support";
					flag = "l";
				} else {
					predicate = "attack";
					if(statusType == ATTACK) {
						updateInteractionStatistics(connection, targetUser, domain, "attacks_received");
						statistic = "attacks";
						flag = "d";
					} else if(statusType == DELETE) {
						updateInteractionStatistics(connection, targetUser, domain, "deletions_received");
						statistic = "deletions";
						flag = "c";
					} else { // statusType == UPDATE
						updateInteractionStatistics(connection, targetUser, domain, "updates_received");
						statistic = "updates";
						flag = "m";
					}
				}
//				try {
//					TxtWriter writer = new TxtWriter();
//					writer.write(predicate + "("+ username +"," + fkAuthorId + "," + flag + ").");
//					writer.close();
//				} catch (IOException ioe) {
//					//return "Error writing file";
//					ioe.printStackTrace();;
//				}
			} else
				statistic = "comments";
			updateInteractionStatistics(connection, username, domain, statistic);
		} else {
			System.out.println("Nessun utente o stesso utente - Valutazione non inserita e statistiche di interazione non aggiornate");
		}
		return fkAuthorId;
	}

	public Integer addDomain(String username, String id, String domain) throws SQLException {
		return associateDomain(username, id, domain, "+");
	}
	public Integer removeDomain(String username, String id, String domain) throws SQLException {
		return associateDomain(username, domain, id, "-");
	}
	public Integer associateDomain(String username, String domain, String id, String action) throws SQLException {

		String oldAction;
		String statusType = UPDATE; //ste cambiare a seconda che sia aggiunta o rimozione? Es.: aggiunta -> comment, rimozione -> delete
		if (action.equals("+")) {
			oldAction = "-";
		} else { // action = "-"
			oldAction = "+";
		}
//		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//		WelcomeBean wb= (WelcomeBean) ec.getSessionMap().get("welcomeBean");
//		LoginBean lb= (LoginBean) ec.getSessionMap().get("loginBean");
		String query = "SELECT id, username FROM author WHERE graph_id = " + id
				+ " AND type = '" + "e" + "' AND attribute_value = '" + domain
				+ "' AND is_active = true AND description = '" + oldAction + "'";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		Integer fkAuthorId = null;
		String targetUser = null;
		if(resultSet.next()) {
			fkAuthorId = resultSet.getInt(1);
			targetUser = resultSet.getString(2);
			statement.executeUpdate("UPDATE author SET is_active = false WHERE id = " + fkAuthorId);
		}
		persistAuthor(username, domain, fkAuthorId, id, "e", action, null, domain);
		if(targetUser == null || !targetUser.equals(username)) {
			query = "INSERT INTO status(fk_author_id, graph_id, type, description"
					+ ", username, status_type, notes, domain) VALUES (" 
					+ fkAuthorId + "," + id + ",'" + "e" + "','" + action + "'"
					+ ", '" + username + "', '" + statusType + "', '" + oldAction + "', '" + domain + "')";
			statement.executeUpdate(query);
			// gestione statistiche e stampa dei fatti su file
			//ste se non e' sempre UPDATE semplificare
			String statistic;
			if(statusType != COMMENT) {
				String predicate, flag; //ste usare proprio le costanti di classe invece del flag?
				if(statusType == SUPPORT) {
					updateInteractionStatistics(connection, targetUser, domain, "supports_received");
					statistic = "supports";
					predicate = "support";
					flag = "l";
				} else {
					predicate = "attack";
					if(statusType == ATTACK) {
						updateInteractionStatistics(connection, targetUser, domain, "attacks_received");
						statistic = "attacks";
						flag = "d";
					} else if(statusType == DELETE) {
						updateInteractionStatistics(connection, targetUser, domain, "deletions_received");
						statistic = "deletions";
						flag = "c";
					} else { // statusType == UPDATE
						updateInteractionStatistics(connection, targetUser, domain, "updates_received");
						statistic = "updates";
						flag = "m";
					}
				}
//				try {
//					TxtWriter writer = new TxtWriter();
//					writer.write(predicate + "("+ username +"," + fkAuthorId + "," + flag + ").");
//					writer.close();
//				} catch (IOException ioe) {
//					//return "Error writing file";
//					ioe.printStackTrace();;
//				}
			} else
				statistic = "comments";
			updateInteractionStatistics(connection, username, domain, statistic);
		} else {
			System.out.println("Nessun utente o stesso utente - Valutazione non inserita e statistiche di interazione non aggiornate");
		}
		return fkAuthorId;
	}

	//		public void updateAccessStatistics(String username, String domain, String type, String name) {
	//			updateAccessStatistics(username, domain, type, name, null);
	//		}
	public void updateAccessStatistics(String username, String domain, String type, String name, String graphId) {
		//			String query = "INSERT INTO users_domains (username,domain) VALUES ('" 
		//					+ lb.username + "', '" + domain 
		//					+ "') ON CONFLICT DO UPDATE SET last_login = now()";

		if (connection == null) {
			System.out.println("null");
		}
		try {
			Statement statement = connection.createStatement();
			String selectQuery = "SELECT id FROM reader WHERE username = '" + username + "' AND domain = '" + domain + "'";
			selectQuery += (type == null ? " AND type IS NULL" : " AND type = '" + type + "'");
			selectQuery += (name == null ? " AND name IS NULL" : " AND name = '" + name + "'");
			selectQuery += (graphId == null ? " AND graph_id IS NULL" : " AND graph_id = " + graphId);
			ResultSet result = statement.executeQuery(selectQuery);
			if(result.next()) {
				int id = result.getInt(1);
				statement.executeUpdate("UPDATE reader SET accesses = accesses + 1 WHERE id = " + id);
			} else {
				String insertQuery = "INSERT INTO reader (username,domain";
				insertQuery += (type == null ? "" : ",type");
				insertQuery += (name == null ? "" : ",name");
				insertQuery += (graphId == null ? "" : ",graph_id");
				insertQuery += ",accesses) VALUES ('" + username + "', '" + domain + "'";
				insertQuery += (type == null ? "" : ",'" + type + "'");
				insertQuery += (name == null ? "" : ",'" + name + "'");
				insertQuery += (graphId == null ? "" : "," + graphId);
				insertQuery += ",1)";
				statement.executeUpdate(insertQuery);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String insertSuggestion(String username, String domain, String notes) {		
//		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//		LoginBean lb =(LoginBean) ec.getSessionMap().get("loginBean");
		String query = "INSERT INTO suggestions(username, domain, suggestion) VALUES ('" 
				+ username + "', '" + domain + "', '" + notes.replace("'", "''") + "')";
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch(Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "Suggestion sent";
	}

	public List<Author> getAllAuthors() throws SQLException {
		String query;
		query = "SELECT * FROM author ORDER BY id ASC";
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(query);
		List<Author> authors = new ArrayList<Author>();
		while(result.next()) {
			Author author = new Author();
			author.setId(result.getInt("id"));
			author.setGraph_id(result.getInt("graph_id"));
			author.setType(result.getString("type"));
			author.setAttributeKey(result.getString("attribute_key"));
			author.setAttributeValue(result.getString("attribute_value"));
			author.setCreationDate(result.getTimestamp("creation_date"));
			author.setDescription(result.getString("description"));
			author.setUsername(result.getString("username"));
			author.setIsActive(result.getBoolean("is_active"));
			authors.add(author);
		}
		return authors;
	}

	public int getCurrentStatusTypeByFkAuthorId(String statusType, Integer fkAuthorId, Timestamp creationDate, String username) throws SQLException {
		String query = "SELECT COUNT(*) FROM status "
				+ "WHERE status_type = '" + statusType 
				+ "' AND fk_author_id = "+ fkAuthorId 
				+ " AND creation_date >= '" + creationDate + "'"
				+ "AND username != '" + username + "'";
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(query);
		result.next();
		return result.getInt(1);
	}

	public HashMap<String, Integer> getStatusCountPerUsername(List<Author> idsList, String statusType, Integer fkAuthorId, Timestamp creationDate) throws SQLException {
		String idsCommaSep = "";
		for(Author auth : idsList) {
			idsCommaSep += auth.getId() + ",";
		}
		idsCommaSep = idsCommaSep.substring(0, idsCommaSep.length()-1);

		String query = "SELECT count(*) as count, username FROM status "
				+ "WHERE fk_author_id IN ("+ idsCommaSep + ") "
				+ "AND status_type = '" + statusType + "' "
				+ "AND creation_date > '"+ creationDate + "' "
				+ "GROUP BY username";
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(query);
		HashMap<String,Integer> mapUserAndStatus = new HashMap<String, Integer>();
		while(result.next()) {
			String username = result.getString("username");
			Integer countAllStatus = result.getInt("count");
			mapUserAndStatus.put(username, countAllStatus);
		}
		return mapUserAndStatus;
	}

	public String insertFile(String filename, String id, char type, String username, String domain, String desc) throws SQLException {
//		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//		LoginBean lb= (LoginBean) ec.getSessionMap().get("loginBean");
		String ext = filename.substring(filename.lastIndexOf("."), filename.length());
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery("SELECT max(progr) FROM files WHERE id_graph = " + id + " AND type_graph = '" + type + "'");
		int prog = 1;
		if(result != null && result.next()) {
			//result.next();
			prog = result.getInt(1) + 1;
		}
		String newname = type + id + "_" + prog;
		statement.executeUpdate("INSERT INTO files (id_graph,type_graph,progr,filename,ext,username,domain,description) VALUES (" 
				+ id + ", '" + type + "', " + prog + ", '" + filename + "', '" + ext + "', '" 
				+ username + "', '" + domain + "', '" + desc + "')");
		return newname + ext;
	}

	public List<Attachment> getAttachments(String id, char type) {
		List<Attachment> attachments = new ArrayList<Attachment>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT progr,ext,id_graph,type_graph,description FROM files WHERE id_graph = " + id + " AND type_graph = '" + type + "' ORDER BY progr DESC");
			while(result.next()) {
				attachments.add(new Attachment(result.getString("progr"),result.getString("ext"),result.getString("description"),result.getString("type_graph")+result.getString("id_graph")+"_"+result.getString("progr")));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return attachments;
	}

	public Boolean hasAttachments(String id, char type) {
		return !getAttachments(id, type).isEmpty();
	}

	// UPDATE table_name SET column_name = value [, column_name = value ...] [WHERE condition]

	// renameDomain: DOMAINS, USERS_DOMAINS, AUTHOR, STATUS, ACCESSES; nome file XML, tag <domain> nel file XML

	public void renameEnt(String oldLabel, String newLabel) {

		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("UPDATE author SET description = '" + newLabel + "' WHERE type = '" + "e" 
					+ "' AND description = '" + oldLabel + "'");
			// [where type=Entity] non necessario perche' le relazioni sono Sogg.rel.Ogg e l'etichetta inizia per minuscola
			statement.executeUpdate("UPDATE status SET description = '" + newLabel + "' WHERE type = '" + "e" 
					+ "' AND description = '" + oldLabel + "'");
			// [where type=Entity] non necessario perche' le relazioni sono Sogg.rel.Ogg e l'etichetta inizia per minuscola
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void renameRel(String oldRel, String newRel, String oldId, String newId) {
		//UPDATE public.author set description = REPLACE(description,?,?) where domain = ? and type= ? and description like ?
		//Object[] values = new Object[] { "." + oldValue + ".", "." + newValue + ".", domain, "Relation", "%." + oldValue + ".%" };
		try {
			Statement statement = connection.createStatement();
			System.out.println("UPDATE author SET graph_id = '" + newId + "', description = '" + newRel 
					+ "' WHERE graph_id = '" + oldId + "';");
			statement.executeUpdate(
					"UPDATE author SET graph_id = '" + newId + "', description = '" + newRel 
					+ "' WHERE graph_id = '" + oldId + "';");
			statement.executeUpdate(
					"UPDATE status SET graph_id = '" + newId + "', description = '" + newRel 
					+ "' WHERE graph_id = '" + oldId + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void renameAttrEnt(String domain, String ent, String oldName, String newName) {
		//UPDATE public.author SET attribute_key = ? WHERE domain=? AND description = ? AND attribute_key=? AND type = ?

		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("UPDATE author SET attribute_key = '" + newName + "' WHERE type = '" + "e" 
					+ "' AND domain = '" + domain + "' AND attribute_key = '" + oldName + "' AND description = '" + ent + "'");
			// [where type=Entity] non necessario perche' le relazioni sono Sogg.rel.Ogg
			statement.executeUpdate("UPDATE status SET attribute_key = '" + newName + "' WHERE type = '" + "e" 
					+ "' AND domain = '" + domain + "' AND attribute_key = '" + oldName + "' AND description = '" + ent + "'");
			// [where type=Entity] non necessario perche' le relazioni sono Sogg.rel.Ogg
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void renameAttrRel(String rel, String oldName, String newName) {
		//UPDATE public.author SET attribute_key = ? WHERE domain=? AND description like ? AND attribute_key=? AND type = ?
		try {
			Statement statement = connection.createStatement();
			//				statement.executeUpdate("UPDATE author SET attribute_key = '" + newName + "' WHERE type = '" + GraphDB.RELATION + "' AND attribute_key = '" + oldName + "' AND description = '" + rel // in realta' Sogg.rel.Ogg
			//+ "'");
			// [where type=Relation] non necessario perche' le entita' non sono Sogg.rel.Ogg
			//				statement.executeUpdate("UPDATE status SET attribute_key = '" + newName + "' WHERE type = '" + GraphDB.RELATION + "' AND attribute_key = '" + oldName + "' AND description = '" + rel // in realta' Sogg.rel.Ogg 
			//+ "'");
			// [where type=Relation] non necessario perche' le entita' non sono Sogg.rel.Ogg
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void renameTypes(String table) {
		try {
			System.out.println("renameTypes");
			Statement statement = connection.createStatement();
			statement.executeUpdate("UPDATE " + table + " SET type='" + ENTITY + "' WHERE type='Entity'");
			statement.executeUpdate("UPDATE " + table + " SET type='" + RELATION + "' WHERE type='Relation'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fixRelationshipDescriptions(String table) {
		HashMap<String, String> map = new HashMap<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT id, description FROM " + table + " WHERE type='r'");
			while(result.next()) {
				String id = result.getString("id");
				String desc = result.getString("description");
				if(desc.contains(".")) {
					desc = desc.split("\\.")[1];
					map.put(id, desc);
				}
			}
			for (String id : map.keySet()) {
				statement.executeUpdate("UPDATE " + table + " SET description='" + map.get(id) + "' WHERE id='" + id + "'");
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	// renameAttrVal
	public void main(String[] args) {
		renameTypes("author");
		renameTypes("status");
		fixRelationshipDescriptions("author");
		fixRelationshipDescriptions("status");
	}


}
