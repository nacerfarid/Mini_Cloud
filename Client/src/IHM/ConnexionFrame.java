package IHM;

import core.EConfiguration;
import core.Hashage;
import core.ICloud;
import core.ICloudClient;
import core.IConfiguration;
import core.IUtilisateur;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.*;

public class ConnexionFrame extends JFrame implements ActionListener{
	
	public static ICloud cloud;
	private final static String RMILookup = "cloud";
	private final static int port = 42678;
	private IUtilisateur utilisateur;
	
	private JLabel logintext;
	private JTextField loginsaisie;
	private JLabel mdptext;
	private JPasswordField mdpsaisie;
	private int largeur;
	private int hauteur;
	private int nblig = 6;
	private int nbcol = 10;
	private JButton connexion;
	private JButton inscription;
	Container contenu;
    int largeurcloudframe = 800;
    int hauteurcloudframe = 600;
    
    private KeyListener keylistener = new KeyListener() {
		
    	@Override
    	public void keyPressed(KeyEvent e) {
    		if(e.getKeyCode()==KeyEvent.VK_ENTER) seConnecter();
    	}

    	@Override
    	public void keyReleased(KeyEvent e) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public void keyTyped(KeyEvent e) {
    		// TODO Auto-generated method stub
    		
    	}
	};
	
	
	public ConnexionFrame(int larg, int haut) throws RemoteException, NotBoundException{
		this.setTitle("FastCloud");
		this.largeur = larg;
		this.hauteur = haut;
		this.setSize(this.largeur, this.hauteur);
		contenu = getContentPane();
		contenu.setLayout(null);
		
		logintext = new JLabel();
		logintext.setBounds(largeur/nbcol, hauteur/nblig,80, 20);
		logintext.setText("LOGIN :");
		contenu.add(logintext);
		
		loginsaisie = new JTextField(30);
		loginsaisie.setBounds(largeur/nbcol+logintext.getWidth(), hauteur/nblig,80,20);
		contenu.add(loginsaisie);
		
		mdptext = new JLabel();
		mdptext.setBounds(largeur/nbcol, 2*hauteur/nblig,80, 20);
		mdptext.setText("MDP :");
		contenu.add(mdptext);
		
		mdpsaisie = new JPasswordField(30);
		mdpsaisie.setBounds(largeur/nbcol+mdptext.getWidth(), 2*hauteur/nblig,80,20);
		mdpsaisie.setEchoChar('*');
		contenu.add(mdpsaisie);
		
		connexion = new JButton("Se Connecter");
		connexion.setBounds(6*largeur/nbcol, 3*hauteur/nblig,140, 40);
		contenu.add(connexion);
		connexion.addActionListener(this);
		
		mdpsaisie.addKeyListener(keylistener);
		loginsaisie.addKeyListener(keylistener);
		
		Registry registry = LocateRegistry.getRegistry(port);
		cloud = (ICloud) registry.lookup(RMILookup);
		
		// config enregistrer
		if(cloud.getConfigurationServeur().getConfigValue(EConfiguration.SERVEUR_ENREGISTREMENT))
			this.boutonenregistrer();
	}

	private void boutonenregistrer() {
		inscription = new JButton("Pas encore inscrit ? ");
		inscription.setBounds(largeur/nbcol, 3*hauteur/nblig,150,40);
		Font f=inscription.getFont().deriveFont(10.0f);
		inscription.setFont(f);
		contenu.add(inscription);
		inscription.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connexion){
			seConnecter(); 
        }
		if (e.getSource() == inscription)
			sEnregistrer();
	}
	
	private IUtilisateur login() {
		// TODO check si le user est freeze
		
		IUtilisateur utilisateur = null;
		//Connexion au serveur
		try {
			//Registry registry = LocateRegistry.getRegistry(port);
			//cloud = (ICloud) registry.lookup(RMILookup);
			
			IConfiguration configuration = cloud.getConfigurationServeur();
			utilisateur = cloud.login(loginsaisie.getText(), Hashage.hashSHA2(String.valueOf(mdpsaisie.getPassword())));
			
		} catch(RemoteException ex) {
			ex.printStackTrace();
		}
		return utilisateur;
	}	

    private void lancerFrame(ICloudClient icloud, IConfiguration config) {
            try {
                JFrame cloud = new CloudFrame(icloud,config);
                cloud.pack();
                cloud.setSize(largeurcloudframe,hauteurcloudframe);
                cloud.setLocationRelativeTo(null);
                //cloud.setResizable(false);
                cloud.setVisible(true);
                this.dispose();
            } catch (RemoteException ex) {
                ExceptionFrame exceptionFrame = new ExceptionFrame(new CloudException("Probl�me de RMI"),(JFrame)this.getParent(),1); 
            }
    }
	
    private void seConnecter() {
    	try{
            if (loginsaisie.getText().equals(""))throw new CloudException("Login non renseigné");
            if (mdpsaisie.getPassword().length == 0)throw new CloudException("MDP non renseigné");
            utilisateur = login();
            if (utilisateur == null)throw new CloudException("Erreur identifiants");
            if(utilisateur.getFreeze()) throw new CloudException("Compte bloque, contactez l'administrateur");
            // RECUP CONFIG ET ICloudClient
            IConfiguration config = cloud.getConfigurationServeur();
            ICloudClient icloud = utilisateur.getCloudClient();
            
            lancerFrame(icloud,config);
            
		}catch(CloudException | core.CloudException | RemoteException ex){
			new ExceptionFrame(ex,this,1);
        } 
    }
    
    private void sEnregistrer() {
    	EnregistrementFrame fen = new EnregistrementFrame(500,400, cloud);
        fen.setLocationRelativeTo(null);
        fen.setResizable(false);
        fen.setVisible(true);
    }
}
