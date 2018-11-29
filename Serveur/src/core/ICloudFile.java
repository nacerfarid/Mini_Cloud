package core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe representant la structure de donnée du cloud. Permet la navigation
 * vers le proprietaire, les accesseurs et les abonnés. Possede les getters et
 * les setters necessaires à l'usage de la classe. Possede une méthode de
 * notification des modifications par mail aux abonnés. Possede des methodes de
 * gestion du partage et d'abonnement, utilisée uniquement via les classes
 * Partage et Notification.
 * 
 * @author Groupe6
 *
 */

public interface ICloudFile extends Remote {

	/**
	 * Permet de notifier la modification d'une cloudFile
	 * 
	 * @throws RemoteException
	 *             .
	 */
	void NotifierModificationParMail() throws RemoteException;

	/**
	 * Permet de modifier un droit d'acces de l'utilisateur
	 * 
	 * @param utilisateur
	 *            : utilisateur qu'on veut lui changer l'accès
	 * @param readOnly
	 *            : mode d'accès
	 * @throws RemoteException
	 *             .
	 */
	void modifierModeAcces(String utilisateur, boolean readOnly) throws RemoteException;

	/**
	 * Permet de modifier un droit d'acces des utilisateurs
	 * 
	 * @param utilisateurs
	 *            : liste d'utilisateurs
	 * @param readOnly
	 *            : mode d'accès
	 * @throws RemoteException
	 *             .
	 */
	void modifierModeAcces(List<String> utilisateurs, boolean readOnly) throws RemoteException;

	/**
	 * 
	 * @return l'utilisateur proprietaire du CloudFile
	 * @throws RemoteException
	 *             .
	 */
	IUtilisateur getProprietaire() throws RemoteException;

	/**
	 * Permet d'ajouter un acces a l'utilisateur.
	 * 
	 * @param utilisateur
	 *            : Utilisateur a qui on donne l'acces
	 * @param readOnly
	 *            : Mode d'acces
	 * @throws RemoteException
	 *             .
	 */
	void ajouterUtilisateurAyantAcces(Utilisateur utilisateur, Boolean readOnly) throws RemoteException;

	/**
	 * Permet de retirer un acces a l'utilisateur.
	 * 
	 * @param utilisateur
	 *            : Utilisateur a qui on donne l'acces
	 * @throws RemoteException
	 *             .
	 */
	void supprimerUtilisateurAyantAcces(Utilisateur utilisateur) throws RemoteException;

	/**
	 * 
	 * @return la liste des Utilisateurs abonnes au CloudFile
	 * @throws RemoteException
	 *             .
	 */
	List<Utilisateur> getAbonnes() throws RemoteException;

	/**
	 * Permet de partager une CloudFile
	 * 
	 * @param utilisateurs
	 *            : liste de Login ou Email des utilisateurs auquels on partage
	 *            l'acces.
	 * @param readOnly
	 *            : Mode d'acces.
	 * @throws RemoteException
	 *             .
	 */
	void partager(List<String> utilisateurs, boolean readOnly) throws RemoteException;

	/**
	 * Permet de retirer un partage d'une CloudFile
	 * 
	 * @param utilisateurs
	 *            : liste de Login ou Email des utilisateurs auquels on partage
	 *            l'acces.
	 * @throws RemoteException
	 *             .
	 */
	void supprimerPartage(List<String> utilisateurs) throws RemoteException;

	/**
	 * Permet de retirer un partage d'une CloudFile
	 * 
	 * @param utilisateur
	 *            : liste de Login ou Email des utilisateurs auquels on partage
	 *            l'acces.
	 * @throws RemoteException
	 *             .
	 */
	void supprimerPartage(String utilisateur) throws RemoteException;

	/**
	 * Permet de partager une CloudFile
	 * 
	 * @param utilisateur
	 *            : Login ou Email de l'utilisateurs auquel on partage l'acces.
	 * @param readOnly
	 *            : Mode d'acces.
	 * @throws RemoteException
	 *             .
	 */
	void partager(String utilisateur, boolean readOnly) throws RemoteException;

	/**
	 * Permet d'ajouter un abonnement d'une liste d'utilisateur
	 * 
	 * @param nouveauxAbonnes
	 *            : liste des nouveaux abonnés
	 * @throws RemoteException
	 *             .
	 */
	void ajouteAbonnes(List<Utilisateur> nouveauxAbonnes) throws RemoteException;

	

	/**
	 * Permet d'ajouter un abonnement pour l'utilisateur indiqué
	 * 
	 * @param abonne
	 *            : nouveau abonné
	 * @throws RemoteException
	 *             .
	 */
	void ajouteAbonne(Utilisateur abonne) throws RemoteException;
	
