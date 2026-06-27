package ufop.poo.tp2.view;

import ufop.poo.tp2.model.Medicao;
import ufop.poo.tp2.model.RegressaoLinear;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraficoPanel extends JPanel {
    private List<Medicao> dados;
    private RegressaoLinear regressao;
    private double limiteOutlier;
    private int largura, altura;
    private int margemEsquerda = 60;
    private int margemDireita = 40;
    private int margemTopo = 40;
    private int margemBase = 60;
    
    public GraficoPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 500));
    }
    
    public void atualizarDados(List<Medicao> dados, RegressaoLinear regressao, double limiteOutlier) {
        this.dados = dados;
        this.regressao = regressao;
        this.limiteOutlier = limiteOutlier;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        largura = getWidth();
        altura = getHeight();
        
        if (dados == null || dados.isEmpty()) {
            desenharMensagemSemDados(g2);
            return;
        }
        
        desenharEixos(g2);
        desenharPontos(g2);
        
        if (regressao != null && regressao.getN() >= 2) {
            desenharRetaRegressao(g2);
            desenharEquacao(g2);
        }
        
        desenharLegenda(g2);
    }
    
    private void desenharEixos(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        
        // Eixo X
        g2.drawLine(margemEsquerda, altura - margemBase, 
                    largura - margemDireita, altura - margemBase);
        
        // Eixo Y
        g2.drawLine(margemEsquerda, margemTopo, 
                    margemEsquerda, altura - margemBase);
        
        // Rótulos dos eixos
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Temperatura (°C)", largura / 2, altura - 10);
        
        g2.rotate(-Math.PI / 2);
        g2.drawString("Consumo (kWh)", -altura / 2, 20);
        g2.rotate(Math.PI / 2);
    }
    
    private void desenharPontos(Graphics2D g2) {
        // Implementar: encontrar min/max de temperatura e consumo
        // para escalonar os pontos no gráfico
    }
    
    private void desenharRetaRegressao(Graphics2D g2) {
        if (regressao == null) return;
        
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));
        
        // Implementar: desenhar a reta y = beta0 + beta1 * x
    }
    
    private void desenharEquacao(Graphics2D g2) {
        if (regressao == null) return;
        
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        String eq = String.format("y = %.4f x + %.4f", regressao.getBeta1(), regressao.getBeta0());
        String r2 = String.format("R² = %.4f", regressao.getR2());
        
        g2.drawString(eq, margemEsquerda + 10, margemTopo + 20);
        g2.drawString(r2, margemEsquerda + 10, margemTopo + 40);
    }
    
    private void desenharLegenda(Graphics2D g2) {
        int x = largura - margemDireita - 100;
        int y = margemTopo;
        
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        
        g2.setColor(Color.BLACK);
        g2.fillOval(x, y, 8, 8);
        g2.drawString("Normal", x + 12, y + 8);
        
        g2.setColor(Color.RED);
        g2.fillOval(x, y + 20, 8, 8);
        g2.drawString("Outlier", x + 12, y + 28);
        
        g2.setColor(Color.BLUE);
        g2.drawLine(x, y + 45, x + 20, y + 45);
        g2.drawString("Regressão", x + 24, y + 49);
    }
    
    private void desenharMensagemSemDados(Graphics2D g2) {
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Sem dados para exibir", largura / 2 - 80, altura / 2);
    }
    
    private double getMinTemperatura() {
        if (dados == null || dados.isEmpty()) return 0;
        return dados.stream().mapToDouble(Medicao::getTemperatura).min().orElse(0);
    }
    
    private double getMaxTemperatura() {
        if (dados == null || dados.isEmpty()) return 0;
        return dados.stream().mapToDouble(Medicao::getTemperatura).max().orElse(0);
    }
    
    private double getMinConsumo() {
        if (dados == null || dados.isEmpty()) return 0;
        return dados.stream().mapToDouble(Medicao::getConsumoKwh).min().orElse(0);
    }
    
    private double getMaxConsumo() {
        if (dados == null || dados.isEmpty()) return 0;
        return dados.stream().mapToDouble(Medicao::getConsumoKwh).max().orElse(0);
    }
}