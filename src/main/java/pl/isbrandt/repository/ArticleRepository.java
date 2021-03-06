package pl.isbrandt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.isbrandt.model.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
