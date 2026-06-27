package ufop.poo.tp2.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Filtro {
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Double tempMin;
    private Double tempMax;
    private Double latitudeCentro;
    private Double longitudeCentro;
    private Double raioKm;
    
    public Filtro() {
        limpar();
    }
    
    public List<Medicao> aplicar(List<Medicao> dados) {
        List<Medicao> resultado = new ArrayList<>(dados);
        
        resultado = aplicarFiltroTempo(resultado);
        resultado = aplicarFiltroTemperatura(resultado);
        resultado = aplicarFiltroRaio(resultado);
        
        return resultado;
    }
    
    public List<Medicao> aplicarFiltroTempo(List<Medicao> dados) {
        if (dataInicio == null && dataFim == null) {
            return dados;
        }
        
        return dados.stream()
                .filter(m -> {
                    LocalDateTime ts = m.getTimestamp();
                    boolean afterStart = (dataInicio == null) || !ts.isBefore(dataInicio);
                    boolean beforeEnd = (dataFim == null) || !ts.isAfter(dataFim);
                    return afterStart && beforeEnd;
                })
                .collect(Collectors.toList());
    }
    
    public List<Medicao> aplicarFiltroTemperatura(List<Medicao> dados) {
        if (tempMin == null && tempMax == null) {
            return dados;
        }
        
        return dados.stream()
                .filter(m -> {
                    double temp = m.getTemperatura();
                    boolean aboveMin = (tempMin == null) || temp >= tempMin;
                    boolean belowMax = (tempMax == null) || temp <= tempMax;
                    return aboveMin && belowMax;
                })
                .collect(Collectors.toList());
    }
    
    public List<Medicao> aplicarFiltroRaio(List<Medicao> dados) {
        if (latitudeCentro == null || longitudeCentro == null || raioKm == null) {
            return dados;
        }
        
        return dados.stream()
                .filter(m -> calcularDistancia(
                        latitudeCentro, longitudeCentro,
                        m.getLatitude(), m.getLongitude()) <= raioKm)
                .collect(Collectors.toList());
    }
    
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // Fórmula de Haversine - a ser implementada
        // Pode chamar CalculoDistancia.calcular()
        return 0; // implementar
    }
    
    public void limpar() {
        dataInicio = null;
        dataFim = null;
        tempMin = null;
        tempMax = null;
        latitudeCentro = null;
        longitudeCentro = null;
        raioKm = null;
    }
    
    public boolean estaAtivo() {
        return dataInicio != null || dataFim != null ||
               tempMin != null || tempMax != null ||
               (latitudeCentro != null && longitudeCentro != null && raioKm != null);
    }
    
    // Getters e Setters
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public Double getTempMin() { return tempMin; }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }
    public Double getTempMax() { return tempMax; }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }
    public Double getLatitudeCentro() { return latitudeCentro; }
    public void setLatitudeCentro(Double latitudeCentro) { this.latitudeCentro = latitudeCentro; }
    public Double getLongitudeCentro() { return longitudeCentro; }
    public void setLongitudeCentro(Double longitudeCentro) { this.longitudeCentro = longitudeCentro; }
    public Double getRaioKm() { return raioKm; }
    public void setRaioKm(Double raioKm) { this.raioKm = raioKm; }
}