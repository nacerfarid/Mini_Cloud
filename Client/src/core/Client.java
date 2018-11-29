package core;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

 
/**
 * Classe principale qui contient le main du client
 * @author Groupe6
 */
public class Client {
	public static ICloud cloud;
	private final static String RMILookup = "cloud";
	private final static int port = 42678;
	
	public static void main(String[] args) {
		try{
			//Connexion au serveur
			Registry registry = LocateRegistry.getRegistry(port);
			cloud = (ICloud) registry.lookup(RMILookup);
			
			//Upload test
			//TODO Upload a implementer plus tard
			//cloud.upload(new File("/home/sofiane/Documents/a.pdf"));
			
			//Configuration Test
			System.out.println("Test Configuration : ");
			IConfiguration configuration = cloud.getConfigurationServeur();
			System.out.println(EConfiguration.SERVEUR_ENREGISTREMENT.toString() + " : " + configuration.getConfigValue(EConfiguration.SERVEUR_ENREGISTREMENT));
			
			//Utilisateur Test
			IUtilisateur utilisateur =  cloud.login("eloualis", Hashage.hashSHA2("abc123"));
			System.out.println("\n Test Affichage Nom Utilisateur : "+utilisateur.getNom());
			utilisateur.setMotDePasse(Hashage.hashSHA2("abc123"), Hashage.hashSHA2("abc123"));
			utilisateur.setFreeze(false);
			utilisateur.setEmail("nouvel.email@email.com");
			
			//CloudClient Test
			ICloudClient cloudClient = utilisateur.getCloudClient();
			System.out.println("\nTest recuperation CloudClient : ");
			System.out.println("Via cloudClient : "+cloudClient.getUtilisateur().getNom());
			ICloudFile racine = cloudClient.getRacine();
			System.out.println(racine.getNomVirtuel());
			
			//Creation dossier 
			IDossier dossierParent = cloudClient.getRacine();
			//dossierParent.creerDossier("TESTCREA", utilisateur);
			
			//navigation
			System.out.println("\nTest getParent()");
			System.out.println(dossierParent.getNomVirtuel() + " - Contenu :");
			for(ICloudFile cf:dossierParent.getContenu()){
				System.out.println("\t"+cf.getNomVirtuel() + " \t[Parent " + cf.getParent().getNomVirtuel() + "]");
			}
//			cloudClient.creerDossier(parent, "d");
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}