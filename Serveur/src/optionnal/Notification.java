package optionnal;

import core.*;

import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 
 * @author Groupe6
 *
 */
public class Notification{
	IMailer mailer;
	public Notification() throws RemoteException {
		mailer = new Mailer();
	}
	/**
     * Envoyer un mail à tous les utilisateurs abonnés au fichier fic.
     * @param fic : nom du fichier modifié.
     */
	public void sendMailNotification(CloudFile fic){
		if(fic == null){
			throw new CloudException("Fichier null");
		}
		try {
			ICloudFile parent = fic;
			String sujet = "CloudFast : Modification d'un fichier";
			do {
				for (Utilisateur utilisateur : parent.getAbonnes()) {
				String message = "Le fichier" + parent.getNomVirtuel() + "a été modifié le: " + parent.getDateModification().toString();
				mailer.EnvoyerMail(utilisateur.getEmail(), sujet, message);
			}
			parent = parent.getParent();
			}while (parent != null);
		} catch (Exception e) {
			throw new CloudException("Erreur base de données.");
		}		
	}
	
	/**
     * Permet d’abonner un utilisateur à un fichier.
     * @param nouvelAbonne : L’utilisateur qui souhaite s'abonner.
     * @param cloudFile : Nom du fichier auquel s'abonner.
     */
	public void ajoutAbonnement(Utilisateur nouvelAbonne, CloudFile cloudFile){
		if(nouvelAbonne == null || cloudFile ==null){
			throw new CloudException("Erreur: Paramètres null.");
		}
		try {
			if(Serveur.configuration.getNbMaxAbonnements()>0 && nombreAbonnement(nouvelAbonne)<Serveur.configuration.getNbMaxAbonnements()){
				String query = "INSERT INTO EtreAbonne(ID_Utilisateur, ID_Fichier) VALUES(?,?)";
				PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
				preparedStatement.setInt(1, nouvelAbonne.getID());
				preparedStatement.setInt(2, cloudFile.getID());
				preparedStatement.executeUpdate();
				cloudFile.ajouteAbonne(nouvelAbonne);
			}else{
				throw new CloudException("Erreur abonnement : Nombre maximal d'abonnements atteint.");
			}
		} catch (Exception e) {
			throw new CloudException("Erreur base de données. Contactez l'adminstrateur.");
		}
	}
	/**
     * Permet de supprimer un abonnement pour un utilisateur
     * @param util : Utilisateur qui souhaite supprimer son abonnement
     * @param fic : Fichier qui ne sera plus lié à l'utilisateur par un abonnement
     * @throws CloudException .
     */
	public void effacerAbonnement(Utilisateur util, CloudFile fic) throws CloudException{
		if(util == null || fic == null){
			throw new CloudException("Erreur: Paramètres null.");
		}
		try {
			String query = "DELETE FROM EtreAbonne WHERE ID_Fichier = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, fic.getID());
			preparedStatement.executeUpdate();
			fic.retireAbonne(util);
		} catch (Exception e) {
			throw new CloudException("Erreur base de données. Contactez l'adminstrateur.");
		}
	}
	/**
     * Permet d’obtenir le nombre d’abonnements pour un utilisateur
     * @param util : Utilisateur pour lequel on souhaite connaître son nombre d'abonnements.
     */
	private int nombreAbonnement(Utilisateur util){
		if(util == null){
			throw new CloudException("Erreur: Utilisateur doit être non null.");
		}
		int total;
		try{
			String query = "SELECT COUNT(ID_Fichier) AS total FROM EtreAbonne WHERE ID_Utilisateur = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, util.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			rSet.next();
			total = rSet.getInt("total");
		} catch(Exception e){
			throw new CloudException("Erreur base de données. Contactez l'adminstrateur.");
		}
		return total;
	}
	/**
	 * cette fonction parcours tous les fichiers auxquels l'utilisateur actuel est abonné
	 * @param util : utilisateur qu'on souhaite savoir ses abonnements 
	 * @return fic : Liste des abonnements de l'utilisateur
	 */
	public List<CloudFile> listeAbonnement(Utilisateur util){
		if(util == null){
			throw new CloudException("Erreur: Utilisateur doit être non null.");
		}
		ArrayList<CloudFile> fic = new ArrayList<>();
		try {
			String query ="SELECT ID_Fichier, ID_Type FROM EtreAbonne JOIN Fichier on Fichier.ID=EtreAbonne.ID_Fichier WHERE ID_Utilisateur = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, util.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			while(rSet.next()){
				int idFichier = rSet.getInt("ID_Fichier");
				EType type = EType.values()[rSet.getInt("ID_Type") -1];
				if(type == EType.Repertoire) fic.add(new Dossier(idFichier, null));
				else fic.add(new Fichier(idFichier, null));
			}
		} catch (Exception e) {
			throw new CloudException("Erreur Base de données. Contactez l'adminstrateur.");
		}
		return fic;
	}
	/**
	 * elle compare les deux dates, si le test est bon, le fichier concerné est ajouté à
	 * la liste
	 * @param util : utilisateur qu'on souhaite notifier
	 * @return notifications : Liste des nouvelles notifications propres à l'utilisateur
	 */
	public List<CloudFile> getNouvellesNotifications(Utilisateur util){
		if(util == null){
			throw new CloudException("Erreur: Utilisateur doit être non null.");
		}
		Date dateModification;
		Date dateConnection = util.getDerniereConnexion();
		List<CloudFile> notifications = new ArrayList<>();
		List<CloudFile> abonnements = listeAbonnement(util);
		for(int i = 0;i<abonnements.size();i++){
			dateModification = abonnements.get(i).getDateModification();
			if(dateConnection.compareTo(dateModification)<0){
				notifications.add(abonnements.get(i));
			}
		}
		return notifications;
	}
}