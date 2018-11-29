package IHM;

import java.rmi.RemoteException;

import javax.swing.tree.DefaultMutableTreeNode;

import core.EType;
import core.ICloudFile;
import core.IDossier;

public class NodePerso extends DefaultMutableTreeNode{

	private ICloudFile file;
	
	public NodePerso(ICloudFile file, boolean b) throws RemoteException {
		super(file.getNomVirtuel(),b);
		this.file=file;
		
	}

	public NodePerso(ICloudFile file) throws RemoteException {super(file.getNomVirtuel());this.file=file;}

	
	public ICloudFile getFichier(){return this.file;}
	
	
}
