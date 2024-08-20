package test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

import connection.GraphDB;
import domain.DomainData;
import export.KBRestructurer;
import export.Neo4jToProlog;
import export.SchemaToProlog;
/**
 * The TestTree class is used to test the functionality of the project.
 *  */
public class TestTree {

	/**
	 * Exports the facts from Neo4j database to a Prolog file.
	 *
	 * @param destination The destination directory where the Prolog file will be exported.
	 */
	public static void exportPl(String destination) {
		List<String> facts = Neo4jToProlog.createFacts();
		String fileName = "exportedGraph.pl";
		try {
			String filePath = destination + "/" + fileName;
			//System.out.println("-------------------->" + filePath);
			FileWriter out = new FileWriter(new File(filePath));
			out.write(facts.stream().collect(Collectors.joining("\n")));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The main method of the TestTree class.
	 * It exports the facts from the Neo4j database to a Prolog file and then
 	 * exports the schema to a Prolog file. It then restructures the knowledge base.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		try {
			DomainData domainData = new DomainData("\\Users\\Ningo\\Desktop\\GraphBRAINAPI\\src\\graphs\\software_Pinto.gbs");
			System.out.println(domainData.getAllRelationships());
			GraphDB graphdb = new GraphDB("bolt://localhost", "neo4j", "test");
			String[] folder = new String[1];
			folder[0] = "\\Users\\Ningo\\Desktop\\GraphBRAINAPI\\";
			exportPl(folder[0] + "src\\graphs\\");

			try {
				graphdb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			SchemaToProlog.main(folder);
			KBRestructurer.main(new String[0]);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
