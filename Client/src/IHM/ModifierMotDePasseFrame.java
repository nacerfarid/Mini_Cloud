package IHM;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import core.Hashage;
import core.ICloudClient;
import core.ICloudFile;
import core.IUtilisateur;

public class ModifierMotDePasseFrame extends JDialog implements ActionListener{

	private JLabel ancienMdpLabel;
	private JLabel nouveauMdpLabel;
	private JLabel repeterMdpLabel;
	
	private JPasswordField ancienMdpContenu;
	private JPasswordField nouveauMdpContenu;
	private JPasswordField repeterMdpContenu;
	private JButton okBouton;
	private ICloudFile repertoireCourant;
	private ICloudClient client;
	private IUtilisateur utilisateur;
	
	private KeyListener keylistener = new KeyListener() {
		
    	@Override
    	public void keyPressed(KeyEvent e) {
    		if(e.getKeyCode()==KeyEvent.VK_ENTER)
				try {
					modifierMdp();
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
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

	
	public ModifierMotDePasseFrame(IUtilisateur utilisateur) {
		super((JFrame)null, "Modification du mot de passe", true);
		this.utilisateur = utilisateur;
		
		setSize(300, 170);
		setLocationRelativeTo(this.getParent());
		ancienMdpLabel = new JLabel("Ancien mot de passe   ");
		repeterMdpLabel = new JLabel("Repetez mot de passe ");
		nouveauMdpLabel = new JLabel("Nouveau mot de passe");
		ancienMdpContenu = new JPasswordField(10);
		nouveauMdpContenu = new JPasswordField(10);
		repeterMdpContenu = new JPasswordField(10);
		okBouton = new JButton("OK");
		okBouton.addActionListener(this);
		Container contenu = getContentPane();
		contenu.setLayout(new FlowLayout());
		contenu.add(ancienMdpLabel);
		contenu.add(ancienMdpContenu);
		contenu.add(repeterMdpLabel);
		contenu.add(repeterMdpContenu);
		contenu.add(nouveauMdpLabel);
		contenu.add(nouveauMdpContenu);
		contenu.add(okBouton);
		ancienMdpContenu.setEchoChar('*');
		repeterMdpContenu.setEchoChar('*');
		nouveauMdpContenu.setEchoChar('*');
		nouveauMdpContenu.addKeyListener(keylistener);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == okBouton) {
			try {
				if(String.valueOf(ancienMdpContenu.getPassword()).equals("") || String.valueOf(repeterMdpContenu.getPassword()).equals("") || String.valueOf(nouveauMdpContenu.getPassword()).equals("")) throw new CloudException("Remplir tous les champs");
				if(!String.valueOf(ancienMdpContenu.getPassword()).equals(String.valueOf(repeterMdpContenu.getPassword()))) throw new CloudException("Les mots de passe ne correspondent pas.");
				modifierMdp();
			} catch (Exception e1) {
				new ExceptionFrame(e1,(JFrame)this.getParent().getParent(),1);
			}
			
		}
	}

	private void modifierMdp() throws RemoteException {
		// TODO Auto-generated method stub
		utilisateur.setMotDePasse(Hashage.hashSHA2(String.valueOf(this.nouveauMdpContenu.getPassword())), Hashage.hashSHA2(String.valueOf(this.ancienMdpContenu.getPassword())));
		this.dispose();
	}

}
