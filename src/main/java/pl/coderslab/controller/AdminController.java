package pl.coderslab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.model.Article;
import pl.coderslab.model.User;
import pl.coderslab.repository.ArticleRepository;
import pl.coderslab.service.user.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private ArticleRepository articleRepository;
    private ArticleController articleController;

    @Autowired
    public AdminController(UserService userService, ArticleRepository articleRepository, ArticleController articleController) {
        this.userService = userService;
        this.articleRepository = articleRepository;
        this.articleController = articleController;
    }

    @GetMapping("/create-user")
    @ResponseBody
    public String createUser() {
        if (userService.findByUserName("admin") == null) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            userService.save(user);
        }
        return "<h2>admin account<h2></br>" +
                "login: admin </br>" +
                "password: admin";
    }

    @GetMapping({"", "/", "/articles"})
    public String getAdminPanelArticles(Model model) {
        model.addAttribute("articles", articleRepository.findAll());
        return "admin";
    }

    //placeholder for asset management admin page
    @GetMapping("/assets")
    public String getAdminPanelAssets() {
        return "redirect:/admin";
    }

    @GetMapping("/articles/form")
    public String adminArticleFormGet(@RequestParam(required = false, name = "id") Long id, Model model) {
        if (id != null) {
            Article article = articleRepository.getOne(id);
            model.addAttribute("id", id);
            model.addAttribute("title", article.getTitle());
            model.addAttribute("body", article.getBody());
            return "adminArticleForm";
        }
        return "adminArticleForm";
    }

    @PostMapping("/articles/form")
    public String adminArticleFormPost(@RequestParam(required = false, name = "id") Long id,
                                       @RequestParam String title,
                                       @RequestParam String body,
                                       @RequestParam(required = false, name = "file") MultipartFile[] files) {
        if (id != null) {
            articleController.update(id, title, body, files);
        } else {
            articleController.create(title, body, files);
        }
        return "redirect:/admin";
    }

    @GetMapping("/articles/delete")
    public String adminArticleDeleteGet(@RequestParam Long id) {
        articleController.delete(id);
        return "redirect:/admin";
    }
}
