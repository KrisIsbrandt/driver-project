package pl.coderslab.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.service.storage.AssetService;
import pl.coderslab.service.user.UserService;

@Controller
public class IndexController {

    private final UserService userService;
    private final AssetService assetService;

    @Autowired
    public IndexController(UserService userService, AssetService assetService) {
        this.userService = userService;
        this.assetService = assetService;
    }

    @GetMapping({"/", ""})
    public String index() {
        return "index";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable ("id") long id) {
        return "article";
    }

    @GetMapping("/articles/add")
    public String addArticle() {
        return "addArticle";
    }

    //Swagger API documentation
    @GetMapping("/api")
    public String apiDocumentation() {
        return "redirect:/swagger-ui.html";
    }
}
