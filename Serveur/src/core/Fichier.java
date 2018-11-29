package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;

public class Fichier extends CloudFile implements IFichier {

	private static final long serialVersionUID = 5161371987521543977L;

	public Fichier(int id, IDossier parent) throws RemoteException {
		super(id, parent);
	}

	public Fichier(int id, boolean readOnly) throws RemoteException {
		super(id, readOnly);
	}

	@Override
	public byte[] download() throws RemoteException {
		String upload = "./uploads/";
		try {
			return Files.readAllBytes(Paths.get(upload + this.getNomPhysique()));
		} catch (IOException e) {
			throw new CloudException("Erreur serveur : Contactez l'administrateur serveur.");
		}
	}

}
