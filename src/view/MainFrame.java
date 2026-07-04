package view;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import static controller.Utils.*;

import controller.RegressaoController;

//TODO: permitir ordenação dos registros da tabela
//TODO: Corrigir processo de cálculo da regressão


public class MainFrame extends JFrame{
    private Dimension tamanhoTela;
    //opções "globais"
    private JPanel sideBar;
    private JTabbedPane panels;
    private MedicoesPanel medicoesPanel;
    private FiltrosPanel filtrosPanel;
    private RegressaoPanel regressaoPanel;
    private GraficoPanel graficoPanel;

    private RegressaoController regressaoController;

    //arquivos
    private JButton carregaTSV;
    private JButton exportaTSV;
    //filtros
    private JButton limparFiltros;
    //tema
    private JButton temaClaro;
    private JButton temaEscuro;

    public MainFrame(){
        setTitle("BCC 221 - POO | Sistema de Previsão de Consumo Energético");
        setTamanhoTela();
        setSize((int) (tamanhoTela.width/1.25), (int) (tamanhoTela.height/1.25));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //icone principal
        ImageIcon iconPath = carregaIcon("/images/regression.png", 32, 32);
        if (iconPath != null) {
            setIconImage(iconPath.getImage());
        }
        setLayout(new BorderLayout());
        //Função que cria e inicializa os atributos
        inicializarComponentes();
        criaSideBar();
        inicializarEventos();

        regressaoController = new RegressaoController(medicoesPanel, regressaoPanel);

        JPanel sideBarWrapper = new JPanel(new BorderLayout());
        sideBarWrapper.add(sideBar, BorderLayout.CENTER);
        sideBarWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("Component.borderColor")));

        this.add(sideBarWrapper, BorderLayout.WEST);
        this.add(panels, BorderLayout.CENTER);

        setVisible(true);

    }
    private void inicializarComponentes() {
        panels = new JTabbedPane();
        // Propriedade do FlatLaf para deixar as abas com visual moderno
        panels.putClientProperty("JTabbedPane.tabType", "card");

        medicoesPanel = new MedicoesPanel(null);
        filtrosPanel = new FiltrosPanel();
        filtrosPanel.setTabelaModel(medicoesPanel.getTabelaModel());
        regressaoPanel = new RegressaoPanel();
        graficoPanel = new GraficoPanel();

        panels.addTab("Medições", carregaIcon("/images/dark/medicoes.png", 16, 16), medicoesPanel);
        panels.addTab("Filtros", carregaIcon("/images/dark/loupe.png", 16, 16), filtrosPanel);
        panels.addTab("Regressão", carregaIcon("/images/dark/regressao.png", 16, 16), regressaoPanel);
        panels.addTab("Gráfico", carregaIcon("/images/dark/grafico.png", 16, 16), graficoPanel);
    }

    private void criaSideBar() {
        sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setPreferredSize(new Dimension(240, 0));
        sideBar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sideBar.setBackground(UIManager.getColor("Panel.background"));

        JLabel appTitle = new JLabel(String.format("TP2 - POO"));
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 30, 0));
        sideBar.add(appTitle);

//        Botoes
        carregaTSV = new JButton("Carregar TSV", carregaIcon("/images/dark/folder.png", 20, 20));
        exportaTSV = new JButton("Salvar TSV", carregaIcon("/images/dark/disket.png", 20, 20));
        limparFiltros = new JButton("Limpar Filtros", carregaIcon("/images/dark/broom.png", 20, 20));
        temaClaro = new JButton("Tema Claro", carregaIcon("/images/dark/sun.png", 20, 20));
        temaEscuro = new JButton("Tema Escuro", carregaIcon("/images/dark/moon.png", 20, 20));

        JButton[] botoes = {carregaTSV, exportaTSV, limparFiltros, temaClaro, temaEscuro};
        for (JButton btn : botoes) {
            estilizarBotaoSidebar(btn);
        }

        // --- Montagem da Sidebar ---
        JLabel lblArquivos = new JLabel("ARQUIVO");
        estilizarHeader(lblArquivos);
        sideBar.add(lblArquivos);
        sideBar.add(carregaTSV);
        sideBar.add(Box.createRigidArea(new Dimension(0, 5)));
        sideBar.add(exportaTSV);

        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblFiltros = new JLabel("FILTROS");
        estilizarHeader(lblFiltros);
        sideBar.add(lblFiltros);
        sideBar.add(limparFiltros);

        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblTemas = new JLabel("APARÊNCIA");
        estilizarHeader(lblTemas);
        sideBar.add(lblTemas);
        sideBar.add(temaClaro);
        sideBar.add(Box.createRigidArea(new Dimension(0, 5)));
        sideBar.add(temaEscuro);

        sideBar.add(Box.createVerticalGlue());
    }
    private void estilizarBotaoSidebar(JButton btn) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btn.putClientProperty("JButton.buttonType", "borderless");
        btn.putClientProperty("JButton.arc", 15);
    }

    private void estilizarHeader(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIManager.getColor("Label.disabledForeground"));
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
    }

    private void inicializarEventos() {
        carregaTSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser seletorArquivo = new JFileChooser();
                seletorArquivo.setDialogTitle("Selecione o arquivo TSV de Medições");

                seletorArquivo.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos TSV (*.tsv)", "tsv"));
                int resultado = seletorArquivo.showOpenDialog(null);

                // Verifica se o usuário escolheu um arquivo e clicou em "Abrir"
                if (resultado == JFileChooser.APPROVE_OPTION) {
                    File arquivoSelecionado = seletorArquivo.getSelectedFile();
                    carregarTSV(arquivoSelecionado.getAbsolutePath(), medicoesPanel.getTabelaModel());
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
//              TODO: criar metodo de validacao das medicoes lidas
                if(/*Salvar Tsv conseguiu executar*/true) {
                    exportarTSV(medicoesPanel.getTabelaModel());
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

                    panels.setIconAt(0, carregaIcon("/images/light/medicoes.png", 16, 16));
                    panels.setIconAt(1, carregaIcon("/images/light/loupe.png", 16, 16));
                    panels.setIconAt(2, carregaIcon("/images/light/regressao.png", 16, 16));
                    panels.setIconAt(3, carregaIcon("/images/light/grafico.png", 16, 16));

                    carregaTSV.setIcon(carregaIcon("/images/light/folder.png", 16, 16));
                    exportaTSV.setIcon(carregaIcon("/images/light/disket.png", 16, 16));
                    limparFiltros.setIcon(carregaIcon("/images/light/broom.png", 16, 16));
                    temaClaro.setIcon(carregaIcon("/images/light/sun.png", 16, 16));
                    temaEscuro.setIcon(carregaIcon("/images/light/moon.png", 16, 16));
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

                    panels.setIconAt(0, carregaIcon("/images/dark/medicoes.png", 16, 16));
                    panels.setIconAt(1, carregaIcon("/images/dark/loupe.png", 16, 16));
                    panels.setIconAt(2, carregaIcon("/images/dark/regressao.png", 16, 16));
                    panels.setIconAt(3, carregaIcon("/images/dark/grafico.png", 16, 16));

                    carregaTSV.setIcon(carregaIcon("/images/dark/folder.png", 16, 16));
                    exportaTSV.setIcon(carregaIcon("/images/dark/disket.png", 16, 16));
                    limparFiltros.setIcon(carregaIcon("/images/dark/broom.png", 16, 16));
                    temaClaro.setIcon(carregaIcon("/images/dark/sun.png", 16, 16));
                    temaEscuro.setIcon(carregaIcon("/images/dark/moon.png", 16, 16));
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
}
