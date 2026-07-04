package coords;

import med.Medicao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;
//TODO Verificar lista medicao duplicada possivelmente
public class RegressaoLinear {

    private double B0;
    private double B1;
    private double R2;
    private int n;
    private List<Medicao> dados;

    public RegressaoLinear(){
        B0 = 0;
        B1 = 0;
        R2 = 0;
        n = 0;
    }

    public void setDados(List<Medicao> dados) {
        this.dados = dados;
    }

    public void calcularCoeficientes() {

        if (this.dados == null) {
            throw new IllegalArgumentException("A lista de dados não pode ser nula. Use setDados() primeiro.");
        }

        if (this.dados.isEmpty()) {
            throw new IllegalArgumentException("A lista de dados não pode estar vazia.");
        }

        this.n = this.dados.size();

        //calcula as médias
        double somaX = 0, somaY = 0;
        for (Medicao m : this.dados) {
            somaX += m.getTemperatura(); // Assumindo X = Temperatura
            somaY += m.getConsumoKwh();  // Assumindo Y = Consumo
        }

        double mediaX = somaX / this.n;
        double mediaY = somaY / this.n;

        //calculo de B1 (inclinação da reta)
        double numerador = 0, denominador = 0;
        for (Medicao m : this.dados) {
            //calcula os desvios de cada valor da média
            double desvioX = m.getTemperatura() - mediaX;
            double desvioY = m.getConsumoKwh() - mediaY;

            // Acumula os desvios
            numerador += desvioX * desvioY;
            denominador += desvioX * desvioX;
        }

        //verificar se denominador é zero (todas as temperaturas são iguais, ou seja, não há variança)
        if (Math.abs(denominador) < 1e-10) {
            throw new ArithmeticException("Denominador zero: todos os valores de temperatura são iguais");
        }

        this.B1 = numerador / denominador;
        this.B0 = mediaY - (this.B1 * mediaX);
    }


    public double prever(double temperatura) {
        return B0 + B1 * temperatura;
    }

    public double calcularR2() {
        // Validações usando a lista interna
        if (this.dados == null) {
            throw new IllegalArgumentException("A lista de dados não pode ser nula. Use setDados() primeiro.");
        }
        if (this.dados.isEmpty()) {
            throw new IllegalArgumentException("A lista de dados não pode estar vazia.");
        }

        // cálculo da média dos valores reais (Consumo)
        double somaY = 0;
        for (Medicao m : this.dados) {
            somaY += m.getConsumoKwh();
        }
        double mediaY = somaY / this.n;

        //cálculo de SQR (soma dos quadrados dos resíduos) e SQT (soma total dos quadrados)
        double SQR = 0;
        double SQT = 0;

        for (Medicao m : this.dados) {
            double yReal = m.getConsumoKwh();

            //calcula o Y previsto usando a equação da reta (B0 + B1*X)
            double yPrevisto = prever(m.getTemperatura());

            //diferença entre o real e o previsto pelo modelo
            double residuo = yReal - yPrevisto;
            SQR += residuo * residuo;

            //diferença entre o real e a média geral
            double diferenca = yReal - mediaY;
            SQT += diferenca * diferenca;
        }

        //trata divisão por zero
        if (abs(SQT) < 1e-10) {
            return 1.0; // Se SQT é zero, todos os y são iguais, então R² = 1
        }

        double r2 = 1 - (SQR / SQT);

        //garante que R2 esteja no intervalo [0, 1]
        return max(0, min(1, r2));
    }

    // Funcao auxiliar para exibir resultados formatados
    public void exibirResultados() {

        if (this.dados == null || this.dados.isEmpty()) {
            System.out.println("A lista de dados está vazia ou nula. Não há o que exibir.");
            return;
        }

        this.calcularCoeficientes();
        this.calcularR2();

        System.out.println("=== REGRESSÃO LINEAR ===");
        System.out.println("Dados originais:");

        //montando a visualização dos dados
        StringBuilder strX = new StringBuilder("X (Temperatura): [");
        StringBuilder strY = new StringBuilder("Y (Consumo kWh): [");

        for (int i = 0; i < this.n; i++) {
            Medicao m = this.dados.get(i);
            strX.append(m.getTemperatura());
            strY.append(m.getConsumoKwh());

            //adiciona vírgula entre os itens, exceto no último
            if (i < this.n - 1) {
                strX.append(", ");
                strY.append(", ");
            }
        }
        strX.append("]");
        strY.append("]");

        System.out.println(strX.toString());
        System.out.println(strY.toString());

        //exibição dos resultados acessando os atributos da classe diretamente
        System.out.printf("\nCoeficientes encontrados:%n");
        System.out.printf("β0 (intercepto) = %.4f%n", this.B0);
        System.out.printf("β1 (inclinação) = %.4f%n", this.B1);
        System.out.printf("Equação: y = %.4f + %.4f * x%n", this.B0, this.B1);

        System.out.printf("\nCoeficiente de determinação R² = %.4f (%.2f%%)%n", this.R2, this.R2 * 100);
    }

    //Getters
    public double getB0(){
        return B0;
    }
    public double getB1(){
        return B1;
    }
    public double getR2(){
        return R2;
    }
    public double getN(){
        return n;
    }
}

