package coords;

public class Coordenada {
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
        final int R = 6371;
        double latDistance = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double lonDistance = Math.toRadians(p2.getLongitude() - p1.getLongitude());
        double a = Math.sin(latDistance/2) * Math.sin(latDistance/2)
                + Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude()))
                * Math.sin(lonDistance/2) * Math.sin(lonDistance/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
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