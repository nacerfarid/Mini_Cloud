package core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;


/**
 * Permet de gerer l'entité Utilisateur.
 * Possède les accesseurs necessaires a l'usage du cloud.
 * Possède un mecanisme de cache utilisateur.
 * Possède des constructeurs qui recherche en cache ou en base de donnée.
 * Possède une methode de chargement de cloud (uniquement à la connexion).
 * Possède une methode de verification de password via un couple login/password
 * @author Groupe6
 *
 */
public interface IUtilisateur extends Remote {

	/**
	 * @param fic : representation d'un fichier du cloud (CloudFile)
	 * @return true si l'utilisateur est le proprietaire, sinon false
	 * @throws RemoteException .
	 */
	public boolean estProprietaire(ICloudFile fic) throws RemoteException;

	/**
	 * @param fic : CloudFile dont l’accès est à vérifier.
	 * @return true si l'utilisateur a acces au fichier fic, sinon false
	 * @throws RemoteException .
	 */
	public boolean aAcces(ICloudFile fic) throws RemoteException;

	/**
	 * 
	 * @return l'arborescence CloudClient de l'utilisateur
	 * @throws RemoteException .
	 */
	public ICloudClient getCloudClient() throws RemoteException;

	/**
	 * Permet le chargement de la structure de donnée CloudClient pour
	 * l'instance en cour.
	 * @throws RemoteException .
	 */
	public void chargerCloudClient() throws RemoteException;

	/**
	 * 
	 * @return le nom de l'utilisateur
	 * @throws RemoteException .
	 * @throws CloudException .
	 */
	public String getNom() throws CloudException, RemoteException;

	/**
	 * 
	 * @return le prenom de l'utilisateur
	 * @throws RemoteException .
	 */
	public String getPrenom() throws CloudException, RemoteException;

	/**
	 * 
	 * @throws CloudException .
	 * @throws RemoteException .
	 * @return l'ID de l'utilisateur
	 */
	public int getID() throws CloudException, RemoteException;

	/**
	 * 
	 * @return l'email de l'utilisateur
	 * @throws RemoteException .
	 * @throws CloudException .
	 */
	public String getEmail() throws CloudException, RemoteException;

	/**
	 * 
	 * @return le quota de l'utilisateur
	 * @throws RemoteException .
	 * @throws CloudException .
	 */
	public double getQuota() throws CloudException, RemoteException;/**
	/**
	 * 
	 * @throws CloudException .
	 * @throws RemoteException .
	 * @return l'etat de l'utilisateur
	 */
	public boolean getFreeze() throws CloudException, RemoteException;
	/**
	 * 
	 * @return le login de connexion de l'utilisateur
	 * @throws RemoteException .
	 * @throws CloudException .
	 */
	public String getLogin() throws CloudException, RemoteException;
	
	/**
	 * 
	 * @return la date de derniere connexion de l'utilisateur
	 * @throws RemoteException .
	 * @throws CloudException .
	 */
	public Date getDerniereConnexion() throws CloudException, RemoteException;

	/**
	 * 
	 * @param email : nouveau email du compte
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	void setEmail(String email) throws CloudException, RemoteException;

	/**
	 * 
	 * @param prenom : nouveau prenom de l'utilisateur
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	void setPrenom(String prenom) throws CloudException, RemoteException;

	/**
	 * 
	 * @param nom : nouveau nom de l'utilisateur
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	void setNom(String nom) throws CloudException, RemoteException;

	/**
	 * @param mdp : le nouveau mot de passe (Hashé en SHA256)
	 * @param ancienMdp : le mot de passe actuel (Hashé en SHA256)
	 * @throws RemoteException .
	 * 
	 */
	public void setMotDePasse(String mdp, String ancienMdp) throws RemoteException;

	/**
	 * 
	 * @param quota : quota maximum en KiloOctet
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	void setQuota(double quota) throws CloudException, RemoteException;

	/**
	 * 
	 * @param freeze : freeze le compte si true.
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	void setFreeze(boolean freeze) throws CloudException, RemoteException;

	/**
	 * 
	 * @param login : nouveau login du compte.
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	void setLogin(String login) throws CloudException, RemoteException;

	/**
	 * Permet de mettre a jour la date de derniere connexion d'un utilisateur
	 * 
	 * @param date : date de la derniere connexion.
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	void setLastConnexion(Date date) throws CloudException, RemoteException;

	/**
	 * Permet de recuperer les fichiers suivi par l'utilisateur qui on ete
	 * modifier depuis sa derniere connection
	 * @return liste des fichiers suivi et modifier
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	public List<CloudFile> getFichiersANotifier() throws CloudException, RemoteException;
}