package view;

import med.Medicao;
import coords.RegressaoLinear;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.math.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GraficoPanel extends JPanel {
    private List<Medicao> dados;
    private RegressaoLinear regressao;
    private double limiteOutlier;
    private double limiteVerde;
    private int largura, altura;
    private double limiteAbsoluto;
    //Borda que define o tamanho maximo do grafico
    //Define o tamanho do recuo
    //ex: margemEsquerda define uma área de 60 pixels livres à esquerda do gráfico
    private int margemEsquerda = 60;
    private int margemDireita = 230;
    private int margemTopo = 40;
    private int margemBase = 60;

    //Limites da escala
    private double gMinX, gMaxX, gMinY, gMaxY, gStepY;

    //Atributos para utilizacao no efeito de mouse por cima
    private Medicao medicaoMouse=null;
    private int mouseX=-1;
    private int mouseY=-1;


    public GraficoPanel() {
        setPreferredSize(new Dimension(800, 500));

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e){
                mouseX = e.getX();
                mouseY = e.getY();
                verificarMouse();
            }
        });
    }

    private void verificarMouse(){
        if (dados == null || dados.isEmpty())
            return;

        int areaLargura = largura - margemEsquerda - margemDireita;
        int areaAltura  = altura - margemTopo - margemBase;

        Medicao hoverEncontrado = null;
        double escalaX = areaLargura / (gMaxX - gMinX);
        double escalaY = areaAltura / (gMaxY - gMinY);
        for (Medicao m : dados) {
            double x = m.getTemperatura();
            double y = m.getConsumoKwh();

            int px = margemEsquerda + (int) ((m.getTemperatura() - gMinX) * escalaX);
            int py = (altura - margemBase) - (int) ((m.getConsumoKwh() - gMinY) * escalaY);
            // Se o mouse estiver dentro dessa área.
            if (Math.abs(mouseX - px) <= 6 && Math.abs(mouseY - py) <= 6) {
                hoverEncontrado = m;
                break;
            }
        }

        // Se o mouse entrou ou saiu de um ponto.
        if (medicaoMouse != hoverEncontrado) {
            medicaoMouse = hoverEncontrado;
            repaint();
        }
    }

    private void desenharTooltip(Graphics2D g2) {
        // Se não houver nenhum ponto focado, não desenha nada
        if (medicaoMouse == null) return;

        int areaLarg = largura - margemEsquerda - margemDireita;
        int areaAlt  = altura - margemTopo - margemBase;

        // Desenha um anel em volta do ponto selecionado
        int pontoX = margemEsquerda + (int) (((medicaoMouse.getTemperatura() - gMinX) / (gMaxX - gMinX)) * areaLarg);
        int pontoY = (altura - margemBase) - (int) (((medicaoMouse.getConsumoKwh() - gMinY) / (gMaxY - gMinY)) * areaAlt);

        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawOval(pontoX - 7, pontoY - 7, 14, 14);

        // textos da Caixinha
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        String txtTemp = String.format("Temperatura: %.1f °C", medicaoMouse.getTemperatura());
        String txtConsumo = String.format("Consumo: %.1f kWh", medicaoMouse.getConsumoKwh());

        // Calcula o tamanho da caixa dinamicamente
        int larguraTexto = Math.max(g2.getFontMetrics().stringWidth(txtTemp), g2.getFontMetrics().stringWidth(txtConsumo));
        int boxLarg = larguraTexto + 20;
        int boxAlt = 45;

        // Posição flutuante
        int px = pontoX + 15;
        int py = pontoY + 15;

        // Evita que a caixa saia da janela
        if (px + boxLarg > largura) px = mouseX - boxLarg - 10;
        if (py + boxAlt > altura) py = mouseY - boxAlt - 10;

        // Fundo da Caixa
        Color temaPopUp = UIManager.getColor("PopupMenu.background");
        if (temaPopUp == null) temaPopUp = UIManager.getColor("Panel.background");
        g2.setColor(new Color(temaPopUp.getRed(), temaPopUp.getGreen(), temaPopUp.getBlue(), 230));
        g2.fillRoundRect(px, py, boxLarg, boxAlt, 8, 8);

        // Borda da Caixa
        g2.setColor(UIManager.getColor("Component.borderColor"));
        g2.drawRoundRect(px, py, boxLarg, boxAlt, 8, 8);

        // Escreve os textos
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString(txtTemp, px + 10, py + 18);
        g2.drawString(txtConsumo, px + 10, py + 36);
    }

    public void atualizarDados(List<Medicao> dados, RegressaoLinear regressao, double limiteOutlier, double limiteVerde) {
        this.dados = dados;
        this.regressao = regressao;
        this.limiteOutlier = limiteOutlier;
        this.limiteVerde = limiteVerde;

        double media = dados.stream().mapToDouble(Medicao::getConsumoKwh).average().orElse(1.0);
        if (Math.abs(media) < 1e-10) media = 1.0;
        this.limiteAbsoluto = (limiteOutlier / 100.0) * media;

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
            minX -= 5;
        }
        if (maxY == minY) {
            maxY += 10;
            minY -= 10;
        }
        //Tamanho minimo do x da escala
        gMinX = (Math.floor(minX / 5.0) * 5.0)-5.0;
        if (gMinX >= 0)
            gMinX = -5.0;
        //Tamanho maximo do x da escala
        gMaxX = (Math.ceil(maxX / 5.0) * 5.0)+5.0;
        if (gMaxX <= gMinX)
            gMaxX = gMinX + 10.0;

        double amplitudeY = maxY - minY;

        // O intervalo entre as escalas depende do tamanho do Y
        gStepY = 10;
        if (amplitudeY > 500)
            gStepY = 100;
        else
            if (amplitudeY > 200)
                gStepY = 50;
            else
                if (amplitudeY > 100)
                    gStepY = 20;

        // Tamanho minimo do Y na escala
        gMinY = Math.floor((minY - (amplitudeY * 0.1)) / gStepY) * gStepY;
        if (gMinY < 0)
            gMinY = 0;

        //Tamanho maximo do Y na escala
        gMaxY = Math.ceil((maxY + (amplitudeY * 0.1)) / gStepY) * gStepY;
        desenharEixos(g2);
        Shape clipOriginal = g2.getClip();

        // Restringe a area que pode ser desenha para os valores
        g2.clipRect(margemEsquerda, margemTopo, largura - margemEsquerda - margemDireita, altura - margemTopo - margemBase);
        desenharPontos(g2);
        if (regressao != null && regressao.getN() >= 2) {
            desenharRetaRegressao(g2);
        }

        // Permite desenhar a legenda em cima da tabela
        g2.setClip(clipOriginal);
        if (regressao != null && regressao.getN() >= 2) {
            desenharEquacao(g2);
        }
        desenharLegenda(g2);
        desenharTooltip(g2);
    }

    private void desenharEixos(Graphics2D g2) {
        //define a cor como padrão do sistema
        g2.setColor(UIManager.getColor("Label.foreground"));
        //define a espessura da linha do grafico
        g2.setStroke(new BasicStroke(2));

        //calcula o tamanho da area dedicada ao grafico
        //área útil é igual à (medidas totais - medidas das margens)
        int areaLargura = largura - margemEsquerda - margemDireita;
        int areaAltura = altura - margemTopo - margemBase;

        //descobre a altura em pixels onde a linha horizontal do eixo X vai passar
        int eixoX_py = altura - margemBase;
        //Faz uma regra de três para descobrir exatamente em qual pixel fica o ponto com temperatura = 0
        //Assim, se houver temperaturas negativas, o eixo Y se move dinamicamente para a direita
        int eixoY_px = margemEsquerda + (int) (((0 - gMinX) / (gMaxX - gMinX)) * areaLargura);

        //drawLine(x1, y1, x2, y2)
        //desenha o eixo x
        g2.drawLine(margemEsquerda, eixoX_py, largura - margemDireita, eixoX_py); // Linha X (Base Dinâmica)
        //desenha o eixo y
        g2.drawLine(eixoY_px, margemTopo, eixoY_px, altura - margemBase);       // Linha Y (Zero Graus)

        g2.setFont(new Font("Arial", Font.PLAIN, 11));

        //desenha as marcações intermediárias de valores de 5 em 5 (eixo x)
        for (double tick = gMinX; tick <= gMaxX; tick += 5.0) {
            //px indica a posição do traço em x
            int px = margemEsquerda + (int) (((tick - gMinX) / (gMaxX - gMinX)) * areaLargura);
            g2.drawLine(px, eixoX_py - 4, px, eixoX_py + 4);
            String labelNumero = String.valueOf((int) tick);
            int larguraTexto = g2.getFontMetrics().stringWidth(labelNumero);
            g2.drawString(labelNumero, px - (larguraTexto / 2), eixoX_py + 20);
        }
        //desenha as marcações intermediárias de valores de 5 em 5 (eixo y)
        for (double tick = gMinY; tick <= gMaxY; tick += gStepY) {
            int py = (altura - margemBase) - (int) (((tick - gMinY) / (gMaxY - gMinY)) * areaAltura);
            g2.drawLine(eixoY_px - 4, py, eixoY_px + 4, py);
            String labelNumero = String.valueOf((int) tick);
            int larguraTexto = g2.getFontMetrics().stringWidth(labelNumero);
            int ajusteY;
            if (tick == gMinY)
                ajusteY = -4;
            else
                ajusteY = 4;
            g2.drawString(labelNumero, eixoY_px - larguraTexto - 8, py + ajusteY);
        }
        //escreve nomes dos eixos
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Temperatura (°C)", largura / 2 - 40, altura - 10);
        //rotaciona pra escrever na vertical
        g2.rotate(-Math.PI / 2);
        g2.drawString("Consumo (kWh)", -altura / 2 - 40, 20);
        g2.rotate(Math.PI / 2);
    }

    private void desenharPontos(Graphics2D g2) {
        if(dados == null || dados.isEmpty())
            return;

        //Define a area para desenhar os pontos
        int areaLarg = largura - margemEsquerda - margemDireita;
        int areaAlt  = altura - margemTopo - margemBase;

        double mediaY = limiteAbsoluto;

        //Impede que a divisao no limiteAbs utilize zero ou numero muito proximo de 0
        if (Math.abs(mediaY) < 1e-10)
            mediaY = 1.0;

        //Converte a porcentagem num limite com base na media
        double limiteAbs = (limiteOutlier / 100.0) * mediaY;

        for (Medicao m: dados){
            double x = m.getTemperatura();
            double y = m.getConsumoKwh();
            int px = margemEsquerda + (int) (((x - gMinX) / (gMaxX - gMinX)) * areaLarg);
            int py = (altura - margemBase) - (int) (((y - gMinY) / (gMaxY - gMinY)) * areaAlt);
            boolean isOutlier = false;
            boolean isGreen = false;
            if(regressao != null){
                double previsto = regressao.prever(x);
                double erroAbsoluto = Math.abs(y - previsto);
                if(erroAbsoluto > limiteAbs){
                    isOutlier = true;
                }
                else if (erroAbsoluto <= (limiteVerde/100 * limiteAbs))
                    isGreen = true;
            }
            if(isOutlier)
                g2.setColor(Color.RED);
            else if (isGreen)
                g2.setColor(Color.GREEN);
            else
                g2.setColor(UIManager.getColor("Label.foreground"));
            g2.fillOval(px-4, py-4,8,8);
        }
    }

    private void desenharRetaRegressao(Graphics2D g2) {
        if (regressao == null || regressao.getN() < 2)
            return;
        int areaAltura = altura-margemTopo-margemBase;

        //calcula o ponto de partida do grafico
        double y1 = regressao.getB0()+(regressao.getB1() * gMinX);
        int px1 = margemEsquerda;
        int py1 = (altura-margemBase)-(int)(((y1 - gMinY)/(gMaxY - gMinY))*areaAltura);

        //calcula o ponto de chegada
        double y2 = regressao.getB0()+(regressao.getB1() * gMaxX);
        int px2 = largura-margemDireita;
        int py2 = (altura-margemBase)-(int)(((y2 - gMinY)/(gMaxY - gMinY))*areaAltura);

        //traça a linha que vai do ponto inicial ao final
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(px1, py1, px2, py2);

        double mediaY = limiteAbsoluto;
        if (Math.abs(mediaY) < 1e-10)
            mediaY = 1.0;

        double limiteAbs = (limiteOutlier / 100.0) * mediaY;
        double limiteVerdeAbs = (limiteVerde / 100.0) * limiteAbs;

        //linha de limite superior
        double y1Sup = y1 + limiteAbs;
        double y2Sup = y2 + limiteAbs;
        int py1Sup = (altura - margemBase) - (int) (((y1Sup - gMinY) / (gMaxY - gMinY)) * areaAltura);
        int py2Sup = (altura - margemBase) - (int) (((y2Sup - gMinY) / (gMaxY - gMinY)) * areaAltura);

        //linha de limite inferior
        double y1Inf = y1 - limiteAbs;
        double y2Inf = y2 - limiteAbs;
        int py1Inf = (altura - margemBase) - (int) (((y1Inf - gMinY) / (gMaxY - gMinY)) * areaAltura);
        int py2Inf = (altura - margemBase) - (int) (((y2Inf - gMinY) / (gMaxY - gMinY)) * areaAltura);

        //configura o tracejado
        Stroke tracejado = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{8f, 6f}, 0.0f);
        g2.setStroke(tracejado);
        Color corTema = UIManager.getColor("Label.foreground");
        g2.setColor(new Color(corTema.getRed(), corTema.getGreen(), corTema.getBlue(), 120));

        //desenha as duas linhas
        g2.drawLine(px1, py1Sup, px2, py2Sup);
        g2.drawLine(px1, py1Inf, px2, py2Inf);

        //Limites Verdes
        double y1VerdeSup = y1 + limiteVerdeAbs;
        double y2VerdeSup = y2 + limiteVerdeAbs;
        int py1VerdeSup = (altura - margemBase) - (int) (((y1VerdeSup - gMinY) / (gMaxY - gMinY)) * areaAltura);
        int py2VerdeSup = (altura - margemBase) - (int) (((y2VerdeSup - gMinY) / (gMaxY - gMinY)) * areaAltura);

        double y1VerdeInf = y1 - limiteVerdeAbs;
        double y2VerdeInf = y2 - limiteVerdeAbs;
        int py1VerdeInf = (altura - margemBase) - (int) (((y1VerdeInf - gMinY) / (gMaxY - gMinY)) * areaAltura);
        int py2VerdeInf = (altura - margemBase) - (int) (((y2VerdeInf - gMinY) / (gMaxY - gMinY)) * areaAltura);

        // Criando um padrão tracejado ligeiramente diferente (mais curto) para diferenciar visualmente
        Stroke tracejadoVerde = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4f, 4f}, 0.0f);
        g2.setStroke(tracejadoVerde);
        g2.setColor(new Color(0, 150, 0, 140));

        // Desenha as duas retas limites do "corredor verde"
        g2.drawLine(px1, py1VerdeSup, px2, py2VerdeSup);
        g2.drawLine(px1, py1VerdeInf, px2, py2VerdeInf);

    }

    private void desenharEquacao(Graphics2D g2) {
        /*if (regressao == null)
            return;

        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));

        String eq = String.format("y = %.4f x + %.4f", regressao.getB1(), regressao.getB0());
        String r2 = String.format("R² = %.4f", regressao.getR2());

        g2.drawString(eq,15,altura-30);
        g2.drawString(r2,15,altura-15);
        */
        if (regressao == null)
            return;

        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));

        String eq = String.format("y = %.4fx + %.4f", regressao.getB1(), regressao.getB0());
        String r2 = String.format("R² = %.4f", regressao.getR2());

        // Calcula a largura que o texto vai ocupar na tela
        int larguraTexto = Math.max(g2.getFontMetrics().stringWidth(eq), g2.getFontMetrics().stringWidth(r2));

        // Define a largura da caixa (garante que tenha no mínimo 110px para não ficar menor que a legenda)
        int boxLargura = Math.max(larguraTexto + 20, 110);
        int boxAltura = 50;

        //O 'y' desce a altura da legenda (margemTopo + 85) mais 10 pixels de margem/folga.
        int y = margemTopo + 105 + 10;

        // 2. Para alinhar pela direita, descobrimos onde a legenda termina e subtraímos a largura desta caixa.
        int legendaX = largura - margemDireita + 60;
        int x = (legendaX + 110) - boxLargura;

        // Desenha o fundo translúcido
        Color temaPopUp = UIManager.getColor("PopupMenu.background");
        if (temaPopUp == null) {
            temaPopUp = UIManager.getColor("Panel.background");
        }
        g2.setColor(new Color(temaPopUp.getRed(), temaPopUp.getGreen(), temaPopUp.getBlue(), 220));
        g2.fillRoundRect(x, y, boxLargura, boxAltura, 10, 10);

        // Desenha a borda
        g2.setColor(UIManager.getColor("Component.borderColor"));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, boxLargura, boxAltura, 10, 10);

        // Escreve os textos empilhados dentro da caixa
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString(eq, x + 10, y + 20);
        g2.drawString(r2, x + 10, y + 40);
    }

    private void desenharLegenda(Graphics2D g2) {
        int x = largura-margemDireita+60;
        int y = margemTopo;
        int boxLargura = 110;
        int boxAltura = 105;

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

        g2.setColor(Color.GREEN);
        g2.fillOval(x + 10, y + 52, 8, 8);
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString("Ideal", x + 25, y + 60);

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x + 10, y + 72, x + 20, y + 72);
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString("Regressão", x + 25, y + 76);

        Stroke tracejadoLegenda = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4f, 4f}, 0.0f);
        g2.setStroke(tracejadoLegenda);

        Color corTema = UIManager.getColor("Label.foreground");
        g2.setColor(new Color(corTema.getRed(), corTema.getGreen(), corTema.getBlue(), 120));
        g2.drawLine(x + 10, y + 92, x + 20, y + 92);

        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawString("Limites", x + 25, y + 96);
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