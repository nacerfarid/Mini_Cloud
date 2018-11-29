package core;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import baseDeDonnee.BaseDeDonnee;

/**
 * Classe Serveur contenant le point de depart de l'application serveur. Cette
 * classe charge la configuration du serveur, la connexion a la base de donnee
 * et publier l'annuaire de l'interface correspondant au cloud avec Java RMI.
 * 
 * @author Groupe6
 *
 */
public class Serveur {
	private final static String RMILookup = "cloud";
	private final static int port = 42678;
	private final static String fichierConfiguration = "./configuration.xml";
	public static BaseDeDonnee baseDeDonnee;
	public static IConfiguration configuration;

	public static void main(String[] args) {
		try {
			configuration = new Configuration(Serveur.fichierConfiguration);
			baseDeDonnee = new BaseDeDonnee();
			ICloud skeleton = (ICloud) UnicastRemoteObject.exportObject(new Cloud(), port);
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind(RMILookup, skeleton);
			System.out.println("Serveur démarré sur : rmi://localhost/" + RMILookup);			
		} catch (Exception e) {
			throw new CloudException("Erreur Serveur : " + e.getMessage());
		}
	}
}
