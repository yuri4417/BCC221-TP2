package controller;

import coords.RegressaoLinear;
import med.Medicao;
import view.MedicoesPanel;
import view.RegressaoPanel;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.List;
import java.util.ArrayList;

public class RegressaoController {
    private MedicoesPanel medicoesPanel;
    private RegressaoPanel regressaoPanel;
    private RegressaoLinear regressao;

    public RegressaoController(MedicoesPanel medicoesPanel, RegressaoPanel regressaoPanel) {

        this.medicoesPanel = medicoesPanel;
        this.regressaoPanel = regressaoPanel;
        this.regressao = new RegressaoLinear();

        inicializarOuvintes();
        atualizarRegressao();
    }

    private void inicializarOuvintes() {
        //Se a tabela foi alterada, atualiza as medidas de regressão
        medicoesPanel.getTableModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                atualizarRegressao();
            }
        });

        // 2. Ouvir mudanças no botão de ligar/desligar Outliers
        regressaoPanel.getToggleExcluirOutliers().addActionListener(e -> atualizarRegressao());

        // 3. Ouvir mudanças no Slider de Outliers
        regressaoPanel.getSliderOutlierPercentual().addChangeListener(e -> {
            // Só recalcula quando o usuário soltar o slider (para não travar a tela)
            if (!regressaoPanel.getSliderOutlierPercentual().getValueIsAdjusting()) {
                atualizarRegressao();
            }
        });
    }

    // O coração da integração: pega os dados, calcula e atualiza a tela
    public void atualizarRegressao() {
        //pega os dados atuais da tabela
        List<Medicao> dadosCompletos = medicoesPanel.getTableModel().getMedicoes();

        //limpa mensagens de erro antigas
        regressaoPanel.exibirMensagensValidacao("");

        // Validação básica: precisamos de pelo menos 2 pontos para uma reta
        if (dadosCompletos == null || dadosCompletos.size() < 2) {
            regressaoPanel.limparResultados();
            regressaoPanel.exibirMensagensValidacao("Aviso: São necessárias pelo menos 2 medições para calcular a regressão.");
            return;
        }

        try {
            List<Medicao> dadosParaCalculo = dadosCompletos;

            //se excluir outliers estiver selecionado
            if (regressaoPanel.isExcluirOutliers()) {
                //calcula limite permitido
                double limite = regressaoPanel.getLimiteOutlierPercentual();
                //remove valores fora do intervalo permitido
                dadosParaCalculo = removerOutliers(dadosCompletos, limite);

                if(dadosParaCalculo.size() < 2) {
                    regressaoPanel.limparResultados();
                    regressaoPanel.exibirMensagensValidacao("Aviso: Após remover outliers, restaram menos de 2 dados.");
                    return;
                }
            }
            // -----------------------------------------------------------------------------

            // Envia os dados para a classe de matemática
            regressao.setDados(dadosParaCalculo);
            regressao.calcularCoeficientes();
            double r2 = regressao.calcularR2();

            //pega os resultados e envia de volta para serem exibidos
            regressaoPanel.atualizarResultados(regressao.getB0(), regressao.getB1(), r2, dadosParaCalculo.size());

        } catch (Exception ex) {
            // Se algo der erro na matemática (ex: divisão por zero), mostra na caixinha vermelha
            regressaoPanel.limparResultados();
            regressaoPanel.exibirMensagensValidacao("Erro no cálculo: " + ex.getMessage());
        }
    }

    // Método auxiliar para filtrar outliers
    private List<Medicao> removerOutliers(List<Medicao> dados, double limitePercentual) {
        //a detecção de outliers depende do cálculo da própria reta,
        //calcula-se a reta com todos os dados, depois remove os distantes

        RegressaoLinear previsao = new RegressaoLinear();
        previsao.setDados(dados);
        previsao.calcularCoeficientes();

        List<Medicao> dadosFiltrados = new ArrayList<>();

        for (Medicao m : dados) {
            double previsto = previsao.prever(m.getTemperatura());
            double real = m.getConsumoKwh();

            //calcula o erro
            double erro = Math.abs((real - previsto) / real) * 100;

            //se o erro for menor que o limite imposto no slider, mantém o dado
            if (erro <= limitePercentual) {
                dadosFiltrados.add(m);
            }
        }
        return dadosFiltrados;
    }
}