package optionnal;

import java.rmi.RemoteException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import core.CloudException;
import core.Serveur;
/**
* Permet d’envoyer des e-mail via l'adresse configurée dans le XML.
*/
public class Mailer implements IMailer {
	private String mail;
	private String password;
	private String host;
	private int port;

	public Mailer() throws RemoteException{
		mail = Serveur.configuration.getAdresseMail();
		password = Serveur.configuration.getPasswordMail();
		host = Serveur.configuration.getHostMailer();
		port = Serveur.configuration.getPortMailer();
	}
	
	/* (non-Javadoc)
	 * @see optionnal.IMailer#EnvoyerMail(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void EnvoyerMail(String destinataire, String sujet, String contenu) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mail, password);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
			message.setSubject(sujet);
			message.setText(contenu);
			Transport.send(message);
		} catch (MessagingException e) {
			throw new CloudException("Erreur Serveur : Configuration du Mailer invalide.");
		}
	}
}
