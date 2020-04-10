package pl.coderslab.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@ApiModel(description = "All details about the Article.")
public class ArticleDto{

    @ApiModelProperty(notes = "The database generated article ID")
    private long id;

    @ApiModelProperty(notes = "The article title")
    @NotEmpty
    private String title;

    @ApiModelProperty(notes = "The article body")
    @NotEmpty
    private String body;

    @ApiModelProperty(notes = "The article assigned assets")
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
