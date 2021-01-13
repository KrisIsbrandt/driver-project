package pl.isbrandt.service.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.isbrandt.dto.ArticleDto;
import pl.isbrandt.model.Article;
import pl.isbrandt.model.Asset;
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

