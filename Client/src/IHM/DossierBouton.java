package IHM;

import java.rmi.RemoteException;

import core.ICloudFile;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanfilma
 */
public class DossierBouton extends CloudFichierBouton  {
    
    public DossierBouton(ICloudFile file) throws RemoteException
    {
        super(file);
        ImageIcon img = new ImageIcon(getClass().getResource("images/foldericon.png"));
        this.setIcon(img);
        //JLabel nomfichier = new JLabel("");
        
        
    }
    
}
