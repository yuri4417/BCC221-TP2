package med;

import coords.Coordenada;

public class MedicaoValidator {
    public static ErroValidacao erros = new ErroValidacao();

    //LIMITES PARA VALIDAÇÃO DOS DADOS
    private static final double LAT_MIN = -90.0;
    private static final double LAT_MAX = 90.0;
    private static final double LON_MIN = -180.0;
    private static final double LON_MAX = 180.0;
    private static final double TEMP_MIN = -50.0;
    private static final double TEMP_MAX = 60.0;

    public MedicaoValidator() {}

    public static boolean validarCoordenada(Coordenada x) {
        if (x.getLatitude() < LAT_MIN || x.getLatitude() > LAT_MAX || x.getLongitude() < LON_MIN || x.getLongitude() > LON_MAX) {
            erros.incrementarErroCoordenada();
            return false;
        }
        return true;

    }

    public static boolean validarTemperatura(double temp) {
        if(temp < TEMP_MIN || temp > TEMP_MAX) {
            erros.incrementarErroTemperatura();
            return false;
        }
        return true;
    }

    public static boolean validarConsumo(double consumo) {
        if (consumo < 0){
            erros.incrementarErroConsumo();
            return false;
        }
        return true;
    }

    public static void resetarErros() {
        erros = new ErroValidacao();
    }

    public ErroValidacao getErros() {
        return erros;
    }


}
