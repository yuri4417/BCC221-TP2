package ufop.poo.tp2.controller;

import ufop.poo.tp2.model.*;
import ufop.poo.tp2.view.MainFrame;
import ufop.poo.tp2.view.renderers.OutlierTableCellRenderer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.List;

public class SistemaController {
    private MedicaoDAO dao;
    private TabelaModel tableModel;
    private Filtro filtroAtual;
    private RegressaoLinear regressaoAtual;
    private List<Medicao> dadosOriginais;
    private List<Medicao> dadosFiltrados;
    private MainFrame view;
    
    public SistemaController(MainFrame view) {
        this.view = view;
        this.dao = new MedicaoDAO();
        this.tableModel = new TabelaModel();
        this.filtroAtual = new Filtro();
        this.regressaoAtual = new RegressaoLinear();
        
        inicializarEventos();
        
        view.getMedicoesPanel().setTableModel(tableModel);
    }
    
    private void inicializarEventos() {
        // Configurar eventos dos componentes da view
        // Implementar listeners para:
        // - Botão Carregar TSV
        // - Botão Exportar TSV
        // - Botão Sair
        // - Botão Limpar Filtros
        // - Botões Adicionar/Remover linha
        // - Filtros (spinners, sliders, text fields)
        // - Slider de outliers e toggle button
    }
    
    public void carregarArquivoTSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos TSV", "tsv"));
        
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                dadosOriginais = dao.carregarDeTSV(fileChooser.getSelectedFile());
                tableModel.setDadosOriginais(dadosOriginais);
                
                exibirResumoErros();
                aplicarFiltros();
                
                view.exibirMensagemInfo("Arquivo carregado com sucesso!");
            } catch (IOException e) {
                view.exibirMensagemErro("Erro ao carregar arquivo: " + e.getMessage());
            }
        }
    }
    
    public void exportarRelatorioTSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos TSV", "tsv"));
        
        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            String path = arquivo.getAbsolutePath();
            if (!path.endsWith(".tsv")) {
                path += ".tsv";
                arquivo = new File(path);
            }
            
            try (PrintWriter writer = new PrintWriter(arquivo)) {
                writer.println("timestamp\tcidade\tlatitude\tlongitude\ttemperatura\tconsumoKwh\tconsumoPrevisto\tresiduoPercentual");
                
                for (Medicao m : dadosFiltrados) {
                    writer.printf("%s\t%s\t%.6f\t%.6f\t%.2f\t%.2f\t%.4f\t%.2f%n",
                            m.getTimestamp(), m.getCidade(), m.getLatitude(), m.getLongitude(),
                            m.getTemperatura(), m.getConsumoKwh(),
                            m.getConsumoPrevisto(), m.getResiduoPercentual());
                }
                
                view.exibirMensagemInfo("Relatório exportado com sucesso!");
            } catch (FileNotFoundException e) {
                view.exibirMensagemErro("Erro ao exportar: " + e.getMessage());
            }
        }
    }
    
    public void aplicarFiltros() {
        if (dadosOriginais == null) return;
        
        dadosFiltrados = filtroAtual.aplicar(dadosOriginais);
        tableModel.setDadosFiltrados(dadosFiltrados);
        
        recalcularRegressao();
        atualizarGrafico();
    }
    
    public void recalcularRegressao() {
        boolean sucesso = regressaoAtual.calcular(dadosFiltrados);
        
        if (sucesso) {
            view.getRegressaoPanel().atualizarResultados(
                    regressaoAtual.getBeta0(),
                    regressaoAtual.getBeta1(),
                    regressaoAtual.getR2(),
                    regressaoAtual.getN()
            );
            view.atualizarProgressBarR2(regressaoAtual.getR2());
            
            // Atualizar tabela com valores previstos e resíduos
            tableModel.fireTableDataChanged();
        } else {
            view.getRegressaoPanel().limparResultados();
            view.getRegressaoPanel().exibirMensagensValidacao(
                    "Atenção: É necessário pelo menos 2 medições para calcular a regressão.\n" +
                    "Dados filtrados atuais: " + dadosFiltrados.size() + " medição(ões)."
            );
            view.exibirMensagemErro("Não é possível calcular regressão com menos de 2 medições.");
        }
    }
    
    public void adicionarMedicao() {
        // Implementar: abrir diálogo para adicionar nova medição
    }
    
    public void removerMedicoesSelecionadas() {
        int[] linhas = view.getMedicoesPanel().getLinhasSelecionadas();
        if (linhas.length > 0) {
            tableModel.removerMedicoes(linhas);
            dadosOriginais = tableModel.getDadosOriginais();
            aplicarFiltros();
        }
    }
    
    public void limparTodosFiltros() {
        filtroAtual.limpar();
        view.getFiltrosPanel().limparCampos();
        aplicarFiltros();
    }
    
    public void atualizarOutliers() {
        double limite = view.getRegressaoPanel().getLimiteOutlierPercentual();
        boolean excluir = view.getRegressaoPanel().isExcluirOutliers();
        
        tableModel.setLimiteOutlier(limite);
        tableModel.setExcluirOutliers(excluir);
        
        // Atualizar renderer da tabela
        OutlierTableCellRenderer renderer = new OutlierTableCellRenderer();
        renderer.setLimiteOutlier(limite);
        
        // Recalcular regressão se necessário
        if (excluir) {
            recalcularRegressao();
        }
        
        atualizarGrafico();
    }
    
    private void atualizarGrafico() {
        view.getGraficoPanel().atualizarDados(
                dadosFiltrados,
                regressaoAtual,
                view.getRegressaoPanel().getLimiteOutlierPercentual()
        );
    }
    
    private void exibirResumoErros() {
        ErroValidacao erros = dao.getErros();
        view.getRegressaoPanel().exibirMensagensValidacao(erros.getMensagemResumo());
    }
    
    public void sair() {
        System.exit(0);
    }
}