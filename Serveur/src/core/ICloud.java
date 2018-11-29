package core;

import java.rmi.*;

/**
 * Interface commune entre le serveur et le client qui va assurer la connexion
 * au cloud, Elle permet la recuperation de la configuration chargée sur le
 * serveur. Elle permet l'enregistrement d'un nouveau compte (Si le serveur le
 * permet) Elle permet le login a l'utilisateur, et la recuperation de
 * l'utilisateur ainsi que son cloud et ses notifications (si activé)
 * 
 * @author Groupe6
 */
public interface ICloud extends Remote {
	/**
	 * Vérifie le login et le mdp lors de la connexion d'un utilisateur.
	 * 
	 * @param utilisateur
	 *            : Login de l'utilisateur
	 * @param motDePasse
	 *            : mot de passe de l'utilisateur
	 * @return l'utilisateur qui a établi la connection : IUtilisateur
	 * @throws RemoteException
	 *             .
	 */
	public IUtilisateur login(String utilisateur, String motDePasse) throws RemoteException;

	/**
	 * Restitue la configuration actuellement chargée sur le serveur.
	 * 
	 * @return configuration du serveur : IConfiguration.
	 * @throws RemoteException
	 *             .
	 */
	public IConfiguration getConfigurationServeur() throws RemoteException;

	/**
	 * permet l'enregistrement d'un nouveau compte sur le serveur.
	 * 
	 * @param login
	 *            : login de l'utilisateur
	 * @param password
	 *            (Hashé en SHA2) : mot de passe de l'utilisateur
	 * @param nom
	 *            : nom de l'utilisateur
	 * @param prenom
	 *            : prenom de l'utilisateur
	 * @param email
	 *            : email de l'utilisateur
	 * @throws RemoteException
	 *             .
	 */
	public void enregistrement(String login, String password, String nom, String prenom, String email)
			throws RemoteException;
}