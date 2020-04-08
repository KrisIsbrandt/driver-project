package pl.coderslab.controller;

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
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final AssetService assetService;

    @Autowired
    public ArticleController(ArticleService articleService, AssetService assetService) {
        this.articleService = articleService;
        this.assetService = assetService;
    }

    @GetMapping
    public List<ArticleDto> findAll() {
        List<Article> articles = checkFound(articleService.findAll());
        return articles.stream()
                .map(articleService::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public ArticleDto findById(@PathVariable("id") long id) {
        Article article = checkFound(articleService.findById(id));
        return articleService.convertToDto(article);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestParam("title") String title,
                       @RequestParam("body") String body,
                       @RequestParam(name = "file", required = false) MultipartFile[] files) {
        Set<Asset> assets = Arrays.stream(files)
                .map(assetService::store)
                .collect(Collectors.toSet());

        Article article = new Article();
        article.setTitle(title);
        article.setBody(body);
        article.addMultipleAssets(assets);
        checkNotNull(article);
        return articleService.save(article);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("id") long id,
                       @RequestParam("title") String title,
                       @RequestParam("body") String body,
                       @RequestParam(name = "file", required = false) MultipartFile[] files) {
        Article article = checkFound(articleService.findById(id));
        Set<Asset> assets = Arrays.stream(files)
                .map(assetService::store)
                .collect(Collectors.toSet());

        article.setTitle(title);
        article.setBody(body);
        article.removeAllAssets();
        article.addMultipleAssets(assets);
        checkNotNull(article);
        articleService.update(article);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") long id) {
        checkFound(articleService.findById(id));
        articleService.deleteById(id);
    }

    @GetMapping(value = "/{id}/assets")
    public Set<AssetDto> getAssetsByArticleId(@PathVariable("id") long id) {
        Article article = checkFound(articleService.findById(id));
        return article.getAssets().stream()
                .map(assetService::convertToDto)
                .collect(Collectors.toSet());
    }

    @GetMapping(value = "/{id}/assets/{assetId}")
    public AssetDto getAssetByIdAndArticleId(@PathVariable("id") long id,
                                             @PathVariable("assetId") long assetId) {
        Article article = checkFound(articleService.findById(id));
        return article.getAssets().stream()
                .filter(asset -> asset.getId() == assetId)
                .map(assetService::convertToDto)
                .findFirst().orElse(null);
    }

    @PostMapping(value = "/{id}/assets/add")
    @ResponseStatus(HttpStatus.OK)
    public void addNewAssetToArticle(@PathVariable("id") long id,
                                     @RequestParam("file") MultipartFile[] files) {
        Article article = checkFound(articleService.findById(id));
        Set<Asset> assets = Arrays.stream(files)
                .map(assetService::store)
                .collect(Collectors.toSet());
        article.addMultipleAssets(assets);
        checkNotNull(article);
        articleService.update(article);
    }

    @PostMapping(value = "/{id}/assets/assign")
    @ResponseStatus(HttpStatus.OK)
    public void assignExistingAssetToArticle(@PathVariable("id") long id,
                                             @RequestParam("assetId") long assetId) {
        Article article = checkFound(articleService.findById(id));
        Asset asset = checkFound(assetService.findById(assetId));
        article.addAsset(asset);
        articleService.save(article);
    }

    @PostMapping(value = "/{id}/assets/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeExistingAssetFromArticle(@PathVariable("id") long id,
                                               @RequestParam("assetId") long assetId) {
        Article article = checkFound(articleService.findById(id));
        Asset asset = checkFound(assetService.findById(assetId));
        article.removeAsset(asset);
        articleService.save(article);
    }
}
