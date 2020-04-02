package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
