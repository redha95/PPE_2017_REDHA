package service;



import java.io.IOException;
import java.io.StringReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import analyse.ExtracteurConstructeur;
import outils.Resultat;

@Path("xml")
public class ServiceREST {	
	
	@POST
	@Consumes("text/plain")
	public void postXML(String xml) throws ParserConfigurationException, SAXException, IOException {
		System.out.println(xml);		
		creer(xml);
	}
	
	private boolean creer(String xml) throws ParserConfigurationException, SAXException, IOException {
		boolean statut = false;
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
		SAXParser parser = parserFactor.newSAXParser();
		Resultat resultat = new Resultat();
		ExtracteurConstructeur ec = new ExtracteurConstructeur(resultat);
		parser.parse(new InputSource(new StringReader(xml)), ec);
		resultat.enregistrer();
		statut= true;
		return statut;
	}	
}
