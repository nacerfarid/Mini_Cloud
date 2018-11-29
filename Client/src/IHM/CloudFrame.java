package IHM;

import core.*;
import core.CloudException;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.*;

public class CloudFrame extends JFrame implements ActionListener{
    private final int hauteur;
    private final int largeur;
    private final JMenuBar barreMenu;
    private final JMenu menu;
    private final JMenuItem upload;
    private final JButton flecheretour;
	private final JButton flecheSuivant;
    private final JButton flecheparent;
    private JMenuItem gestionProfil;
    private JMenu menuTrierPar;
    private JMenuItem menuTrierParNom;
    private JMenuItem menuTrierParType;
    private JMenuItem menuTrierParTaille;
    private JMenuItem menuTrierParDatecrea;
    private JMenuItem menuTrierParDatemodif;
    private final JButton menuSeDeconnecter;
    private JMenuItem menuNomUtilisateur;
    //private final Box bHor;
    private ArborescencePanel arbopanel;
    private MainPanel principalpanel;
    private final IConfiguration configuration;
	private final JButton boutonCorbeille;
	private ICloudClient cloudClient;
	private JMenuItem nouveauDossierMenu;
	private JOptionPane jop1;
    
    public CloudFrame(ICloudClient cloudclient,IConfiguration config) throws RemoteException
    {
       this.configuration = config;
       this.cloudClient = cloudclient;
       
       hauteur = this.getSize().height;
       largeur = this.getSize().width;
        
        
       barreMenu = new JMenuBar();
       this.setJMenuBar(barreMenu);
       
       
       
       menu = new JMenu("Menu");
       barreMenu.add(menu);
       
       upload = new JMenuItem("Upload");
       menu.add(upload);
       upload.addActionListener(this);
       
       nouveauDossierMenu = new JMenuItem("Nouveau dossier");
       menu.add(nouveauDossierMenu);
       nouveauDossierMenu.addActionListener(this);
       
       if(config.getConfigValue(EConfiguration.CLIENT_OPT_TRI)) {
    	   this.afficherMenuTrier();
       }
       
       if(config.getConfigValue(EConfiguration.CLIENT_VUE_PROFIL)) {
    	   this.afficherMenuProfil();
       }
       
       flecheretour = new JButton("retour");
       barreMenu.add(flecheretour);
       flecheretour.addActionListener(this);
       flecheretour.setEnabled(false);
       
       flecheSuivant = new JButton("suivant");
       barreMenu.add(flecheSuivant);
       flecheSuivant.addActionListener(this);
       flecheSuivant.setEnabled(false);
       
       flecheparent = new JButton("parent");
       barreMenu.add(flecheparent);
       flecheparent.addActionListener(this);
       flecheparent.setEnabled(false);
       
       IUtilisateur user = cloudclient.getUtilisateur();
       String login = user.getLogin();
       menuNomUtilisateur = new JMenuItem("Utilisateur : "+ login);
       menuNomUtilisateur.setFocusable(false);
       barreMenu.add(menuNomUtilisateur);

       
       barreMenu.add(Box.createHorizontalGlue());
       
       boutonCorbeille = new JButton();
       boutonCorbeille.setIcon(new ImageIcon(getClass().getResource("images/corbeille_icon.png")));
       barreMenu.add(boutonCorbeille);
       boutonCorbeille.addActionListener(this);
       
       menuSeDeconnecter = new JButton("Se Deconnecter");
       barreMenu.add(menuSeDeconnecter);
       menuSeDeconnecter.addActionListener(this);
       menuSeDeconnecter.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); 

      this.setLayout(new BorderLayout());
       // TO DO : si arborescence
       
      principalpanel = new MainPanel(cloudclient, config);
      this.add(principalpanel,BorderLayout.CENTER);
      
      if(configuration.getConfigValue(EConfiguration.CLIENT_VUE_ARBORESCENCE)) {
    	  arbopanel = new ArborescencePanel(cloudclient);
          this.add(arbopanel,BorderLayout.LINE_START); 
          
          arbopanel.setMainPanel(principalpanel);
          principalpanel.setArboPanel(arbopanel);
      }
          
