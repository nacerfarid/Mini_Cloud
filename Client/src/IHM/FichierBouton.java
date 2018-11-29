package IHM;

import java.rmi.RemoteException;

import core.ICloudFile;

import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanfilma
 */
public class FichierBouton extends CloudFichierBouton {
    
    public FichierBouton(ICloudFile file) throws RemoteException
    {
        super(file);
        ImageIcon img;
        switch(file.getType()) {
        case Image: img = new ImageIcon(getClass().getResource("images/image_icon.png"));
        	this.setIcon(img);
        	break;
        case Video : img = new ImageIcon(getClass().getResource("images/video_icon.png"));
	    	this.setIcon(img);
	    	break;
        case Document : img = new ImageIcon(getClass().getResource("images/file.png"));
	    	this.setIcon(img);
	    	break;
        case Audio : img = new ImageIcon(getClass().getResource("images/musique_icon.png"));
	    	this.setIcon(img);
	    	break;
        case Web : img = new ImageIcon(getClass().getResource("images/web_icon.png"));
	    	this.setIcon(img);
	    	break;
        case Application : img = new ImageIcon(getClass().getResource("images/application_icone.png"));
	    	this.setIcon(img);
	    	break;
        case Corbeille : img = new ImageIcon(getClass().getResource("images/corbeille_icon.png"));
	    	this.setIcon(img);
	    	break;
		default : img = new ImageIcon(getClass().getResource("images/file.png"));
	    	this.setIcon(img);
	    	break;

        }
        
        //this.setIcon(new ImageIcon("./fileicon2.png"));
        //JLabel nomfichier = new JLabel("");
        
        
    }
    
}
