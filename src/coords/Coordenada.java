package coords;

import static java.lang.Math.*;

public class Coordenada {

    private static final double RAIO_TERRA_KM = 6371.0;

    private double latitude;
    private double longitude;
    
    public Coordenada(){latitude = 0; longitude = 0;}
    public Coordenada(double lat){
        latitude = lat;
        longitude = 0.0;
    }
    public Coordenada(double lat,double lng){
        latitude  = lat;
        longitude = lng;
    }

    public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int RAIO_TERRA = 6371; // Raio da Terra em quilômetros
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA * c;
    }

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public final void setLatitude(double lat){
        latitude = lat;
    }
    public final void setLongitude(double lng) {
        longitude = lng;
    }
}