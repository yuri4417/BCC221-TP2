import javax.swing.*;

import view.MainFrame;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class Main {
    public static void main(String[] args) {
       try {
           System.setProperty("flatlaf.useWindowDecorations", "true");
           UIManager.setLookAndFeel(new FlatMacDarkLaf()); //Inicializacao do flatlaf, para melhor design
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(null, "Flatlaf Error: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
       }
        SwingUtilities.invokeLater(MainFrame::new); //Inicializacao segura da interface grafica
    }
}
