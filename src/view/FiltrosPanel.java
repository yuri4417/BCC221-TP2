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
    private TabelaModel tabelaModel;

    public FiltrosPanel() {
        setLayout(new GridLayout(3, 1, 10, 10));
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

    public void limparFiltrosUI(){
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

    public void setListaOriginal(List<Medicao> lista){
        tabelaModel.setDados(lista);
    }

    public List<Medicao> getListaRegistros(){
        return tabelaModel.getDados();
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

            boolean tempVazia = tempMin.getText().isEmpty() && tempMax.getText().isEmpty();
            boolean locVazia = latitude.getText().isEmpty() && longitude.getText().isEmpty();

            boolean inicioVazio = ((JSpinner.DefaultEditor) dataInicio.getEditor()).getTextField().getText().isEmpty();
            boolean fimVazio = ((JSpinner.DefaultEditor) dataFim.getEditor()).getTextField().getText().isEmpty();

            if (tempVazia && locVazia && inicioVazio && fimVazio) {
                throw new IllegalArgumentException("Nenhum campo de filtro preenchido.");
            }

            //Converte caso o usuario digitar , inves de . (parseDouble converte de string pra numero)
            Double tMin = null, tMax = null, lat = null, lon = null;

            if (!tempMin.getText().trim().isEmpty())
                tMin = Double.parseDouble(tempMin.getText().replace(",","."));
            if (!tempMax.getText().trim().isEmpty())
                tMax = Double.parseDouble(tempMax.getText().replace(",", "."));

            if (!latitude.getText().trim().isEmpty())
                lat = Double.parseDouble(latitude.getText().replace(",", "."));
            if (!longitude.getText().trim().isEmpty())
                lon = Double.parseDouble(longitude.getText().replace(",", "."));

            double raio = ((Number)raioKm.getValue()).doubleValue(); //doubleValue converte pra numero pra double

            //Converter de date para LocalDateTime
            LocalDateTime inicio = null;
            LocalDateTime fim = null;
            if (!inicioVazio) {
                Date dateIni = (Date) dataInicio.getValue();
                inicio = dateIni.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            if (!fimVazio) {
                Date dateFim = (Date) dataFim.getValue();
                fim = dateFim.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }

            tabelaModel.setDadosFiltrados(filtrarMedicoes(inicio, fim, tMin, tMax, lat, lon, raio));
            tabelaModel.fireTableDataChanged();
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira números válidos nos campos.", "Erro de formato", JOptionPane.ERROR_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "ERRO de preenchimento dos filtros: " + e.getMessage(), "Erro de preenchimento", JOptionPane.WARNING_MESSAGE);
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

    public List<Medicao> filtrarMedicoes(LocalDateTime inicio, LocalDateTime fim, Double tMin, Double tMax, Double latAlvo, Double lonAlvo, Double raioKm){
        List<Medicao> medicaoFiltrada = new ArrayList<>(); //Vetor de medicoes que estao no filtro
        for (Medicao m : tabelaModel.getDados()){
            if (tMin != null && m.getTemperatura() < tMin)
                continue;

            if (tMax != null && m.getTemperatura() > tMax)
                continue;

            double distancia = -1;
            if (latAlvo != null && lonAlvo != null) {
                distancia = calcularDistancia(m.getCoordenadas().getLatitude(), m.getCoordenadas().getLongitude(), latAlvo, lonAlvo);
                if (distancia > raioKm)
                    continue;
            }

            LocalDateTime data = m.getTimeStamp();

            if (inicio != null && data.isBefore(inicio))
                continue;

            if (fim != null && data.isAfter(fim))
                continue;

            medicaoFiltrada.add(m);
        }
        return medicaoFiltrada;
    }
}