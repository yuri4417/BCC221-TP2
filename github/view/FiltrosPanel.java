package ufop.poo.tp2.view;

import ufop.poo.tp2.model.Filtro;
import ufop.poo.tp2.controller.SistemaController;
import java.awt.FlowLayout;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FiltrosPanel extends JPanel {
    private JSpinner spinnerDataInicio;
    private JSpinner spinnerDataFim;
    private JSlider sliderTempMin;
    private JSlider sliderTempMax;
    private JTextField txtLatitude;
    private JTextField txtLongitude;
    private JSpinner spinnerRaio;
    private JButton btnLimparFiltros;
    private Filtro filtro;
    
    public FiltrosPanel() {
        this.filtro = new Filtro();
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Grupo Tempo
        JPanel panelTempo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTempo.setBorder(BorderFactory.createTitledBorder("Intervalo de Tempo"));
        
        SpinnerDateModel modelInicio = new SpinnerDateModel();
        spinnerDataInicio = new JSpinner(modelInicio);
        spinnerDataInicio.setEditor(new JSpinner.DateEditor(spinnerDataInicio, "yyyy-MM-dd HH:mm:ss"));
        
        SpinnerDateModel modelFim = new SpinnerDateModel();
        spinnerDataFim = new JSpinner(modelFim);
        spinnerDataFim.setEditor(new JSpinner.DateEditor(spinnerDataFim, "yyyy-MM-dd HH:mm:ss"));
        
        panelTempo.add(new JLabel("Início:"));
        panelTempo.add(spinnerDataInicio);
        panelTempo.add(new JLabel("Fim:"));
        panelTempo.add(spinnerDataFim);
        
        // Grupo Temperatura
        JPanel panelTemp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTemp.setBorder(BorderFactory.createTitledBorder("Intervalo de Temperatura"));
        
        sliderTempMin = new JSlider(-50, 60, -50);
        sliderTempMax = new JSlider(-50, 60, 60);
        sliderTempMin.setMajorTickSpacing(10);
        sliderTempMin.setPaintTicks(true);
        sliderTempMax.setMajorTickSpacing(10);
        sliderTempMax.setPaintTicks(true);
        
        panelTemp.add(new JLabel("Mín:"));
        panelTemp.add(sliderTempMin);
        panelTemp.add(new JLabel("Máx:"));
        panelTemp.add(sliderTempMax);
        
        // Grupo Raio
        JPanel panelRaio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelRaio.setBorder(BorderFactory.createTitledBorder("Raio a partir de coordenada"));
        
        txtLatitude = new JTextField(10);
        txtLongitude = new JTextField(10);
        spinnerRaio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000.0, 10.0));
        
        panelRaio.add(new JLabel("Latitude:"));
        panelRaio.add(txtLatitude);
        panelRaio.add(new JLabel("Longitude:"));
        panelRaio.add(txtLongitude);
        panelRaio.add(new JLabel("Raio (km):"));
        panelRaio.add(spinnerRaio);
        
        // Botão limpar
        btnLimparFiltros = new JButton("Limpar todos os filtros");
        
        add(panelTempo);
        add(panelTemp);
        add(panelRaio);
        add(btnLimparFiltros);
    }
    
    public Filtro getFiltro() {
        // Converter valores dos componentes para o objeto Filtro
        return filtro;
    }
    
    public void limparCampos() {
        // Limpar todos os campos
        filtro.limpar();
    }
    
    public double getTempMin() {
        return sliderTempMin.getValue();
    }
    
    public double getTempMax() {
        return sliderTempMax.getValue();
    }
    
    public JButton getBtnLimparFiltros() { return btnLimparFiltros; }
    public JSpinner getSpinnerDataInicio() { return spinnerDataInicio; }
    public JSpinner getSpinnerDataFim() { return spinnerDataFim; }
    public JSlider getSliderTempMin() { return sliderTempMin; }
    public JSlider getSliderTempMax() { return sliderTempMax; }
    public JTextField getTxtLatitude() { return txtLatitude; }
    public JTextField getTxtLongitude() { return txtLongitude; }
    public JSpinner getSpinnerRaio() { return spinnerRaio; }
}