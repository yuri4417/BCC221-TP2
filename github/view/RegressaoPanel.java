package ufop.poo.tp2.view;

import ufop.poo.tp2.view.renderers.OutlierTableCellRenderer;
import javax.swing.*;
import java.awt.*;

public class RegressaoPanel extends JPanel {
    private JLabel lblBeta0;
    private JLabel lblBeta1;
    private JLabel lblR2;
    private JLabel lblN;
    private JSlider sliderOutlierPercentual;
    private JToggleButton toggleExcluirOutliers;
    private JTextArea txtAreaErros;
    
    public RegressaoPanel() {
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Painel de resultados
        JPanel panelResultados = new JPanel(new GridLayout(2, 4, 10, 10));
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados da Regressão"));
        
        lblBeta0 = new JLabel("β₀: --", SwingConstants.CENTER);
        lblBeta1 = new JLabel("β₁: --", SwingConstants.CENTER);
        lblR2 = new JLabel("R²: --", SwingConstants.CENTER);
        lblN = new JLabel("N: --", SwingConstants.CENTER);
        
        panelResultados.add(lblBeta0);
        panelResultados.add(lblBeta1);
        panelResultados.add(lblR2);
        panelResultados.add(lblN);
        
        // Painel de outliers
        JPanel panelOutliers = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelOutliers.setBorder(BorderFactory.createTitledBorder("Configuração de Outliers"));
        
        sliderOutlierPercentual = new JSlider(0, 100, 10);
        sliderOutlierPercentual.setMajorTickSpacing(10);
        sliderOutlierPercentual.setPaintTicks(true);
        sliderOutlierPercentual.setPaintLabels(true);
        
        toggleExcluirOutliers = new JToggleButton("Excluir outliers da tabela");
        
        panelOutliers.add(new JLabel("Limite de resíduo (%):"));
        panelOutliers.add(sliderOutlierPercentual);
        panelOutliers.add(toggleExcluirOutliers);
        
        // Área de mensagens
        txtAreaErros = new JTextArea(5, 40);
        txtAreaErros.setEditable(false);
        txtAreaErros.setForeground(Color.RED);
        JScrollPane scrollErros = new JScrollPane(txtAreaErros);
        scrollErros.setBorder(BorderFactory.createTitledBorder("Mensagens de Validação"));
        
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelResultados, BorderLayout.NORTH);
        panelCentro.add(panelOutliers, BorderLayout.CENTER);
        
        add(panelCentro, BorderLayout.NORTH);
        add(scrollErros, BorderLayout.CENTER);
    }
    
    public void atualizarResultados(double beta0, double beta1, double r2, int n) {
        lblBeta0.setText(String.format("β₀: %.4f", beta0));
        lblBeta1.setText(String.format("β₁: %.4f", beta1));
        lblR2.setText(String.format("R²: %.4f", r2));
        lblN.setText(String.format("N: %d", n));
    }
    
    public void limparResultados() {
        lblBeta0.setText("β₀: --");
        lblBeta1.setText("β₁: --");
        lblR2.setText("R²: --");
        lblN.setText("N: --");
    }
    
    public double getLimiteOutlierPercentual() {
        return sliderOutlierPercentual.getValue();
    }
    
    public boolean isExcluirOutliers() {
        return toggleExcluirOutliers.isSelected();
    }
    
    public void exibirMensagensValidacao(String mensagem) {
        txtAreaErros.setText(mensagem);
    }
    
    public JSlider getSliderOutlierPercentual() { return sliderOutlierPercentual; }
    public JToggleButton getToggleExcluirOutliers() { return toggleExcluirOutliers; }
}