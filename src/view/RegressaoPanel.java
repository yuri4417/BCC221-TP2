package view;

import javax.swing.*;
import java.awt.*;

public class RegressaoPanel extends JPanel {
    private JLabel labelB0;
    private JLabel labelB1;
    private JLabel labelR2;
    private JLabel labelN;
    private JSlider sliderOutlierPercentual;
    private JToggleButton toggleExcluirOutliers;
    private JTextArea txtAreaErros;
    private JProgressBar barraR2;

    public RegressaoPanel() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        JPanel panelResultados = new JPanel(new BorderLayout(10, 10));
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados da Regressão"));

        //Painel das variáveis de análise
        JPanel panelLabels = new JPanel(new GridLayout(1, 4, 10, 10));
        labelB0 = new JLabel("β₀: --", SwingConstants.CENTER);
        labelB1 = new JLabel("β₁: --", SwingConstants.CENTER);
        labelR2 = new JLabel("R²: --", SwingConstants.CENTER);
        labelN = new JLabel("N: --", SwingConstants.CENTER);

        panelLabels.add(labelB0);
        panelLabels.add(labelB1);
        panelLabels.add(labelR2);
        panelLabels.add(labelN);

        //Painel para barra de R2
        barraR2 = new JProgressBar(0, 100);
        barraR2.setStringPainted(true); // Permite mostrar o texto dentro da barra
        barraR2.setString("-- %");      // Texto inicial

        JPanel panelBarra = new JPanel(new BorderLayout(10, 0));
        panelBarra.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel labelTituloBarra = new JLabel("Qualidade do Ajuste (R²):");

        panelBarra.add(labelTituloBarra, BorderLayout.WEST);
        panelBarra.add(barraR2, BorderLayout.CENTER);

        // Adiciona os subpaineis ao painel de resultados principal
        panelResultados.add(panelLabels, BorderLayout.CENTER);
        panelResultados.add(panelBarra, BorderLayout.SOUTH);


        //Painel de outliers
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


        //Area das mensagens
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

    //atualiza os resultados mostrados
    public void atualizarResultados(double beta0, double beta1, double r2, int n) {
        labelB0.setText(String.format("β₀: %.4f", beta0));
        labelB1.setText(String.format("β₁: %.4f", beta1));
        labelR2.setText(String.format("R²: %.4f", r2));
        labelN.setText(String.format("N: %d", n));

        //atualiza a barra de progresso (convertendo R2 que geralmente é de 0 a 1 para porcentagem 0 a 100)
        int r2Percentual = (int) Math.round(r2 * 100);

        //garante que o valor não passe de 100 ou fique negativo
        r2Percentual = Math.max(0, Math.min(100, r2Percentual));

        barraR2.setValue(r2Percentual);
        barraR2.setString(String.format("%.1f %%", r2 * 100)); // Mostra o valor exato dentro da barra
    }

    //limpa os resultados
    public void limparResultados() {
        labelB0.setText("β₀: --");
        labelB1.setText("β₁: --");
        labelR2.setText("R²: --");
        labelN.setText("N: --");

        //reseta a barra de progresso
        barraR2.setValue(0);
        barraR2.setString("-- %");
    }

    //pega a porcentagem determinada pelo slider
    public double getLimiteOutlierPercentual() {
        return sliderOutlierPercentual.getValue();
    }

    //retorna se a opção de excluir outliers está selecionada ou não
    public boolean isExcluirOutliers() {
        return toggleExcluirOutliers.isSelected();
    }

    //preenche a área de mensagens de erro com a mensagem especificada
    public void exibirMensagensValidacao(String mensagem) {
        txtAreaErros.setText(mensagem);
    }

    //retorna o próprio slider
    public JSlider getSliderOutlierPercentual() { return sliderOutlierPercentual; }

    //retorna o próprio botão de excluir outliers
    public JToggleButton getToggleExcluirOutliers() { return toggleExcluirOutliers; }
}