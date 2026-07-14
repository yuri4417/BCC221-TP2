package view;

import controller.SistemaController;
import med.Medicao;
import static controller.SistemaController.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import coords.Coordenada;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;
import com.github.lgooddatepicker.components.TimePickerSettings;
import javax.swing.UIManager;

public class FiltrosPanel extends JPanel {

    // Botões para aplicar e limpar filtros
    private JButton btnAplicar;
    private JButton btnLimpar;

    // Usando DateTimePicker para selecionar Data e Hora juntos
    private DateTimePicker dataInicio;
    private DateTimePicker dataFim;

    // Filtrar temperatura
    private JTextField tempMin;
    private JTextField tempMax;

    // Filtro de localização
    private JTextField latitude;
    private JTextField longitude;
    private JSpinner raioKm;

    private boolean limpando = false;
    private List<Medicao> listaRegistros = new ArrayList<>();
    private TabelaModel tabelaModel;

    public FiltrosPanel() {
        setLayout(new GridLayout(5, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarComponentes();
        configurarListeners();

        limparFiltrosUI();
    }

    public void limparFiltrosUI() {
        limpando = true;

        // O método .clear() do DateTimePicker limpa o calendário e o relógio de uma vez só
        dataInicio.clear();
        dataFim.clear();

        tempMin.setText("");
        tempMax.setText("");

        latitude.setText("");
        longitude.setText("");

        raioKm.setValue(10);

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
        dataInicio = new DateTimePicker(getConfigsData(), getConfigsTempo());
        dataFim = new DateTimePicker(getConfigsData(), getConfigsTempo());
        tempMin = new JTextField(10);
        tempMax = new JTextField(10);

        latitude = new JTextField(8);
        longitude = new JTextField(8);
        raioKm = new JSpinner(new SpinnerNumberModel(10, 0, 10000, 1));

        // Painel de Intervalo de Tempo
        JPanel panelTempo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTempo.setBorder(BorderFactory.createTitledBorder("Intervalo de Tempo"));
        panelTempo.add(new JLabel("Início:"));
        panelTempo.add(dataInicio);
        panelTempo.add(new JLabel("Fim:"));
        panelTempo.add(dataFim);

        // Painel de Temperatura
        JPanel panelTempe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTempe.setBorder(BorderFactory.createTitledBorder("Intervalo de Temperatura"));
        panelTempe.add(new JLabel("Min:"));
        panelTempe.add(tempMin);
        panelTempe.add(new JLabel("Max:"));
        panelTempe.add(tempMax);

        // Painel de Busca por Raio
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

        // Painel para os botões de ação
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
            limparFiltrosUI();
            if (tabelaModel != null) {
                tabelaModel.atualizarOutliers();
                tabelaModel.fireTableDataChanged();
            }
        });
    }

    private void apliqueFiltros() {
        if (limpando)
            return;

        try {
            // Captura dados de temperatura
            Double tMin = parseDouble(tempMin.getText());
            Double tMax = parseDouble(tempMax.getText());

            // Captura dados de coordenadas
            Double latIni = parseDouble(latitude.getText());
            Double lonIni = parseDouble(longitude.getText());

            double raio = ((Number) raioKm.getValue()).doubleValue();

            // ATUALIZADO: Captura inteligente de Data e Hora
            LocalDateTime inicio = null;
            LocalDateTime fim = null;

            // Extrai os valores internos do DateTimePicker de início
            LocalDate dateIni = dataInicio.getDatePicker().getDate();
            LocalTime timeIni = dataInicio.getTimePicker().getTime();

            // Extrai os valores internos do DateTimePicker de fim
            LocalDate dateFim = dataFim.getDatePicker().getDate();
            LocalTime timeFim = dataFim.getTimePicker().getTime();

            // Regra para Data Inicial
            if (dateIni != null) {
                inicio = LocalDateTime.of(dateIni, timeIni != null ? timeIni : LocalTime.MIN);
            }

            // Regra para Data Final
            if (dateFim != null) {
                fim = LocalDateTime.of(dateFim, timeFim != null ? timeFim : LocalTime.MAX);
            }

            // Impede a aplicação se nenhum filtro foi definido
            if (tMin == null && tMax == null && latIni == null && lonIni == null && inicio == null && fim == null) {
                throw new IllegalArgumentException("Nenhum campo preenchido.");
            }

            // Envia para o TabelaModel e atualiza a tela
            tabelaModel.setDadosFiltrados(verificarFiltros(inicio, fim, tMin, tMax, latIni, lonIni, raio));
            tabelaModel.fireTableDataChanged();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira apenas valores válidos nos campos numéricos.", "Erro de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    public List<Medicao> verificarFiltros(LocalDateTime inicio, LocalDateTime fim, Double tMin, Double tMax, Double latInicial, Double lonInicial, double raioKm) {
        List<Medicao> medicaoFiltrada = new ArrayList<>();

        for (Medicao m : listaRegistros) {
            boolean passaFiltro = true;

            // Filtros de tempo (compara LocalDateTime completo)
            if (inicio != null && m.getTimeStamp().isBefore(inicio))
                passaFiltro = false;
            if (fim != null && m.getTimeStamp().isAfter(fim))
                passaFiltro = false;

            // Filtros de temperatura
            if (tMin != null && m.getTemperatura() < tMin)
                passaFiltro = false;
            if (tMax != null && m.getTemperatura() > tMax)
                passaFiltro = false;

            // Filtro de localização (Busca por Raio)
            if (passaFiltro && latInicial != null && lonInicial != null) {
                double latMedicao = m.getCoordenadas().getLatitude();
                double lonMedicao = m.getCoordenadas().getLongitude();

                double distanciaReal = Coordenada.calcularDistancia(latInicial, lonInicial, latMedicao, lonMedicao);

                if (distanciaReal > raioKm) {
                    passaFiltro = false;
                }
            }

            // Se passou por todas as barreiras dos filtros ativos, adiciona à lista filtrada
            if (passaFiltro) {
                medicaoFiltrada.add(m);
            }
        }

        return medicaoFiltrada;
    }

    public void trocaTemaCalendario() {
        DatePickerSettings[] configuracoes = {
                dataInicio.getDatePicker().getSettings(),
                dataFim.getDatePicker().getSettings()
        };
        for (DatePickerSettings dateSettings : configuracoes)
            SistemaController.aplicarTemaCalendario(dateSettings);

        // redesenha as novas cores
        dataInicio.repaint();
        dataFim.repaint();
    }
}