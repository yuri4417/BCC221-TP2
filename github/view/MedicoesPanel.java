package ufop.poo.tp2.view;

import ufop.poo.tp2.model.TabelaModel;
import ufop.poo.tp2.view.renderers.OutlierTableCellRenderer;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class MedicoesPanel extends JPanel {
    private JTable tabela;
    private TabelaModel tableModel;
    private JButton btnAdicionar;
    private JButton btnRemover;
    private JScrollPane scrollPane;
    
    public MedicoesPanel(TabelaModel model) {
        this.tableModel = model != null ? model : new TabelaModel();
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        tabela = new JTable(tableModel);
        tabela.setFillsViewportHeight(true);
        
        // Configurar renderer para outliers
        TableColumnModel colModel = tabela.getColumnModel();
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(new OutlierTableCellRenderer());
        }
        
        scrollPane = new JScrollPane(tabela);
        
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdicionar = new JButton("Adicionar linha");
        btnRemover = new JButton("Remover linha(s) selecionada(s)");
        
        panelBotoes.add(btnAdicionar);
        panelBotoes.add(btnRemover);
        
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);
    }
    
    public void atualizarTabela() {
        tableModel.fireTableDataChanged();
    }
    
    public int[] getLinhasSelecionadas() {
        return tabela.getSelectedRows();
    }
    
    public void limparSelecao() {
        tabela.clearSelection();
    }
    
    public TabelaModel getTableModel() {
        return tableModel;
    }
    
    public JButton getBtnAdicionar() { return btnAdicionar; }
    public JButton getBtnRemover() { return btnRemover; }

    public void setTableModel(TabelaModel tableModel) {
        // Criar a funcao
    }
    
}