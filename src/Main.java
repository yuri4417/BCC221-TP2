import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import view.MainFrame;

import javax.swing.*;

public class Main {
    //ATENÇÃO: NÃO REMOVER MAIN
    public static void main(String[] args) {
//        try {
//            System.setProperty("flatlaf.useWindowDecorations", "true");
//            UIManager.setLookAndFeel(new FlatMacDarkLaf()); //Inicializacao do flatlaf, para melhor design
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(null, "Flatlaf Error: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
//        }
        SwingUtilities.invokeLater(() -> new MainFrame()); //Inicializacao segura da interface grafica
    }
}
