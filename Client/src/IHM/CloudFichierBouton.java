package IHM;

import core.*;
import java.rmi.RemoteException;
import javax.swing.AbstractButton;
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
public abstract class CloudFichierBouton extends JButton{
    private ICloudFile fichierAssocie;
    
    public CloudFichierBouton(ICloudFile file) throws RemoteException
    {
        this.fichierAssocie = file;
        this.setText(file.getNomVirtuel());
        this.setVerticalTextPosition(AbstractButton.BOTTOM);
        this.setHorizontalTextPosition(AbstractButton.CENTER); 
    }

    /**
     * @return the fichierAssocié
     */
    public ICloudFile getFichierAssocie() {
        return fichierAssocie;
    }

    /**
     * @param fichierAssocié the fichierAssocié to set
     */
    public void setFichierAssocie(ICloudFile fichierAssocie) {
        this.fichierAssocie = fichierAssocie;
    }
    
    public String getNom() throws RemoteException
    {
        return fichierAssocie.getNomVirtuel();
    }
}