	/**
	 * Permet de retirer un abonnement pour l'utilisateur indiqué
	 * 
	 * @param abonne
	 *            : nouveau abonné
	 * @throws RemoteException
	 *             .
	 */
	void retireAbonne(Utilisateur abonne) throws RemoteException;
	/**
	 * Permet de retirer un abonnement d'une liste d'utilisateur
	 * 
	 * @param nouveauxAbonnes
	 *            : liste des nouveaux abonnés
	 * @throws RemoteException
	 *             .
	 */
	void retireAbonnes(List<Utilisateur> abonnes) throws RemoteException;

	/**
	 * 
	 * @return la liste des utilisateurs ayant acces au CloudFile
	 * @throws RemoteException
	 *             .
	 */
	Map<Utilisateur, Boolean> getUtilisateursAyantAcces() throws RemoteException;

	/**
	 * 
	 * @return true si le type du CloudFile est un dossier, false sinon
	 * @throws RemoteException
	 *             .
	 */
	boolean estDossier() throws RemoteException;

	/**
	 * 
	 * @return true si le type du CloudFile n'est pas un dossier, false sinon
	 * @throws RemoteException
	 *             .
	 */
	boolean estFichier() throws RemoteException;

	/**
	 * 
	 * @return le CloudFile parent.
	 * @throws RemoteException
	 *             .
	 */
	IDossier getParent() throws RemoteException;

	/**
	 * change le parent du ClouFile dans la BDD
	 * 
	 * @param parent
	 *            : nouveau CloudFile parent
	 * @throws RemoteException
	 *             .
	 */
	void setParent(ICloudFile parent) throws RemoteException;

	/**
	 * 
	 * @return la date de creation du CloudFile
	 * @throws RemoteException
	 *             .
	 */
	Date getDateCreation() throws RemoteException;

	/**
	 * 
	 * @return la date de creation du CloudFile
	 * @throws RemoteException
	 *             .
	 */
	Date getDateModification() throws RemoteException;

	/**
	 * @param dateModif
	 *            : nouvelle date de modification du cloudFile
	 * @throws RemoteException
	 *             .
	 */
	void setDateModification(Date dateModif) throws RemoteException;

	/**
	 * 
	 * @return le nom virtuel du CloudFile
	 * @throws RemoteException
	 *             .
	 */
	String getNomVirtuel() throws RemoteException;

	/**
	 * @param nomVirtuel
	 *            : nouveau nom virtuel
	 * @throws RemoteException
	 *             .
	 */
	void setNomVirtuel(String nomVirtuel) throws RemoteException;

	/**
	 * 
	 * @return le nom physique du CloudFile
	 * @throws RemoteException
	 *             .
	 */
	String getNomPhysique() throws RemoteException;

	/**
	 * 
	 * @return l'ID de l'objet CloudFile
	 * @throws RemoteException
	 *             .
	 */
	int getID() throws RemoteException;

	/**
	 * @param nomPhysique
	 *            : nouveau nom physique
	 * @throws RemoteException
	 *           	/**
	 * Permet de s'abonner a un fichier.
	 */  
	void setNomPhysique(String nomPhysique) throws RemoteException;

	/**
	 * 
	 * @return la taille du CloudFile
	 * @throws RemoteException
	 *             .
	 */
	double getTaille() throws RemoteException;

	/**
	 * @param taille
	 *            : nouvelle taille
	 * @throws RemoteException .
	 *             .
	 */
	void setTaille(double taille) throws RemoteException;

	/**
	 * 
	 * @return le type du CloudFile
	 * @throws RemoteException .
	 *             .
	 */
	EType getType() throws RemoteException;

	/**
	 * @param type
	 *            : nouveau type du cloudFile
	 * @throws RemoteException .
	 *             .
	 */
	void setType(EType type) throws RemoteException;
	
	/**
	 * Permet de supprimer une cloudFile
	 * @throws RemoteException .
	 */
	void supprimer() throws RemoteException;
	/**
	 * Permet de supprimer une cloudFile
	 * @throws RemoteException .
	 */
	void restaurer() throws RemoteException;
	
	/**
	 * Permet de s'abonner à une cloudFile
	 * @param utilisateur : Utilisateur a abonner.
	 * @throws RemoteException .
	 */
	void abonner(int utilisateur) throws RemoteException;
	
	/**
	 * Permet de se désabonner à une cloudFile
	 * @param utilisateur : Utilisateur a désabonner.
	 * @throws RemoteException .
	 */
	void desabonner(Utilisateur utilisateur) throws RemoteException;
	
	

}