package pl.coderslab.service.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.coderslab.dto.ArticleDto;
import pl.coderslab.model.Article;
import pl.coderslab.model.Asset;
import java.util.List;

public interface ArticleService {

    Article save(Article article);

    void update(Article article);

    void assignAsset(Article article, Asset asset);

    void assignMultipleAssets(Article article, List<Asset> assets);

    void removeAsset(Article article, Asset asset);

    void removeMultipleMultipleAssets(Article article, List<Asset> assets);

    void removeAllAssets(Article article);

    List<Article> findAll();

    Page<Article> findAll(Pageable pageable);

    Article findById(Long id);

    void deleteById(Long id);

    ArticleDto convertToDto(Article article);

    Article convertToEntity(ArticleDto articleDto);
}

