package IHM;

import static core.EType.Repertoire;
import core.EConfiguration;
import core.EType;
import core.ICloudClient;
import core.ICloudFile;
import core.IConfiguration;
import core.IDossier;
import core.IFichier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.Configuration;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanfilma
 */
public class MainPanel extends JPanel implements MouseListener, ActionListener {
	private final JLabel cheminCourant;
	private InfoFichierPanel infoPanel;
	ArrayList<CloudFichierBouton> fichiersaffiches;
	ArrayList<ICloudFile> ifichiers;
	JPanel corps;
	private JPopupMenu menuDroitVide;
	private JMenuItem uploadVide;
	private JMenuItem newDossierVide;
	private int nbcolonnes = 4;
	private JPopupMenu menuDroitDossier;
	private JMenuItem downloadFichier;
	private JMenuItem renommerDossier;
	private JMenuItem supprimerFichier;
	private ICloudClient cloudclient;
	private IDossier repertoireCourant;
	private IDossier repertoireParent;
	private Stack<IDossier> pileRetour;
	private int curseur;
	private boolean corbeille;
	private JMenuItem videCorbeille;
	private JPanel entete;
	private ArborescencePanel arbopanel;
	private ICloudFile fichierSelectionne;
	private JMenuItem restaurerFichier;
	private JMenuItem sabonnerFichier;
	private IConfiguration config;

	public MainPanel(ICloudClient icloudclient, IConfiguration config) throws RemoteException {
		this.cloudclient = icloudclient;
		this.setLayout(new BorderLayout());
		this.config = config;
		entete = new JPanel();
		entete.setBackground(Color.LIGHT_GRAY);
		this.add(entete, BorderLayout.PAGE_START);
		cheminCourant = new JLabel("");
		entete.add(cheminCourant);
		this.pileRetour = new Stack<IDossier>();
		this.curseur = 1;

		// on récupère les premières informations, à remettre ensuite quand
		// on reaffiche le tout
		repertoireCourant = cloudclient.getRacine();
		repertoireParent = repertoireCourant;
		ifichiers = repertoireCourant.getContenu();

		pileRetour.push(repertoireCourant);

		this.remplirEntete();

		corps = new JPanel();
		this.add(corps, BorderLayout.CENTER);
		corps.addMouseListener(this);

		GridBagLayout g = new GridBagLayout();
		corps.setLayout(g);

		this.AfficherDossiers();

		if(config.getConfigValue(EConfiguration.CLIENT_VUE_INFO_FICHIER)) {
			infoPanel = new InfoFichierPanel();
			this.add(infoPanel,BorderLayout.SOUTH);
			infoPanel.setBackground(Color.GRAY);
			infoPanel.setVisible(false);
		}

	}

	

