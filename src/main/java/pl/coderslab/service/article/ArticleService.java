package pl.coderslab.service.article;

import pl.coderslab.model.Article;
import pl.coderslab.model.Asset;

import java.util.List;

public interface ArticleService {

    Long save(Article article);

    void update(Article article);

    void assignAsset(Article article, Asset asset);

    void assignMultipleAssets(Article article, List<Asset> assets);

    void removeAsset(Article article, Asset asset);

    void removeMultipleMultipleAssets(Article article, List<Asset> assets);

    void removeAllAssets(Article article);

    List<Article> findAll();

    Article findById(Long id);

    void deleteById(Long id);

}

