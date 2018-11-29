package core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface IUtilisateur extends Remote {

	/**
	 * @require paramOK : fic != null
	 * @param fic
	 *            : representation d'un fichier du cloud (CloudFile)
	 * @throws CloudException
	 * @throws RemoteException
	 * @return true si l'utilisateur est le proprietaire, sinon false
	 */
	public boolean estProprietaire(ICloudFile fic) throws CloudException, RemoteException;

	/**
	 * @require paramOK : fic != null
	 * @param fic
	 * @throws CloudException
	 * @throws RemoteException
	 * @return true si l'utilisateur a acces au fichier fic, sinon false
	 */
	public boolean aAcces(ICloudFile fic) throws CloudException, RemoteException;

	/**
	 * 
	 * @return le login de connexion de l'utilisateur
	 */
	public String getLogin() throws RemoteException;

	/**
	 * 
	 * @throws CloudException
	 *             .
	 * @throws RemoteException
	 *             .
	 * @return l'ID de l'utilisateur
	 */
	public int getID() throws CloudException, RemoteException;

	/**
	 * 
	 * @throws CloudException
	 * @throws RemoteException
	 * @return le nom de l'utilisateur
	 */
	public String getNom() throws CloudException, RemoteException;

	/**
	 * 
	 * @throws CloudException
	 * @throws RemoteException
	 * @return le prenom de l'utilisateur
	 */
	public String getPrenom() throws CloudException, RemoteException;

	/**
	 * 
	 * @throws CloudException
	 * @return l'email de l'utilisateur
	 */
	public String getEmail() throws CloudException, RemoteException;

	/**
	 * 
	 * @throws CloudException
	 * @throws RemoteException
	 * @return le quota de l'utilisateur
	 */
	public double getQuota() throws CloudException, RemoteException;

	/**
	 * 
	 * @throws CloudException
	 * @throws RemoteException
	 * @return la date de derniere connexion de l'utilisateur
	 */
	public Date getDerniereConnexion() throws CloudException, RemoteException;

	/**
	 * 
	 * @throws CloudException
	 * @throws RemoteException
	 * @return l'arborescence CloudClient de l'utilisateur
	 */
	public ICloudClient getCloudClient() throws CloudException, RemoteException;

	/**
	 * 
	 * @throws CloudException
	 * @throws RemoteException
	 * @return la date de derniere connexion de l'utilisateur
	 */
	boolean getFreeze() throws CloudException, RemoteException;

	/**
	 * 
	 * @param email
	 *            : nouvelle email du compte
	 * @throws CloudException
	 * @throws RemoteException
	 */
	void setEmail(String email) throws CloudException, RemoteException;

	/**
	 * 
	 * @param prenom
	 *            : nouveau prenom de l'utilisateur
	 * @throws CloudException
	 * @throws RemoteException
	 * @throws CloudException
	 */
	void setPrenom(String prenom) throws CloudException, RemoteException;

	/**
	 * 
	 * @param nom
	 *            : nouveau nom de l'utilisateur
	 * @throws RemoteException
	 * @throws CloudException
	 */
	void setNom(String nom) throws CloudException, RemoteException;

	/**
	 * @require paramOK : mdp != null
	 * @param mdp
	 *            : le nouveau mot de passe
	 * @param ancienMdp
	 *            : le mot de passe actuel
	 * @ensure majOK : this.password == mdp
	 */
	public void setMotDePasse(String nouveauMdp, String ancienMdp) throws RemoteException;

	/**
	 * 
	 * @param quota
	 *            : quota maximum en GigaOctet
	 * @throws CloudException
	 * @throws RemoteException
	 */
	void setQuota(double quota) throws CloudException, RemoteException;

	/**
	 * 
	 * @param freeze
	 *            : freeze le compte si true.
	 * @throws CloudException
	 * @throws RemoteException
	 */
	void setFreeze(boolean freeze) throws CloudException, RemoteException;

	/**
	 * 
	 * @param login
	 *            : nouveau login du compte.
	 * @throws CloudException
	 * @throws RemoteException
	 */
	void setLogin(String login) throws CloudException, RemoteException;

	/**
	 * Permet de recuperer les fichiers suivi par l'utilisateur qui on ete
	 * modifier depuis sa derniere connection
	 * 
	 * @return liste des fichiers suivi et modifier
	 * @throws CloudException
	 *             .
	 * @throws RemoteException
	 *             .
	 */
	public List<ICloudFile> getFichiersANotifier() throws CloudException, RemoteException;

}