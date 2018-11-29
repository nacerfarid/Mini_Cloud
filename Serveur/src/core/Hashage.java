package core;
import java.security.*;

/**
 * Classe utlitaire permettant de hashé un password.
 * Algorithme actuellement implementé : SHA2.
 * @author Groupe6
 */

public class Hashage {	
	/**
	 * Methode qui renvoie le hash SHA-2 de la chaine str passée en parametre
	 * @param str : chaine de caractère à hasher
	 * @return SHA-256 de str
	 */
	public static String hashSHA2(String str){
		if(str.equals("")) throw new CloudException("hashSHA2() : chaine vide.");
		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(str.getBytes());
			byte byteData[] = md.digest();
	        for (int i = 0; i < byteData.length; i++) {
	            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
