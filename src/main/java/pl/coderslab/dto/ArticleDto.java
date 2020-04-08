package pl.coderslab.dto;

import java.util.HashSet;
import java.util.Set;

public class ArticleDto{
    private long id;
    private String title;
    private String body;
    private Set<AssetDto> assets = new HashSet<>();

    public ArticleDto() {
    }

    public ArticleDto(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public ArticleDto(long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Set<AssetDto> getAssets() {
        return assets;
    }

    public void setAssets(Set<AssetDto> assets) {
        this.assets = assets;
    }
}
