package core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IDossier extends ICloudFile{

	ArrayList<ICloudFile> getContenu() throws RemoteException;
	/**
	 * 
	 * @return le nom virtuel du Dossier
	 */
	String getNomVirtuel()throws RemoteException;
	
	/**
	 * Permet de creer un dossier dans un dossier parent 
	 * @param nom : Nom du nouveau dossier
	 * @param proprietaire : proprietaire du dossier creer
	 * @return Nouveau dossier
	 * @throws RemoteException
	 * @throws CloudException : Si le dossier est deja existant
	 */
	void creerDossier(String nom, IUtilisateur proprietaire) throws RemoteException, CloudException;
	/**
	 * Permet d'uploader un fichier dans un dossier
	 * @param file : fichier a upload
	 * @return ICloudFile : l'objet correspondant au fichier uploade
	 * @throws RemoteException
	 * @throws CloudException
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	void upload(String name, byte[] fileInputStream, IUtilisateur proprietaire) throws RemoteException, CloudException, IOException;

}