	@Override
	public void mouseClicked(MouseEvent e) {
		// CLik dans le vide
		if (e.getSource() == corps) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				/*try {
					System.out.println(cloudclient.getUtilisateur().getLogin());
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				// Bouton gauche = InfoPanel
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				afficherPopUpvide(e);
			}
		}

		// Clik sur les fichiers
		for (CloudFichierBouton i : fichiersaffiches) {
			if (e.getSource() == i) {
				
				if (e.getClickCount() == 2) {
					try {
						if (i.getFichierAssocie().getType() == Repertoire) {
							this.refresh(i.getFichierAssocie());
							if (!corbeille)addToHistorique(repertoireCourant);
						}
					} catch (RemoteException ex) {
						new ExceptionFrame(new CloudException(
								"Problème de RMI"), (JFrame) this.getParent(),
								1);
					}
				}
				
				else {
					// Bouton gauche = InfoPanel
					if (e.getButton() == MouseEvent.BUTTON1) {
						try {
							if(infoPanel != null) {
								this.infoPanel.setFichier(i.getFichierAssocie());
								infoPanel.setVisible(true);
							}
							
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						try {
							afficherPopUpFichier(i.getFichierAssocie());
							menuDroitDossier.show(corps, i.getX() + 50,i.getY() + 50);
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	private void afficherPopUpvide(MouseEvent e) {
		menuDroitVide = new JPopupMenu();
		// corps.add(menuDroitVide);


		if (!corbeille) {
			uploadVide = new JMenuItem("Upload");
			menuDroitVide.add(uploadVide);
			uploadVide.addActionListener(this);

			newDossierVide = new JMenuItem("Nouveau dossier");
			menuDroitVide.add(newDossierVide);
			newDossierVide.addActionListener(this);
		}

		if (corbeille) {
			videCorbeille = new JMenuItem("Vider la corbeille");
			menuDroitVide.add(videCorbeille);
			videCorbeille.addActionListener(this);
		}

		menuDroitVide.show(corps, e.getX(), e.getY());
	}

	private void afficherPopUpFichier(ICloudFile i) throws RemoteException {
		menuDroitDossier = new JPopupMenu();
		// corps.add(menuDroitVide);
		this.fichierSelectionne = i;
		if (!corbeille){
		if (i.getType()!=Repertoire)
		{
			downloadFichier = new JMenuItem("Download");
			menuDroitDossier.add(downloadFichier);
			downloadFichier.addActionListener(this);
		}
		
		renommerDossier = new JMenuItem("Renommer");
		menuDroitDossier.add(renommerDossier);
		renommerDossier.addActionListener(this);
		
		if ((this.config.getConfigValue(EConfiguration.SERVEUR_NOTIF_POPUP))||(config.getConfigValue(EConfiguration.SERVEUR_NOTIF_MAIL))) {
			sabonnerFichier = new JMenuItem("S'abonner");
			menuDroitDossier.add(sabonnerFichier);
			sabonnerFichier.addActionListener(this);
		}
		
		supprimerFichier = new JMenuItem("Supprimer");
		menuDroitDossier.add(supprimerFichier);
		supprimerFichier.addActionListener(this);
		}
		else {
		restaurerFichier = new JMenuItem("Restaurer");
		menuDroitDossier.add(restaurerFichier);
		restaurerFichier.addActionListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == uploadVide) {
			try {
				this.upload();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if (e.getSource() == newDossierVide) {
			try {
				this.nouveauDossier();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == videCorbeille) {
			try {
				this.refresh(this.cloudclient.getCorbeille());
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == downloadFichier) {
			try {
				download((IFichier)fichierSelectionne);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == renommerDossier) {
			JOptionPane jop = new JOptionPane(); 
		    String newNom = jop.showInputDialog(null, "Veuillez choisir un nouveau nom en gardant l'extension", "Renommer fichier", JOptionPane.QUESTION_MESSAGE);
		    try {
		    	System.out.println(fichierSelectionne.getType());
		    	if(!newNom.contains(".") && !(fichierSelectionne.getType()==EType.Repertoire) && !(fichierSelectionne.getType()==EType.Fichier)) {
		    		String[] tab = fichierSelectionne.getNomVirtuel().split("\\.");
		    		newNom += "."+tab[tab.length-1];
		    	}
				fichierSelectionne.setNomVirtuel(newNom);
				this.refresh(repertoireCourant);
				this.refreshArboPanel();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    
		  }
			
	
		if (e.getSource() == supprimerFichier) {
			try {
				this.fichierSelectionne.supprimer();
				this.refresh(repertoireCourant);
				this.refreshArboPanel();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if (e.getSource() == restaurerFichier) {
			try {
				this.fichierSelectionne.restaurer();
				this.refresh(repertoireCourant);
				this.refreshArboPanel();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if (e.getSource() == sabonnerFichier) {
			{
				try {
					this.fichierSelectionne.abonner(this.cloudclient.getUtilisateur().getID());
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void afficherInfoFichier() {

	}

	public void clearInfoFichier() {

	}

	public void AfficherDossiers() throws RemoteException {

		fichiersaffiches = new ArrayList<CloudFichierBouton>();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(20, 20, 20, 20);

		// Boucle parcours i fichiers = nouveau bouton à laquel on associe le
		// fichier

		for (ICloudFile i : ifichiers) {
			c.gridx = ifichiers.indexOf(i) % nbcolonnes * 50; // pour avoir les
																// boutons
																// différenciés
			// c.gridy = i/nblignes*50;
			// c.gridwidth = 200;
			// c.gridheight = 200;
			// c.weightx = 0;
			// c.weighty = 0;

			// On vérifie ici l'instance dossier ou fichier
			if (i.getType() == Repertoire)
				fichiersaffiches.add(new DossierBouton(i));
			else {
				fichiersaffiches.add(new FichierBouton(i));
			}
			fichiersaffiches.get(ifichiers.indexOf(i)).addMouseListener(this);
			corps.add(fichiersaffiches.get(ifichiers.indexOf(i)), c);
			// corps.add(new FichierPanel(),c);
		}

	}

	private void remplirEntete() throws RemoteException {
		cheminCourant.setText(getPath());
	}

	public void refresh(ICloudFile i) throws RemoteException {
		// se positionner

		repertoireCourant = (IDossier) i;
		
		
		CloudFrame cf = (CloudFrame) SwingUtilities.getWindowAncestor(this);
		if(repertoireCourant.equals(this.cloudclient.getCorbeille()) || repertoireCourant.equals(this.cloudclient.getRacine())) {
			cf.getButtonParent().setEnabled(false);
		}
		else 
		{
			repertoireParent = (IDossier) i.getParent();
			cf.getButtonParent().setEnabled(true);
		}
			
		
		ifichiers = repertoireCourant.getContenu();
		corps.removeAll();
		this.revalidate();
		this.repaint();

		this.remplirEntete();
		this.AfficherDossiers();
		
		
	}

	public void retour() throws RemoteException {
		curseur--;
		refresh(pileRetour.get(curseur - 1));
		CloudFrame cf = (CloudFrame) SwingUtilities.getWindowAncestor(this);
		if (curseur == 1)
			cf.getButtonPrecedent().setEnabled(false);
		cf.getButtonSuivant().setEnabled(true);
	}

	public void suivant() throws RemoteException {

		curseur++;
		refresh(pileRetour.get(curseur - 1));

		CloudFrame cf = (CloudFrame) SwingUtilities.getWindowAncestor(this);
		if (pileRetour.size() == curseur) cf.getButtonSuivant().setEnabled(false);
		cf.getButtonPrecedent().setEnabled(true);
	}

	public void parent() throws RemoteException {
		refresh(repertoireParent);
	}

	public void nouveauDossier() throws RemoteException {
		NouveauDossierFrame nouveauDossier = new NouveauDossierFrame(this.cloudclient, repertoireCourant);
		nouveauDossier.setVisible(true);
		refresh(repertoireCourant);
		this.refreshArboPanel();
	}

	public boolean getCorbeille() {
		return corbeille;
	}

	public void setCorbeille(boolean corbeille) {
		this.corbeille = corbeille;
	}

	public void addToHistorique(IDossier dossier) {
		while(pileRetour.size() != curseur) pileRetour.pop();
		pileRetour.add(curseur, dossier);
		curseur++;
		CloudFrame cf = (CloudFrame) SwingUtilities.getWindowAncestor(this);
		if (pileRetour.size() == curseur) cf.getButtonSuivant().setEnabled(false);
		cf.getButtonPrecedent().setEnabled(true);
	}
	
	public void setNavigation(Boolean value){
		CloudFrame cf = (CloudFrame) SwingUtilities.getWindowAncestor(this);
		if (curseur != 1) cf.getButtonPrecedent().setEnabled(value);
		if (pileRetour.size() != curseur) cf.getButtonSuivant().setEnabled(value);
	}
	public IDossier getElementCourant(){
		return pileRetour.elementAt(curseur-1);
	}
	
	public void upload() throws IOException {
		JFileChooser fileChooser = new JFileChooser(new File("."));
    	File fichier = null;
    	if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
    		fichier = fileChooser.getSelectedFile();
    	}
    	
		if (fichier != null) {
	    	boolean exist = fichier.exists();
			byte buffer[] = new byte[(int) fichier.length()];
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(fichier));
			input.read(buffer, 0, buffer.length);
			repertoireCourant.upload(fichier.getName(), buffer, this.cloudclient.getUtilisateur());
			input.close();
			}
    	refresh(repertoireCourant);
    	
    	this.refreshArboPanel();
	}
	
	public void download(IFichier fichierADownload) throws IOException {
		JFileChooser fileChooser = new JFileChooser(new File("."));
    	File fichier = null;
    	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
    		fichier = fileChooser.getSelectedFile();
    	}
		
		if(fichierADownload != null){
			byte download[] = fichierADownload.download();
			File newFile;
			if (fichier!= null){
				newFile = new File(fichier.getPath()+"/"+fichierADownload.getNomVirtuel());
			}
			else {newFile = new File("./"+fichierADownload.getNomVirtuel());}
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newFile));
			bufferedOutputStream.write(download, 0, download.length);
			bufferedOutputStream.close();
		}
    	
	}
	
	public String getPath() throws RemoteException {
		String path = "";
		IDossier repCourant = repertoireCourant;
		IDossier repParent = (IDossier) repertoireCourant.getParent();
		while(repParent != null) {
			path = repCourant.getNomVirtuel()+"/"+path;
			repCourant = (IDossier) repCourant.getParent();
			repParent = (IDossier) repParent.getParent();
			
		}
		path = repCourant.getNomVirtuel()+"/"+path;
		path = path.substring(0,path.length()-1);
		return path;
	}



	public void setArboPanel(ArborescencePanel arbopanel) {
		this.arbopanel = arbopanel;
		
	}
	
	public void refreshArboPanel() throws RemoteException
	{
		this.arbopanel.removeAll();
		this.arbopanel.repaint();
		this.arbopanel.refresh(this.cloudclient);

	}
}