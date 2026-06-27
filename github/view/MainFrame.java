package ufop.poo.tp2.view;
import ufop.poo.tp2.model.TabelaModel;

import ufop.poo.tp2.controller.SistemaController;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private MedicoesPanel medicoesPanel;
    private FiltrosPanel filtrosPanel;
    private RegressaoPanel regressaoPanel;
    private GraficoPanel graficoPanel;
    private JProgressBar progressBarR2;
    private JMenuBar menuBar;
    private SistemaController controller;
    
    public MainFrame() {
        setTitle("Sistema de Análise Energética");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        inicializarComponentes();
        criarMenuBar();
        
        controller = new SistemaController(this);
        
        setJMenuBar(menuBar);
        setVisible(true);
    }
    
    private void inicializarComponentes() {
        tabbedPane = new JTabbedPane();
        
        medicoesPanel = new MedicoesPanel(null); // TableModel será setado depois
        filtrosPanel = new FiltrosPanel();
        regressaoPanel = new RegressaoPanel();
        graficoPanel = new GraficoPanel();
        
        tabbedPane.addTab("Medições", medicoesPanel);
        tabbedPane.addTab("Filtros", filtrosPanel);
        tabbedPane.addTab("Regressão e Previsão", regressaoPanel);
        tabbedPane.addTab("Gráfico", graficoPanel);
        
        progressBarR2 = new JProgressBar(0, 100);
        progressBarR2.setStringPainted(true);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(progressBarR2, BorderLayout.SOUTH);
    }
    
    private void criarMenuBar() {
        menuBar = new JMenuBar();
        
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemCarregar = new JMenuItem("Carregar TSV");
        JMenuItem itemExportar = new JMenuItem("Exportar Relatório TSV");
        JMenuItem itemSair = new JMenuItem("Sair");
        
        menuArquivo.add(itemCarregar);
        menuArquivo.add(itemExportar);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);
        
        JMenu menuFiltros = new JMenu("Filtros");
        JMenuItem itemLimparFiltros = new JMenuItem("Limpar todos filtros");
        menuFiltros.add(itemLimparFiltros);
        
        menuBar.add(menuArquivo);
        menuBar.add(menuFiltros);
        
        // Actions serão conectadas ao controller
    }
    
    public void atualizarProgressBarR2(double r2) {
        int valor = (int) (r2 * 100);
        progressBarR2.setValue(valor);
        progressBarR2.setString(String.format("R² = %.4f", r2));
        
        // Colorir a barra conforme R²
        if (r2 <= 0.05) {
            progressBarR2.setForeground(Color.BLUE);
        } else if (r2 <= 0.08) {
            progressBarR2.setForeground(Color.YELLOW);
        } else {
            progressBarR2.setForeground(Color.RED);
        }
    }
    
    public void exibirMensagemErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    public void exibirMensagemInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }


    // Getters para os componentes
    public MedicoesPanel getMedicoesPanel() { return medicoesPanel; }
    public FiltrosPanel getFiltrosPanel() { return filtrosPanel; }
    public RegressaoPanel getRegressaoPanel() { return regressaoPanel; }
    public GraficoPanel getGraficoPanel() { return graficoPanel; }
}