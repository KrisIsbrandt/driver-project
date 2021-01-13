package pl.isbrandt.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class IndexController {

    @GetMapping({"/", ""})
    public String index() {
        return "index";
    }

    @GetMapping("/articles/{id}")
    public String showArticle(@PathVariable ("id") long id, Model model) {
        model.addAttribute("articleId", id);
        return "article";
    }

    @GetMapping({"/articles/add", "/articles/edit/{id}"})
    public String addOrEditArticle(@PathVariable (name = "id", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("articleId", id);
        }
        return "adminArticleForm";
    }

    //Swagger API documentation
    @GetMapping("/api")
    public String apiDocumentation() {
        return "redirect:/swagger-ui.html";
    }
}
