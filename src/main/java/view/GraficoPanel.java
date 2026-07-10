package view;

import med.Medicao;
import coords.RegressaoLinear;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.math.*;

public class GraficoPanel extends JPanel {
    private List<Medicao> dados;
    private RegressaoLinear regressao;
    private double limiteOutlier;
    private int largura, altura;
    private int margemEsquerda = 60;
    private int margemDireita = 140;
    private int margemTopo = 40;
    private int margemBase = 60;

    private double gMinX, gMaxX, gMinY, gMaxY, gStepY;

    public GraficoPanel() {
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

        double minX = getMinTemperatura();
        double maxX = getMaxTemperatura();
        double minY = getMinConsumo();
        double maxY = getMaxConsumo();
        if (maxX == minX) {
            maxX += 5;
            minX -= 5; }
        if (maxY == minY) {
            maxY += 10;
            minY -= 10;
        }
        gMinX = Math.floor(minX / 5.0) * 5.0;
        if (gMinX >= 0)
            gMinX = -5.0;
        gMaxX = Math.ceil(maxX / 5.0) * 5.0;
        if (gMaxX <= gMinX) gMaxX = gMinX + 10.0;

        double amplitudeY = maxY - minY;

        gStepY = 10;
        if (amplitudeY > 500)
            gStepY = 100;
        else
            if (amplitudeY > 200)
                gStepY = 50;
            else
                if (amplitudeY > 100)
                    gStepY = 20;
        gMinY = Math.floor((minY - (amplitudeY * 0.1)) / gStepY) * gStepY;
        if (gMinY < 0)
            gMinY = 0;
        gMaxY = Math.ceil((maxY + (amplitudeY * 0.1)) / gStepY) * gStepY;
        desenharEixos(g2);
        Shape clipOriginal = g2.getClip();
        g2.clipRect(margemEsquerda, margemTopo, largura - margemEsquerda - margemDireita, altura - margemTopo - margemBase);
        desenharPontos(g2);
        if (regressao != null && regressao.getN() >= 2) {
            desenharRetaRegressao(g2);
        }
        g2.setClip(clipOriginal);
        if (regressao != null && regressao.getN() >= 2) {
            desenharEquacao(g2);
        }
        desenharLegenda(g2);
    }

    private void desenharEixos(Graphics2D g2) {
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.setStroke(new BasicStroke(2));

        int areaLargura = largura - margemEsquerda - margemDireita;
        int areaAltura = altura - margemTopo - margemBase;
        int eixoX_py = altura - margemBase;

        int eixoY_px = margemEsquerda + (int) (((0 - gMinX) / (gMaxX - gMinX)) * areaLargura);

        g2.drawLine(margemEsquerda, eixoX_py, largura - margemDireita, eixoX_py); // Linha X (Base Dinâmica)
        g2.drawLine(eixoY_px, margemTopo, eixoY_px, altura - margemBase);       // Linha Y (Zero Graus)
        g2.setFont(new Font("Arial", Font.PLAIN, 11));

        for (double tick = gMinX; tick <= gMaxX; tick += 5.0) {
            int px = margemEsquerda + (int) (((tick - gMinX) / (gMaxX - gMinX)) * areaLargura);
            g2.drawLine(px, eixoX_py - 4, px, eixoX_py + 4);
            String labelNumero = String.valueOf((int) tick);
            int larguraTexto = g2.getFontMetrics().stringWidth(labelNumero);
            g2.drawString(labelNumero, px - (larguraTexto / 2), eixoX_py + 20);
        }
        for (double tick = gMinY; tick <= gMaxY; tick += gStepY) {
            int py = (altura - margemBase) - (int) (((tick - gMinY) / (gMaxY - gMinY)) * areaAltura);
            g2.drawLine(eixoY_px - 4, py, eixoY_px + 4, py);
            String labelNumero = String.valueOf((int) tick);
            int larguraTexto = g2.getFontMetrics().stringWidth(labelNumero);
            g2.drawString(labelNumero, eixoY_px - larguraTexto - 8, py + 4);
        }
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Temperatura (°C)", largura / 2 - 40, altura - 10);
        g2.rotate(-Math.PI / 2);
        g2.drawString("Consumo (kWh)", -altura / 2 - 40, 20);
        g2.rotate(Math.PI / 2);
    }

    private void desenharPontos(Graphics2D g2) {
        if(dados==null||dados.isEmpty())
            return;
        int areaLarg = largura-margemEsquerda-margemDireita;
        int areaAlt  = altura-margemTopo-margemBase;

        double somaY = 0;
        for (Medicao m : dados) {
            somaY += m.getConsumoKwh();
        }
        double mediaY;
        if(dados.isEmpty()){
            mediaY=1.0;
        }
        else{
            mediaY= somaY/dados.size();
        }
        if (Math.abs(mediaY) < 1e-10) mediaY = 1.0;
        double limiteAbs = (limiteOutlier / 100.0) * mediaY;
        for (Medicao m: dados){
            double x = m.getTemperatura();
            double y = m.getConsumoKwh();
            int px = margemEsquerda + (int) (((x - gMinX) / (gMaxX - gMinX)) * areaLarg);
            int py = (altura - margemBase) - (int) (((y - gMinY) / (gMaxY - gMinY)) * areaAlt);
            boolean isOutlier = false;
            if(regressao != null){
                double previsto = regressao.prever(x);
                double erroAbsoluto = Math.abs(y - previsto);
                if(erroAbsoluto > limiteAbs){
                    isOutlier = true;
                }
            }
            if(isOutlier)
                g2.setColor(Color.RED);
            else
                g2.setColor(UIManager.getColor("Label.foreground"));
            g2.fillOval(px-4, py-4,8,8);
        }
    }

