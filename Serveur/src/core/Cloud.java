package core;

import java.rmi.RemoteException;

import optionnal.Enregistrement;

public class Cloud implements ICloud{
	Connexion connecteur;
	Enregistrement enregistrement;

	protected Cloud() throws RemoteException {
		super();
		connecteur = new Connexion();
		if(Serveur.configuration.getConfigValue(EConfiguration.SERVEUR_ENREGISTREMENT)) enregistrement = new Enregistrement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloud#login()
	 */
	@Override
	public IUtilisateur login(String utilisateur, String motDePasse) throws RemoteException {
		return connecteur.login(utilisateur, motDePasse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.ICloud#getConfigurationServeur()
	 */
	@Override
	public IConfiguration getConfigurationServeur() throws RemoteException {
		return Serveur.configuration;
	}

	@Override
	public void enregistrement(String login, String password, String nom, String prenom, String email)
			throws RemoteException {
			enregistrement.enregistrement(login, password, nom, prenom, email);	
	}
	
}
