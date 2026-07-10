package view;

import static med.MedicaoValidator.*;
import coords.Coordenada;
import med.Medicao;
import view.renders.OutlierTableCellRenderer;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MedicoesPanel extends JPanel {
    private JTable tabela;
    private TabelaModel tableModel;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JScrollPane scrollPane;
    private JLabel lblArquivo;

    public MedicoesPanel(TabelaModel model) {
        this.tableModel = model != null ? model : new TabelaModel();
        inicializarComponentes();
        inicializarEventos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        lblArquivo = new JLabel("Nenhum arquivo carregado");
        lblArquivo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblArquivo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(lblArquivo, BorderLayout.NORTH);

        tabela = new JTable(tableModel) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Se a tabela estiver vazia, imprime uma mensagem padrao
                if (getRowCount() == 0) {
                    Graphics2D g2d = (Graphics2D) g.create();

                    // Suaviza as bordas da fonte (Antialiasing)
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(UIManager.getColor("Label.disabledForeground"));
                    g2d.setFont(new Font("Segoe UI", Font.ITALIC, 14));

                    String mensagem = "Nenhuma medição carregada/adequada aos filtros";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(mensagem)) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                    g2d.drawString(mensagem, x, y);
                    g2d.dispose();
                }
            }
        };
        tabela.setAutoCreateRowSorter(true); // permite ordenacao pelos campos
        tabela.setFillsViewportHeight(true);

        // Configurar renderer para outliers
        TableColumnModel colModel = tabela.getColumnModel();
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(new OutlierTableCellRenderer());
        }

        scrollPane = new JScrollPane(tabela);

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdicionar = new JButton("Adicionar linha");
        btnAdicionar.setToolTipText("Adiciona uma nova medição");
        btnEditar = new JButton("Editar selecionado");
        btnEditar.setToolTipText("Edita uma medição");
        btnRemover = new JButton("Remover linha(s) selecionada(s)");
        btnRemover.setToolTipText("Remove medição");

        panelBotoes.add(btnAdicionar);
        panelBotoes.add(btnEditar);
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
                        try {
                            if (stringData.getText().trim().isEmpty() || stringCidade.getText().trim().isEmpty() || stringLatitude.getText().trim().isEmpty() || stringLongitude.getText().trim().isEmpty() || stringTemp.getText().trim().isEmpty() || stringConsumo.getText().trim().isEmpty()) {
                                throw new IllegalArgumentException("Todos os campos precisam ser preenchidos.");
                            }
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
                            double latitude = Double.parseDouble(stringLatitude.getText().replace(",", "."));
                            double longitude = Double.parseDouble(stringLongitude.getText().replace(",", "."));
                            Coordenada coord = new coords.Coordenada(latitude, longitude);
                            double temperatura = Double.parseDouble(stringTemp.getText().replace(",", "."));
                            double consumo = Double.parseDouble(stringConsumo.getText().replace(",", "."));

                            StringBuilder erro = new StringBuilder();
                            if (!validarCoordenada(coord))
                                erro.append("- Latitude/Longitude fora dos limites permitidos.\n");
                            if (!validarTemperatura(temperatura))
                                erro.append("- A temperatura deve estar no intervalo de -90ºC e 50ºC.\n");
                            if (!validarConsumo(consumo))
                                erro.append("- O consumo não pode ser negativo.\n");
                            if (erro.length() > 0)
                                throw new IllegalArgumentException(erro.toString());

                            medicao.setCidade(cidadeFormatada.toString().trim());
                            medicao.setCoordenadas(coord);
                            medicao.setTemperatura(temperatura);
                            medicao.setConsumoKwh(consumo);

                            tableModel.adicionarMedicao(medicao);
                            int ultLinha = tabela.getRowCount() - 1;
                            if (ultLinha >= 0) {
                                tabela.setRowSelectionInterval(ultLinha, ultLinha);
                                tabela.scrollRectToVisible(tabela.getCellRect(ultLinha, 0, true));
                            }
                            break;
                        } catch (NumberFormatException e) { //Erro de formato de numeros, showMessageDialog cria um pop-up com botao ok
                            JOptionPane.showMessageDialog(MedicoesPanel.this, "Os campos de Latitude, Longitude, Temperatura e Consumo aceitam apenas números.", "Erro de Digitação", JOptionPane.ERROR_MESSAGE);
                        } catch (IllegalArgumentException e) { //Erro de campos sem nada e dados corretamente formatados porém inválidos
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

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editRegistro();
            }
        });
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Verifica se foi botão esquerdo e se foram 2 cliques
                if (e.getClickCount() == 2 && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {

                    // Garante que o usuário clicou em uma linha válida e não no espaço vazio da tabela
                    int linhaClicada = tabela.rowAtPoint(e.getPoint());
                    if (linhaClicada != -1) {
                        editRegistro();
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

    public void setNomeArquivo(String nomeArquivo) {
        lblArquivo.setText("Arquivo: " + nomeArquivo);
    }

    private void editRegistro() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(MedicoesPanel.this, "Selecione uma linha para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int linha = tabela.convertRowIndexToModel(linhaSelecionada);
        Medicao medicao = tableModel.getMedicao(linha);

        if (medicao != null) {
            JTextField stringTemp = new JTextField(String.valueOf(medicao.getTemperatura()));
            JTextField stringConsumo = new JTextField(String.valueOf(medicao.getConsumoKwh()));
            Object[] camposEdicao = {
                    "Cidade: " + medicao.getCidade(),
                    "Data/Hora: " + medicao.getTimeStamp().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "Nova Temperatura (°C):", stringTemp,
                    "Novo Consumo (kWh):", stringConsumo
            };

            while (true) {
                int opcao = JOptionPane.showConfirmDialog(MedicoesPanel.this, camposEdicao, "Editar Medição", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (opcao == JOptionPane.OK_OPTION) {
                    try {
                        if (stringTemp.getText().trim().isEmpty() || stringConsumo.getText().trim().isEmpty()) {
                            throw new IllegalArgumentException("Ambos os campos precisam ser preenchidos.");
                        }

                        double temperatura = Double.parseDouble(stringTemp.getText().replace(",", "."));
                        double consumo = Double.parseDouble(stringConsumo.getText().replace(",", "."));

                        StringBuilder erro = new StringBuilder();
                        if (!validarTemperatura(temperatura))
                            erro.append("- A temperatura deve estar no intervalo de -90ºC e 50ºC.\n");
                        if (!validarConsumo(consumo))
                            erro.append("- O consumo não pode ser negativo.\n");
                        if (erro.length() > 0)
                            throw new IllegalArgumentException(erro.toString());

                        medicao.setTemperatura(temperatura);
                        medicao.setConsumoKwh(consumo);

                        tableModel.atualizarOutliers();

                        int novaLinhaView = tabela.convertRowIndexToView(linha);
                        if (novaLinhaView != -1) {
                            tabela.setRowSelectionInterval(novaLinhaView, novaLinhaView);
                        }
                        break;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(MedicoesPanel.this, "Os campos aceitam apenas números.", "Erro de Digitação", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(MedicoesPanel.this, e.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MedicoesPanel.this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    break;
                }
            }
        }
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