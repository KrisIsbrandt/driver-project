package pl.coderslab.service.article;

import pl.coderslab.model.Article;
import pl.coderslab.model.Asset;

import java.util.List;

public interface ArticleService {

    Long save(Article article);

    void update(Article article);

    void assignAsset(Article article, Asset asset);

    void assignMultipleAssets(Article article, List<Asset> assets);

    List<Article> findAll();

    Article findById(Long id);

    void deleteById(Long id);

}

