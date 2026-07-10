package controller;

import coords.RegressaoLinear;
import med.Medicao;
import view.MedicoesPanel;
import view.RegressaoPanel;
import view.TabelaModel;
import view.GraficoPanel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.List;
import java.util.ArrayList;

public class RegressaoController {
    private MedicoesPanel medicoesPanel;
    private RegressaoPanel regressaoPanel;
    private GraficoPanel graficoPanel;
    private RegressaoLinear regressao;

    public RegressaoController(MedicoesPanel medicoesPanel, RegressaoPanel regressaoPanel,GraficoPanel graficoPanel) {
        this.medicoesPanel = medicoesPanel;
        this.regressaoPanel = regressaoPanel;
        this.graficoPanel = graficoPanel;
        this.regressao = new RegressaoLinear();

        inicializarListeners();
        atualizarRegressao();
    }

    private void inicializarListeners() {
        //Se a tabela foi alterada, atualiza as medidas de regressão
        medicoesPanel.getTableModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                atualizarRegressao();
            }
        });

        //Ouvir mudanças no botão de ligar/desligar Outliers
        regressaoPanel.getToggleExcluirOutliers().addActionListener(e -> atualizarRegressao());

        //Ouvir mudanças no Slider de Outliers
        regressaoPanel.getSliderOutlierPercentual().addChangeListener(e -> {
            // Só recalcula quando o usuário soltar o slider (para não travar a tela)
            if (!regressaoPanel.getSliderOutlierPercentual().getValueIsAdjusting()) {
                atualizarRegressao();
            }
        });
    }

    // pega os dados, calcula e atualiza a tela
    public void atualizarRegressao() {
        //pega os dados atuais da tabela
        List<Medicao> dadosCompletos = medicoesPanel.getTableModel().getMedicoes();

        //limpa mensagens de erro antigas
        regressaoPanel.exibirMensagensValidacao("");

        //precisamos de pelo menos 2 pontos para uma reta
        if (dadosCompletos == null || dadosCompletos.size() < 2) {
            regressaoPanel.limparResultados();
            regressaoPanel.exibirMensagensValidacao("Aviso: São necessárias pelo menos 2 medições para calcular a regressão.");
            if (graficoPanel != null) {
                graficoPanel.atualizarDados(new ArrayList<>(), null, 5.0, 0.0);
            }
            return;
        }

        List<Medicao> dadosParaCalculo = dadosCompletos;
        double limite = regressaoPanel.getLimiteOutlierPercentual();
        double limiteVerde = regressaoPanel.getLimiteVerde();

        // Se o botão de "Excluir da tabela" estiver ATIVADO
        if (regressaoPanel.isExcluirOutliers()) {
            dadosParaCalculo = removerOutliers(dadosCompletos, limite);
            if (dadosParaCalculo.size() < 2) {
                regressaoPanel.limparResultados();
                regressaoPanel.exibirMensagensValidacao("Aviso: Após remover outliers, restaram menos de 2 dados.");

                TabelaModel model = medicoesPanel.getTableModel();
                model.setLimiteOutlier(limite);
                model.setShowOutliers(false);
                if (graficoPanel != null)
                    graficoPanel.atualizarDados(model.getDadosFiltrados(), null, limite, limiteVerde);
                return;
            }
        }

        //calcula os coeficientes e r2
        regressao.setDados(dadosParaCalculo);
        regressao.calcularCoeficientes();
        double r2 = regressao.calcularR2();

        //alimenta todas as medições com os novos resíduos gerados pela reta
        for (Medicao m : dadosCompletos) {
            double previsto = regressao.prever(m.getTemperatura());
            m.setConsumoPrevisto(previsto);

            // Calcula o resíduo
            double real = m.getConsumoKwh();
            double residuo = (real != 0) ? ((real - previsto) / real) * 100 : 0;
            m.setResiduoPercentual(residuo);
        }

        //sincroniza com o TabelaModel
        TabelaModel model = medicoesPanel.getTableModel();
        model.setLimiteOutlier(limite);

        // Se 'isExcluirOutliers' for true, 'showOutliers' no modelo vira false (para esconder)
        // Se 'isExcluirOutliers' for false, 'showOutliers' no modelo vira true (para exibir todos e pintar de vermelho)
        model.setShowOutliers(!regressaoPanel.isExcluirOutliers());

        //exibe os resultados nos JLabels da View
        regressaoPanel.atualizarResultados(regressao.getB0(), regressao.getB1(), r2, dadosParaCalculo.size());

        if (graficoPanel != null) {
            graficoPanel.atualizarDados(model.getDadosFiltrados(), regressao, limite, limiteVerde);
        }
    }

    //filtrar outliers
    private List<Medicao> removerOutliers(List<Medicao> dados, double limitePercentual) {
        //a detecção de outliers depende do cálculo da própria reta,
        //calcula-se a reta com todos os dados, depois remove os distantes

        RegressaoLinear previsao = new RegressaoLinear();
        previsao.setDados(dados);
        previsao.calcularCoeficientes();

        double somaY = 0;
        for (Medicao m : dados) {
            somaY += m.getConsumoKwh();
        }
        double mediaY = somaY / dados.size();

        //se a média for nula ou absurda por conta de erros
        if (Math.abs(mediaY) < 1e-10) {
            mediaY = 1.0;
        }

        //define o limite absoluto baseado no slider
        double limiteAbsoluto = Math.abs((limitePercentual / 100.0) * mediaY);

        List<Medicao> dadosFiltrados = new ArrayList<>();

        for (Medicao m : dados) {
            double previsto = previsao.prever(m.getTemperatura());
            double real = m.getConsumoKwh();

            //calcula o erro
            double erro = Math.abs(real - previsto);

            //se o erro for menor que o limite imposto no slider, mantém o dado
            if (erro <= limiteAbsoluto) {
                dadosFiltrados.add(m);
            }
        }
        return dadosFiltrados;
    }
}