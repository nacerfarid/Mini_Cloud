package core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Module necessaire a la connexion et a la verification d'un couple login-password.
 * Permet de retourner une instance de Utilisateur qui contiendra tout ses attributs construits (Email, Nom, Prenom, Cloud, ...).
 * 
 */

public class Connexion extends UnicastRemoteObject implements IConnexion {

	protected Connexion() throws RemoteException  {
		super();
	}
	private static final long serialVersionUID = -6033540902596028447L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.IConnexion#login()
	 */
	@Override
	public IUtilisateur login(String nomUtilisateur, String mdp) throws RemoteException, CloudException {
		IUtilisateur utilisateur = null;
		if(Utilisateur.verifierMotDePasse(nomUtilisateur, mdp))
			utilisateur = Utilisateur.chargerUtilisateur(nomUtilisateur);
		else throw new CloudException("Erreur : Mot de passe non correct.");
		return utilisateur;
	}
}