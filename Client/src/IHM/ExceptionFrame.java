package IHM;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ExceptionFrame{
    public ExceptionFrame(Exception e,JFrame frame,int code)
    {
        if (code==1)
            JOptionPane.showMessageDialog(frame, e.getMessage(),"Erreur",JOptionPane.ERROR_MESSAGE);
    }
}