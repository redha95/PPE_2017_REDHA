package analyse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import modele.Utilisateur;
import outils.Resultat;

public class ExtracteurConstructeur extends DefaultHandler {

	private static List<Utilisateur> employees = new ArrayList<>();
    private static Utilisateur empl = null;
    private static String text = null;
    private Resultat resultat;    
    
    public ExtracteurConstructeur(Resultat resultat) {
		super();
		this.resultat = resultat;
	}

	@Override
    // A start tag is encountered.
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    
    	switch (qName) {
                      // Create a new Employee.
                      case "utilisateur": {
                           empl = new Utilisateur();
//                            empl.setIdutilisateur(Integer.valueOf(attributes.getValue("ID")));
                           break;
                      }
                 }
            }
        
   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
   
	   switch (qName) {
                      case "utilisateur": {
                       // The end tag of an employee was encountered, so add the employee to the list.
                    	empl.setDerniere_mise_a_jour(new Date());
                    	empl.setModifie_par("ADMIN");
                        employees.add(empl);
                        break;
                      }
                      case "email": {
                            empl.setEmail(text);
                            break;
                       }
                      case "nom": {
                            empl.setNom(text);
                            break;
                       }
                      case "prenom": {
                            empl.setPrenom(text);
                            break;
                       }
                       case "password": {
                            empl.setPassword(text);
                            break;
                       }
                 }
             }
         
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
                  text = String.copyValueOf(ch, start, length).trim();
    }
    
    public void endDocument() throws SAXException {
		System.out.println("Fin de l'extraction");
		resultat.setEmployees(employees);
	}
	
}
