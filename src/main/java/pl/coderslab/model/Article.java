package pl.coderslab.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToMany
    private Set<Asset> assets = new HashSet<>();

    private LocalDateTime created;

    private LocalDateTime updated;

    private int version = 1;

    public Article() {
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

    public void addAsset(Asset asset) {
        this.assets.add(asset);
    }

    public void addMultipleAssets(Collection<Asset> collection) {
        assets.addAll(collection);
    }

    public void removeAsset(Asset asset) {
        this.assets.remove(asset);
    }

    public void removeAllAssets() {
        this.assets.clear();
    }

    //Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String articleTitle) {
        this.title = articleTitle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String articleBody) {
        this.body = articleBody;
    }

    public Set<Asset> getAssets() {
        return assets;
    }

    public void setAssets(Set<Asset> assetList) {
        this.assets = assetList;
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

}
