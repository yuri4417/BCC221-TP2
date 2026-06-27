package ufop.poo.tp2.model;

import java.time.LocalDateTime;

public class Medicao {
    private LocalDateTime timestamp;
    private String cidade;
    private double latitude;
    private double longitude;
    private double temperatura;
    private double consumoKwh;
    private double consumoPrevisto;
    private double residuoPercentual;
    
    public Medicao() {
        // construtor padrão
    }
    
    public Medicao(LocalDateTime timestamp, String cidade, double latitude, 
                   double longitude, double temperatura, double consumoKwh) {
        this.timestamp = timestamp;
        this.cidade = cidade;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperatura = temperatura;
        this.consumoKwh = consumoKwh;
        this.consumoPrevisto = 0.0;
        this.residuoPercentual = 0.0;
    }
    
    // Getters e Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCidade() {
        return cidade;
    }
    
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public double getTemperatura() {
        return temperatura;
    }
    
    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }
    
    public double getConsumoKwh() {
        return consumoKwh;
    }
    
    public void setConsumoKwh(double consumoKwh) {
        this.consumoKwh = consumoKwh;
    }
    
    public double getConsumoPrevisto() {
        return consumoPrevisto;
    }
    
    public void setConsumoPrevisto(double consumoPrevisto) {
        this.consumoPrevisto = consumoPrevisto;
    }
    
    public double getResiduoPercentual() {
        return residuoPercentual;
    }
    
    public void setResiduoPercentual(double residuoPercentual) {
        this.residuoPercentual = residuoPercentual;
    }
    
    @Override
    public String toString() {
        return String.format("%s | %s | %.4f | %.4f | %.2f°C | %.2fkWh", 
                timestamp, cidade, latitude, longitude, temperatura, consumoKwh);
    }
}