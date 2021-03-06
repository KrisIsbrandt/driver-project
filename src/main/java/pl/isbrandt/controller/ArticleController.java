package pl.isbrandt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import pl.isbrandt.dto.ArticleDto;
import pl.isbrandt.dto.AssetDto;
import pl.isbrandt.event.PaginatedResultsRetrievedEvent;
import pl.isbrandt.exception.ResourceNotFoundException;
import pl.isbrandt.model.Article;
import pl.isbrandt.model.Asset;
import pl.isbrandt.service.article.ArticleService;
import pl.isbrandt.service.storage.AssetService;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pl.isbrandt.service.RestPredictions.*;

@RestController
@RequestMapping("/api/v1/articles")
@Api(value = "Article Management Operations")
public class ArticleController {

    private static Logger logger = LogManager.getLogger(ArticleController.class);

    private final ArticleService articleService;
    private final AssetService assetService;
    private ApplicationEventPublisher eventPublisher;

    private Predicate<MultipartFile> emptyFile = MultipartFile::isEmpty;

    @Autowired
    public ArticleController(ArticleService articleService, AssetService assetService, ApplicationEventPublisher eventPublisher) {
        this.articleService = articleService;
        this.assetService = assetService;
        this.eventPublisher = eventPublisher;
    }

    @ApiOperation(value = "View a list of available articles", response = List.class)
    @GetMapping
    public List<ArticleDto> findPaginated(
            @ApiParam(value = "Retrieved page number") @RequestParam(name = "page", required = false, defaultValue = "${request.defaultPageValue}") int page,
            @ApiParam(value = "Number of objects per page") @RequestParam(name = "size", required = false, defaultValue = "${request.defaultSizeValue}") int size,
            final UriComponentsBuilder uriBuilder,
            final HttpServletResponse response) {
        Page<Article> resultPage = checkFound(articleService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"))));
        if (page > resultPage.getTotalPages()) {
            throw new ResourceNotFoundException("Page parameter exceeded the total number of found pages");
        }
        String apiEndpointPrefix = "/api/v1/";
        eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Article>(Article.class,
                uriBuilder, response, apiEndpointPrefix, page, resultPage.getTotalPages(), size));

        return resultPage.stream()
                .map(articleService::convertToDto)
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "View a list of available articles", response = List.class)
    @GetMapping("/other")
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
    public ArticleDto create(@ApiParam(value = "Article title", required = true) @RequestParam("title") String title,
                       @ApiParam(value = "Article body", required = true) @RequestParam("body") String body,
                       @ApiParam(value = "Optional array of files to be stored as assets assigned to article") @RequestParam(name = "file", required = false) MultipartFile[] files) {
        Article article = new Article();

        if (files != null) {
            Set<Asset> assets = Arrays.stream(files)
                    .filter(emptyFile.negate()) //Predicate.not(MultipartFile::isEmpty)
                    .map(assetService::store)
                    .collect(Collectors.toSet());
            article.addMultipleAssets(assets);
        }
        article.setTitle(convertNewlineCharacterToHTMLBreakTag(title));
        article.setBody(convertNewlineCharacterToHTMLBreakTag(body));
        checkNotNull(article);
        article = articleService.save(article);
        return articleService.convertToDto(article);
    }

    @ApiOperation(value = "Update the article's title and body")
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@ApiParam(value = "Article Id to update article object", required = true) @PathVariable("id") long id,
                       @ApiParam(value = "Article title", required = true) @RequestParam("title") String title,
                       @ApiParam(value = "Article body", required = true) @RequestParam("body") String body,
                       @ApiParam(value = "Optional array of files to add to existing assigned assets to article.") @RequestParam(name = "file", required = false) MultipartFile[] files) {
        Article article = checkFound(articleService.findById(id));

        if (files != null) {
            Set<Asset> assets = Arrays.stream(files)
                    .filter(emptyFile.negate())
                    .map(assetService::store)
                    .collect(Collectors.toSet());
            article.addMultipleAssets(assets);
        }

        article.setTitle(convertNewlineCharacterToHTMLBreakTag(title));
        article.setBody(convertNewlineCharacterToHTMLBreakTag(body));
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
                                              @ApiParam(value = "Optional asset Id to retrieve only specific asset object") @RequestParam(value = "assetId", required = false, defaultValue = "0") long assetId) {
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
                .filter(emptyFile.negate())
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
