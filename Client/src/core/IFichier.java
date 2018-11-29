package core;

import java.rmi.RemoteException;

public interface IFichier extends ICloudFile {

	/**
	 * Permet le telechargement du fichier
	 */
	
	byte[] download() throws RemoteException;
}
