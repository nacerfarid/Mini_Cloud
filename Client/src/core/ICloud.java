package core;
import java.rmi.*;

/**
 * Interface commune entre le serveur et le client qui va assurer le transfert de fichiers, 
 * elle contient la signature de la méthode login
 * @author Groupe6
 */
public interface ICloud extends Remote{	
	public IUtilisateur login(String utilisateur, String motDePasse) throws RemoteException;
	public IConfiguration getConfigurationServeur();
	/**
	 * permet l'enregistrement d'un nouveau compte sur le serveur.
	 * 
	 * @param login
	 *            : login de l'utilisateur
	 * @param password
	 *            (Hashé en SHA2) : mot de passe de l'utilisateur
	 * @param nom
	 *            : nom de l'utilisateur
	 * @param prenom
	 *            : prenom de l'utilisateur
	 * @param email
	 *            : email de l'utilisateur
	 * @throws RemoteException
	 *             .
	 */
	public void enregistrement(String login, String password, String nom, String prenom, String email)
			throws RemoteException;
}