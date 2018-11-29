package core;

import java.rmi.RemoteException;
/**
 * Classe representant un fichier.
 * Dispose d'une methode download d'un fichier.
 * @author Groupe6
 *
 */
public interface IFichier extends ICloudFile {

	/**
	 * Permet le telechargement du fichier
	 * @return le fichier téléchargé sur le client
	 * @throws RemoteException .
	 */
	byte[] download() throws RemoteException;
}
