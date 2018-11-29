package optionnal;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMailer extends Remote {

	/**
	 * Permet à partir d’identifiants de connexion + adresse smtp d’envoyer un mail.
	 * Utile lors d’une modification d’un fichier sur lequel des abonnements sont présents.
	 * @param destinataire : Le destinataire du mail
	 * @param sujet : Le sujet du mail
	 * @param contenu : Le contenu du mail
	 * @throws RemoteException .
	 */
	void EnvoyerMail(String destinataire, String sujet, String contenu) throws RemoteException;

}