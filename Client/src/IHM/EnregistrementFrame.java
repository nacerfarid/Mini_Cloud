package IHM;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import core.Hashage;
import core.ICloud;
import core.ICloudClient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanfilma
 */
public class EnregistrementFrame extends JFrame implements ActionListener {
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
    private JButton enregistrement;
    private ICloud cloud;
    
    public EnregistrementFrame(int i, int j, ICloud cloud)
    {
        
        // MODE ENREGISTREMENT
    	this.cloud = cloud;
        this.setSize(i, j);
        this.setTitle("S'enregistrer");
        
        contenu = getContentPane();
        contenu.setLayout(null);
        
        // Methode Generale
        remplirFenetre();
		
	/*
        NOM
        mail
        prenom
        quota
	*/	
        mdpIntitule = new JLabel();
        mdpIntitule.setText("MDP :");
        mdpIntitule.setBounds(largeur/8, 2*hauteur/6, 80, 20);
        contenu.add(mdpIntitule);
                
        mdpContenu = new JPasswordField(15);
        mdpContenu.setBounds(largeur/8+mdpIntitule.getWidth(), 2*hauteur/6, 80, 20);
        mdpContenu.setEchoChar('*');
        contenu.add(mdpContenu);
        
        enregistrement = new JButton("Se Connecter");
        enregistrement.setBounds(4*largeur/8, 4*hauteur/6,140, 40);
        contenu.add(enregistrement);
        enregistrement.addActionListener(this);
        
    }
    
    public void remplirFenetre()
    {
        
        
       
        
        hauteur=this.getSize().height;
        largeur=this.getSize().width;
        
        loginIntitule = new JLabel();
        loginIntitule.setText("LOGIN :");
        loginIntitule.setBounds(largeur/8, hauteur/6, 80, 20);
        contenu.add(loginIntitule);
                
        loginContenu = new JTextField(20);
        loginContenu.setBounds(largeur/8+loginIntitule.getWidth(), hauteur/6, 80, 20);
        contenu.add(loginContenu);
        
        
        prenomIntitule = new JLabel();
        prenomIntitule.setText("PRENOM :");
        prenomIntitule.setBounds(4*largeur/8, hauteur/6, 80, 20);
        contenu.add(prenomIntitule);
                
        prenomContenu = new JTextField(15);
        prenomContenu.setBounds(4*largeur/8+prenomIntitule.getWidth(), hauteur/6, 80, 20);
        contenu.add(prenomContenu);
        
        nomIntitule = new JLabel();
        nomIntitule.setText("NOM :");
        nomIntitule.setBounds(4*largeur/8, 2*hauteur/6, 80, 20);
        contenu.add(nomIntitule);
                
        nomContenu = new JTextField(15);
        nomContenu.setBounds(4*largeur/8+nomIntitule.getWidth(), 2*hauteur/6, 80, 20);
        contenu.add(nomContenu);
        
        mailIntitule = new JLabel();
        mailIntitule.setText("MAIL :");
        mailIntitule.setBounds(4*largeur/8, 3*hauteur/6, 80, 20);
        contenu.add(mailIntitule);
                
        mailContenu = new JTextField(15);
        mailContenu.setBounds(4*largeur/8+mailIntitule.getWidth(), 3*hauteur/6, 80, 20);
        contenu.add(mailContenu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       if (e.getSource() == enregistrement){
    	   try {
    		   if (loginContenu.getText().equals(""))throw new CloudException("Login non renseigné");
    		   if (mdpContenu.getPassword().length == 0)throw new CloudException("MDP non renseigné");
    		   
    		   System.out.println("login: "+loginContenu.getText());
    		   System.out.println("password: "+String.valueOf(mdpContenu.getPassword()));
    		   cloud.enregistrement(loginContenu.getText(), Hashage.hashSHA2(String.valueOf(mdpContenu.getPassword())), nomContenu.getText(), prenomContenu.getText(), mailContenu.getText());
    		   this.dispose();
    	   } catch(CloudException | core.CloudException | RemoteException ex){
   			new ExceptionFrame(ex,this,1);
           } 
           
    	   
       }
       
       
    }
}
