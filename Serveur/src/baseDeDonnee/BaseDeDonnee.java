package baseDeDonnee;

import java.sql.*;

import core.CloudException;
import core.Serveur;

/**
 * Classe permettant la connexion à la base de donnee et l'execution de requete
 * SQL (simple ou preparée).
 * 
 * @author Groupe6
 * 
 */
public class BaseDeDonnee {
	//DB Distant
	// private static final String DB_URL =
	// "jdbc:mysql://mysql-miageproject.alwaysdata.net/miageproject_cloud";
	// private final String USER = "137505";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static String DB_URL;
	private String USER;
	private final String PASS;
	private Connection connection;
	private Statement statement;

	/**
	 * Construit la connection à la base de donnée avec un driver de connection
	 * approprié.
	 * 
	 * @throws CloudException .
	 */
	public BaseDeDonnee() throws CloudException {
		try {
			DB_URL = "jdbc:mysql://" + Serveur.configuration.getAdresseBDD() + "/"+Serveur.configuration.getNomBDD();
			USER = Serveur.configuration.getUserBDD();
			PASS = Serveur.configuration.getPassBDD();
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
		} catch (Exception e) {
			throw new CloudException("Erreur : Connexion à la base de données échouée.");
		}
	}

	/**
	 * Permet d'executer une requete SQL sur la base de donnée
	 * 
	 * @param query : la requete SQL
	 * @return Resultat : Les resultats de la requete
	 * @throws SQLException .
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		return statement.executeQuery(query);
	}

	/**
	 * Méthode permettant la fermeture de la connexion lors de la suppression de l'instance BaseDeDonnée, à la fermeture du serveur.
	 * @throws Throwable .
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		connection.close();
		statement.close();
	}

	/**
	 * Permet de préparer une requête SQL sur la base de donnée.
	 * @param query : la requête SQL à préparer.
	 * @return : La requête SQL préparer.
	 * @throws SQLException .
	 */
	public PreparedStatement prepareStatement(String query) throws SQLException {
		return connection.prepareStatement(query);
	}

}