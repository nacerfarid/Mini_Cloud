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
import javax.swing.JTextField;

import core.CloudException;
import core.ICloudClient;
import core.ICloudFile;
import core.IDossier;

public class NouveauDossierFrame extends JDialog implements ActionListener {

	
	private JTextField champTexte;
	private JButton okBouton;
	private ICloudFile repertoireCourant;
	private ICloudClient client;

	private KeyListener keylistener = new KeyListener() {
		
    	@Override
    	public void keyPressed(KeyEvent e) {
    		if(e.getKeyCode()==KeyEvent.VK_ENTER) creerDossier();
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
	
	public NouveauDossierFrame(ICloudClient client, ICloudFile repertoireCourant) {
		super((JFrame)null, "Creation nouveau dossier", true);
		setSize(250, 120);
		setLocationRelativeTo(this.getParent());
		champTexte = new JTextField(20);
		okBouton = new JButton("OK");
		okBouton.addActionListener(this);
		Container contenu = getContentPane();
		contenu.setLayout(new FlowLayout());
		contenu.add(champTexte);
		contenu.add(okBouton);
		this.repertoireCourant = repertoireCourant;
		this.client = client;
		champTexte.addKeyListener(keylistener);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == okBouton) {
			creerDossier();
//			try {
//				if(champTexte.getText().equals("")) throw new CloudException("Rentrez un nom de dossier.");
//				((IDossier)repertoireCourant).creerDossier(champTexte.getText(), client.getUtilisateur());
//				setVisible(false);
//			} catch (RemoteException | CloudException e1) {
//				new ExceptionFrame(e1,(JFrame)this.getParent().getParent(),1);
//			}
		}
	}
	
	private void creerDossier() {
		try {
			if(champTexte.getText().equals("")) throw new CloudException("Rentrez un nom de dossier.");
			((IDossier)repertoireCourant).creerDossier(champTexte.getText(), client.getUtilisateur());
			setVisible(false);
		} catch (RemoteException | CloudException e1) {
			new ExceptionFrame(e1,(JFrame)this.getParent().getParent(),1);
		}
	}
	
}
