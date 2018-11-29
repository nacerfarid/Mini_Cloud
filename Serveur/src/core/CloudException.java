package core;
/**
 * Classe permettant de lever des exceptions dans le serveur.
 * @author sofiane
 *
 */
public class CloudException extends RuntimeException {
	private String message;
	private static final long serialVersionUID = -7921272632468341809L;

	public CloudException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
