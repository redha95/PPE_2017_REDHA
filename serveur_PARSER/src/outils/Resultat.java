package outils;

import java.util.List;

import javax.persistence.EntityManager;

import modele.Role;
import modele.Utilisateur;
import ressources.FournisseurDePersistance;

public class Resultat {

	private List<Utilisateur> employees;

	public Resultat() {
		super();
	}

	public void setEmployees(List<Utilisateur> employees) {
		this.employees = employees;
	}

	public List<Utilisateur> getEmployees() {
		return employees;
	}
	
	public boolean enregistrer() {
		EntityManager em = null;
		try {
				em = FournisseurDePersistance.getInstance().fournir();
				em.getTransaction().begin();
				Role rolemagasin = (Role) em.createNativeQuery("select * from role where role='magasin'", Role.class).getSingleResult();
				for(Utilisateur ref : employees) {
					rolemagasin.ajoutUtilisateur(ref);
				}
				em.getTransaction().commit();
		return true;		
		} catch (Exception e) {e.printStackTrace(); return false; }
		finally {
			em.close();
		}	
	}
}
