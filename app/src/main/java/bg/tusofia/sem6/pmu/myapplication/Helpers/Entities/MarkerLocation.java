package bg.tusofia.sem6.pmu.myapplication.Helpers.Entities;

public class MarkerLocation {
    private int id;
    private String name;
    private Double latitude;
    private Double longitude;

    public MarkerLocation(int id, String name, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
