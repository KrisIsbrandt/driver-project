package pl.coderslab.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

@ApiModel(description = "All details about the Asset")
public class AssetDto {

    @ApiModelProperty(notes = "The database generated asset ID")
    private long id;

    @ApiModelProperty(notes = "The asset name")
    private String name;

    @ApiModelProperty(notes = "The asset file extension")
    private String type;

    @ApiModelProperty(notes = "The asset location to href")
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

    @Override
    public String toString() {
        return "AssetDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
