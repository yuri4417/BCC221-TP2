package view;

import controller.Utils;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import med.Medicao;

public class MainFrame extends JFrame{

    private Dimension tamanhoTela;

    //opções "globais"
    private JMenuBar barra;

    private JTabbedPane panels;

    private MedicoesPanel medicoesPanel;
    private FiltrosPanel filtrosPanel;
    private RegressaoPanel regressaoPanel;
    private GraficoPanel graficoPanel;

    //arquivos
    private JMenuItem carregaTSV;
    private JMenuItem exportaTSV;

    //filtros
    private JMenuItem limparFiltros;

    //tema
    private JMenuItem temaClaro;
    private JMenuItem temaEscuro;

    public MainFrame(){
        setTitle("BCC 221 - POO | Sistema de Previsão de Consumo Energético");
        setTamanhoTela();
        setSize((int) (tamanhoTela.width/1.25), (int) (tamanhoTela.height/1.25));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Função que cria e inicializa os atributos
        inicializarComponentes();
        criaMenuBar();

        inicializarEventos();
        setJMenuBar(barra);

        setVisible(true);

    }

    private void inicializarComponentes(){

        //Panels contém todos os paineis seguintes
        panels = new JTabbedPane();

        medicoesPanel = new MedicoesPanel(null);
        filtrosPanel = new FiltrosPanel();
        filtrosPanel.setTabelaModel(medicoesPanel.getTabelaModel());
        regressaoPanel = new RegressaoPanel();
        graficoPanel = new GraficoPanel();

        panels.addTab("Medições", medicoesPanel);
        panels.addTab("Filtros", filtrosPanel);
        panels.addTab("Regressão", regressaoPanel);
        panels.addTab("Gráfico", graficoPanel);

        this.add(panels);
    }

    private void criaMenuBar(){
        barra = new JMenuBar();

        JMenu arquivos = new JMenu("Arquivo");
        JMenu filtros = new JMenu("Filtros");
        JMenu temas = new JMenu("Temas");

        carregaTSV = new JMenuItem("Carregar TSV");
        exportaTSV = new JMenuItem("Exportar TSV");

        limparFiltros = new JMenuItem("Limpar Filtros");

        temaClaro  = new JMenuItem("Tema Claro");
        temaEscuro = new JMenuItem("Tema Escuro");

        arquivos.add(carregaTSV);
        arquivos.add(exportaTSV);

        filtros.add(limparFiltros);

        temas.add(temaClaro);
        temas.add(temaEscuro);

        barra.add(arquivos);
        barra.add(filtros);
        barra.add(temas);
    }

    private void inicializarEventos() {
        carregaTSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser seletorArquivo = new JFileChooser();
                seletorArquivo.setDialogTitle("Selecione o arquivo TSV de Medições");

                seletorArquivo.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos TSV (*.tsv)", "tsv"));

                // Exibe a janela de diálogo na tela.
                // O "null" centraliza a janela na tela. Você pode trocar por "this" se estiver dentro de um JFrame.
                int resultado = seletorArquivo.showOpenDialog(null);

                // Verifica se o usuário escolheu um arquivo e clicou em "Abrir"
                if (resultado == JFileChooser.APPROVE_OPTION) {
                    java.io.File arquivoSelecionado = seletorArquivo.getSelectedFile();
                    Utils.carregarTSV(arquivoSelecionado.getAbsolutePath(), medicoesPanel.getTabelaModel());
                    medicoesPanel.getTabelaModel().atualizarOutliers();
                    medicoesPanel.getTabelaModel().fireTableDataChanged();
                    filtrosPanel.setListaOriginal(medicoesPanel.getTabelaModel().getDados());
                    SwingUtilities.updateComponentTreeUI(MainFrame.this);
                }
                else {
                    System.out.println("A seleção de arquivo foi cancelada.");
                }
            }
        });

        exportaTSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Salvar TSV atual com linhas adicionadas ou excluidas

                if(/*Salvar Tsv conseguiu executar*/true) {
                    Utils.exportarTSV("/home/yreis188/Downloads/Teste/teste.tsv", medicoesPanel.getTabelaModel());
                    SwingUtilities.updateComponentTreeUI(MainFrame.this);
                }
                else{
                    System.out.println("O salvamento do arquivo foi cancelada.");
                }
            }
        });

        limparFiltros.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filtrosPanel.limparFiltrosUI();
                medicoesPanel.getTabelaModel().atualizarOutliers();
                medicoesPanel.getTabelaModel().fireTableDataChanged();
                System.out.println("Filtros limpos.");
            }
        });
        temaClaro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try{
                    UIManager.setLookAndFeel(new FlatMacLightLaf());
                    SwingUtilities.updateComponentTreeUI(MainFrame.this);
                }
                catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Flatlaf Error: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        temaEscuro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    UIManager.setLookAndFeel(new FlatMacDarkLaf());
                    SwingUtilities.updateComponentTreeUI(MainFrame.this);
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Flatlaf Error: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    //getters
    public MedicoesPanel getMedicoesPanel() { return medicoesPanel; }
    public FiltrosPanel getFiltrosPanel() { return filtrosPanel; }
    public RegressaoPanel getRegressaoPanel() { return regressaoPanel; }
    public GraficoPanel getGraficoPanel() { return graficoPanel; }

    public final void setTamanhoTela(){
        tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
    }

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
