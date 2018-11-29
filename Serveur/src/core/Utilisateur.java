package core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

import optionnal.Notification;

public class Utilisateur extends UnicastRemoteObject implements IUtilisateur {

	private static Map<String, Utilisateur> cacheUtilisateur = new HashMap<>();
	private static final long serialVersionUID = 4947032031967161787L;
	private int ID;
	private String login;
	private String nom;
	private String prenom;
	private String email;
	private boolean freeze;
	private double quota;
	private Date lastConnection;
	private CloudClient cloudClient;
	private List<CloudFile> fichiersANotifier;
	private Notification notifier;

	/**
	 * @require paramOK : login != null && password != null && nom != null &&
	 *          prenom != null && email != null
	 * @require paramOK : login != "" && nom != "" && prenom != "" && email !=
	 *          ""
	 * @param login
	 *            : nom d'utilisateur
	 * @param password
	 *            : le mot de passe associé
	 * @param nom
	 *            : nom reel de l'utilisateur
	 * @param prenom
	 *            : prenom reel de l'utilisateur
	 * @param email
	 *            : email de l'utilisateur
	 */
	private Utilisateur(int id, String login, String nom, String prenom, String email, boolean freeze,
			Date derniereConnection) throws RemoteException {
		super();
		if (login == null || login.equals(""))
			throw new CloudException("Login incorrect.");
		if (nom == null || nom.equals(""))
			throw new CloudException("Nom incorrect.");
		if (prenom == null || prenom.equals(""))
			throw new CloudException("Prenom incorrect.");
		if (email == null || email.equals(""))
			throw new CloudException("Email incorrect.");
		this.ID = id;
		this.login = login;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.freeze = freeze;
		this.lastConnection = derniereConnection;
		this.notifier = new Notification();
	}

	/**
	 * @require paramOK : login != null && nom != null && prenom != null &&
	 *          email != null
	 * @require paramOK : login != "" && nom != "" && prenom != "" && email !=
	 *          "" && quota>=0
	 * @param login
	 *            : nom d'utilisateur
	 * @param password
	 *            : le mot de passe associé
	 * @param nom
	 *            : nom reel de l'utilisateur
	 * @param prenom
	 *            : prenom reel de l'utilisateur
	 * @param email
	 *            : email de l'utilisateur
	 */
	private Utilisateur(int id, String login, String nom, String prenom, String email, boolean freeze, double quota,
			Date derniereConnection) throws RemoteException {
		super();
		if (login == null || login.equals(""))
			throw new CloudException("Login incorrect.");
		if (nom == null || nom.equals(""))
			throw new CloudException("Nom incorrect.");
		if (prenom == null || prenom.equals(""))
			throw new CloudException("Prenom incorrect.");
		if (email == null || email.equals(""))
			throw new CloudException("Email incorrect.");
		if (quota < 0)
			throw new CloudException("Quota incorrect.");
		this.ID = id;
		this.login = login;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.freeze = freeze;
		this.quota = quota;
		this.lastConnection = derniereConnection;
		this.notifier = new Notification();
	}

