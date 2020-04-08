package pl.coderslab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.dto.ArticleDto;
import pl.coderslab.model.Article;
import pl.coderslab.model.Asset;
import pl.coderslab.repository.AssetRepository;
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
    private final AssetRepository assetRepository;
    private final AssetService assetService;

    @Autowired
    public ArticleController(ArticleService articleService, AssetRepository assetRepository, AssetService assetService) {
        this.articleService = articleService;
        this.assetRepository = assetRepository;
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
    public ArticleDto findById(@PathVariable ("id") long id) {
        Article article = checkFound(articleService.findById(id));
        return articleService.convertToDto(article);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestParam ("title") String title,
                       @RequestParam ("body") String body,
                       @RequestParam(name = "file", required = false)  MultipartFile[] files) {
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
    public void update(@PathVariable ("id") long id,
                       @RequestParam ("title") String title,
                       @RequestParam ("body") String body,
                       @RequestParam(name = "file", required = false)  MultipartFile[] files) {
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
    public void delete(@PathVariable ("id") long id) {
        checkFound(articleService.findById(id));
        articleService.deleteById(id);
    }
}
