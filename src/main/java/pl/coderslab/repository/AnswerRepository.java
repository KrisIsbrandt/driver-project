package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
