package service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import modele.Utilisateur;
import ressources.FournisseurDePersistance;
import ressources.MessageDTO;

@Path("dto")
public class ServiceREST {
	
	private final static String QUEUE_NAME = "journal-des-authentifications";
	private String messageJournal;
	private String nomprenom;
	private String role;
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public MessageDTO getDTO(@QueryParam("email") String email, @QueryParam("password") String password) {
		
		MessageDTO message = new MessageDTO();
		
		if(authentifier(email, password)) {
			message.setBienvenue("Bienvenue " + nomprenom + ", Votre rôle est : ");
			message.setRole(role);
		}
		else
			message.setBienvenue(messageJournal);
		
		return message;		
	}
	
	private boolean authentifier(String email, String password) {
		boolean statut = false;
		EntityManager em = null;
		try {
				em = FournisseurDePersistance.getInstance().fournir();
				em.getTransaction().begin();
				Query requete = em.createNativeQuery("SELECT * FROM UTILISATEUR WHERE EMAIL = ?", Utilisateur.class);
				requete.setParameter(1, email);
				Utilisateur utilisateur = (Utilisateur) requete.getSingleResult();
				nomprenom = utilisateur.getNom() + " " + utilisateur.getPrenom();
				role = utilisateur.getRole().getRole();
				messageJournal = email +"|" + nomprenom +"|";
				if(!utilisateur.getPassword().equals(password)) {
					messageJournal += "mauvais password";
				}
				else {
						
						messageJournal += "succes";
						statut = true;
				}
				em.getTransaction().commit();
		} catch (Exception e) {
			messageJournal = email + "|null|utilisateur inconnu";
			em.getTransaction().rollback();
		}
		finally {
			try {
					em.close();
					journaliser();
			} catch (Exception e) {e.printStackTrace();
			}			
		}
		return statut;
	}
	
	private void journaliser() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("rabbitmq");
	    Connection connexion = (Connection) factory.newConnection();
	    Channel channel = connexion.createChannel();
	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    System.out.println(messageJournal);
	    channel.basicPublish("", QUEUE_NAME, null, messageJournal.getBytes());
	    channel.close();
	    connexion.close();
	}
}