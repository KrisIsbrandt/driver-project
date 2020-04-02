package pl.coderslab.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private String questionDescription;

    @OneToMany(mappedBy = "articleQuestion")
    private Set<Answer> answer = new HashSet<>();

    private LocalDateTime created;

    private LocalDateTime updated;

    private int version = 1;

    public Question() {
    }

    //Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public Set<Answer> getAnswer() {
        return answer;
    }

    public void setAnswer(Set<Answer> answer) {
        this.answer = answer;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @PrePersist
    public void setCreated() {
        this.created = LocalDateTime.now();
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    @PreUpdate
    public void setUpdated() {
        this.updated = LocalDateTime.now();
        this.version += 1;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
