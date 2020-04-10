package pl.coderslab.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.dto.ArticleDto;
import pl.coderslab.dto.AssetDto;
import pl.coderslab.model.Article;
import pl.coderslab.model.Asset;
import pl.coderslab.service.article.ArticleService;
import pl.coderslab.service.storage.AssetService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.coderslab.service.RestPredictions.checkFound;
import static pl.coderslab.service.RestPredictions.checkNotNull;

@RestController
@RequestMapping("/api/v1/articles")
@Api(value = "Article Management Operations")
public class ArticleController {

    private final ArticleService articleService;
    private final AssetService assetService;

    @Autowired
    public ArticleController(ArticleService articleService, AssetService assetService) {
        this.articleService = articleService;
        this.assetService = assetService;
    }

    @ApiOperation(value = "View a list of available articles", response = List.class)
    @GetMapping
    public List<ArticleDto> findAll() {
        List<Article> articles = checkFound(articleService.findAll());
        return articles.stream()
                .map(articleService::convertToDto)
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Get an article by Id", response = ArticleDto.class)
    @GetMapping(value = "/{id}")
    public ArticleDto findById(@ApiParam(value = "Article Id from which article object will retrieve", required = true) @PathVariable("id") long id) {
        Article article = checkFound(articleService.findById(id));
        return articleService.convertToDto(article);
    }

    @ApiOperation(value = "Add an article")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@ApiParam(value = "Article title", required = true) @RequestParam("title") String title,
                       @ApiParam(value = "Article body", required = true) @RequestParam("body") String body,
                       @ApiParam(value = "Optional array of files to be stored as assets assigned to article") @RequestParam(name = "file", required = false) MultipartFile[] files) {
        Article article = new Article();

        if (files.length > 0) {
            Set<Asset> assets = Arrays.stream(files)
                    .map(assetService::store)
                    .collect(Collectors.toSet());
            article.addMultipleAssets(assets);
        }

        article.setTitle(title);
        article.setBody(body);
        checkNotNull(article);
        return articleService.save(article);
    }

    @ApiOperation(value = "Update an article")
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@ApiParam(value = "Article Id to update article object", required = true) @PathVariable("id") long id,
                       @ApiParam(value = "Article title", required = true) @RequestParam("title") String title,
                       @ApiParam(value = "Article body", required = true) @RequestParam("body") String body,
                       @ApiParam(value = "Optional array of files to override existing assigned assets to article. If not provided then updated article object will have no assets assigned", required = true) @RequestParam(name = "file", required = false) MultipartFile[] files) {
        Article article = checkFound(articleService.findById(id));
        article.removeAllAssets();
        if (files.length > 0) {
            Set<Asset> assets = Arrays.stream(files)
                    .map(assetService::store)
                    .collect(Collectors.toSet());
            article.addMultipleAssets(assets);
        }
        article.setTitle(title);
        article.setBody(body);
        checkNotNull(article);
        articleService.update(article);
    }

    @ApiOperation(value = "Delete an article")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@ApiParam(value = "Article Id from which article object will delete from database", required = true)  @PathVariable("id") long id) {
        checkFound(articleService.findById(id));
        articleService.deleteById(id);
    }

    @ApiOperation(value = "Get assets assigned to an article")
    @GetMapping(value = "/{id}/assets")
    public Set<AssetDto> getAssetsByArticleId(@ApiParam(value = "Article Id from which article's assets object will retrieve", required = true)  @PathVariable("id") long id,
                                              @ApiParam(value = "Optional asset Id to retrieve only specific asset object") @RequestParam(value = "assetId", required = false) long assetId) {
        Article article = checkFound(articleService.findById(id));
        if (assetId > 0) {
            return article.getAssets().stream()
                    .filter(asset -> asset.getId() == assetId)
                    .map(assetService::convertToDto)
                    .collect(Collectors.toSet());
        }
        return article.getAssets().stream()
                .map(assetService::convertToDto)
                .collect(Collectors.toSet());

    }

    @ApiOperation(value = "Add a new uploaded asset to an article")
    @PostMapping(value = "/{id}/assets/add")
    @ResponseStatus(HttpStatus.OK)
    public void addNewAssetToArticle(@ApiParam(value = "Article Id from which article's assets object will be appended with provided files", required = true) @PathVariable("id") long id,
                                     @ApiParam(value = "Array of files to be converted and assigned as assets to article", required = true) @RequestParam("file") MultipartFile[] files) {
        Article article = checkFound(articleService.findById(id));
        Set<Asset> assets = Arrays.stream(files)
                .map(assetService::store)
                .collect(Collectors.toSet());
        article.addMultipleAssets(assets);
        checkNotNull(article);
        articleService.update(article);
    }

    @ApiOperation(value = "Assign an existing asset to an article")
    @PostMapping(value = "/{id}/assets/assign")
    @ResponseStatus(HttpStatus.OK)
    public void assignExistingAssetToArticle(@ApiParam(value = "Article Id from which article's assets set will be appended with an existing asset", required = true)  @PathVariable("id") long id,
                                             @ApiParam(value = "Asset Id from which assets object will be appended to the article") @RequestParam("assetId") long assetId) {
        Article article = checkFound(articleService.findById(id));
        Asset asset = checkFound(assetService.findById(assetId));
        article.addAsset(asset);
        articleService.save(article);
    }

    @ApiOperation(value = "Remove an asset assigned to an article")
    @PostMapping(value = "/{id}/assets/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeExistingAssetFromArticle(@ApiParam(value = "Article Id from which assets object will be removed", required = true) @PathVariable("id") long id,
                                               @ApiParam(value = "Asset Id that should be removed from article object", required = true) @RequestParam("assetId") long assetId) {
        Article article = checkFound(articleService.findById(id));
        Asset asset = checkFound(assetService.findById(assetId));
        article.removeAsset(asset);
        articleService.save(article);
    }
}
