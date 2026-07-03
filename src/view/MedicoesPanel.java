package view;

import med.Medicao;
import view.TabelaModel;
import view.OutlierTableCellRenderer;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class MedicoesPanel extends JPanel {
    private JTable tabela;
    private TabelaModel tableModel;
    private JButton btnAdicionar;
    private JButton btnRemover;
    private JScrollPane scrollPane;

    public MedicoesPanel(TabelaModel model) {
        this.tableModel = model != null ? model : new TabelaModel();
        inicializarComponentes();
        inicializarEventos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        tabela = new JTable((TabelaModel) tableModel);
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

    private void inicializarEventos(){
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JTextField stringData = new JTextField(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                JTextField stringCidade = new JTextField();
                JTextField stringTemp = new JTextField();
                JTextField stringConsumo = new JTextField();
                JTextField stringLatitude = new JTextField();
                JTextField stringLongitude = new JTextField();

                Object[] novaMedicao = {
                        "Data/Hora (yyyy-MM-dd HH:mm:ss):", stringData,
                        "Cidade:", stringCidade,
                        "Latitude:", stringLatitude,
                        "Longitude:", stringLongitude,
                        "Temperatura (°C):", stringTemp,
                        "Consumo (kWh):", stringConsumo
                };
                int opcao = JOptionPane.showConfirmDialog(MedicoesPanel.this,novaMedicao, "Adicionar Nova Medição",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

                if(opcao == JOptionPane.OK_OPTION){
                    try {
                        Medicao medicao = new Medicao();
                        java.time.format.DateTimeFormatter format = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        medicao.setTimeStamp(java.time.LocalDateTime.parse(stringData.getText(), format));
                        medicao.setCidade(stringCidade.getText());
                        medicao.setTemperatura(Double.parseDouble(stringTemp.getText().replace(",",".")));
                        medicao.setConsumoKwh(Double.parseDouble(stringConsumo.getText().replace(",",".")));

                        coords.Coordenada coord = new coords.Coordenada(Double.parseDouble(stringLatitude.getText().replace(",",".")),Double.parseDouble(stringLongitude.getText().replace(",",".")));
                        medicao.setCoordenadas(coord);
                        tableModel.adicionarMedicao(medicao);
                        int ultLinha = tabela.getRowCount() - 1;
                        if (ultLinha >= 0){
                            tabela.setRowSelectionInterval(ultLinha,ultLinha);
                            tabela.scrollRectToVisible(tabela.getCellRect(ultLinha,0,true));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int[] linhasSelecionadas = tabela.getSelectedRows();
                if (linhasSelecionadas.length == 0) {
                    JOptionPane.showMessageDialog(MedicoesPanel.this,
                            "Selecione pelo menos uma linha para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirmacao = JOptionPane.showConfirmDialog(MedicoesPanel.this,
                        "Tem certeza que deseja remover as medições selecionadas?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
                if (confirmacao == JOptionPane.YES_OPTION) {
                    tableModel.removerMedicoes(linhasSelecionadas);
                    limparSelecao();
                }
            }
        });
    }

    public void atualizarTabela() {
        //tableModel.fireTableDataChanged();
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
        this.tableModel = tableModel;

        tabela.setModel((TableModel) tableModel);

        // Reaplicar o renderer para todas as colunas
        TableColumnModel colModel = tabela.getColumnModel();
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(new OutlierTableCellRenderer());
        }

        tabela.revalidate();
        tabela.repaint();
    }

    public TabelaModel getTabelaModel(){
        return tableModel;
    }

}