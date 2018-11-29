package core;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Structure de donnée contenant les dossiers racine, partage et corbeille de l'utilisateur.
 * permet aussi la navigation vers le proprietaire du cloudClient.
 * @author Groupe6
 *
 */
public interface ICloudClient extends Remote{

	/**
	 * Permet de recuperer le proprietaire de l'objet CloudClient
	 * @return Utilisateur du cloudClient
	 * @throws RemoteException .
	 */
	IUtilisateur getUtilisateur() throws RemoteException;
	/**
	 * @return le partage de l'utilisateur
	 * @throws RemoteException .
	 */
	public IDossier getPartage() throws RemoteException;
	/**
	 * 
	 * @return la racine de l'utilisateur
	 * @throws RemoteException .
	 */
	public IDossier getRacine() throws RemoteException;
	/**
	 * 
	 * @return la corbeille de l'utilisateur
	 * @throws RemoteException .
	 */
	public IDossier getCorbeille() throws RemoteException;

}