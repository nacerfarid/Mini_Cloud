package IHM;

import java.awt.Component;
import java.rmi.RemoteException;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import core.EType;

public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // decide what icons you want by examining the node
        if (value instanceof NodePerso) {
        	NodePerso node = (NodePerso) value;
           try {
			if (node.getFichier().getType()==EType.Repertoire) {
			        // decide based on some property of your Contact obj
			            setIcon(UIManager.getIcon("FileView.directoryIcon"));
			        } else {
			            setIcon(UIManager.getIcon("FileView.fileIcon"));
			        }
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            }

        return this;
    }

}