package view;

import med.Medicao;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import coords.Coordenada;
import static java.lang.Math.*;

public class FiltrosPanel extends JPanel {

    //Botões para aplicar e limpar filtros
    private JButton btnAplicar;
    private JButton btnLimpar;

    // TODO: implementar CALENDÁRIO pra escolher data
    //Filtrar o tempo
    private JSpinner dataInicio;
    private JSpinner dataFim;

    //Filtrar temperatura
    private JTextField tempMin;
    private JTextField tempMax;

    //Filtro de localização
    //Filtra as cidades localizadas a uma certa distância de uma certa coordenada
    private JTextField latitude;
    private JTextField longitude;
    private JSpinner raioKm;

    private boolean limpando = false;
    private List<Medicao> listaRegistros = new ArrayList<>();
    private TabelaModel tabelaModel;

    public FiltrosPanel() {
        setLayout(new GridLayout(4, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarComponentes();
        configurarListeners();

        limparFiltrosUI();
    }

    private void limparTextoSpinner(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setText("");
        }
    }

    public void limparFiltrosUI() {
        limpando = true;

        dataInicio.setValue(new Date());
        dataFim.setValue(new Date());

        tempMin.setText("");
        tempMax.setText("");

        latitude.setText("");
        longitude.setText("");

        raioKm.setValue(10);

        limparTextoSpinner(dataInicio);
        limparTextoSpinner(dataFim);

        limpando = false;
    }

    public void setTabelaModel(TabelaModel model) {
        this.tabelaModel = model;
    }

    public void setListaOriginal(List<Medicao> lista) {
        this.listaRegistros = lista;
    }

    public List<Medicao> getListaRegistros() {
        return listaRegistros;
    }

    private void inicializarComponentes() {
        dataInicio = new JSpinner(new SpinnerDateModel());
        dataFim = new JSpinner(new SpinnerDateModel());
        tempMin = new JTextField(10);
        tempMax = new JTextField(10);

        latitude = new JTextField(8);
        longitude = new JTextField(8);
        raioKm = new JSpinner(new SpinnerNumberModel(10, 0, 10000, 1));

        //Data
        //Panel que filtra os dados relacionados a data
        JPanel panelTempo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTempo.setBorder(BorderFactory.createTitledBorder("Intervalo de Tempo"));
        panelTempo.add(new JLabel("Início:"));
        panelTempo.add(dataInicio);
        panelTempo.add(new JLabel("Fim:"));
        panelTempo.add(dataFim);

        //Temperatura
        //Painel que filtra os dados relacionados a temperatura
        JPanel panelTempe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTempe.setBorder(BorderFactory.createTitledBorder("Intervalo de Temperatura"));
        panelTempe.add(new JLabel("Min:"));
        panelTempe.add(tempMin);
        panelTempe.add(new JLabel("Max:"));
        panelTempe.add(tempMax);

        //Distância
        //Painel que filtra dados relacionados a distancia
        JPanel panelCoord = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCoord.setBorder(BorderFactory.createTitledBorder("Busca por Raio"));
        panelCoord.add(new JLabel("Lat:"));
        panelCoord.add(latitude);
        panelCoord.add(new JLabel("Lon:"));
        panelCoord.add(longitude);
        panelCoord.add(new JLabel("Raio (km):"));
        panelCoord.add(raioKm);

        add(panelTempo);
        add(panelTempe);
        add(panelCoord);

        // Cria um painel para os botões e adiciona ao FiltrosPanel
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAplicar = new JButton("Aplicar Filtros");
        btnLimpar = new JButton("Remover Filtros");

        panelBotoes.add(btnAplicar);
        panelBotoes.add(btnLimpar);
        add(panelBotoes);
    }

    private void configurarListeners() {
        btnAplicar.addActionListener(e -> apliqueFiltros());

        btnLimpar.addActionListener(e -> {
            limparFiltrosUI(); // Limpa os campos visuais da interface

            if (tabelaModel != null) {
                // atualizarOutliers() no TabelaModel restaura os dadosFiltrados
                // para a lista original (e mantém a regra de outliers ativa)
                tabelaModel.atualizarOutliers();
                tabelaModel.fireTableDataChanged(); // Atualiza a tabela na tela
            }
        });
    }

    private void apliqueFiltros() {
        if (limpando) // Para evitar que tente aplicar novos filtros enquanto limpa
            return;

        try {
            //Captura dados relacionados a temperatura (caso existam)
            Double tMin = null, tMax = null;
            if (!tempMin.getText().trim().isEmpty())
                tMin = Double.parseDouble(tempMin.getText().replace(",", "."));
            if (!tempMax.getText().trim().isEmpty())
                tMax = Double.parseDouble(tempMax.getText().replace(",", "."));

            //Captura dados relecionados as coordenadas (caso existam)
            Double latIni = null, lonIni = null;
            if (!latitude.getText().trim().isEmpty())
                latIni = Double.parseDouble(latitude.getText().replace(",", "."));
            if (!longitude.getText().trim().isEmpty())
                lonIni = Double.parseDouble(longitude.getText().replace(",", "."));

            double raio = ((Number) raioKm.getValue()).doubleValue();

            //Captura datas
            LocalDateTime inicio = null;
            LocalDateTime fim = null;

            boolean inicioVazio = ((JSpinner.DefaultEditor) dataInicio.getEditor()).getTextField().getText().isEmpty();
            boolean fimVazio = ((JSpinner.DefaultEditor) dataFim.getEditor()).getTextField().getText().isEmpty();

            if (!inicioVazio) {
                Date dateIni = (Date) dataInicio.getValue();
                inicio = dateIni.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            if (!fimVazio) {
                Date dateFim = (Date) dataFim.getValue();
                fim = dateFim.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }

            //Impede a aplicação do filtro apenas se nada foi digitado (mas filtrar os dados com apenas alguns parâmetros)
            if (tMin == null && tMax == null && latIni == null && lonIni == null && inicio == null && fim == null) {
                throw new IllegalArgumentException("Nenhum campo preenchido.");
            }

            //Envia os dados coletados para a verificação (sem latFim e lonFim)
            tabelaModel.setDadosFiltrados(verificarFiltros(inicio, fim, tMin, tMax, latIni, lonIni, raio));
            tabelaModel.fireTableDataChanged();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira apenas valores válidos.", "Erro de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }


    // Assinatura atualizada: Removidos latFinal e lonFinal
    public List<Medicao> verificarFiltros(LocalDateTime inicio, LocalDateTime fim, Double tMin, Double tMax, Double latInicial, Double lonInicial, double raioKm) {
        List<Medicao> medicaoFiltrada = new ArrayList<>();

        for (Medicao m : listaRegistros) {
            // Inicia assumindo que a medição passa
            boolean passaFiltro = true;

            //Filtros de tempo
            //se campo foi preenchido e a data está fora do intervalo não passa pelo filtro
            if (inicio != null && m.getTimeStamp().isBefore(inicio))
                passaFiltro = false;
            if (fim != null && m.getTimeStamp().isAfter(fim))
                passaFiltro = false;

            //Filtros de temperatura
            //se campo foi preenchido e temperatura está fora do intervalo não passa pelo filtro
            if (tMin != null && m.getTemperatura() < tMin)
                passaFiltro = false;
            if (tMax != null && m.getTemperatura() > tMax)
                passaFiltro = false;

            //Filtro de localização
            //So executa o cálculo de distância se as duas coordenadas base tiverem sido informadas
            if (passaFiltro && latInicial != null && lonInicial != null) {
                double latMedicao = m.getCoordenadas().getLatitude();
                double lonMedicao = m.getCoordenadas().getLongitude();

                double distanciaReal = Coordenada.calcularDistancia(latInicial, lonInicial, latMedicao, lonMedicao);

                if (distanciaReal > raioKm) {
                    passaFiltro = false;
                }
            }

            // Se sobreviveu a todos os testes ativos, adiciona à tabela
            if (passaFiltro) {
                medicaoFiltrada.add(m);
            }
        }

        return medicaoFiltrada;
    }
}