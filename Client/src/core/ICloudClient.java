package core;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICloudClient extends Remote{

	/**
	 * Permet de recuperer le proprietaire de l'objet CloudClient
	 * @return Utilisateur du cloudClient
	 * @throws RemoteException
	 */
	IUtilisateur getUtilisateur() throws RemoteException;

	public IDossier getPartage();
	public IDossier getRacine();
	public IDossier getCorbeille();

}