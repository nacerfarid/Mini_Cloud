package optionnal;

import java.rmi.RemoteException;
import java.sql.*;
import java.util.Date;

import core.*;

/**
 * Permet la creation d'un compte utilisateur sur le serveur.
 * @author Groupe6
 *
 */
public class Enregistrement {
	/**
	 * la fonction calcule le nombre d'enregistrements dans la base de données
	 * @return total : le nombre d'utilisateurs enregistrés
	 * @throws RemoteException .
	 */
	private static int nombreEnregitrements() throws RemoteException{
		int total;
		try{
			String query = "SELECT COUNT(*) AS total FROM Utilisateur";
			PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
			ResultSet rSet = preparedStatement.executeQuery();
			rSet.next();
			total = rSet.getInt("total");
		} catch(Exception e){
			throw new CloudException("Erreur base de données. Contactez l'administrateur.");
		}
		return total;
	}
	/**
	 * la fonction permet de faire l'enregistrement d'un nouvel utilisateur
	 * @param login : login de l'utilisateur
	 * @param password : mot de passe de l'utilisateur (hashé en SHA-2)
	 * @param nom : nom de l'utilisateur
	 * @param prenom : prenom de l'utilisateur
	 * @param email : email de l'utilisateur
	 */
	public void enregistrement(String login, String password, String nom, String prenom, String email){
		if(login == "" || password == "" || nom == "" || prenom == ""){
			throw new CloudException("Erreur : Paramètres null.");			
		}
		try {
			if(nombreEnregitrements()<Serveur.configuration.getNbMaxUtilisateurs()){
				String query = "SELECT login FROM Utilisateur WHERE login = ?";
				PreparedStatement preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
				preparedStatement.setString(1, login);
				ResultSet rSet = preparedStatement.executeQuery();
				rSet.next();
				if(!rSet.next()){
						double quota = Serveur.configuration.getQuota();
						Timestamp lastConnection = new Timestamp(new Date().getTime());
						query = "INSERT INTO Utilisateur (login,password,nom,prenom,email,quota,lastConnection) VALUES(?,?,?,?,?,?,?)";
						preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
						preparedStatement.setString(1, login);
						preparedStatement.setString(2, password);
						preparedStatement.setString(3, nom);
						preparedStatement.setString(4, prenom);
						preparedStatement.setString(5, email);
						preparedStatement.setDouble(6, quota);
						preparedStatement.setTimestamp(7, lastConnection);
						preparedStatement.executeUpdate();
						rSet = preparedStatement.getGeneratedKeys();
						rSet.next();
						int lastInsert = rSet.getInt(1);
						String fichierRacine = "racine_" + login , fichierCorbeille = "corbeille_" + login;
						Timestamp dateCreation,dateModification;
						dateCreation = lastConnection; dateModification = lastConnection;
						//Table fichier --fichierRacine
						query = "INSERT INTO Fichier (dateCreation,dateModification,nomVirtuel,nomPhysique,taille,ID_Type,ID_Proprietaire) VALUES (?,?,?,?,?,?,?)";
						preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
						preparedStatement.setTimestamp(1, dateCreation);
						preparedStatement.setTimestamp(2, dateModification);
						preparedStatement.setString(3, fichierRacine);
						preparedStatement.setString(4, null);
						preparedStatement.setString(5, null);
						preparedStatement.setInt(6, 1);
						preparedStatement.setInt(7, lastInsert);
						preparedStatement.executeUpdate();
						//Table fichier --fichierCorbeille
						query = "INSERT INTO Fichier (dateCreation,dateModification,nomVirtuel,nomPhysique,taille,ID_Type,ID_Proprietaire) VALUES (?,?,?,?,?,?,?)";
						preparedStatement = Serveur.baseDeDonnee.prepareStatement(query);
						preparedStatement.setTimestamp(1, dateCreation);
						preparedStatement.setTimestamp(2, dateModification);
						preparedStatement.setString(3, fichierCorbeille);
						preparedStatement.setString(4, null);
						preparedStatement.setString(5, null);
						preparedStatement.setInt(6, 8);
						preparedStatement.setInt(7, lastInsert);
						preparedStatement.executeUpdate();
					}else{
						throw new CloudException("Erreur : Utilisateur dèjà existant.");
					}
			}else{
				throw new CloudException("Erreur : Nombre maximal d'utilisateurs atteint.");
			}
		} catch (Exception e) {
			throw new CloudException("Erreur base de données. Contactez l'administrateur.");
		}	
	}
}