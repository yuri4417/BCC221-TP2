import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import view.MainFrame;

import javax.swing.*;

public class Main {
    //ATENÇÃO: NÃO REMOVER MAIN
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Flatlaf Error: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        MainFrame frame = new MainFrame();
    }
}
