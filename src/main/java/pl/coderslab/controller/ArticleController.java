package pl.coderslab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.model.Article;
import pl.coderslab.service.RestPredictions;
import pl.coderslab.service.article.ArticleService;

import java.util.List;

import static pl.coderslab.service.RestPredictions.checkFound;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public List<Article> findAll() {
        return checkFound(articleService.findAll());
    }

    @GetMapping(value = "/{id}")
    public Article findById(@PathVariable ("id") Long id) {
        return checkFound(articleService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody Article resource) {
        RestPredictions.checkNotNull(resource);
        return articleService.save(resource);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable ("id") Long id, @RequestBody Article resource) {
        RestPredictions.checkNotNull(resource);
        RestPredictions.checkFound(articleService.findById(id));
        articleService.update(resource);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable ("id") Long id) {
        RestPredictions.checkFound(articleService.findById(id));
        articleService.deleteById(id);
    }

}
