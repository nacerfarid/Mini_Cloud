package core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ICloudFile extends Remote {
	/**
	 * 
	 * @return le type du CloudFile
	 */
	EType getType()throws RemoteException;
	
	/**
	 * 
	 * @return le nom virtuel du CloudFile
	 */
	String getNomVirtuel()throws RemoteException;

	/**
	 * 
	 * @return le CloudFile parent.
	 */
	IDossier getParent()throws RemoteException;
	/**
	 * 
	 * @return la taille du CloudFile.
	 */
	double getTaille()throws RemoteException;
	/**
	 * 
	 * @return le proprietaire du CloudFile.
	 */
	IUtilisateur getProprietaire()throws RemoteException;
	/**
	 * 
	 * @return la date de creation du CloudFile.
	 */
	Date getDateCreation()throws RemoteException;
	/**
	 * 
	 * @return la date de derniere modification du CloudFile.
	 */
	Date getDateModification()throws RemoteException;
	/**
	 * 
	 * @return la liste des IUtilisateur auxquels le CloudFile a ete partage.
	 */
	Map<IUtilisateur, Boolean> getUtilisateursAyantAcces()throws RemoteException;
	/**
	 * Permet de supprimer une cloudFile
	 * @throws RemoteException .
	 */
	void supprimer() throws RemoteException;
	
	/**
	 * Permet de s'abonner à une cloudFile
	 * @param utilisateur : Utilisateur a abonner.
	 * @throws RemoteException .
	 */
	void abonner(int utilisateur) throws RemoteException;
	
	/**
	 * Permet de se désabonner à une cloudFile
	 * @param utilisateur : Utilisateur a désabonner.
	 * @throws RemoteException .
	 */
	void desabonner(IUtilisateur utilisateur) throws RemoteException;
	
	/**
	 * @param nomVirtuel
	 *            : nouveau nom virtuel
	 * @throws RemoteException
	 *             .
	 */
	void setNomVirtuel(String nomVirtuel) throws RemoteException;
	/**
	 * Permet de supprimer une cloudFile
	 * @throws RemoteException .
	 */
	void restaurer() throws RemoteException;
}
