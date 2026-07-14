package view.renders;

import med.Medicao;
import view.TabelaModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class OutlierTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        int modelColumn = table.convertColumnIndexToModel(column);
        Object valorFormatado = value;

        if ((modelColumn == 6 || modelColumn == 7) && value instanceof Double) {
            valorFormatado = String.format("%.2f", (Double) value);
        }

        Component c = super.getTableCellRendererComponent(table, valorFormatado, isSelected, hasFocus, row, column);

        TabelaModel model = (TabelaModel) table.getModel();
        int modelRow = table.convertRowIndexToModel(row);
        Medicao m = model.getMedicao(modelRow);

        if (m != null) {
            double limite = model.getLimiteOutlier();

            //determina o "limite baixo"
            double limiteVerde = limite * model.getPorcentagemLimiteVerde();

            //salva o residuo de cada registro
            double residuoAbsoluto = Math.abs(m.getResiduoPercentual());

            //Outliers (Vermelho): Resíduo maior que o limite
            if (residuoAbsoluto > limite) {
                if (isSelected) {
                    c.setBackground(new Color(255, 150, 150)); // Vermelho mais escuro (selecionado)
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(255, 220, 220)); // Fundo vermelho claro
                    c.setForeground(new Color(150, 0, 0));     // Texto vermelho escuro
                }
            }
            //Ótimos (Verde): Resíduo menor ou igual ao limite verde
            else if (residuoAbsoluto <= limiteVerde) {
                if (isSelected) {
                    c.setBackground(new Color(150, 255, 150)); // Verde mais escuro (selecionado)
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(220, 255, 220)); // Fundo verde claro
                    c.setForeground(new Color(0, 120, 0));     // Texto verde escuro
                }
            }
            //Médios (Cor Padrão): O que sobrou entre o limite verde e o limite vermelho
            else {
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }
            }
        }
        return c;
    }
}