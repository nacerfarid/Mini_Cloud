package IHM;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.*;

public class Main {

	public static void main(String[] args) throws RemoteException, NotBoundException {
		JFrame connex = new ConnexionFrame(400,200);
		connex.setLocationRelativeTo(null);
		connex.setResizable(false);
		connex.setVisible(true);
	}

}