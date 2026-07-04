package view;

import med.ErroValidacao;
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

//TODO: permitir ordenação dos dados da tabela

//TODO: exibir que não há dados quando não dados (nenhum arquivo carregado ou filtros removeram todas as possibilidades)
// TODO Label quando nao ha medicoes
//TODO: exibir que não há dados na tela inicial quando nenhum arquivo foi importado
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
        for (int i = 1; i < tabela.getColumnCount(); i++) {
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

    private void inicializarEventos() {
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
                        "Data/Hora:", stringData,
                        "Cidade:", stringCidade,
                        "Latitude:", stringLatitude,
                        "Longitude:", stringLongitude,
                        "Temperatura (°C):", stringTemp,
                        "Consumo (kWh):", stringConsumo
                };
                while (true) {
                    int opcao = JOptionPane.showConfirmDialog(MedicoesPanel.this, novaMedicao, "Adicionar Nova Medição", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (opcao == JOptionPane.OK_OPTION) {
                        if (stringData.getText().trim().isEmpty() || stringCidade.getText().trim().isEmpty() || stringLatitude.getText().trim().isEmpty() || stringLongitude.getText().trim().isEmpty() || stringTemp.getText().trim().isEmpty() || stringConsumo.getText().trim().isEmpty()) {

                            throw new IllegalArgumentException("Todos os campos precisam ser preenchidos.");
                        }
                        try {
                            Medicao medicao = new Medicao();
                            java.time.format.DateTimeFormatter format = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            medicao.setTimeStamp(java.time.LocalDateTime.parse(stringData.getText(), format));

                            String textoCidade = stringCidade.getText().trim(); // Pega o texto digitado, retira espaco no inicio/fim
                            StringBuilder cidadeFormatada = new StringBuilder(); // StringBuilder ajuda na formatacao de texto
                            if (!textoCidade.isEmpty()) { //Verifica se esta vazia
                                String[] palavras = textoCidade.split("\\s+"); // Coloca as palavra em um vetor, \\s+ separa pelos espacos
                                for (String palavra : palavras) { // percorre o vetor das palavras
                                    if (!palavra.isEmpty()) { //Se existir uma palavra
                                        cidadeFormatada.append(Character.toUpperCase(palavra.charAt(0))).append(palavra.substring(1).toLowerCase()).append(" ");
                                        //Junta as palavras com a primeira letra Maiuscula nelas
                                    }
                                }
                            }
                            // TODO: usar ErroValidacao aqui também
                            medicao.setCidade(cidadeFormatada.toString().trim());
                            double temperatura = Double.parseDouble(stringTemp.getText().replace(",", "."));
                            if (temperatura > 50 || temperatura < -90) {
                                throw new IllegalArgumentException("A temperatura deve estar no intervalo de -90ºC e 50ºC");
                            } else
                                medicao.setTemperatura(temperatura);
                            double consumo = Double.parseDouble(stringConsumo.getText().replace(",", "."));
                            if (consumo < 0) {
                                throw new IllegalArgumentException("O consumo não pode ser negativo");
                            } else
                                medicao.setConsumoKwh(consumo);

                            double latitude = Double.parseDouble(stringLatitude.getText().replace(",", "."));
                            if (latitude < -90 || latitude > 90)
                                throw new IllegalArgumentException("A latitude deve estar no intervalo de -90 a 90");
                            double longitude = Double.parseDouble(stringLongitude.getText().replace(",", "."));
                            if (longitude < -180 || longitude > 180)
                                throw new IllegalArgumentException("A longitude deve estar no intervalo de -180 a 180");

                            coords.Coordenada coord = new coords.Coordenada(latitude, longitude);
                            medicao.setCoordenadas(coord);

                            tableModel.adicionarMedicao(medicao);
                            int ultLinha = tabela.getRowCount() - 1;
                            if (ultLinha >= 0) {
                                tabela.setRowSelectionInterval(ultLinha, ultLinha);
                                tabela.scrollRectToVisible(tabela.getCellRect(ultLinha, 0, true));
                            }
                            break;
                        } catch (
                                NumberFormatException e) { //Erro de formato de numeros, showMessageDialog cria um pop-up com botao ok
                            JOptionPane.showMessageDialog(MedicoesPanel.this, "Os campos de Latitude, Longitude, Temperatura e Consumo aceitam apenas números.", "Erro de Digitação", JOptionPane.ERROR_MESSAGE);
                        } catch (
                                IllegalArgumentException e) { //Erro de campos sem nada e dados corretamente formatados porém inválidos
                            JOptionPane.showMessageDialog(MedicoesPanel.this, e.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
                        } catch (java.time.format.DateTimeParseException e) { //Formato da data errado
                            JOptionPane.showMessageDialog(MedicoesPanel.this, "O formato da data está incorreto.\nUse exatamente: yyyy-MM-dd HH:mm:ss", "Erro na Data", JOptionPane.ERROR_MESSAGE);
                        } catch (Exception e) { //Erro desconhecido
                            JOptionPane.showMessageDialog(MedicoesPanel.this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }

                    } else { //Clicou no x
                        break;
                    }
                }
            }
        });

        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int[] linhas = tabela.getSelectedRows();
                if (linhas.length == 0) {
                    JOptionPane.showMessageDialog(MedicoesPanel.this, "Selecione pelo menos uma linha para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirmacao = JOptionPane.showConfirmDialog(MedicoesPanel.this, "Tem certeza que deseja remover as medições selecionadas?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
                if (confirmacao == JOptionPane.YES_OPTION) {
                    tableModel.removerMedicoes(linhas);
                    limparSelecao();
                }
            }
        });
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

    public JButton getBtnAdicionar() {
        return btnAdicionar;
    }

    public JButton getBtnRemover() {
        return btnRemover;
    }

    public void setTableModel(TabelaModel tableModel) {
        this.tableModel = tableModel;
        tabela.setModel((TableModel) tableModel);
        // Reaplicar o renderer para todas as colunas
        TableColumnModel colModel = tabela.getColumnModel();
        for (int i = 1; i < tabela.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(new OutlierTableCellRenderer());
        }

        tabela.revalidate();
        tabela.repaint();
    }

    public TabelaModel getTabelaModel(){
        return tableModel;
    }

}