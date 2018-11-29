package core;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import optionnal.Partage;

public class CloudClient extends UnicastRemoteObject implements ICloudClient {

	private static final long serialVersionUID = 8795427502549670067L;
	private Utilisateur utilisateur;
	private Dossier racine;
	private Dossier corbeille;
	private Dossier partage;
	private Partage modulePartage;

	/**
	 * @param utilisateur
	 *            : Nom de l’utilisateur courant pour cette arborescence
	 * @throws RemoteException
	 *             .
	 */
	public CloudClient(Utilisateur utilisateur) throws RemoteException {
		super();
		this.utilisateur = utilisateur;
		corbeille = resoudreDossier("corbeille_" + utilisateur.getLogin());
		racine = resoudreDossier("racine_" + utilisateur.getLogin());
		if (Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE)) {
			modulePartage = new Partage();
			partage = new Dossier(modulePartage.getFichiersPartages(this.utilisateur), this.utilisateur);
		}
		purgeCorbeille(corbeille);
	}

	public Dossier getRacine() {
		return racine;
	}

	public Dossier getCorbeille() {
		return corbeille;
	}

	public Dossier getPartage() {
		return partage;
	}

	/**
	 * permet de retirer un fichier au dossier partage du cloudClient
	 * 
	 * @param cloudFile
	 *            : Fichier a retirer
	 */
	public void supprimerFichierPartage(CloudFile cloudFile) {
		if (cloudFile == null)
			throw new CloudException("Erreur : paramètre null (CloudFile).");
		if (!this.partage.getContenu().contains(cloudFile))
			throw new CloudException("Erreur : Vous ne pouvez retirer un accès qui n'existe pas !");
		this.partage.getContenu().remove(cloudFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudClient#getUtilisateur()
	 */
	@Override
	public Utilisateur getUtilisateur() {
		return this.utilisateur;
	}

	/**
	 * Point d’entrer dans l’application.Permet de résoudre le dossier dont le
	 * nom est passé en paramètre en récupérant son ID puis en mettant en place
	 * un dossier avec celui-ci + Résolution de l’arborescence du dossier.
	 * 
	 * @param :
	 *            nom : Correspond au nom du dossier à résoudre.
	 * @return : Le dossier résolu grâce à la création de son arborescence (voir
	 *         classe Dossier).
	 * @throws RemoteException
	 *             .
	 */
	private Dossier resoudreDossier(String nom) throws RemoteException {
		Dossier dossier = null;
		try {
			String query = "Select ID From Fichier Where nomVirtuel=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, nom);
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() == false)
				throw new CloudException(
						"Compte non valide, Intégrité des dossiers de l'utilisateur corrompus. Contactez l'administrateur.");
			rSet.next();
			int id = rSet.getInt("ID");
			dossier = new Dossier(id, null);
		} catch (CloudException e) {
			throw e;
		} catch (SQLException e) {
			throw new CloudException("Erreur base de données. Contactez l'administrateur.");
		}
		return dossier;
	}

	/**
	 * Permet de supprimer les fichiers vieux de 1 mois de la corbeille.
	 */
	@SuppressWarnings("unchecked")
	private void purgeCorbeille(Dossier root) throws RemoteException {
		String query;
		PreparedStatement preparedStatement;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -(Serveur.configuration.getDureeCorbeille()));
		Date date = calendar.getTime();
		for (CloudFile cloudFile : (List<CloudFile>) root.getContenu().clone()) {
			if (cloudFile.getDateModification().compareTo(date) < 0) {
				root.getContenu().remove(cloudFile);
				try {
					if (cloudFile.getType() != EType.Repertoire && cloudFile.getType() != EType.Corbeille) {
						new File(Serveur.configuration.getUploadPath() + cloudFile.getNomPhysique()).delete();

						query = "DELETE FROM Contient where ID_Contenu=?";
						preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
						preparedStatement.setInt(1, cloudFile.getID());
						preparedStatement.executeUpdate();
						
						query = "DELETE FROM Fichier where ID=?";
						preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
						preparedStatement.setInt(1, cloudFile.getID());
						preparedStatement.executeUpdate();
					} else {
								purgeCorbeille((Dossier)cloudFile);

								query = "DELETE FROM Contient where ID_Contenu=?";
								preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
								preparedStatement.setInt(1, cloudFile.getID());
								preparedStatement.executeUpdate();

								query = "DELETE FROM Fichier where ID=?";
								preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
								preparedStatement.setInt(1, cloudFile.getID());
								preparedStatement.executeUpdate();
							}
					}
				 catch (SQLException e) {
					throw new CloudException("Erreur base de données. Contactez l'administrateur.");
				}

			}
		}
	}

}