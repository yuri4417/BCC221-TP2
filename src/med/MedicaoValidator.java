package med;

public class MedicaoValidator {
    private final ErroValidacao erros;

    //LIMITES PARA VALIDAÇÃO DOS DADOS
    private static final double LAT_MIN = -90.0;
    private static final double LAT_MAX = 90.0;
    private static final double LON_MIN = -180.0;
    private static final double LON_MAX = 180.0;
    private static final double TEMP_MIN = -50.0;
    private static final double TEMP_MAX = 60.0;

    public MedicaoValidator() {
        this.erros = new ErroValidacao();
    }

    public boolean validarCoordenada(double lat, double lon) {
        if (lat < LAT_MIN || lat > LAT_MAX || lon < LON_MIN || lon > LON_MAX) {
            erros.incrementarErroCoordenada();
            return false;
        }
        return true;

    }

    public boolean validarTemperatura(double temp) {
        if(temp < TEMP_MIN || temp > TEMP_MAX) {
            erros.incrementarErroTemperatura();
            return false;
        }
        return true;
    }

    public boolean validarConsumo(double consumo) {
        if (consumo < 0){
            erros.incrementarErroConsumo();
            return false;
        }
        return true;
    }

    public ErroValidacao getErros() {
        return erros;
    }


}
