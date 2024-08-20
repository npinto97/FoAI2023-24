package connection;

import java.io.File;

import domain.DomainData;
import it.lacam.mondo.auth.IMondo;
import it.lacam.mondo.auth.MondoFactory;
import it.lacam.mondo.auth.model.MondoUser;

public class GB {
	private GraphDB graphdb;
	private RelationalDB relationaldb;
	private DomainData domainData;
	
	public GB(String kgName) {
		//relationaldb = new RelationalDB();
		//String res = relationaldb.getDbInfo(kgName);
		//String urlGraph = res.split(";")[0];
		//String usernameGraph = res.split(";")[1];
		//String pwdGraph = res.split(";")[2];
		//String ontologyPath = res.split(";")[3];
		String urlGraph = "bolt://localhost";
		String usernameGraph = "neo4j";
		String pwdGraph = "test";
		graphdb = new GraphDB(urlGraph, usernameGraph, pwdGraph);
	}
	
	public GB(String kgName, String username, String password) {
		try {
			IMondo mondo = MondoFactory.getDefault();
			MondoUser u = mondo.login(username, password);
			if(u!=null) {
				relationaldb = new RelationalDB();
				String res = relationaldb.getDbInfo(kgName);
				String urlGraph = res.split(";")[0];
				String usernameGraph = res.split(";")[1];
				String pwdGraph = res.split(";")[2];
				String ontologyPath = res.split(";")[3];
				System.out.println(urlGraph + " and " + usernameGraph + " and " + pwdGraph);
				urlGraph = "bolt://localhost";
				usernameGraph = "neo4j";
				pwdGraph = "test";
				graphdb = new GraphDB(urlGraph, usernameGraph, pwdGraph);
			}	
		} catch (Exception e) {
			e.printStackTrace();
        }
		
	}
	
	public GB(String kgName, String user, String password, String domainName, File webInf) {
		this(kgName, user, password);
		try {
			domainData = new DomainData(domainName, webInf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GraphDB getGraphdb() {
		return graphdb;
	}

	public void setGraphdb(GraphDB graphdb) {
		this.graphdb = graphdb;
	}
	
	public RelationalDB getRelationaldb() {
		return relationaldb;
	}

	public void setRelationaldb(RelationalDB relationaldb) {
		this.relationaldb = relationaldb;
	}

	public DomainData getDomainData() {
		return domainData;
	}

	public void setDomainData(DomainData domainData) {
		this.domainData = domainData;
	}
}
