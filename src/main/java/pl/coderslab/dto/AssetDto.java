package pl.coderslab.dto;

public class AssetDto {
    private long id;
    private String assetName;
    private String assetType;
    private String assetLocation;

    public AssetDto() {
    }

    public AssetDto(String assetName, String assetType, String assetLocation) {
        this.assetName = assetName;
        this.assetType = assetType;
        this.assetLocation = assetLocation;
    }

    public AssetDto(long id, String assetName, String assetType, String assetLocation) {
        this.id = id;
        this.assetName = assetName;
        this.assetType = assetType;
        this.assetLocation = assetLocation;
    }

    //Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetLocation() {
        return assetLocation;
    }

    public void setAssetLocation(String assetLocation) {
        this.assetLocation = assetLocation;
    }
}
