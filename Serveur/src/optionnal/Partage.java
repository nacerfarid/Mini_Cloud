package optionnal;

import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import core.*;

public class Partage {

	/**
	 * la fonction permet de donner l'acces au fichier à la liste d'utilisateurs
	 * 
	 * @param readOnly : droit d'acces pour le fichier
	 * @param file : fichier à partager
	 * @param utilisateurs : liste des utilisateurs avec qui on souhaite partager
	 * @throws CloudException .
	 * @throws RemoteException . 
	 */
	public void partagerAcces(boolean readOnly, CloudFile file, List<String> utilisateurs)
			throws CloudException, RemoteException {
		if (file == null || utilisateurs == null) {
			throw new CloudException("Erreur: Paramètres ne doivent pas être nulls.");
		}
		for (String u : utilisateurs) {
			partagerAcces(readOnly, file, u);
		}
	}

	/**
	 * la fonction permet de donner l'acces au fichier à l'utilisateur défini
	 * 
	 * @param readOnly : droit d'acces pour le fichier
	 * @param file : fichier à partager
	 * @param utilisateur : login ou Email de l'utilisateur avec qui on souhaite partager
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	public void partagerAcces(boolean readOnly, CloudFile file, String utilisateur)
			throws CloudException, RemoteException {
		if (file == null || utilisateur == null || utilisateur.equals(""))
			throw new CloudException("Erreur: Paramètres ne doivent pas être nuls.");
		try {
			Utilisateur user = resoudreUtilisateur(utilisateur);
			if (verifierAccesPresent(file, user))
				throw new CloudException("Erreur : Impossible de creer un acces déjà present.");
			int maxPartageParUtilisateur = Serveur.configuration.getNbMaxPartage();
			int maxPartageParFichier = Serveur.configuration.getMaxFichiersPartages();
			if (maxPartageParFichier != 0 && maxPartageParFichier <= nombrePartagesDuFichier(file))
				throw new CloudException("Erreur : Le fichier a atteind le nombre max de partage.");
			if (maxPartageParUtilisateur != 0 && maxPartageParUtilisateur <= nombrePartagesParUtilisateur(user))
				throw new CloudException("Erreur : Le fichier a atteind le nombre max de partage.");
			String query = "INSERT INTO AvoirAccesFichier VALUES (?,?,?)";
			PreparedStatement preparedStatement1 = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement1.setInt(1, user.getID());
			preparedStatement1.setInt(2, file.getID());
			preparedStatement1.setBoolean(3, readOnly);
			preparedStatement1.executeUpdate();
			user.getCloudClient().getPartage().getContenu().add(file);
			file.ajouterUtilisateurAyantAcces(user, readOnly);
		} catch (CloudException e) {
			throw e;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de donnée : Contactez l'administrateur.");
		}

	}

	/**
	 * Permet de recupere un utilisateur via son email ou son login
	 * @param utilisateur : Login ou email (String)
	 * @return Utilisateur 
	 */
	private Utilisateur resoudreUtilisateur(String utilisateur) {
		Utilisateur user = null;
		try {
			String query = "SELECT ID FROM Utilisateur WHERE login = ? OR email=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, utilisateur);
			preparedStatement.setString(2, utilisateur);
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.next())
				user = Utilisateur.getUtilisateur(rSet.getInt("ID"));
			else
				throw new CloudException("Erreur : Utilisateur introuvable.");
		} catch (Exception e) {
			throw new CloudException("Erreur Base de donnée : Contactez l'administrateur.");
		}
		return user;
	}

	/**
	 * la fonction renvoie la liste de partage de l'utilisateur
	 * 
	 * @param utilisateur : Login ou email (String)
	 * @return liste des fichiers partages de l'utilisateur
	 * @throws RemoteException .
	 */
	public List<CloudFile> getFichiersPartages(Utilisateur utilisateur) throws RemoteException {
		if (utilisateur == null) {
			throw new CloudException("Erreur: Utilisateur doit être non nul.");
		}
		return resoudrePartage(utilisateur);
	}

	/**
     * Permet d’enlever l’accès du fichier aux utilisateurs passés en paramètres.
     * @param file : fichier dont l’accès est à modifier.
     * @param utilisateurs : liste des utilisateurs dont l’accès sera supprimé.
     * @throws CloudException .
     * @throws RemoteException . 
     */
	public void enleverAcces(CloudFile file, List<String> utilisateurs) throws CloudException, RemoteException {
		if (file == null || utilisateurs == null) {
			throw new CloudException("Erreur: Paramètres ne doivent pas être nuls.");
		}
		for (String u : utilisateurs) {
			enleverAcces(file, u);
		}
	}

	/**
     * Permet d'enlever l’accès du fichier pour l'utilisateur passé en Paramètre.
     * @param file :  fichier dont l’accès est à modifier.
     * @param utilisateur :  utilisateur dont l’accès sera supprimé.
     * @throws CloudException  .
     * @throws RemoteException .
     */
	public void enleverAcces(CloudFile file, String utilisateur) throws CloudException, RemoteException {
		if (file == null || utilisateur == null || utilisateur.equals("")) {
			throw new CloudException("Erreur: Paramètres ne doivent pas être nulls.");
		}
		try {
			Utilisateur user = resoudreUtilisateur(utilisateur);
			if (!verifierAccesPresent(file, user))
				throw new CloudException("Erreur : Impossible de supprimer un acces non present.");
			String query = "DELETE FROM AvoirAccesFichier WHERE ID_Fichier = ? and ID_Utilisateur=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, file.getID());
			preparedStatement.setInt(2, user.getID());
			preparedStatement.executeUpdate();
			user.getCloudClient().supprimerFichierPartage(file);
			file.supprimerUtilisateurAyantAcces(user);
		} catch (SQLException e) {
			throw new CloudException("Erreur base de donnees.");
		}
	}

	/**
     * Permet de  renvoyer le nombre de fichiers partagés avec l'utilisateur
     * @param util : Utilisateur dont on souhaite connaître le nombre de partages
     * @return : Le nombre de partage total pour cet utilisateur
     */
	private int nombrePartagesParUtilisateur(Utilisateur util) {
		if (util == null)
			throw new CloudException("Erreur: Utilisateur doit être non null.");

		int total;
		try {
			String query = "SELECT COUNT(ID_Fichier) FROM AvoirAccesFichier WHERE ID_Utilisateur = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, util.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			rSet.next();
			total = rSet.getInt(1);
		} catch (Exception e) {
			throw new CloudException("Erreur base de données. Contactez l'adminstrateur.");
		}
		return total;
	}
	/**
     * Permet de renvoyer le nombre de partage du fichier passé en paramètre
     * @param fic : CloudFile dont on souhaite connaître son nombre de partage
     * @return : Le nombre de partage concernant le fichier
     */
	private int nombrePartagesDuFichier(CloudFile fic) {
		if (fic == null) {
			throw new CloudException("Erreur: fichier doit être non null.");
		}
		int total;
		try {
			String query = "SELECT COUNT(*) FROM AvoirAccesFichier where ID_Fichier = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, fic.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			rSet.next();
			total = rSet.getInt(1);
		} catch (Exception e) {
			throw new CloudException("Erreur base de données. Contactez l'adminstrateur.");
		}
		return total;
	}
	/**
     * Permet de construire l’arborescence des partages de l’utilisateurs en allant chercher dans la BDD 
     * les fichiers dont l’utilisateur à un accès puis en ajoutant ceux-ci dans 
     * la liste de CloudFile en les instanciant en fonction de leurs types (Dossier ou fichier).
     * @param utilisateur : Utilisateur dont on souhaite l'arborescence de ses partages.
     * @return : L’arborescence des partages de l’utilisateur.
     * @throws RemoteException .
     */
	private List<CloudFile> resoudrePartage(Utilisateur utilisateur) throws RemoteException {
		List<CloudFile> listePartage = new ArrayList<>();
		try {
			String query = "Select ID_Fichier, readOnly From AvoirAccesFichier Where ID_Utilisateur=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, utilisateur.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() != false) {
				while (rSet.next()) {
					query = " Select ID_Type From Fichier Where ID=?";
					preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
					preparedStatement.setInt(1, rSet.getInt("ID_Fichier"));
					ResultSet rSet2 = preparedStatement.executeQuery();
					if (rSet2.isBeforeFirst() == false)
						throw new CloudException("Erreur : Impossible de creer l'arborscence.");
					rSet2.next();
					EType type = EType.values()[rSet2.getInt("ID_Type") - 1];
					if (type == EType.Repertoire)
						listePartage.add(new Dossier(rSet.getInt("ID_Fichier"), rSet.getBoolean("readOnly")));
					else
						listePartage.add(new Fichier(rSet.getInt("ID_Fichier"), rSet.getBoolean("readOnly")));
				}
			}
		} catch (CloudException e) {
			throw e;
		} catch (SQLException e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		} catch (Exception e) {
			throw new CloudException("Erreur : Contactez l'administrateur.");
		}
		return listePartage;
	}

	/**
	 * Methode pour verifier si un acces est déjà disponible
	 * @return true si un acces est present, false sinon.
	 */
	private boolean verifierAccesPresent(CloudFile cloudFile, Utilisateur utilisateur) {
		try {
			String query = "Select ID_Utilisateur, ID_Fichier from AvoirAccesFichier where ID_Utilisateur = ? and ID_Fichier=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, utilisateur.getID());
			preparedStatement.setInt(2, cloudFile.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			return rSet.isBeforeFirst();
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/**
	 * Modifie l'acces de la cloudFile pour les Utilisateurs avec la valeur readOnly
	 * @param utilisateurs : liste d'utilisateur dont l'acces sera modifié
	 * @param cloudFile : Le fihcier dont l'acces sera modifié
	 * @param readOnly : nouvelle valeur d'acces
	 * @throws RemoteException .
	 */
	public void modifierModeAcces(List<String> utilisateurs, CloudFile cloudFile, boolean readOnly)
			throws RemoteException {
		if (utilisateurs == null || utilisateurs.equals("")) {
			throw new CloudException("Erreur: Paramètres ne doivent pas être nuls.");
		}
		for (String u : utilisateurs) {
			modifierModeAcces(u, cloudFile, readOnly);
		}
	}
	
	/**
	 * Modifie l'acces de la cloudFile pour l'Utilisateur avec la valeur readOnly
	 * @param utilisateur : l'utilisateur dont l'acces sera modifié
	 * @param cloudFile : Le fichier dont l'acces sera modifié
	 * @param readOnly : nouvelle valeur d'acces
	 * @throws RemoteException .
	 */
	public void modifierModeAcces(String utilisateur, CloudFile cloudFile, boolean readOnly) throws RemoteException {
		Utilisateur user = resoudreUtilisateur(utilisateur);
		if (!verifierAccesPresent(cloudFile, user))
			throw new CloudException("Erreur : Impossible de supprimer un acces non present.");
		if (cloudFile.getUtilisateursAyantAcces().get(user).equals(readOnly))
			throw new CloudException("Erreur : les droits d'acces sont déjà defini comme demandé.");
		try {
			String query = "Update AvoirAccesFichier set readOnly=? WHERE ID_Fichier = ? and ID_Utilisateur=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setBoolean(1, readOnly);
			preparedStatement.setInt(2, cloudFile.getID());
			preparedStatement.setInt(3, user.getID());
			preparedStatement.executeUpdate();
			cloudFile.modifierAccesUtilisateurAyantAcces(user, readOnly);
			
		} catch (SQLException e) {
			throw new CloudException("Erreur base de donnees.");
		}

	}
}