	/**
	 * Constructeur de la classe Utilisateur.
	 * @param login : login de l'utilisateur
	 * @return l'utilisateur
	 * @throws RemoteException .
	 */
	public static Utilisateur getUtilisateur(String login) throws RemoteException {
		if (login == null || login.equals(""))
			throw new CloudException("Erreur récupération Utilisateur : Login incorrect.");
		Utilisateur utilisateur;
		if (cacheUtilisateur.containsKey(login))
			utilisateur = cacheUtilisateur.get(login);
		else {
			try {
				String query = "SELECT id,login,nom,prenom,email,freeze,quota, lastConnection FROM Utilisateur where login=?";
				PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
				preparedStatement.setString(1, login);
				ResultSet rSet = preparedStatement.executeQuery();
				if (rSet.isBeforeFirst() == false)
					throw new CloudException("Utilisateur non existant.");
				rSet.next();
				int id = rSet.getInt("id");
				String username = rSet.getString("login");
				String nom = rSet.getString("nom");
				String prenom = rSet.getString("prenom");
				String email = rSet.getString("email");
				boolean freeze = rSet.getBoolean("freeze");
				Date derniereConnection = rSet.getTimestamp("lastConnection");
				if (Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_LIMITE_ESPACE)) {
					double quota = rSet.getDouble("quota");
					utilisateur = new Utilisateur(id, username, nom, prenom, email, freeze, quota, derniereConnection);
				} else {
					utilisateur = new Utilisateur(id, username, nom, prenom, email, freeze, derniereConnection);
				}
				if (utilisateur != null)
					cacheUtilisateur.put(utilisateur.getLogin(), utilisateur);
			} catch (Exception e) {
				throw new CloudException("Erreur Base De Donnée : " + e.getMessage());
			}
		}
		return utilisateur;
	}

	/**
	 * Constructeur de la classe Utilisateur.
	 * @param id : id de l'utilisateur
	 * @return l'utilisateur
	 */
	public static Utilisateur getUtilisateur(int id) {
		if (id < 0)
			throw new CloudException("Erreur récupération Utilisateur : ID incorrect.");
		try {
			String query = "SELECT login FROM Utilisateur where id=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() == false)
				throw new CloudException("Utilisateur non existant.");
			rSet.next();
			String login = rSet.getString("login");
			return Utilisateur.getUtilisateur(login);
		} catch (Exception e) {
			throw new CloudException("Erreur Base De Donnée : " + e.getMessage());
		}
	}

	/**
	 * Methode permettant de charger un utilisateur et son cloud a la connexion.
	 * La date de derniere connexion est mise a jour ici.
	 * 
	 * @param login : Login de l'utilisateur
	 * @return l'utilisateur
	 * @throws RemoteException .
	 */
	public static Utilisateur chargerUtilisateur(String login) throws RemoteException {
		Utilisateur result = Utilisateur.getUtilisateur(login);
		result.chargerCloudClient();
		if (Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_NOTIF_POPUP)) {
			result.chargerFichiersANotifier();
		}
		result.setLastConnexion(new Date());
		return result;
	}

	/**
	 * Permet le chargement des fichiers dont la date de modification est
	 * ulterieur a la date de derniere connexion.
	 */
	private void chargerFichiersANotifier() {
		this.fichiersANotifier = this.notifier.getNouvellesNotifications(this);
	}

	/**
	 * Constructeur de la classe Utilisateur.
	 * @param id : ID de l'utilisateur
	 * @throws RemoteException .
	 */
	public Utilisateur(int id) throws RemoteException {
		if (id < 1)
			throw new CloudException("Erreur récupération Utilisateur : ID incorrect.");
		try {
			String query = "SELECT id,login,nom,prenom,email,freeze,quota FROM Utilisateur where ID=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() == false)
				throw new CloudException("Erreur : Utilisateur non existant.");
			rSet.next();
			login = rSet.getString("login");
			nom = rSet.getString("nom");
			prenom = rSet.getString("prenom");
			email = rSet.getString("email");
			freeze = rSet.getBoolean("freeze");
			if (Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_LIMITE_ESPACE))
				quota = rSet.getDouble("quota");
		} catch (Exception e) {
			throw new CloudException("Erreur Base De Donnée : Contactez l'administrateur.");
		}
	}

	/**
	 * Verifie l'egalite du mot de passe passe en paramettre avec le mot de
	 * passe en BDD pour le login passe en parametre.
	 * @param login : login de l'utilisateur
	 * @param mdp : mot de passe de l'utilisateur
	 * @return true / false en fonction de l'exactitude du mdp : Boolean
	 * @throws RemoteException .
	 */
	public static boolean verifierMotDePasse(String login, String mdp) throws RemoteException {
		if (mdp == null || mdp.equals(""))
			throw new CloudException("Erreur : Mot De Passe invalide.");
		try {
			String query = "Select password From Utilisateur Where login=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, login);
			ResultSet rSet = preparedStatement.executeQuery();
			if (rSet.isBeforeFirst() == false)
				throw new CloudException("Erreur : Utilisateur non existant.");
			rSet.next();
			String bddPassword = rSet.getString("password");
			return bddPassword.equals(mdp);
		} catch (Exception e) {
			throw new CloudException("Erreur Base De Donnée : Contactez l'administrateur. ");
		}
	}

	/**
	 * @param cloudFile : representation d'un fichier du cloud (CloudFile)
	 * @return true si l'utilisateur est le proprietaire, sinon false
	 */
	public boolean estProprietaire(ICloudFile cloudFile) throws RemoteException {
		if (cloudFile == null)
			throw new CloudException("Erreur : Paramètre null (CloudFile).");
		boolean result = false;
		if (cloudFile.getProprietaire() == this)
			result = true;
		return result;
	}

	/**
	 * @param cloudFile : representation d'un fichier du cloud (CloudFile)
	 * @return true si l'utilisateur a acces au fichier fic, sinon false
	 */
	public boolean aAcces(ICloudFile cloudFile) throws RemoteException {
		if (cloudFile == null)
			throw new CloudException("Erreur : Paramètre null (CloudFile).");
		return cloudFile.getUtilisateursAyantAcces().containsValue(this);
	}

	/**
	 * 
	 * @return la liste des CloudFile qui ont été supprimés par l'utilisateur
	 */
	@SuppressWarnings("unused")
	private Dossier lesFichiersSupprimes() throws RemoteException {
		return this.cloudClient.getCorbeille();
	}

	/**
	 * 
	 * @return la liste des CloudFile qui sont accessibles par l'utilisateur
	 */
	@SuppressWarnings("unused")
	private Dossier lesFichiersAccessibles() throws RemoteException {
		return this.cloudClient.getPartage();
	}

	/**
	 * 
	 * @return la liste des CloudFile de l'utilisateur
	 */
	@SuppressWarnings("unused")
	private Dossier mesFichiers() throws RemoteException {
		return this.cloudClient.getRacine();
	}

	@Override
	public int getID() {
		return this.ID;
	}

	/**
	 * 
	 * @return l'arborescence CloudClient de l'utilisateur
	 */
	public CloudClient getCloudClient() throws RemoteException {
		return cloudClient;
	}

	/**
	 * 
	 * @return le nom de l'utilisateur
	 */
	public String getNom() throws RemoteException {
		return this.nom;
	}

	/**
	 * 
	 * @return le prenom de l'utilisateur
	 */
	public String getPrenom() throws RemoteException {
		return this.prenom;
	}

	/**
	 * 
	 * @return l'email de l'utilisateur
	 */
	public String getEmail() throws RemoteException {
		return this.email;
	}

	/**
	 * 
	 * @return le quota de l'utilisateur
	 */
	public double getQuota() throws RemoteException {
		return this.quota;
	}

	/**
	 * 
	 * @return la date de derniere connexion de l'utilisateur
	 */
	public Date getDerniereConnexion() {
		return this.lastConnection;
	}

	/**
	 * 
	 * @return le login de connexion de l'utilisateur
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * Permet le chargement de la structure de donnée CloudClient pour
	 * l'instance en cour.
	 */
	public void chargerCloudClient() {
		try {
			cloudClient = new CloudClient(this);
		} catch (Exception e) {
			throw new CloudException("Erreur : Impossible de charger l'arborescence du client.");
		}
	}

	/**
	 * @param mdp : le nouveau mot de passe (Hashé en SHA256)
	 * @param ancienMdp : le mot de passe actuel (Hashé en SHA256)
	 */
	public void setMotDePasse(String mdp, String ancienMdp) throws CloudException {
		try {
			if (verifierMotDePasse(this.login, ancienMdp)) {
				String query = "UPDATE Utilisateur SET password=? WHERE id=?";
				PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
				preparedStatement.setString(1, mdp);
				preparedStatement.setInt(2, this.ID);
				int resultat = preparedStatement.executeUpdate();
				if (resultat <= 0) {
					throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
				} else if (!verifierMotDePasse(this.login, mdp))
					throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				throw new CloudException("Erreur : Ancien mot de passe incorrect.");
		} catch (CloudException e) {
			throw e;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/**
	 * 
	 * @param quota : quota maximum en KiloOctet
	 * @throws CloudException .
	 */
	@Override
	public void setQuota(double quota) throws CloudException {
		try {
			String query = "Update Utilisateur set quota = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setDouble(1, quota);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.quota = quota;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/**
	 * 
	 * @param freeze : freeze le compte si true.
	 * @throws CloudException .
	 */
	@Override
	public void setFreeze(boolean freeze) throws CloudException {
		try {
			String query = "Update Utilisateur set freeze = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setBoolean(1, freeze);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.freeze = freeze;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/**
	 * 
	 * @param email : nouvelle email du compte
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	@Override
	public void setEmail(String email) throws CloudException, RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_GERER_PROFIL))
			throw new CloudException("Modification du profil non autorise.");
		try {
			if (email == "" || email == null)
				throw new CloudException("setEmail() incorrect : email vide");
			String query = "Update Utilisateur set email = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, email);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.email = email;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/**
	 * 
	 * @param prenom : nouveau prenom de l'utilisateur
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	@Override
	public void setPrenom(String prenom) throws CloudException, RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_GERER_PROFIL))
			throw new CloudException("Modification du profil non autorise.");
		try {
			if (prenom == "" || prenom == null)
				throw new CloudException("setPrenom() incorrect : prenom vide");
			String query = "Update Utilisateur set prenom = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, prenom);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.prenom = prenom;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/**
	 * 
	 * @param nom : nouveau nom de l'utilisateur
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	@Override
	public void setNom(String nom) throws CloudException, RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_GERER_PROFIL))
			throw new CloudException("Modification du profil non autorise.");
		try {
			if (nom == "" || nom == null)
				throw new CloudException("setNom() incorrect : nom vide");
			String query = "Update Utilisateur set nom = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, nom);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.nom = nom;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/**
	 * 
	 * @param login : nouveau login du compte.
	 * @throws CloudException .
	 * @throws RemoteException .
	 */
	@Override
	public void setLogin(String login) throws CloudException, RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_GERER_PROFIL))
			throw new CloudException("Modification du profil non autorise.");
		try {
			if (login == "" || login == null)
				throw new CloudException("setLogin() incorrect : login vide");
			
			String query = "Select login from Utilisateur where login=?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, login);
			ResultSet rSet = preparedStatement.executeQuery();
			if(rSet.isBeforeFirst()) throw new CloudException("Erreur : Login déjà existant.");	
			
			query = "Update Utilisateur set login = ? where id = ?";
			preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, login);
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			else this.login = login;
			//Actialisation de ses dossiers racine et corbeille.
			int idRacine = this.cloudClient.getRacine().getID();
			query = "UPDATE Fichier SET nomVirtuel = ? WHERE ID = ?";
			preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, "racine_" + login);
			preparedStatement.setInt(2, idRacine);
			preparedStatement.executeUpdate();
			int idCorbeille = this.cloudClient.getCorbeille().getID();
			query = "UPDATE Fichier SET nomVirtuel = ? WHERE ID = ?";
			preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setString(1, "corbeille_" + login);
			preparedStatement.setInt(2, idCorbeille);
			preparedStatement.executeUpdate();

			this.cloudClient.getRacine().setNomVirtuel("racine_" + login);
			this.cloudClient.getCorbeille().setNomVirtuel("corbeille_" + login);
		} catch (CloudException e) {
			throw e;
		}
		catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	/**
	 * Permet de mettre a jour la date de derniere connexion d'un utilisateur
	 * 
	 * @param date : date de la derniere connexion.
	 * @throws CloudException .
	 */
	@Override
	public void setLastConnexion(Date date) throws CloudException {
		try {
			if (date == null)
				throw new CloudException("setLastConnexion() incorrect : date nulle");
			String query = "Update Utilisateur set lastConnection = ? where id = ?";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
			preparedStatement.setInt(2, this.ID);
			int resultat = preparedStatement.executeUpdate();
			if (resultat <= 0) {
				throw new CloudException("Erreur Base de Données : Mise à jour non effectuée.");
			} else
				this.lastConnection = date;
		} catch (Exception e) {
			throw new CloudException("Erreur Base de Données : Contactez l'administrateur.");
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof Utilisateur)
			this.equals((Utilisateur) obj);
		return false;
	}

	public boolean equals(Utilisateur util) {
		return this.ID == util.getID();
	}

	public List<CloudFile> getFichiersANotifier() throws RemoteException {
		if (!Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_NOTIF_POPUP))
			throw new CloudException("Erreur : Notification non disponible.");
		return this.fichiersANotifier;
	}

	public void setFichiersANotifier(List<CloudFile> fichiersANotifier) {
		this.fichiersANotifier = fichiersANotifier;
	}

	@Override
	public boolean getFreeze() throws CloudException, RemoteException {
		return freeze;
	}

}