package core;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Classe permettant la connexion au serveur du cloud.
 * @author Groupe6
 *
 */
public interface IConnexion extends Remote{
	/**
	 * Vérifie le login et le mdp lors de la connexion d'un utilisateur
	 * @param nomUtilisateur : nom d'utilisateur
	 * @param mdp : mot de passe de l'utilisateur
	 * @return l'utilisateur qui a etabli la connection.
	 * @throws CloudException : Utilisateur Inconnu, Mot de passe Invalide, Erreur de base de donnee.
	 * @throws RemoteException .
	 */
	public IUtilisateur login (String nomUtilisateur, String mdp) throws RemoteException,CloudException;
}
