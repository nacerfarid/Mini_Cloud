package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Dossier extends CloudFile implements IDossier {
	private ArrayList<CloudFile> contenu;

	/**
	 * Constructeur du dossier partage.
	 * @param contenu
	 * @param proprietaire
	 * @throws RemoteException .
	 */
	public Dossier(List<CloudFile> contenu, Utilisateur proprietaire) throws RemoteException {
		super();
		this.nomVirtuel = "Dossier Partagé";
		this.type = EType.Repertoire;
		this.contenu = (ArrayList<CloudFile>) contenu;
		this.proprietaire = proprietaire;

	}

	public Dossier(int id, IDossier parent) throws RemoteException {
		super(id, parent);
		contenu = resoudreArborescence();
	}

	public Dossier(int id, boolean readOnly) throws RemoteException {
		super(id, readOnly);
		contenu = resoudreArborescence(readOnly);
	}

	private static final long serialVersionUID = -7964046163264652815L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.IDossier#getContenu()
	 */
	@Override
	public ArrayList<CloudFile> getContenu() {
		return contenu;
	}

	/**
	 * Methode interne permettant de creer l'arborescence. Resoud le contenu
	 * d'un repertoire.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	private ArrayList<CloudFile> resoudreArborescence() throws RemoteException {
		ArrayList<CloudFile> result = new ArrayList<>();
		try {
			String query = "Select ID_Contenu From Contient Where ID_Conteneur=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, this.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() != false) {
				while (rSet.next()) {
					query = " Select ID_Type From Fichier Where ID=?";
					preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
					preparedStatement.setInt(1, rSet.getInt("ID_Contenu"));
					ResultSet rSet2 = preparedStatement.executeQuery();
					if (rSet2.isBeforeFirst() == false)
						throw new CloudException("Erreur : Impossible de creer l'arborscence.");
					rSet2.next();
					EType type = EType.values()[rSet2.getInt("ID_Type") - 1];
					if (type == EType.Repertoire)
						result.add(new Dossier(rSet.getInt("ID_Contenu"), this));
					else
						result.add(new Fichier(rSet.getInt("ID_Contenu"), this));
				}
				for (CloudFile cloudFile : result)
					this.taille += cloudFile.taille;
			}
		} catch (CloudException e) {
			throw e;
		} catch (SQLException e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		} catch (Exception e) {
			throw new CloudException("Erreur : Contactez l'administrateur.");
		}
		return result;
	}

	/**
	 * Methode interne permettant de creer l'arborescence. Resoud le contenu
	 * d'un repertoire. Prend en compte le mode d'acces.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	private ArrayList<CloudFile> resoudreArborescence(boolean readOnly) throws RemoteException {
		ArrayList<CloudFile> result = new ArrayList<>();
		try {
			String query = "Select ID_Contenu From Contient Where ID_Conteneur=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, this.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() != false) {
				while (rSet.next()) {
					query = " Select ID_Type From Fichier Where ID=?";
					preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
					preparedStatement.setInt(1, rSet.getInt("ID_Contenu"));
					ResultSet rSet2 = preparedStatement.executeQuery();
					if (rSet2.isBeforeFirst() == false)
						throw new CloudException("Erreur : Impossible de creer l'arborscence.");
					rSet2.next();
					EType type = EType.values()[rSet2.getInt("ID_Type") - 1];
					if (type == EType.Repertoire)
						result.add(new Dossier(rSet.getInt("ID_Contenu"), readOnly));
					else
						result.add(new Fichier(rSet.getInt("ID_Contenu"), readOnly));
				}
				for (CloudFile cloudFile : result)
					this.taille += cloudFile.taille;
			}
		} catch (CloudException e) {
			throw e;
		} catch (SQLException e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		} catch (Exception e) {
			throw new CloudException("Erreur : Contactez l'administrateur.");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.IDossier#creerDossier()
	 */
	@Override
	public void creerDossier(String nom, IUtilisateur proprietaire) throws RemoteException, CloudException {
		if (nom.contains("racine_") || nom.contains("corbeille_"))
			throw new CloudException("Erreur : Nom de dossier interdit.");
		CloudFile result = null;
		for (CloudFile cFile : this.contenu) {
			if (cFile.getNomVirtuel().equals(nom))
				throw new CloudException("Erreur : Dossier déjà existant !");
		}
		try {
			String query = "INSERT INTO Fichier (dateCreation, dateModification, nomVirtuel, taille, ID_Type, ID_Proprietaire)"
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			Timestamp timestamp = new Timestamp(new Date().getTime());
			preparedStatement.setTimestamp(1, timestamp);
			preparedStatement.setTimestamp(2, timestamp);
			preparedStatement.setString(3, nom);
			preparedStatement.setDouble(4, 0);
			preparedStatement.setInt(5, 1);
			preparedStatement.setInt(6, proprietaire.getID());
			preparedStatement.execute();
			ResultSet rSet = preparedStatement.getGeneratedKeys();
			rSet.next();
			int lastInsert = rSet.getInt(1);
			result = new Dossier(lastInsert, this);
			query = "INSERT INTO Contient (ID_Conteneur, ID_Contenu) VALUES (?,?)";
			preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, this.getID());
			preparedStatement.setInt(2, lastInsert);
			preparedStatement.execute();
			this.contenu.add(result);
		} catch (Exception e) {
			throw new CloudException("Erreur : Impossible de creer le dossier.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.IDossier#upload()
	 */
	public void upload(String name,byte[] in, IUtilisateur proprietaire) throws CloudException, IOException {
		if (name.contains("racine_") || name.contains("corbeille_"))
			throw new CloudException("Erreur : Nom de fichier interdit.");
		if (Serveur.configuration.getTailleMaxFichier()>0 && in.length > Serveur.configuration.getTailleMaxFichier()*1000000000)
			throw new CloudException("Erreur : Taille du fichier trop importante.");
		if (Serveur.configuration.getTailleMaxNomFichier()>0 && name.length() > Serveur.configuration.getTailleMaxNomFichier())
			throw new CloudException("Erreur : Nom du fichier trop long.");
		Double tailleTotal = proprietaire.getCloudClient().getRacine().getTaille();
		CloudFile cFile = contientFichier(name);
		if (cFile != null) {
			if (Serveur.configuration.getQuota()!=0 && tailleTotal + in.length - cFile.getTaille() > Serveur.configuration.getQuota()*1000000000)
				throw new CloudException("Erreur : Dépassement de quota.");
			else
				modifierFichier(in, cFile);

		} else {
			if (Serveur.configuration.getQuota()!=0  && tailleTotal + in.length > Serveur.configuration.getQuota()*1000000000)
				throw new CloudException("Erreur : Dépassement de quota.");
			else
				ajouterFichier(name,in, proprietaire);
		}
		IDossier tmp = this;
		do  {
			double taille = 0;
			for (CloudFile cloudFile : tmp.getContenu()) {
				taille += cloudFile.getTaille();
			}
			tmp.setTaille(taille);
			tmp=tmp.getParent();
		} while (tmp != null);
	}

	/**
	 * la fonction va créer l'enregistrement dans la base de données ainsi la
	 * copie vers l'emplacement souhaité
	 * 
	 * @param file
	 * @param proprietaire
	 * @throws CloudException
	 * @throws IOException
	 */
	private void ajouterFichier(String name,byte[] in, IUtilisateur proprietaire) throws CloudException, IOException {
		String uploadPath = Serveur.configuration.getUploadPath();
		Date dateCreation = new Date();
		String nomPhysique = Hashage.hashSHA2(name + dateCreation.toString());
		OutputStream outputStream = new FileOutputStream(new File(uploadPath + nomPhysique));
		outputStream.write(in, 0, in.length);
		String query = "INSERT INTO Fichier (dateCreation, dateModification, nomVirtuel, taille, ID_Type, ID_Proprietaire, nomPhysique)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		int lastInsert = 0;
		try {
			Timestamp timestamp = new Timestamp(dateCreation.getTime());
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setTimestamp(1, timestamp);
			preparedStatement.setTimestamp(2, timestamp);
			preparedStatement.setString(3, name);
			preparedStatement.setDouble(4, in.length);
			preparedStatement.setInt(5, trouverType(name).ordinal()+1);
			preparedStatement.setInt(6, proprietaire.getID());
			preparedStatement.setString(7, nomPhysique);
			preparedStatement.execute();
			ResultSet rSet = preparedStatement.getGeneratedKeys();
			rSet.next();
			lastInsert = rSet.getInt(1);
			query = "INSERT INTO Contient (ID_Conteneur, ID_Contenu) VALUES (?,?)";
			preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, this.getID());
			preparedStatement.setInt(2, lastInsert);
			preparedStatement.execute();
			this.contenu.add(new Fichier(lastInsert, this));
			this.NotifierModificationParMail();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			outputStream.close();
		}
	}

	/**
	 * la fonction va remplacer le fichier ainsi que mettre à jour la date et la
	 * taille dans la base de données
	 * 
	 * @param file
	 * @param dateModification
	 * @throws IOException
	 */
	private void modifierFichier(byte[] in, CloudFile cfile) throws IOException {
		String uploadPath = Serveur.configuration.getUploadPath();
		OutputStream outputStream = new FileOutputStream(new File(uploadPath+cfile.getNomPhysique()));
		outputStream.write(in, 0, in.length);
		try {
			Date dateModification = new Date();
			String query = "UPDATE Fichier SET dateModification = ?, taille = ? WHERE ID = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setTimestamp(1, new Timestamp(dateModification.getTime()));
			preparedStatement.setDouble(2, in.length);
			preparedStatement.setInt(3, cfile.getID());
			preparedStatement.executeUpdate();
			this.setTaille(in.length);
			this.setDateModification(new Timestamp(dateModification.getTime()));
			cfile.NotifierModificationParMail();
		} catch (SQLException e) {
			throw new CloudException("Erreur base de données.");
		} finally {
			outputStream.close();
		}
	}

	/**
	 * verifie si un fichier est present dans ce dossier
	 * 
	 * @param nom
	 * @return
	 */
	private CloudFile contientFichier(String nom) {
		CloudFile result = null;
		for (CloudFile cFile : this.contenu) {
			if (cFile.getNomVirtuel().equals(nom))
				result = cFile;
		}
		return result;
	}

	/**
	 * la fonction revoie le type de fichier selon son extension
	 * 
	 * @param nomFichier
	 * @return
	 */
	private static EType trouverType(String nomFichier) {
		EType type = null;
		List<String> documents = new ArrayList<String>() {
			/****/
			private static final long serialVersionUID = -45973167587425734L;
			{
				add("pdf");
				add("txt");
				add("doc");
				add("docx");
				add("c");
				add("java");
				add("csv");
				add("xls");
			}
		};
		List<String> images = new ArrayList<String>() {
			/****/
			private static final long serialVersionUID = 7279283018453011772L;
			{
				add("gif");
				add("ico");
				add("img");
				add("png");
				add("jpg");
				add("jpeg");
			}
		};
		List<String> videos = new ArrayList<String>() {
			/****/
			private static final long serialVersionUID = -3528297743491959081L;
			{
				add("mp4");
				add("flv");
				add("avi");
				add("mkv");
				add("wmv");
				add("mpeg");
			}
		};
		List<String> audios = new ArrayList<String>() {
			/****/
			private static final long serialVersionUID = 189453214526600585L;
			{
				add("mp3");
				add("ogg");
				add("wav");
				add("rm");
				add("raw");
			}
		};
		List<String> webs = new ArrayList<String>() {
			/****/
			private static final long serialVersionUID = 2357401003144899556L;
			{
				add("htm");
				add("html");
				add("php");
				add("xhtml");
				add("xml");
				add("php3");
			}
		};
		List<String> applications = new ArrayList<String>() {
			/****/
			private static final long serialVersionUID = -3806234422105219272L;
			{
				add("exe");
				add("dpkg");
				add("rpm");
				add("bin");
				add("apk");
				add("bat");
				add("msi");
			}
		};
		String extension = "";
		int i = nomFichier.lastIndexOf('.');
		if (i > 0) {
			extension = nomFichier.substring(i + 1);
		}
		if (documents.contains(extension))
			type = EType.Document;
		else if (images.contains(extension))
			type = EType.Image;
		else if (audios.contains(extension))
			type = EType.Audio;
		else if (videos.contains(extension))
			type = EType.Video;
		else if (webs.contains(extension))
			type = EType.Web;
		else if (applications.contains(extension))
			type = EType.Application;
		else type = EType.Fichier;
		return type;
	}

}