      if ((this.configuration.getConfigValue(EConfiguration.SERVEUR_NOTIF_POPUP))||(configuration.getConfigValue(EConfiguration.SERVEUR_NOTIF_MAIL))) {
	      jop1 = new JOptionPane();
	      List<ICloudFile> liste = user.getFichiersANotifier();
	      String message ="";
	      for (ICloudFile i : liste)
	      {
	    	  message += i.getNomVirtuel()+"a été notifié\n";
	      }
	      
	      if (liste.size()>0) jop1.showMessageDialog(null, message, "Message informatif", JOptionPane.INFORMATION_MESSAGE);
      }
    }
    
    public JButton getButtonSuivant(){
    	return flecheSuivant;
    }
    public JButton getButtonPrecedent(){
    	return flecheretour;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == upload){
        	try {
				principalpanel.upload();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        
        if (e.getSource() == flecheretour){
            try {
				principalpanel.retour();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        if (e.getSource() == flecheSuivant){
            try {
				principalpanel.suivant();
				
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        if (e.getSource() == flecheparent){
            try {
				principalpanel.parent();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
        }
        if (e.getSource() == nouveauDossierMenu){
            try {
				principalpanel.nouveauDossier();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        if (e.getSource() == boutonCorbeille){
            try {
				if(!principalpanel.getCorbeille()) {
					principalpanel.refresh(cloudClient.getCorbeille());
					principalpanel.setCorbeille(true);
					this.boutonCorbeille.setIcon(null);
					this.boutonCorbeille.setText(principalpanel.getElementCourant().getNomVirtuel());
					this.boutonCorbeille.repaint();
					principalpanel.setNavigation(false);
					this.arbopanel.setVisible(false);
				}
				else {
					principalpanel.refresh(principalpanel.getElementCourant());
					principalpanel.setCorbeille(false);
					this.boutonCorbeille.setIcon(new ImageIcon(getClass().getResource("images/corbeille_icon.png")));
					this.boutonCorbeille.setText("");
					this.boutonCorbeille.repaint();
					principalpanel.setNavigation(true);
					this.arbopanel.setVisible(true);
				}
            	
				
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        if (e.getSource() == menuSeDeconnecter){
            {
            	JFrame connex;
				try {
					connex = new ConnexionFrame(400,200);
					connex.setLocationRelativeTo(null);
	        		connex.setResizable(false);
	        		connex.setVisible(true);
				} catch (RemoteException | NotBoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		this.dispose();
            }
        }
      
        if (e.getSource() == menuTrierParNom){
            System.out.println("On va trier par : "+menuTrierParNom.getText());
        }
       
        if (e.getSource() == menuTrierParType){
            System.out.println("On va trier par : "+menuTrierParType.getText());
        }
        
        if (e.getSource() == menuTrierParTaille){
            System.out.println("On va trier par : "+menuTrierParTaille.getText());
        }

        if (e.getSource() == menuTrierParDatecrea){
            System.out.println("On va trier par : "+menuTrierParDatecrea.getText());
        }
        
        if (e.getSource() == menuTrierParDatemodif){
            System.out.println("On va trier par : "+menuTrierParDatemodif.getText());
        }
        
        if (e.getSource() == gestionProfil){
        	ModificationProfilFrame fen;
			try {
				fen = new ModificationProfilFrame(500,400, this.cloudClient.getUtilisateur(), this.configuration,principalpanel,this.cloudClient.getRacine(),this);
				fen.setLocationRelativeTo(null);
	            fen.setResizable(false);
	            fen.setVisible(true);
			} catch (CloudException | RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        
    }

    private void afficherMenuTrier() {
       menuTrierPar = new JMenu("Trier Par");
       menu.add(menuTrierPar);
       
       menuTrierParNom = new JMenuItem("Nom");
       menuTrierPar.add(menuTrierParNom);
       menuTrierParNom.addActionListener(this);
       
       menuTrierParType = new JMenuItem("Type");
       menuTrierPar.add(menuTrierParType);
       menuTrierParType.addActionListener(this);
       
       menuTrierParTaille = new JMenuItem("Taille");
       menuTrierPar.add(menuTrierParTaille);
       menuTrierParTaille.addActionListener(this);
       
       menuTrierParDatecrea = new JMenuItem("Date de Création");
       menuTrierPar.add(menuTrierParDatecrea);
       menuTrierParDatecrea.addActionListener(this);
       
       menuTrierParDatemodif = new JMenuItem("Date de modification");
       menuTrierPar.add(menuTrierParDatemodif);
       menuTrierParDatemodif.addActionListener(this);
       
       
    }

    private void afficherMenuProfil() {
       gestionProfil = new JMenuItem("Gestion Profil");
       menu.add(gestionProfil);
       gestionProfil.addActionListener(this);
    }

	public JButton getButtonParent() {
		return flecheparent;
	}

	public void refresh() {
		 IUtilisateur user;
		try {
			user = this.cloudClient.getUtilisateur();
			String login = user.getLogin();
	    	this.menuNomUtilisateur.setText("Utilisateur : "+login);
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
    	
    }
	
}
