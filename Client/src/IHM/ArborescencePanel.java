
package IHM;

import static core.EType.Repertoire;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import core.EType;
import core.ICloudClient;
import core.ICloudFile;
import core.IDossier;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanfilma
 */
public class ArborescencePanel extends JPanel implements MouseListener {
	private ICloudClient cloudclient;
	private JTree arbre;
	private MainPanel mainpanel;
   
    public ArborescencePanel(ICloudClient cloudclient) throws RemoteException
    {
    	this.setBackground(Color.WHITE);
    	this.refresh(cloudclient);
    }
    
    public void refresh(ICloudClient cloudclient) throws RemoteException
    {
    	this.cloudclient = cloudclient;
    	//DefaultMutableTreeNode fichiers = new DefaultMutableTreeNode("Fichiers");
    	
    	
    	NodePerso racine = new NodePerso(cloudclient.getRacine(),true);
    	arbre = new JTree(racine);
    	
    	arbre.setCellRenderer(new MyTreeCellRenderer());
    	
    	this.remplir(cloudclient.getRacine(), racine);
    	this.add(arbre);
    	arbre.addMouseListener(this);
    }

    public void remplir(IDossier dossier,NodePerso racine) throws RemoteException
    {
    	for (ICloudFile i : dossier.getContenu())
    	{
    		NodePerso courant = new NodePerso(i);
    		racine.add(courant);
    		if (i.getType() == EType.Repertoire)
    		{
    			this.remplir((IDossier)i, courant);
    		}
    	}
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		NodePerso nodeSelectionne = (NodePerso) arbre.getLastSelectedPathComponent();
			try {
				if (nodeSelectionne.getFichier().getType() == Repertoire)
					mainpanel.refresh(nodeSelectionne.getFichier());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void setMainPanel(MainPanel principalpanel) {
		this.mainpanel = principalpanel;
		
	}
}
