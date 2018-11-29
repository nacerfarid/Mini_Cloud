package IHM;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import core.CloudException;
import core.EConfiguration;
import core.IConfiguration;
import core.IDossier;
import core.IUtilisateur;

public class ModificationProfilFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7960631213205168851L;
	private JLabel loginIntitule;
    private JTextField loginContenu;
    private JLabel mdpIntitule;
    private JPasswordField mdpContenu;
    private JLabel prenomIntitule;
    private JTextField prenomContenu;
    private JLabel nomIntitule;
    private JTextField nomContenu;
    private int hauteur;
    private int largeur;
    private JTextField mailContenu;
    private JLabel mailIntitule;
    private final Container contenu;
    private JButton modifier;
    private IUtilisateur utilisateur;
	private JButton boutonMdp;
	private IConfiguration config;
	private MainPanel mainpanel;
	private IDossier racine;
	private CloudFrame parentFrame;
	private JLabel quotaIntitule;
	private JLabel quotaContenu;
	
    
	public ModificationProfilFrame(int i, int j, IUtilisateur utilisateur, IConfiguration config, MainPanel principalpanel, IDossier iDossier, CloudFrame frame) throws CloudException, RemoteException {
		// MODE ENREGISTREMENT
        this.parentFrame = frame;
		this.mainpanel=principalpanel;
		this.setSize(i, j);
        this.setTitle("Modifier profil");
        this.utilisateur = utilisateur;
        this.config = config;
        contenu = getContentPane();
        contenu.setLayout(null);
		this.racine = iDossier;
	/*
        NOM
        mail
        prenom
        quota
	*/	
        
        hauteur=this.getSize().height;
        largeur=this.getSize().width;
        
        loginIntitule = new JLabel();
        loginIntitule.setText("LOGIN :");
        loginIntitule.setBounds(largeur/8, hauteur/6, 80, 20);
        contenu.add(loginIntitule);
                
        loginContenu = new JTextField(20);
        loginContenu.setBounds(largeur/8+loginIntitule.getWidth(), hauteur/6, 80, 20);
        loginContenu.setText(utilisateur.getLogin());
        loginContenu.setFocusable(false);
        contenu.add(loginContenu);
        
        
        prenomIntitule = new JLabel();
        prenomIntitule.setText("PRENOM :");
        prenomIntitule.setBounds(largeur/8, 2*hauteur/6, 80, 20);
        contenu.add(prenomIntitule);
                
        prenomContenu = new JTextField(15);
        prenomContenu.setBounds(largeur/8+prenomIntitule.getWidth(), 2*hauteur/6, 80, 20);
        prenomContenu.setText(utilisateur.getPrenom());
        contenu.add(prenomContenu);
        prenomContenu.setFocusable(false);
        
        nomIntitule = new JLabel();
        nomIntitule.setText("NOM :");
        nomIntitule.setBounds(4*largeur/8, 2*hauteur/6, 80, 20);
        contenu.add(nomIntitule);
                
        nomContenu = new JTextField(15);
        nomContenu.setBounds(4*largeur/8+nomIntitule.getWidth(), 2*hauteur/6, 80, 20);
        nomContenu.setText(utilisateur.getNom());
        nomContenu.setFocusable(false);
        contenu.add(nomContenu);
        
        mailIntitule = new JLabel();
        mailIntitule.setText("MAIL :");
        mailIntitule.setBounds(4*largeur/8, hauteur/6, 80, 20);
        contenu.add(mailIntitule);
                
        mailContenu = new JTextField(15);
        mailContenu.setBounds(4*largeur/8+mailIntitule.getWidth(), hauteur/6, 120, 20);
        mailContenu.setText(utilisateur.getEmail());
        mailContenu.setFocusable(false);
        contenu.add(mailContenu);
        
        
        if(config.getConfigValue(EConfiguration.SERVEUR_GERER_PROFIL)) {
        	loginContenu.setFocusable(true);
        	nomContenu.setFocusable(true);
        	prenomContenu.setFocusable(true);
        	mailContenu.setFocusable(true);
        	
        	modifier = new JButton("Appliquer modifications");
            modifier.setBounds(4*largeur/8, 4*hauteur/6,140, 40);
            contenu.add(modifier);
            modifier.addActionListener(this);
            
            boutonMdp = new JButton("Modifier mot de passe");
            boutonMdp.setBounds(largeur/8, 4*hauteur/6,140, 40);
            contenu.add(boutonMdp);
            boutonMdp.addActionListener(this);
            
        }
        
        if(config.getConfigValue(EConfiguration.CLIENT_VUE_QUOTA)) {
        quotaIntitule = new JLabel();
        quotaIntitule.setText("QUOTA :");
        quotaIntitule.setBounds(4*largeur/8, 3*hauteur/6, 80, 20);
        contenu.add(quotaIntitule);
                
        quotaContenu = new JLabel();
        quotaContenu.setBounds(4*largeur/8+quotaIntitule.getWidth(), 3*hauteur/6, 120, 20);
        quotaContenu.setText(String.valueOf(utilisateur.getQuota())+" Gio");
        contenu.add(quotaContenu);
        }
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==boutonMdp) {
			ModifierMotDePasseFrame modifMdpFrame = new ModifierMotDePasseFrame(utilisateur);
			modifMdpFrame.setVisible(true);
		}
		if(e.getSource()==modifier) {
			System.out.println("requete appliquer modifs");
			try {
				if(prenomContenu.getText().equals("") || nomContenu.getText().equals("") || loginContenu.getText().equals("") || mailContenu.getText().equals("")) throw new CloudException("Champ vide");
				utilisateur.setNom(nomContenu.getText());
				utilisateur.setPrenom(prenomContenu.getText());
				utilisateur.setEmail(mailContenu.getText());
				utilisateur.setLogin(loginContenu.getText());
				
				
				this.revalidate();
				this.repaint();
				mainpanel.refresh(this.racine);
				mainpanel.refreshArboPanel();
				this.parentFrame.refresh();
			} catch (CloudException | RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
