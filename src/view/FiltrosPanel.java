package view;

import med.Medicao;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.*;
import coords.Coordenada;

public class FiltrosPanel extends JPanel {
    //Filtrar o tempo
    private JSpinner dataInicio;
    private JSpinner dataFim;
    //Filtrar temperatura
    private JTextField tempMin;
    private JTextField tempMax;
    //Filtro de Raio
    private JTextField latitude;
    private JTextField longitude;
    private JSpinner raioKm;
    private boolean limpando = false;
    private List<Medicao> listaRegistros = new ArrayList<>();
    private TabelaModel tabelaModel;

    public FiltrosPanel() {
        setLayout(new GridLayout(3, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarComponentes();
        configurarListeners();
    }

    public void limparFiltrosUI(){
        limpando = true;
        dataInicio.setValue(new Date());
        dataFim.setValue(new Date());
        tempMin.setText("");
        tempMax.setText("");
        latitude.setText("");
        longitude.setText("");
        raioKm.setValue(10);
        limpando=false;
    }

    public void setTabelaModel(TabelaModel model) {
        this.tabelaModel = model;
    }

    public void setListaOriginal(List<Medicao> lista){
        this.listaRegistros = lista;
    }

    public List<Medicao> getListaRegistros(){
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

        JPanel panelTempo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTempo.setBorder(BorderFactory.createTitledBorder("Intervalo de Tempo"));
        panelTempo.add(new JLabel("Início:"));
        panelTempo.add(dataInicio);
        panelTempo.add(new JLabel("Fim:"));
        panelTempo.add(dataFim);

        JPanel panelTempe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTempe.setBorder(BorderFactory.createTitledBorder("Intervalo de Temperatura"));
        panelTempe.add(new JLabel("Min:"));
        panelTempe.add(tempMin);
        panelTempe.add(new JLabel("Max:"));
        panelTempe.add(tempMax);

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
    }

    private void configurarListeners() {
        dataInicio.addChangeListener(e -> apliqueFiltros());
        dataFim.addChangeListener(e -> apliqueFiltros());
        raioKm.addChangeListener(e -> apliqueFiltros());
    }
//  TODO: Aplicar filtros automaticamente ou criar um botão de aplicar filtros
    private void apliqueFiltros() {
        if (limpando) //Pra evitar que o tente aplicar novos filtros enquanto limpa
            return;
        try{
            if (tempMin.getText().trim().isEmpty() || tempMax.getText().trim().isEmpty() || latitude.getText().trim().isEmpty() || longitude.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Todos os campos de texto devem ser preenchidos.");
            }
            //Converte caso o usuario digitar , inves de . (parseDouble converte de string pra numero)
            double tMin = Double.parseDouble(tempMin.getText().replace(",", "."));
            double tMax = Double.parseDouble(tempMax.getText().replace(",","."));

            double latAlvo = Double.parseDouble(latitude.getText().replace(",","."));
            double longAlvo = Double.parseDouble(longitude.getText().replace(",","."));
            double raio = ((Number)raioKm.getValue()).doubleValue(); //doubleValue converte pra numero pra double

            //getValue() de JSpinner volta um Date
            Date dateIni = (Date) dataInicio.getValue();
            Date dateFim = (Date) dataFim.getValue();

            //Converter de date para LocalDateTime
            LocalDateTime inicio = dateIni.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); //Instant faz com que registre o tempo atual da maquina, o zoneId pega o fuso horário da maquina
            LocalDateTime fim = dateFim.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            List<Medicao> filtradas = verificarFiltros(inicio,fim,tMin,tMax,latAlvo,longAlvo,raio,listaRegistros);

            if (tabelaModel != null) {
                tabelaModel.setDadosFiltrados(filtradas); // Troca a lista
                tabelaModel.fireTableDataChanged();       // Grita para a tela atualizar!
            }
        }
        catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos antes de continuar.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int RAIO_TERRA = 6371; // Raio da Terra em quilômetros
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA * c;
    }

    public List<Medicao> verificarFiltros(LocalDateTime inicio, LocalDateTime fim, double tMin, double tMax, double latAlvo, double lonAlvo, double raioKm, List<Medicao> medicoesLidas){
        List<Medicao> medicaoFiltrada = new ArrayList<>(); //Vetor de medicoes que estao no filtro
        for (Medicao m : medicoesLidas){

            boolean tempoFiltro = !m.getTimeStamp().isBefore(inicio) && !m.getTimeStamp().isAfter(fim);

            boolean tempeFiltro = m.getTemperatura() >= tMin && m.getTemperatura() <= tMax;

            double latMedicao = m.getCoordenadas().getLatitude();
            double lonMedicao = m.getCoordenadas().getLongitude();

            double distanciaReal = calcularDistancia(latAlvo, lonAlvo, latMedicao, lonMedicao);
            boolean distanciaFiltro = distanciaReal <= raioKm;
            if (tempoFiltro && tempeFiltro && distanciaFiltro) {
                medicaoFiltrada.add(m);
            }
        }
        return medicaoFiltrada;
    }
}