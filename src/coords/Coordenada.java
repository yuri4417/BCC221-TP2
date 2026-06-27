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

    public static double distancia(Coordenada p1, Coordenada p2) {
        //latDistance é a diferença entre as latitudes
        double latDistance = toRadians(p2.getLatitude() - p1.getLatitude());
        //lonDistance é a diferença entre as longitudes
        double lonDistance = toRadians(p2.getLongitude() - p1.getLongitude());
        //
        double a = pow(sin(latDistance/2), 2) //sin^2 (latDistance / 2)
                + cos(toRadians(p1.getLatitude())) * cos(toRadians(p2.getLatitude())) * pow(sin(lonDistance/2), 2);
              //+ cos (latitude p1)                * cos (latitude p2)                * sin^2 (lonDistance / 2)

             //c = 2 * atan2(sqrt(a), sqrt(1 - a))
        double c = 2 * atan2(sqrt(a), sqrt(1-a));

        return RAIO_TERRA_KM * c;
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