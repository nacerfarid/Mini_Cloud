package IHM;

import java.awt.Color;
import java.awt.FlowLayout;
import java.rmi.RemoteException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import core.ICloudFile;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanfilma
 */
public class InfoFichierPanel extends JPanel{
	private JLabel nomFichierLabel;
	private JLabel nomFichier;
	private JLabel typeLabel;
	private JLabel type;
	private JLabel tailleLabel;
	private JLabel taille;
	private JLabel proprietaireLabel;
	private JLabel proprietaire;
	private JLabel dateCreationLabel;
	private JLabel dateCreation;
	private JLabel dateModifLabel;
	private JLabel dateModif;
	private JLabel partageLabel;
	private JLabel partage;
	private ICloudFile file;
	
	public InfoFichierPanel() {
		this.setLayout(new FlowLayout());
		
		nomFichierLabel = new JLabel("Nom : ");
		nomFichier = new JLabel();
		typeLabel = new JLabel("Type : ");
		type = new JLabel();
		tailleLabel = new JLabel("Taille : ");
		taille = new JLabel();
		proprietaireLabel = new JLabel("Proprietaire : ");
		proprietaire = new JLabel();
		dateCreationLabel = new JLabel("Date creation : ");
		dateCreation = new JLabel();
		dateModifLabel = new JLabel("Date modif : ");
		dateModif = new JLabel();
		partageLabel = new JLabel("Partage avec : ");
		/*String listeUtilisateurs = this.file.getUtilisateursAyantAcces()
		for(int i = 0;i < this.file.getUtilisateursAyantAcces().size();i++) {
			if(this.file.getUtilisateursAyantAcces().get(i)) {
				listeUtilisateurs += " | "+this.file.getUtilisateursAyantAcces().;
			}
			
		}
		partage = new JLabel(listeUtilisateurs);*/
		this.add(nomFichierLabel);
		this.add(nomFichier);
		this.add(typeLabel);
		this.add(type);
		this.add(tailleLabel);
		this.add(taille);
		this.add(proprietaireLabel);
		this.add(proprietaire);
		this.add(dateCreationLabel);
		this.add(dateCreation);
		this.add(dateModifLabel);
		this.add(dateModif);
		this.add(partageLabel);
		//this.add(partage);
		this.setBackground(Color.LIGHT_GRAY);
	}
	
	
	
	public void setFichier(ICloudFile file) throws RemoteException
	{
		this.removeAll();
		
		this.file = file;
		nomFichierLabel = new JLabel("Nom : ");
		nomFichier = new JLabel(this.file.getNomVirtuel());
		typeLabel = new JLabel("Type : ");
		type = new JLabel(this.file.getType().toString());
		tailleLabel = new JLabel("Taille : ");
		taille = new JLabel(String.valueOf(this.file.getTaille()));
		proprietaireLabel = new JLabel("Proprietaire : ");
		proprietaire = new JLabel(this.file.getProprietaire().getLogin());
		dateCreationLabel = new JLabel("Date creation : ");
		dateCreation = new JLabel(this.file.getDateCreation().toString());
		dateModifLabel = new JLabel("Date modif : ");
		dateModif = new JLabel(this.file.getDateModification().toString());
		partageLabel = new JLabel("Partage avec : ");
		/*String listeUtilisateurs = this.file.getUtilisateursAyantAcces()
		for(int i = 0;i < this.file.getUtilisateursAyantAcces().size();i++) {
			if(this.file.getUtilisateursAyantAcces().get(i)) {
				listeUtilisateurs += " | "+this.file.getUtilisateursAyantAcces().;
			}
			
		}
		partage = new JLabel(listeUtilisateurs);*/
		this.add(nomFichierLabel);
		this.add(nomFichier);
		this.add(typeLabel);
		this.add(type);
		this.add(tailleLabel);
		this.add(taille);
		this.add(proprietaireLabel);
		this.add(proprietaire);
		this.add(dateCreationLabel);
		this.add(dateCreation);
		this.add(dateModifLabel);
		this.add(dateModif);
		this.add(partageLabel);
		
		this.revalidate();
		this.repaint();
	}
	
	
	
}
