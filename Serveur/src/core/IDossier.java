package core;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Represente un repertoire dans l'arborescence du cloud. Permet la creation de
 * dossier, et l'upload de fichier dans un repertoire, et la recuperation du
 * contenu du repertoire.
 * 
 * @author Groupe6
 *
 */
public interface IDossier extends ICloudFile {

	/**
	 * Permet de recuperer les CloudFile contenu dans le dossier
	 * 
	 * @return contenu du dossier
	 * @throws RemoteException
	 *             .
	 * @throws CloudException
	 *             .
	 */
	ArrayList<CloudFile> getContenu() throws RemoteException, CloudException;

	/**
	 * Permet de creer un dossier dans un dossier parent
	 * 
	 * @param nom
	 *            : Nom du nouveau dossier
	 * @param proprietaire
	 *            : proprietaire du dossier creer
	 * @throws RemoteException
	 *             .
	 * @throws CloudException
	 *             : Si le dossier est deja existant
	 */
	void creerDossier(String nom, IUtilisateur proprietaire) throws RemoteException, CloudException;

	/**
	 * Permet d'uploader un fichier dans un dossier
	 * 
	 * @param name
	 *            : nom du fichier
	 * @param fileInputStream
	 *            :
	 * @param proprietaire
	 *            : propriétaire du fichier
	 * @throws RemoteException
	 *             .
	 * @throws CloudException
	 *             .
	 * @throws IOException
	 *             .
	 */
	void upload(String name, byte[] fileInputStream, IUtilisateur proprietaire)
			throws RemoteException, CloudException, IOException;

}