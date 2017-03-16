package uk.co.frips.sample.barapp.data.entity;

public class Bar {
    public final String id;
    public final String name;
    public final double latitude;
    public final double longitude;

    public Bar(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Bar{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
