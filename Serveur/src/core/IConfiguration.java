package core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Interface qui permet au client d’obtenir la configuration du serveur pour
 * pouvoir utiliser les informations de configurations nécessaires.
 */
public interface IConfiguration extends Remote {


	/**
	 * Permet de construire un fichier XML à partir d’une map entre
	 * EConfiguration et Boolean et des attributs de la classe en utilisant un
	 * parseur XML.
	 * 
	 * @param quota
	 *            : Quota max de l’utilisateur.
	 * @param nbUtilisateurs
	 *            : nbUtilisateur max enregistré.
	 * @param bufferSize
	 *            : Taille du buffer.
	 * @param nbFichiersPartages
	 *            : nb max de fichiers que peut partager un utilisateur.
	 * @param tailleMaxFichier .
	 * @param tailleMaxNomFichier .
	 * @param lgMinPassword .
	 * @param nbMaxAbonnements
	 *            : nb max d’abonnement pour un utilisateur.
	 * @param adresseBDD .
	 * @param nomBDD .
	 * @param userBDD .
	 * @param passBDD .
	 * @param adresseMail
	 *            : Mail utilisé par le mailer pour l’envoi des notifications.
	 * @param passwordMail .
	 * @param host
	 *            : Host SMTP.
	 * @param port .
	 * @param map
	 *            : Map contenant les EConfigurations + boolean associées.
	 * @throws RemoteException
	 *             .
	 */
	void save(double quota, int nbUtilisateurs, double bufferSize, int nbFichiersPartages, double tailleMaxFichier,
			int tailleMaxNomFichier, int lgMinPassword, int nbMaxAbonnements, String adresseBDD, String nomBDD,
			String userBDD, String passBDD, String adresseMail, String passwordMail, String UploadPath, String host,
			int port, Map<EConfiguration, Boolean> map) throws RemoteException;

	/**
	 * Renvoie la valeur de la configuration pour l'énumération passée en
	 * paramètre
	 * 
	 * @param config
	 *            : la configuration actuelle
	 * @return la valeur de la configuration si elle est active ou pas
	 * @throws RemoteException
	 *             .
	 */
	boolean getConfigValue(EConfiguration config) throws RemoteException;

	/**
	 * 
	 * @return la map contenant les différentes configurations
	 * @throws RemoteException
	 *             .
	 */
	Map<EConfiguration, Boolean> getConfig() throws RemoteException;

	/**
	 * 
	 * @return le quota maximum configurer sur le serveur
	 * @throws RemoteException
	 *             .
	 */
	double getQuota() throws RemoteException;

	/**
	 * 
	 * @return le nombre d'utilisateurs maximum sur le cloud
	 * @throws RemoteException
	 *             .
	 */
	int getNbMaxUtilisateurs() throws RemoteException;

	/**
	 * 
	 * @return la taille buffer pour l'upload de fichier
	 * @throws RemoteException
	 *             .
	 */
	int getBufferSize() throws RemoteException;

	/**
	 * 
	 * @return le nombre maximum de fichiers partages
	 * @throws RemoteException
	 *             .
	 */
	int getMaxFichiersPartages() throws RemoteException;

	/**
	 * 
	 * @return la taille maximale d'un fichier
	 * @throws RemoteException
	 *             .
	 */
	double getTailleMaxFichier() throws RemoteException;

	/**
	 * 
	 * @return la longueur max du nom d'un fichier
	 * @throws RemoteException
	 *             .
	 */
	int getTailleMaxNomFichier() throws RemoteException;

	/**
	 * 
	 * @return la taille min du mot de passe
	 * @throws RemoteException
	 *             .
	 */
	int getLgMinPassword() throws RemoteException;

	/**
	 * 
	 * @return le nombre max d'abonnements possible
	 * @throws RemoteException
	 *             .
	 */
	int getNbMaxAbonnements() throws RemoteException;

	/**
	 * 
	 * @return le nombre max de partage possible
	 * @throws RemoteException
	 *             .
	 */
	int getNbMaxPartage() throws RemoteException;

	/**
	 * 
	 * @return l'adresse de la BDD
	 * @throws RemoteException
	 *             .
	 */
	String getAdresseBDD() throws RemoteException;

	/**
	 * 
	 * @return le nom de la BDD
	 * @throws RemoteException
	 *             .
	 */
	String getNomBDD() throws RemoteException;

	/**
	 * 
	 * @return l'utilisateur de la BDD
	 * @throws RemoteException
	 *             .
	 */
	String getUserBDD() throws RemoteException;

	/**
	 * 
	 * @return le mot de passe de la BDD
	 * @throws RemoteException
	 *             .
	 */
	String getPassBDD() throws RemoteException;

	/**
	 * 
	 * @return l'adresse mail utilisée pour le mailer
	 * @throws RemoteException
	 *             .
	 */
	String getAdresseMail() throws RemoteException;

	/**
	 * 
	 * @return le mot de passe du mail pour le mailer
	 * @throws RemoteException
	 *             .
	 */
	String getPasswordMail() throws RemoteException;

	/**
	 * 
	 * @return le nom de l'host du mailer
	 * @throws RemoteException
	 *             .
	 */
	String getHostMailer() throws RemoteException;

	/**
	 * 
	 * @return le nom de l'host du mailer
	 * @throws RemoteException
	 *             .
	 */
	int getPortMailer() throws RemoteException;

	/**
	 * 
	 * @return le chemin choisi pour l'emplacement des uploads
	 * @throws RemoteException
	 *             .
	 */
	String getUploadPath() throws RemoteException;
	/**
	 * 
	 * @return duree de conservation d'un fichier en corbeille.
	 * @throws RemoteException .
	 *             .
	 */
	int getDureeCorbeille() throws RemoteException;
}