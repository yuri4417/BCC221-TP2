package ufop.poo.tp2.model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MedicaoDAO {
    private ErroValidacao erros;
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Limites de validação
    private static final double LAT_MIN = -90.0;
    private static final double LAT_MAX = 90.0;
    private static final double LON_MIN = -180.0;
    private static final double LON_MAX = 180.0;
    private static final double TEMP_MIN = -50.0;
    private static final double TEMP_MAX = 60.0;
    
    public MedicaoDAO() {
        this.erros = new ErroValidacao();
    }
    
    public List<Medicao> carregarDeTSV(File arquivo) throws IOException {
        List<Medicao> medicacoes = new ArrayList<>();
        erros.resetar();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean primeiraLinha = true;
            
            while ((linha = reader.readLine()) != null) {
                erros.incrementarLinhasProcessadas();
                
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue; // pula cabeçalho
                }
                
                Medicao m = parseLinha(linha);
                if (m != null) {
                    medicacoes.add(m);
                    erros.incrementarLinhasValidas();
                }
            }
        }
        
        return medicacoes;
    }
    
    private Medicao parseLinha(String linha) {
        String[] campos = linha.split("\t");
        if (campos.length < 6) {
            erros.incrementarErroFormato();
            return null;
        }
        
        try {
            LocalDateTime timestamp = LocalDateTime.parse(campos[0].trim(), FORMATTER);
            String cidade = campos[1].trim();
            double latitude = Double.parseDouble(campos[2].trim());
            double longitude = Double.parseDouble(campos[3].trim());
            double temperatura = Double.parseDouble(campos[4].trim());
            double consumoKwh = Double.parseDouble(campos[5].trim());
            
            // Validações
            if (!validarCoordenada(latitude, longitude)) {
                erros.incrementarErroCoordenada();
                return null;
            }
            
            if (!validarTemperatura(temperatura)) {
                erros.incrementarErroTemperatura();
                return null;
            }
            
            if (!validarConsumo(consumoKwh)) {
                erros.incrementarErroConsumo();
                return null;
            }
            
            return new Medicao(timestamp, cidade, latitude, longitude, temperatura, consumoKwh);
            
        } catch (Exception e) {
            erros.incrementarErroFormato();
            return null;
        }
    }
    
    public boolean validarCoordenada(double lat, double lon) {
        return lat >= LAT_MIN && lat <= LAT_MAX && lon >= LON_MIN && lon <= LON_MAX;
    }
    
    public boolean validarTemperatura(double temp) {
        return temp >= TEMP_MIN && temp <= TEMP_MAX;
    }
    
    public boolean validarConsumo(double consumo) {
        return consumo >= 0;
    }
    
    public ErroValidacao getErros() {
        return erros;
    }
}