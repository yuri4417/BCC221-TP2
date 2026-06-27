package ufop.poo.tp2.model;

import java.util.List;

public class RegressaoLinear {
    private double beta0;
    private double beta1;
    private double r2;
    private int n;
    private List<Medicao> dados;
    
    public RegressaoLinear() {
        this.beta0 = 0.0;
        this.beta1 = 0.0;
        this.r2 = 0.0;
        this.n = 0;
    }
    
    public boolean calcular(List<Medicao> dados) {
        this.dados = dados;
        this.n = dados.size();
        
        if (n < 2) {
            return false;
        }
        
        calcularBeta0Beta1();
        calcularR2();
        calcularResiduos();
        
        return true;
    }
    
    private void calcularBeta0Beta1() {
        double somaX = 0, somaY = 0;
        double somaXY = 0, somaX2 = 0;
        
        for (Medicao m : dados) {
            double x = m.getTemperatura();
            double y = m.getConsumoKwh();
            somaX += x;
            somaY += y;
            somaXY += x * y;
            somaX2 += x * x;
        }
        
        double denominador = n * somaX2 - somaX * somaX;
        
        if (denominador != 0) {
            beta1 = (n * somaXY - somaX * somaY) / denominador;
            beta0 = (somaY - beta1 * somaX) / n;
        }
    }
    
    private void calcularR2() {
        double somaY = 0;
        for (Medicao m : dados) {
            somaY += m.getConsumoKwh();
        }
        double yMedio = somaY / n;
        
        double somaResiduos = 0;
        double somaTotal = 0;
        
        for (Medicao m : dados) {
            double yReal = m.getConsumoKwh();
            double yPrevisto = preverConsumo(m.getTemperatura());
            
            somaResiduos += Math.pow(yReal - yPrevisto, 2);
            somaTotal += Math.pow(yReal - yMedio, 2);
        }
        
        if (somaTotal != 0) {
            r2 = 1 - (somaResiduos / somaTotal);
        } else {
            r2 = 0;
        }
    }
    
    public double preverConsumo(double temperatura) {
        return beta0 + beta1 * temperatura;
    }
    
    public void calcularResiduos() {
        if (dados == null) return;
        
        for (Medicao m : dados) {
            double previsto = preverConsumo(m.getTemperatura());
            m.setConsumoPrevisto(previsto);
            
            if (previsto != 0) {
                double residuo = ((m.getConsumoKwh() - previsto) / previsto) * 100;
                m.setResiduoPercentual(residuo);
            } else {
                m.setResiduoPercentual(0);
            }
        }
    }
    
    // Getters
    public double getBeta0() { return beta0; }
    public double getBeta1() { return beta1; }
    public double getR2() { return r2; }
    public int getN() { return n; }
}