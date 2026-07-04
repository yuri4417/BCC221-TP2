package med;

public class ErroValidacao {
    private int errosCoordenada;
    private int errosTemperatura;
    private int errosConsumo;
    private int errosFormato;
    private int linhasProcessadas;
    private int linhasValidas;
    private int errosTotais;
    public ErroValidacao() {
        //contrutor que usa função de resetar dados
        resetar();
    }

    //Funções de incrementar dados
    public void incrementarErroCoordenada() {
        errosCoordenada++;
    }
    public void incrementarErroTemperatura() {
        errosTemperatura++;
    }
    public void incrementarErroConsumo() {
        errosConsumo++;
    }
    public void incrementarErroFormato() {
        errosFormato++;
    }
    public void incrementarLinhasProcessadas() {
        linhasProcessadas++;
    }
    public void incrementarLinhasValidas() {
        linhasValidas++;
    }
    public void incrementarErrosTotais() {errosTotais++;}
    public String getMensagemResumo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMO DE VALIDAÇÃO ===\n");
        sb.append("Linhas processadas: ").append(linhasProcessadas).append("\n");
        sb.append("Linhas válidas: ").append(linhasValidas).append("\n");
        sb.append("Linhas rejeitadas: ").append(linhasProcessadas - linhasValidas).append("\n\n");
        sb.append("Erros por tipo:\n");
        sb.append("- Erro de coordenada: ").append(errosCoordenada).append("\n");
        sb.append("- Erro de temperatura: ").append(errosTemperatura).append("\n");
        sb.append("- Erro de consumo: ").append(errosConsumo).append("\n");
        sb.append("- Erro de formato: ").append(errosFormato).append("\n");
        return sb.toString();
    }

    public void resetar() {
        errosCoordenada = 0;
        errosTemperatura = 0;
        errosConsumo = 0;
        errosFormato = 0;
        linhasProcessadas = 0;
        linhasValidas = 0;
        errosTotais = 0;
    }

    // Getters
    public int getErrosCoordenada() {
        return errosCoordenada;
    }
    public int getErrosTemperatura() {
        return errosTemperatura;
    }
    public int getErrosConsumo() {
        return errosConsumo;
    }
    public int getErrosFormato() {
        return errosFormato;
    }
    public int getLinhasProcessadas() {
        return linhasProcessadas;
    }
    public int getLinhasValidas() {
        return linhasValidas;
    }
    public int getErrosTotais() {return errosTotais;}
}
