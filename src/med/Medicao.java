package med;

import java.time.LocalDateTime;
import coords.Coordenada;

public class Medicao {
    private LocalDateTime timestamp;
    private String        cidade;
    private Coordenada    coordenadas;
    private double        temperatura, consumoKwh;
    private double        consumoPrevisto;
    private double        residuoPercentual;

    // Construtores
    public Medicao(){
        this.timestamp = LocalDateTime.now(); // Inicializa com o tempo atual
        this.coordenadas = new Coordenada();
    }
    public Medicao(String cidade, double latitude, double longitude, double temperatura, double consumoKwh) {
        this.cidade = cidade;
        this.coordenadas = new Coordenada(latitude, longitude);
        this.temperatura = temperatura;
        this.consumoKwh = consumoKwh;
    }

    // Getters
    public LocalDateTime getTimeStamp(){
        return timestamp;
    }
    public String getCidade(){
        return cidade;
    }
    public Coordenada getCoordenadas(){
        return coordenadas;
    }
    public double getTemperatura(){
        return temperatura;
    }
    public double getConsumoKwh() {
        return consumoKwh;
    }
    public double getConsumoPrevisto() {
        return consumoPrevisto;
    }
    public double getResiduoPercentual(){
        return residuoPercentual;
    }

    // Setters
    public final void setCidade(String x) {cidade = x;}
    public final void setTimeStamp(LocalDateTime x){
        timestamp = x;
    }
    public final void setCoordenadas(Coordenada x){
        if(x == null)
            this.coordenadas = null;
        else
            // Aloca um objeto novo para evitar alteração do conteúdo por outro ponteiro
            this.coordenadas = new Coordenada(x.getLatitude(), x.getLongitude());
    }
    public final void setTemperatura(double x){
        temperatura = x;
    }
    public final void setConsumoKwh(double x) { consumoKwh = x;}
    @Override
    public String toString(){
        return String.format("%s\t%s\t%.4f\t%.4f\t%.1f\t%.2f", timestamp, cidade, coordenadas.getLatitude(), coordenadas.getLongitude(), temperatura, consumoKwh);
    }

}