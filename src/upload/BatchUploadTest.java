package upload;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import domain.DomainData;

public class BatchUploadTest {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, Exception {
		DomainData domain = new DomainData("software_pinto", new File("src\\graphs\\software_Pinto.gbs"));		// schema da caricare
		if (BatchUpload.upload("src\\graphs\\nodes_pinto.json", domain, true)) {		//caricamento istanze batch
			System.out.println("--Dataset correctly uploaded--");
		}
	}

}
