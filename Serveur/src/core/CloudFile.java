package core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import optionnal.Partage;

public abstract class CloudFile extends UnicastRemoteObject implements ICloudFile {

	private static final long serialVersionUID = 4897971627137424778L;
	protected int ID;
	protected Date dateCreation;
	protected Date dateModification;
	protected String nomVirtuel;
	protected String nomPhysique;
	protected EType type;
	protected double taille;
	protected boolean readOnly;
	protected IDossier dossierParent;
	protected IUtilisateur proprietaire;
	List<Utilisateur> abonnes;
	Map<Utilisateur, Boolean> utilisateursAyantAcces;
	Partage modulePartage;
	optionnal.Notification moduleNotification;

	/**
	 * Constructeur permettant de recuperer une cloudFile dans la BDD.
	 * 
	 * @require paramOK : nomVirtuel != null && taille != null && type != null
	 *          && proprietaire != null && dateCreation != null
	 * @param nomVirtuel
	 * @param taille
	 * @param type
	 * @param proprietaire
	 * @param dateCreation
	 */
	protected CloudFile(int id, IDossier parent) throws RemoteException {
		super();
		if (id < 0)
			throw new CloudException("CloudFile ID incorrect.");
		try {
			String query = "SELECT dateCreation,dateModification,nomPhysique,nomVirtuel,taille,ID_Type,ID_Proprietaire FROM Fichier where ID=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() == false)
				throw new CloudException("Erreur : Cloudfile inexistante.");
			rSet.next();
			ID = id;
			dateCreation = rSet.getTimestamp("dateCreation");
			dateModification = rSet.getTimestamp("dateModification");
			nomPhysique = rSet.getString("nomPhysique");
			nomVirtuel = rSet.getString("nomVirtuel");
			taille = rSet.getDouble("taille");
			type = EType.values()[rSet.getInt("ID_Type") - 1];
			proprietaire = Utilisateur.getUtilisateur(rSet.getInt("ID_Proprietaire"));
			dossierParent = parent;
			readOnly = false;
			utilisateursAyantAcces = resoudreAyantAcces();
			abonnes = resoudreAbonne();
			if (Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE))
				modulePartage = new Partage();
			if (Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_NOTIF_MAIL))
				moduleNotification = new optionnal.Notification();
		} catch (Exception e) {
			throw new CloudException("Erreur Base De Données.");
		}
	}

	/**
	 * 
	 * @param id
	 *            : ID de la cloudFile dans la BDD
	 * @param readOnly
	 *            : Mode d'acces à la cloudFile.
	 * @throws RemoteException
	 */
	protected CloudFile(int id, boolean readOnly) throws RemoteException {
		super();
		if (id < 0)
			throw new CloudException("CloudFile ID incorrect.");
		try {
			String query = "SELECT dateCreation,dateModification,nomPhysique,nomVirtuel,taille,ID_Type,ID_Proprietaire FROM Fichier where ID=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() == false)
				throw new CloudException("Fichier non existant.");
			rSet.next();
			ID = id;
			dateCreation = rSet.getTimestamp("dateCreation");
			dateModification = rSet.getTimestamp("dateModification");
			nomPhysique = rSet.getString("nomPhysique");
			nomVirtuel = rSet.getString("nomVirtuel");
			taille = rSet.getDouble("taille");
			type = EType.values()[rSet.getInt("ID_Type") - 1];
			proprietaire = Utilisateur.getUtilisateur(rSet.getInt("ID_Proprietaire"));
			dossierParent = null;
			this.readOnly = readOnly;
			utilisateursAyantAcces = resoudreAyantAcces();
			abonnes = resoudreAbonne();
			if (Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE))
				modulePartage = new Partage();
		} catch (Exception e) {
			throw new CloudException("Erreur Base De Donnée : " + e.getMessage());
		}
	}

	public CloudFile() throws RemoteException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getProprietaire()
	 */
	@Override
	public IUtilisateur getProprietaire() {
		return this.proprietaire;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getAbonnes()
	 */
	@Override
	public List<Utilisateur> getAbonnes() {
		return this.abonnes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#ajouteAbonnes(java.util.List)
	 */
	@Override
	public void ajouteAbonnes(List<Utilisateur> nouveauxAbonnes) {
		this.abonnes.addAll(nouveauxAbonnes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#ajouteAbonne(core.Utilisateur)
	 */
	@Override
	public void ajouteAbonne(Utilisateur abonne) {
		this.abonnes.add(abonne);
	}

	@Override
	public void retireAbonne(Utilisateur abonne) throws RemoteException {
		this.abonnes.remove(abonne);
	}

	@Override
	public void retireAbonnes(List<Utilisateur> abonnes) throws RemoteException {
		this.abonnes.removeAll(abonnes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getUtilisateursAyantAcces()
	 */
	@Override
	public Map<Utilisateur, Boolean> getUtilisateursAyantAcces() {
		return this.utilisateursAyantAcces;
	}

	public void ajouterUtilisateurAyantAcces(Utilisateur utilisateur, Boolean readOnly) {
		if (this.utilisateursAyantAcces.containsKey(this))
			throw new CloudException("Erreur : Vous ne pouvez ajouter un accès qui existe déjà !");
		this.utilisateursAyantAcces.put(utilisateur, readOnly);
	}

	public void supprimerUtilisateurAyantAcces(Utilisateur utilisateur) {
		if (!this.utilisateursAyantAcces.containsKey(utilisateur))
			throw new CloudException("Erreur : Vous ne pouvez ajouter un accès qui n'existe pas !");
		this.utilisateursAyantAcces.remove(utilisateur);
	}

	public void modifierAccesUtilisateurAyantAcces(Utilisateur utilisateur, boolean readOnly) {
		if (!this.utilisateursAyantAcces.containsKey(utilisateur))
			throw new CloudException("Erreur : Vous ne pouvez modifier un accès qui n'existe pas !");
		this.utilisateursAyantAcces.put(utilisateur, readOnly);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#estDossier()
	 */
	@Override
	public boolean estDossier() {
		return this.type == EType.Repertoire;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#estFichier()
	 */
	@Override
	public boolean estFichier() {
		return this.type != EType.Repertoire;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getParent()
	 */
	@Override
	public IDossier getParent() {
		return dossierParent;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#setParent(core.ICloudFile)
	 */
	@Override
	public void setParent(ICloudFile parent) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getDateCreation()
	 */
	@Override
	public Date getDateCreation() {
		return this.dateCreation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getDateModification()
	 */
	@Override
	public Date getDateModification() {
		return this.dateModification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#setDateModification(java.util.Date)
	 */
	@Override
	public void setDateModification(Date date) throws CloudException {
		try {
			if (date == null)
				throw new CloudException("Erreur : paramètre null (date).");
			String query = "Update Fichier set dateModification = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.dateModification = date;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getNomVirtuel()
	 */
	@Override
	public String getNomVirtuel() {
		return this.nomVirtuel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#setNomVirtuel(java.lang.String)
	 */
	@Override
	public void setNomVirtuel(String nomVirtuel) throws CloudException {
		try {
			if (nomVirtuel == "" || nomVirtuel == null)
				throw new CloudException("Erreur : paramètre null (Nom Vituel).");
			String query = "Update Fichier set nomVirtuel = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, nomVirtuel);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.nomVirtuel = nomVirtuel;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getNomPhysique()
	 */
	@Override
	public String getNomPhysique() {
		return this.nomPhysique;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#setNomPhysique(java.lang.String)
	 */
	@Override
	public void setNomPhysique(String nomPhysique) throws CloudException {
		try {
			if (nomPhysique == "" || nomPhysique == null)
				throw new CloudException("Erreur : paramètre null (Nom Physique).");
			String query = "Update Fichier set nomPhysique = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, nomPhysique);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.nomPhysique = nomPhysique;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getTaille()
	 */
	@Override
	public double getTaille() {
		return this.taille;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#setTaille(double)
	 */
	@Override
	public void setTaille(double taille) throws CloudException {
		try {
			if (taille < 0)
				throw new CloudException("Erreur : paramètre null (Taille).");
			String query = "Update Fichier set taille = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setDouble(1, taille);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.taille = taille;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#getType()
	 */
	@Override
	public EType getType() {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloudFile#setType(core.Type)
	 */
	@Override
	public void setType(EType type) throws CloudException {
		try {
			if (type == null)
				throw new CloudException("Erreur : paramètre null (Type).");
			String query = "Update Fichier set ID_Type = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, EType.valueOf(type.toString()).ordinal() + 1);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0)
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			else
				this.type = type;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	@Override
	public int getID() throws RemoteException {
		return this.ID;
	}

	/**
	 * Methode permettant de resoudre la liste des utilisateurs qui ont acces a
	 * la CloudFile
	 * 
	 * @return Utilisateurs Ayant l'Acces.
	 */
	private Map<Utilisateur, Boolean> resoudreAyantAcces() {
		Map<Utilisateur, Boolean> result = new HashMap<Utilisateur, Boolean>();
		try {
			String query = "Select ID_Utilisateur, readOnly from AvoirAccesFichier where ID_Fichier = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, this.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			while (rSet.next()) {
				result.put(Utilisateur.getUtilisateur(rSet.getInt("ID_Utilisateur")), rSet.getBoolean("readOnly"));
			}
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
		return result;
	}

	/**
	 * Methode permettant de resoudre la liste des utilisateurs qui sont abonné
	 * à la CloudFile
	 * 
	 * @return Utilisateurs abonné.
	 */
	private List<Utilisateur> resoudreAbonne() {
		List<Utilisateur> result = new ArrayList<>();
		try {
			String query = "Select ID_Utilisateur from EtreAbonne where ID_Fichier = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, this.getID());
			ResultSet rSet = preparedStatement.executeQuery();
			while (rSet.next()) {
				result.add(Utilisateur.getUtilisateur(rSet.getInt("ID_Utilisateur")));
			}
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
		return result;
	}

	public void partager(List<String> utilisateurs, boolean readOnly) throws RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE))
			throw new CloudException("Erreur : Module partage désactivé sur le serveur.");
		this.modulePartage.partagerAcces(readOnly, this, utilisateurs);
	}

	public void partager(String utilisateur, boolean readOnly) throws RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE))
			throw new CloudException("Erreur : Module partage désactivé sur le serveur.");
		this.modulePartage.partagerAcces(readOnly, this, utilisateur);
	}

	public void supprimerPartage(String utilisateur) throws RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE))
			throw new CloudException("Erreur : Module partage désactivé sur le serveur.");
		this.modulePartage.enleverAcces(this, utilisateur);
	}

	public void supprimerPartage(List<String> utilisateurs) throws RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE))
			throw new CloudException("Erreur : Module partage désactivé sur le serveur.");
		this.modulePartage.enleverAcces(this, utilisateurs);
	}

	@Override
	public void modifierModeAcces(String utilisateur, boolean readOnly) throws RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE))
			throw new CloudException("Erreur : Module partage désactivé sur le serveur.");
		this.modulePartage.modifierModeAcces(utilisateur, this, readOnly);
	}

	@Override
	public void modifierModeAcces(List<String> utilisateurs, boolean readOnly) throws RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_PARTAGE))
			throw new CloudException("Erreur : Module partage désactivé sur le serveur.");
		this.modulePartage.modifierModeAcces(utilisateurs, this, readOnly);
	}

	@Override
	public void NotifierModificationParMail() throws RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_NOTIF_MAIL))
			throw new CloudException("Erreur : Module notification par mail désactivé sur le serveur.");
		moduleNotification.sendMailNotification(this);
	}
	
	@Override
	public void supprimer() throws RemoteException {
		IDossier corbeille = this.proprietaire.getCloudClient().getCorbeille();
		supprimerTousAcces();
		try {
			String query = "Update Contient set ID_Conteneur=? where ID_Contenu=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, corbeille.getID());
			preparedStatement.setInt(2, this.getID());
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else {
				this.getParent().getContenu().remove(this);
				corbeille.getContenu().add(this);
				this.setDateModification(new Date());
			}
		} catch (CloudException e) {
			throw e;
		} catch (Exception e) {
			throw new CloudException("Erreur base de donnée. Contactez l'administrateur.");
		} 
	}
	
	
	
	@Override
	public void restaurer() throws RemoteException {
		try {
			String query = "Update Contient set ID_Conteneur=? where ID_Contenu=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, dossierParent.getID());
			preparedStatement.setInt(2, this.getID());
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else {
				this.getParent().getContenu().add(this);
				this.proprietaire.getCloudClient().getCorbeille().getContenu().remove(this);
			}
		} catch (CloudException e) {
			throw e;
		} catch (Exception e) {
			throw new CloudException("Erreur base de donnée. Contactez l'administrateur.");
		} 
	}

	/**
	 * Permet la suppression de tous les partages et les abonnements.
	 * @throws RemoteException .
	 */
	private void supprimerTousAcces() throws RemoteException{
		for(Map.Entry<Utilisateur, Boolean> entry : this.utilisateursAyantAcces.entrySet()){
			supprimerPartage(entry.getKey().getLogin());
		}
		for(Utilisateur abonne : abonnes){
			desabonner(abonne);
		}
	}

	
	public void abonner(int user){
		Utilisateur utilisateur = Utilisateur.getUtilisateur(user);
		this.moduleNotification.ajoutAbonnement(utilisateur, this);
	}
	
	

	@Override
	public void desabonner(Utilisateur utilisateur) throws RemoteException {
		this.moduleNotification.effacerAbonnement(utilisateur, this);		
	}
	
	
}