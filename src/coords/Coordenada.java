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