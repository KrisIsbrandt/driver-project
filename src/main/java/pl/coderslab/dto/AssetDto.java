package pl.coderslab.dto;

public class AssetDto {
    private long id;
    private String name;
    private String type;
    private String location;

    public AssetDto() {
    }

    public AssetDto(String name, String type, String location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    public AssetDto(long id, String name, String type, String location) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
    }

    //Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
