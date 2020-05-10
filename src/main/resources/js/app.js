$(function () {

    // const ROOT = document.location.hostname + "/api/v1/articles";
    const ROOT = "http://localhost:8080/api/v1/articles";

    function getArticles() {
        $.get(ROOT)
         .done(function(articles) {
            render(articles);
         });
    }

    function render(articles) {
        var container = $("#articles");
        container.empty();

        for (var i = 0; i < articles.length; i++) {
            createNewEntry(articles[i], container);
        }
    }


    function createNewEntry(article, appendHere) {
        var articleTitle = article.title;

        var articleBody = article.body;
        articleBody = articleBody.substr(0,250);
        if (article.body.length > 250) {
            articleBody += "...";
        }

        var imageLocation = getNthElement(article.assets, 0);
        imageLocation = imageLocation.location; //   + '\\' + imageLocation.name;


        var entry = [
            "<div class='w3-card-4 w3-margin w3-white'>",
                "<div class='w3-container'>",
                    "<h3><b>", articleTitle,"</b></h3>",
                "</div>",
                "<div class='w3-container'>",
                    "<div style='width: 30%; float: left; padding-bottom: 10px'>",
                        "<img src=", imageLocation, " style='width:100%'></div>",
                "<div style='width: 70%; float: right; padding-left: 10px'>",
                    "<p>", articleBody, "</p>",
                        "<div>",
                            "<p><a class='w3-button w3-padding-large w3-white w3-border' href='LINK_TO_ARTICLE'><b>READ MORE »</b></a></p>",
                        "</div>",
                "<div>",
                "</div>",
            "</div>"
            ]

        $(entry.join('')).appendTo(appendHere);
    }

    function getNthElement(data, n) {
        var element;

        $.each(data, function(index, value) {
            if(index === n) {
                element = value;
            }
        });
        return element;
    }
    getArticles();
});