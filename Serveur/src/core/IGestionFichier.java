package core;
import java.io.*;
import java.rmi.*;

/**
 * Interface FichierClient contient la signature des deux méthodes dont on a besoin : 
 * envoyerDonnees() et getNom()
 * @author Groupe6
 */
public interface IGestionFichier extends Remote{
	/**
	 * la fonction upload prend en paramètre trois argument le nom du serveur, le fichier qu'on
	 * veut uploader et la destination dans le serveur
	 * @param server nom du serveur
	 * @param src fichier source
	 * @param dest fichier destination
	 * @throws IOException
	 */
	public boolean upload(File src) throws IOException;
	/**
	 * la fonction download quand à elle prend les mêmes paramètres que la fonction upload sauf
	 * qu'elle fait l'opération inverse, elle copie un fichier à partir d'un serveur vers un fichier
	 * en local
	 * @param server nom du serveur
	 * @param src fichier source
	 * @param dest fichier destination
	 * @return 
	 * @throws IOException
	 */
	public File download(ICloudFile dest) throws IOException;
}