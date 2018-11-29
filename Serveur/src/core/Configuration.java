package core;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Configuration extends UnicastRemoteObject implements IConfiguration {

	private static final long serialVersionUID = 6814563783311443592L;
	private Map<EConfiguration, Boolean> configModule;
	private double quota;
	private int nbMaxUtilisateurs;
	private int bufferSize;
	private int nbMaxFichiersPartages;
	private double tailleMaxFichier;
	private int tailleMaxNomFichier;
	private int lgMinPassword;
	private int nbMaxAbonnements;
	private int nbMaxPartage;
	private int dureeCorbeille;
	private String adresseBDD;
	private String nomBDD;
	private String userBDD;
	private String passBDD;
	private String adresseMail;
	private String passwordMail;
	private String host;
	private String uploadPath;

	private int port;

	public Configuration(String filename) throws RemoteException {
		super();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder p;
		try {
			p = dbFactory.newDocumentBuilder();
			Document doc = p.parse(filename);
			Element root = doc.getDocumentElement();
			loadModules(root);
			loadParams(root);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new CloudException("Configuration : Erreur de chargement du fichier de configuration.");
		}

	}

	/**
	 * Permet, à partir de la racine XML, d’initialiser une map contenant les
	 * configurations et leurs valeurs.
	 * 
	 * @param racine
	 *            : Racine du document XML.
	 * @throws RemoteException
	 *             .
	 */
	private void loadModules(Element racine) {
		configModule = new HashMap<EConfiguration, Boolean>();

		try {
			XPathFactory xpf = XPathFactory.newInstance();
			XPath path = xpf.newXPath();
			String expression = "count(//config)";
			String str = (String) path.evaluate(expression, racine);
			int nbconfig = Integer.parseInt(str);

			// envoi des config pour remplir la map

			for (int i = 0; i < nbconfig; i++) {

				Node k = racine.getElementsByTagName("config").item(i).getAttributes().item(0);
				Node v = racine.getElementsByTagName("config").item(i).getAttributes().item(1);

				// stockage des élements dans la map

				EConfiguration c = this.getEnumConfig(k.getTextContent());
				Boolean b = new Boolean(v.getTextContent());

				configModule.put(c, b);
			}
		} catch (XPathExpressionException e) {
			throw new CloudException("Configuration : Erreur chargement de la configuration.");
		}
	}

	/**
	 * Permet à partir de la racine XML, d’initialiser les attributs
	 * correspondants aux différents paramètres contenues dans le fichier XML.
	 * 
	 * @param racine
	 *            : Racine du document XML.
	 * @throws RemoteException
	 *             .
	 */
	private void loadParams(Element racine) {
		quota = Double.parseDouble(racine.getElementsByTagName("quota").item(0).getTextContent());
		nbMaxUtilisateurs = Integer.parseInt(racine.getElementsByTagName("nbMaxUtilisateurs").item(0).getTextContent());
		bufferSize = Integer.parseInt(racine.getElementsByTagName("bufferSize").item(0).getTextContent());
		nbMaxFichiersPartages = Integer
				.parseInt(racine.getElementsByTagName("nbMaxFichiersPartages").item(0).getTextContent());
		tailleMaxFichier = Double.parseDouble(racine.getElementsByTagName("tailleMaxFichier").item(0).getTextContent());
		tailleMaxNomFichier = Integer
				.parseInt(racine.getElementsByTagName("tailleMaxNomFichier").item(0).getTextContent());
		lgMinPassword = Integer.parseInt(racine.getElementsByTagName("longueurMinPassword").item(0).getTextContent());
		nbMaxAbonnements = Integer.parseInt(racine.getElementsByTagName("nbMaxAbonnements").item(0).getTextContent());
		nbMaxPartage = Integer.parseInt(racine.getElementsByTagName("nbMaxPartage").item(0).getTextContent());
		dureeCorbeille = Integer.parseInt(racine.getElementsByTagName("dureeCorbeille").item(0).getTextContent());
		adresseBDD = racine.getElementsByTagName("adresseBDD").item(0).getTextContent();
		nomBDD = racine.getElementsByTagName("nomBDD").item(0).getTextContent();
		userBDD = racine.getElementsByTagName("userBDD").item(0).getTextContent();
		passBDD = racine.getElementsByTagName("passBDD").item(0).getTextContent();
		uploadPath = racine.getElementsByTagName("UploadPath").item(0).getTextContent();
		adresseMail = racine.getElementsByTagName("adresseMail").item(0).getTextContent();
		passwordMail = racine.getElementsByTagName("passwordMail").item(0).getTextContent();
		host = racine.getElementsByTagName("hostMail").item(0).getTextContent();
		port = Integer.parseInt(racine.getElementsByTagName("portMail").item(0).getTextContent());
	}

	/**
	 * @require paramok : cc != null
	 * @param cc
	 * @return l'enumeration correspondant au string passe en parametre
	 */
	private EConfiguration getEnumConfig(String cc) {
		switch (cc) {
		case "SERVEUR_ENREGISTREMENT":
			return EConfiguration.SERVEUR_ENREGISTREMENT;
		case "SERVEUR_GERER_PROFIL":
			return EConfiguration.SERVEUR_GERER_PROFIL;
		case "SERVEUR_PARTAGE":
			return EConfiguration.SERVEUR_PARTAGE;
		case "SERVEUR_NOTIF_MAIL":
			return EConfiguration.SERVEUR_NOTIF_MAIL;
		case "SERVEUR_NOTIF_POPUP":
			return EConfiguration.SERVEUR_NOTIF_POPUP;
		case "SERVEUR_LIMITE_ESPACE":
			return EConfiguration.SERVEUR_LIMITE_ESPACE;
		case "CLIENT_VUE_PROFIL":
			return EConfiguration.CLIENT_VUE_PROFIL;
		case "CLIENT_VUE_QUOTA":
			return EConfiguration.CLIENT_VUE_QUOTA;
		case "CLIENT_VUE_INFO_FICHIER":
			return EConfiguration.CLIENT_VUE_INFO_FICHIER;
		case "CLIENT_VUE_ARBORESCENCE":
			return EConfiguration.CLIENT_VUE_ARBORESCENCE;
		case "CLIENT_OPT_TRI":
			return EConfiguration.CLIENT_OPT_TRI;
		case "CLIENT_FCT_DEPLACEMENT":
			return EConfiguration.CLIENT_FCT_DEPLACEMENT;
		default:
			throw new IllegalArgumentException("Configuration inconnue.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.IConfiguration#save(java.util.Map)
	 */
	@Override
	public void save(double quota, int nbUtilisateurs, double bufferSize, int nbFichiersPartages,
			double tailleMaxFichier, int tailleMaxNomFichier, int lgMinPassword, int nbMaxAbonnements,
			String adresseBDD, String nomBDD, String userBDD, String passBDD, String adresseMail, String passwordMail, String uploadPath,
			String host, int port, Map<EConfiguration, Boolean> map) {
		try {

			DocumentBuilder dbFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dbFactory.newDocument();

			// Generation entete + racine
			doc.setXmlVersion("1.0");
			Element configuration = doc.createElement("configuration");
			doc.appendChild(configuration);

			Element configParam = doc.createElement("configParam");
			configuration.appendChild(configParam);

			Element elementQuota = doc.createElement("quota");
			elementQuota.setTextContent(String.valueOf(quota));
			configParam.appendChild(elementQuota);

			Element elementNbUtilisateurs = doc.createElement("nbMaxUtilisateurs");
			elementNbUtilisateurs.setTextContent(String.valueOf(nbUtilisateurs));
			configParam.appendChild(elementNbUtilisateurs);

			Element elementBufferSize = doc.createElement("bufferSize");
			elementBufferSize.setTextContent(String.valueOf(bufferSize));
			configParam.appendChild(elementBufferSize);

			Element elementNbFichiersPartages = doc.createElement("nbMaxFichiersPartages");
			elementNbFichiersPartages.setTextContent(String.valueOf(nbFichiersPartages));
			configParam.appendChild(elementNbFichiersPartages);

			Element elementTailleMaxFichier = doc.createElement("tailleMaxFichier");
			elementTailleMaxFichier.setTextContent(String.valueOf(tailleMaxNomFichier));
			configParam.appendChild(elementTailleMaxFichier);

			Element elementTailleMaxNomFichier = doc.createElement("tailleMaxNomFichier");
			elementTailleMaxNomFichier.setTextContent(String.valueOf(tailleMaxNomFichier));
			configParam.appendChild(elementTailleMaxNomFichier);

			Element elementLongueurMinPassword = doc.createElement("longueurMinPassword");
			elementLongueurMinPassword.setTextContent(String.valueOf(lgMinPassword));
			configParam.appendChild(elementLongueurMinPassword);

			Element elementNbMaxAbonnements = doc.createElement("nbMaxAbonnements");
			elementNbMaxAbonnements.setTextContent(String.valueOf(nbMaxAbonnements));
			configParam.appendChild(elementNbMaxAbonnements);

			Element elementAdresseBDD = doc.createElement("adresseBDD");
			elementAdresseBDD.setTextContent(adresseBDD);
			configParam.appendChild(elementAdresseBDD);

			Element elementNomBDD = doc.createElement("nomBDD");
			elementNomBDD.setTextContent(nomBDD);
			configParam.appendChild(elementNomBDD);

			Element elementUserBDD = doc.createElement("userBDD");
			elementUserBDD.setTextContent(userBDD);
			configParam.appendChild(elementUserBDD);

			Element elementPassBDD = doc.createElement("passBDD");
			elementPassBDD.setTextContent(passBDD);
			configParam.appendChild(elementPassBDD);

			Element elementUploadPath = doc.createElement("UploadPath");
			elementUploadPath.setTextContent(uploadPath);
			configParam.appendChild(elementUploadPath);
			
			Element elementDureeCorbeille = doc.createElement("dureeCorbeille");
			elementDureeCorbeille.setTextContent(String.valueOf(dureeCorbeille));
			configParam.appendChild(elementDureeCorbeille);

			Element elementAdresseMail = doc.createElement("adresseMail");
			elementAdresseMail.setTextContent(adresseMail);
			configParam.appendChild(elementAdresseMail);

			Element elementPasswordMail = doc.createElement("passwordMail");
			elementPasswordMail.setTextContent(passwordMail);
			configParam.appendChild(elementPasswordMail);

			Element elementHost = doc.createElement("host");
			elementHost.setTextContent(host);
			configParam.appendChild(elementHost);

			Element elementPort = doc.createElement("port");
			elementPort.setTextContent(String.valueOf(port));
			configParam.appendChild(elementPort);

			Iterator<Entry<EConfiguration, Boolean>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<EConfiguration, Boolean> entry = (Map.Entry<EConfiguration, Boolean>) iterator.next();
				String key = (String) entry.getKey().toString();
				String value = (String) entry.getValue().toString();

				Element config = doc.createElement("config");
				configuration.appendChild(config);
				config.setAttribute("key", key);
				config.setAttribute("value", value);
			}

			// création du fichier de save
			File f = new File("configuration.xml");
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);
			transformer.transform(source, result);
		} catch (Throwable e) {
			throw new CloudException("Configuration : Erreur enregistrement de la configuration.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.IConfiguration#getConfigValue(core.EConfiguration)
	 */
	@Override
	public boolean getConfigValue(EConfiguration config) {
		return this.configModule.get(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.IConfiguration#getConfig()
	 */
	@Override
	public Map<EConfiguration, Boolean> getConfig() {
		return this.configModule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getQuota()
	 */
	@Override
	public double getQuota() {
		return quota;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getNbUtilisateurs()
	 */
	@Override
	public int getNbMaxUtilisateurs() {
		return nbMaxUtilisateurs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getBufferSize()
	 */
	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getNbFichiersPartages()
	 */
	@Override
	public int getMaxFichiersPartages() {
		return nbMaxFichiersPartages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getTailleMaxFichier()
	 */
	@Override
	public double getTailleMaxFichier() {
		return tailleMaxFichier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getTailleMaxNomFichier()
	 */
	@Override
	public int getTailleMaxNomFichier() {
		return tailleMaxNomFichier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getLgMinPassword()
	 */
	@Override
	public int getLgMinPassword() {
		return lgMinPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getNbMaxAbonnements()
	 */
	@Override
	public int getNbMaxAbonnements() {
		return nbMaxAbonnements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getAdresseBDD()
	 */
	@Override
	public String getAdresseBDD() {
		return adresseBDD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getNomBDD()
	 */
	@Override
	public String getNomBDD() {
		return nomBDD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getUserBDD()
	 */
	@Override
	public String getUserBDD() {
		return userBDD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getPassBDD()
	 */
	@Override
	public String getPassBDD() {
		return passBDD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getAdresseMail()
	 */
	@Override
	public String getAdresseMail() {
		return adresseMail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getPasswordMail()
	 */
	@Override
	public String getPasswordMail() {
		return passwordMail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getHost()
	 */
	@Override
	public String getHostMailer() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getPort()
	 */
	@Override
	public int getPortMailer() {
		return port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IConfiguration#getNbMaxPartage()
	 */
	@Override
	public int getNbMaxPartage() throws RemoteException {
		return nbMaxPartage;
	}

	public String getUploadPath() {
		return uploadPath;
	}
	
	public int getDureeCorbeille() {
		return dureeCorbeille;
	}
}