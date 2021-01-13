package pl.isbrandt.service.article;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import pl.isbrandt.dto.ArticleDto;
import pl.isbrandt.model.Article;
import pl.isbrandt.model.Asset;
import pl.isbrandt.repository.ArticleRepository;
import pl.isbrandt.repository.AssetRepository;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final AssetRepository assetRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, AssetRepository assetRepository, ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.assetRepository = assetRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Article save(Article article) {
        articleRepository.save(article);
        return article;
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
    public void removeAsset(Article article, Asset asset) {
        article.removeAsset(asset);
        articleRepository.save(article);
    }

    @Override
    public void removeMultipleMultipleAssets(Article article, List<Asset> assets) {
        for (Asset asset : assets) {
            article.removeAsset(asset);
        }
        articleRepository.save(article);
    }

    @Override
    public void removeAllAssets(Article article) {
        article.removeAllAssets();
        articleRepository.save(article);
    }

    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Override
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Article findById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public ArticleDto convertToDto(Article article) {
        return modelMapper.map(article, ArticleDto.class);
    }

    @Override
    public Article convertToEntity(ArticleDto articleDto) {
        return modelMapper.map(articleDto, Article.class);
    }
}
