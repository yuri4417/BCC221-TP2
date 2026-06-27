package coords;

import java.util.Arrays;

import static java.lang.Math.*;

public class RegressaoLinear {

    /**
     * Calcula os coeficientes beta0 (intercepto) e beta1 (inclinação) da regressão linear
     * usando o método dos mínimos quadrados
     *
     * @param x array com os valores da variável independente
     * @param y array com os valores da variável dependente
     * @return array com [beta0, beta1]
     * @throws IllegalArgumentException se os arrays forem nulos ou vazios ou de tamanhos diferentes
     */
    public static double[] calcularCoeficientes(double[] x, double[] y) {
        // Validações
        if (x == null || y == null)
            throw new IllegalArgumentException("Os arrays não podem ser nulos");
        if (x.length == 0 || y.length == 0)
            throw new IllegalArgumentException("Os arrays não podem estar vazios");
        if (x.length != y.length)
            throw new IllegalArgumentException("Os arrays devem ter o mesmo tamanho");
        int n = x.length;

        // Cálculo das médias
        // Soma todos os elementos de x e y, e divide pelo total de elementos
        double somaX = 0, somaY = 0;
        for (int i = 0; i < n; i++) {
            somaX += x[i];
            somaY += y[i];
        }
        double mediaX = somaX / n;
        double mediaY = somaY / n;

        // Cálculo de beta1 (inclinação da reta)
        double numerador = 0, denominador = 0;
        for (int i = 0; i < n; i++) {
            //calcula os desvios de cada valor da média
            double diffX = x[i] - mediaX;
            double diffY = y[i] - mediaY;
            //acumula os desvios
            numerador += diffX * diffY;
            denominador += diffX * diffX;
        }

        // Verificar se denominador é zero (todos os x são iguais)
        if (abs(denominador) < 1e-10) {
            throw new ArithmeticException("Denominador zero: todos os valores de x são iguais");
        }

        double beta1 = numerador / denominador;
        double beta0 = mediaY - beta1 * mediaX;

        return new double[]{beta0, beta1};
    }

    /**
     * Retorna o valor de y previsto para um dado x usando a equação y = beta0 + beta1*x
     *
     * @param x valor da variável independente
     * @param beta0 intercepto
     * @param beta1 inclinação
     * @return valor previsto de y
     */
    public static double prever(double x, double beta0, double beta1) {
        return beta0 + beta1 * x;
    }

    /**
     * Calcula o coeficiente de determinação R²
     * R² = 1 - (SQR / SQT)
     * onde SQR é a soma dos quadrados dos resíduos e SQT é a soma total dos quadrados
     *
     * @param yReais array com os valores reais de y
     * @param yPrevistos array com os valores previstos de y
     * @return coeficiente R² (entre 0 e 1)
     * @throws IllegalArgumentException se os arrays forem nulos ou vazios ou de tamanhos diferentes
     */
    public static double calcularR2(double[] yReais, double[] yPrevistos) {
        // Validações
        if (yReais == null || yPrevistos == null) {
            throw new IllegalArgumentException("Os arrays não podem ser nulos");
        }
        if (yReais.length == 0 || yPrevistos.length == 0) {
            throw new IllegalArgumentException("Os arrays não podem estar vazios");
        }
        if (yReais.length != yPrevistos.length) {
            throw new IllegalArgumentException("Os arrays devem ter o mesmo tamanho");
        }

        int n = yReais.length;

        // Cálculo da média dos valores reais
        double somaY = 0;
        for (double y : yReais) {
            somaY += y;
        }
        double mediaY = somaY / n;

        // Cálculo de SQR (Soma dos Quadrados dos Resíduos) e SQT (Soma Total dos Quadrados)
        double SQR = 0; // Soma dos quadrados dos resíduos
        double SQT = 0; // Soma total dos quadrados

        for (int i = 0; i < n; i++) {
            double residuo = yReais[i] - yPrevistos[i];
            SQR += residuo * residuo;

            double diferenca = yReais[i] - mediaY;
            SQT += diferenca * diferenca;
        }

        // Evitar divisão por zero
        if (abs(SQT) < 1e-10) {
            return 1.0; // Se SQT é zero, todos os y são iguais, então R² = 1
        }

        double r2 = 1 - (SQR / SQT);

        // Garantir que R² esteja no intervalo [0, 1] devido a erros de arredondamento
        return max(0, min(1, r2));
    }

    // Funcao auxiliar para exibir resultados formatados
    public static void exibirResultados(double[] x, double[] y) {
        System.out.println("=== REGRESSÃO LINEAR ===");
        System.out.println("Dados originais:");
        System.out.println("X: " + Arrays.toString(x));
        System.out.println("Y: " + Arrays.toString(y));

        double[] coeficientes = calcularCoeficientes(x, y);
        double beta0 = coeficientes[0];
        double beta1 = coeficientes[1];

        System.out.printf("\nCoeficientes encontrados:%n");
        System.out.printf("β0 (intercepto) = %.4f%n", beta0);
        System.out.printf("β1 (inclinação) = %.4f%n", beta1);
        System.out.printf("Equação: y = %.4f + %.4f * x%n", beta0, beta1);

        // Calcular valores previstos
        double[] yPrevistos = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            yPrevistos[i] = prever(x[i], beta0, beta1);
        }

        double r2 = calcularR2(y, yPrevistos);
        System.out.printf("\nCoeficiente de determinação R² = %.4f (%.2f%%)%n", r2, r2 * 100);
    }
}

