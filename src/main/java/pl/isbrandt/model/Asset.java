package pl.isbrandt.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String type;

    private String location;

    private LocalDateTime created;

    private LocalDateTime updated;

    private int version = 1;

    public Asset() {
    }

    @PrePersist
    public void onCreation() {
        this.created = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updated = LocalDateTime.now();
        this.version += 1;
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

    public void setName(String assetName) {
        this.name = assetName;
    }

    public String getType() {
        return type;
    }

    public void setType(String assetType) {
        this.type = assetType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String assetLocation) {
        this.location = assetLocation;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", version=" + version +
                '}';
    }
}
