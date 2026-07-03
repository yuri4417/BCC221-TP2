package view;

import med.Medicao;
import view.TabelaModel;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class OutlierTableCellRenderer extends DefaultTableCellRenderer {
    private double limiteOutlier;

    public OutlierTableCellRenderer() {
        this.limiteOutlier = 10.0;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        TabelaModel model = (TabelaModel) table.getModel();
        Medicao m = model.getMedicao(row);

        if (m != null && Math.abs(m.getResiduoPercentual()) > limiteOutlier) {
            if (!isSelected) {
                c.setBackground(Color.RED);
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(Color.MAGENTA);
                c.setForeground(Color.BLACK);
            }
        }
        else {
            if (!isSelected) {
                // Fundo alternado ou padrão
                if (row % 2 == 0) {
                    Color mudarColor = UIManager.getColor("Table.alternateRowColor");
                    c.setBackground(mudarColor != null ? mudarColor : table.getBackground());
                }
                else {
                    c.setBackground(table.getBackground());
                }
                c.setForeground(table.getForeground());
            } else {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
        }
        // Formatação para colunas numéricas
        if (column == 4) { // Temperatura
            setText(String.format("%.2f°C", (Double) value));
        } else if (column == 5 || column == 6) { // Consumo
            setText(String.format("%.2f", (Double) value));
        } else if (column == 7) { // Resíduo
            setText(String.format("%.2f%%", (Double) value));
        }
        return c;
    }

    public void setLimiteOutlier(double limite) {
        this.limiteOutlier = limite;
    }
}