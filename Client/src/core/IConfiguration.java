package core;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConfiguration extends Remote {

	/**
	 * 
	 * @param config
	 * @require paramok : config != null
	 * @return le boolean déterminant si la config est activé ou non
	 */
	boolean getConfigValue(EConfiguration config)throws RemoteException;

}