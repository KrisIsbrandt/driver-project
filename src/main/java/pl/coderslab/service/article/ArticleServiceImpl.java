package pl.coderslab.service.article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderslab.model.Article;
import pl.coderslab.model.Asset;
import pl.coderslab.repository.ArticleRepository;
import pl.coderslab.repository.AssetRepository;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final AssetRepository assetRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, AssetRepository assetRepository) {
        this.articleRepository = articleRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public Long save(Article article) {
        articleRepository.save(article);
        return article.getId();
    }

    @Override
    public void update(Article article) {
        articleRepository.save(article);
    }

    @Override
    public void assignAsset(Article article, Asset asset) {
        article.addAsset(asset);
        articleRepository.save(article);
    }

    @Override
    public void assignMultipleAssets(Article article, List<Asset> assets) {
        for (Asset asset : assets) {
            article.addAsset(asset);
        }
        articleRepository.save(article);
    }

    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Override
    public Article findById(Long id) {
        return articleRepository.findById(id).get();
    }

    @Override
    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }
}