    private void desenharRetaRegressao(Graphics2D g2) {
        if (regressao == null || regressao.getN() < 2)
            return;
        int areaAltura = altura-margemTopo-margemBase;

        double y1 = regressao.getB0()+(regressao.getB1() * gMinX);
        int px1 = margemEsquerda;
        int py1 = (altura-margemBase)-(int)(((y1 - gMinY)/(gMaxY - gMinY))*areaAltura);

        double y2 = regressao.getB0()+(regressao.getB1() * gMaxX);
        int px2 = largura-margemDireita;
        int py2 = (altura-margemBase)-(int)(((y2 - gMinY)/(gMaxY - gMinY))*areaAltura);

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(px1, py1, px2, py2);

        double somaY = 0;
        for (Medicao m : dados) {
            somaY += m.getConsumoKwh();
        }
        double mediaY = dados.isEmpty() ? 1.0 : somaY / dados.size();
        if (Math.abs(mediaY) < 1e-10) mediaY = 1.0;
        double limiteAbs = (limiteOutlier / 100.0) * mediaY;

        double y1Sup = y1 + limiteAbs;
        double y2Sup = y2 + limiteAbs;
        int py1Sup = (altura - margemBase) - (int) (((y1Sup - gMinY) / (gMaxY - gMinY)) * areaAltura);
        int py2Sup = (altura - margemBase) - (int) (((y2Sup - gMinY) / (gMaxY - gMinY)) * areaAltura);

        double y1Inf = y1 - limiteAbs;
        double y2Inf = y2 - limiteAbs;
        int py1Inf = (altura - margemBase) - (int) (((y1Inf - gMinY) / (gMaxY - gMinY)) * areaAltura);
        int py2Inf = (altura - margemBase) - (int) (((y2Inf - gMinY) / (gMaxY - gMinY)) * areaAltura);

        Stroke tracejado = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{8f, 6f}, 0.0f);
        g2.setStroke(tracejado);
        Color corTema = UIManager.getColor("Label.foreground");
        g2.setColor(new Color(corTema.getRed(), corTema.getGreen(), corTema.getBlue(), 120));

        g2.drawLine(px1, py1Sup, px2, py2Sup);
        g2.drawLine(px1, py1Inf, px2, py2Inf);
    }

    private void desenharEquacao(Graphics2D g2) {
        if (regressao == null)
            return;

        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));

        String eq = String.format("y = %.4f x + %.4f", regressao.getB1(), regressao.getB0());
        String r2 = String.format("R² = %.4f", regressao.getR2());

        g2.drawString(eq,margemEsquerda+10,margemTopo+20);
        g2.drawString(r2,margemEsquerda+10,margemTopo+40);
    }

    private void desenharLegenda(Graphics2D g2) {
        int x = largura-margemDireita+15;
        int y = margemTopo;
        int boxLargura = 110;
        int boxAltura = 85;

        Color temaPopUp = UIManager.getColor("PopupMenu.background");
        if (temaPopUp == null) {
            temaPopUp = UIManager.getColor("Panel.background");
        }
        g2.setColor(new Color(temaPopUp.getRed(), temaPopUp.getGreen(), temaPopUp.getBlue(), 220));
        g2.fillRoundRect(x, y, boxLargura, boxAltura, 10, 10);

        g2.setColor(UIManager.getColor("Component.borderColor"));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, boxLargura, boxAltura, 10, 10);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));

        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.fillOval(x + 10, y + 12, 8, 8);
        g2.drawString("Normal", x + 25, y + 20);

        g2.setColor(Color.RED);
        g2.fillOval(x + 10, y + 32, 8, 8);
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString("Outlier", x + 25, y + 40);

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x + 10, y + 52, x + 20, y + 52);
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString("Regressão", x + 25, y + 56);

        Stroke tracejadoLegenda = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4f, 4f}, 0.0f);
        g2.setStroke(tracejadoLegenda);

        Color corTema = UIManager.getColor("Label.foreground");
        g2.setColor(new Color(corTema.getRed(), corTema.getGreen(), corTema.getBlue(), 120));
        g2.drawLine(x + 10, y + 72, x + 20, y + 72);

        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString("Limites", x + 25, y + 76);
    }

    private void desenharMensagemSemDados(Graphics2D g2) {
        g2.setColor(UIManager.getColor("Label.disabledForeground"));
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Sem dados para exibir", largura / 2 - 80, altura / 2);
    }

    private double getMinTemperatura() {
        if (dados == null || dados.isEmpty())
            return 0;
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