const ROOT = location.protocol + '//'+ location.hostname + '/';
var linkMap = {};
var httpHeaderLink = "";
var currentPage = 0;
var totalPages = 0;
var articleId = $('meta[name="articleId"]').attr('value');
var articleLink = ROOT + "api/v1/articles/" + articleId;

function connect(link, webpage) {
    link = link || (ROOT + "api/v1/articles");
    webpage = webpage || "home";
    $.get(link)
        .done(function (output, status, xhr) {
            if (webpage === "home") {
                httpHeaderLink = xhr.getResponseHeader('link');
                currentPage = xhr.getResponseHeader('current-page');
                totalPages = xhr.getResponseHeader('total-pages');
                render(output);
                createPagination(httpHeaderLink, currentPage, totalPages);
            } else if (webpage === "article") {
                renderArticle(output);
            }
        })
        .fail(function () {
            alert("Server is not responding");
        })
}

function render(output) {
    var container = $("#articles");
    container.empty();

    for (var i = 0; i < output.length; i++) {
        createNewEntry(output[i], container);
    }
}

function renderArticle(output) {
    var articleTitle = $('#articleTitle');
    var articleBody = $('#articleBody');
    var text = replacePlaceholdersWithAssets(output.body, output.assets);

    articleTitle.html('<b>' + output.title + '</b>');
    articleBody.html(text);
}

function createNewEntry(article, appendHere) {
    var linkToArticle = ROOT + "articles/" + article.id;
    var articleTitle = article.title;

    var articleBody = article.body;
    articleBody = articleBody.substr(0,250);
    if (article.body.length > 250) {
        articleBody += "...";
    }

    var entry = [
        "<div class=\"w3-card-4 w3-margin w3-white\">",
        "<div class=\"w3-container\">",
        "<h3><b>", articleTitle,"</b></h3>",
        "</div>",
        "<div class=\"w3-container\">",
        "<div style=\"width: 30%; float: left; padding-bottom: 10px\">"
    ];

    //Check if an article has any assets, if not then do not add any image
    var imageLocation = article.assets[0];
    if (typeof imageLocation !== 'undefined' || imageLocation !== null) {
        var image = "<img src=\"" + imageLocation.location + "\" style=\"width:80%; height: 80%\">";
        entry.push(image);
    }

    var part2 = [
        "</div>",
        "<div style=\"width: 70%; float: right; padding-left: 10px\">",
        "<p>", articleBody, "</p>",
        "<div>",
        "<p><a class=\"w3-button w3-padding-large w3-white w3-border\" href=\"", linkToArticle,"\"><b>READ MORE »</b></a></p>",
        "</div>",
        "<div>",
        "</div>",
        "</div>"
    ];

    entry.push(part2.join(''));
    $(entry.join('')).appendTo(appendHere);
}

function createPagination(link, currentPage, totalPages) {
    if (link === null || currentPage === null || totalPages === null) {
        link = "";
        currentPage = 0;
        totalPages = 1;
    }

    //Transform link header into map
    linkMap = {};
    var linkArray = link.match(/[^<>]+(?=\>)|[^="]+(?=\")/g);
    $.each(linkArray, function (i) {
        if (i % 2 === 1) {
            linkMap[linkArray[i]] = linkArray[i-1];
        }
    });

    //Make anchors to navigate
    var navigation = $('<a>' + (parseInt(currentPage) + 1)  + ' of ' + (parseInt(totalPages)) + '</a>');
    navigation.attr('onclick', "return false").attr('class', 'inactiveLink');

    var firstPage = $('<a href=\"#\" onclick=\"connect(linkMap[\'first\']);\">&laquo;</a>');
    if (typeof linkMap["first"] === "undefined") {
        firstPage.attr('onclick', "return false").attr('class', 'inactiveLink');
    }

    var prevPage  = $('<a href=\"#\" onclick=\"connect(linkMap[\'prev\']);\">previous</a>');
    if (typeof linkMap["prev"] === "undefined") {
        prevPage.attr('onclick', "return false").attr('class', 'inactiveLink');
    }

    var nextPage  = $('<a href=\"#\" onclick=\"connect(linkMap[\'next\']);\">next</a>');
    if (typeof linkMap["next"] === "undefined") {
        nextPage.attr('onclick', "return false").attr('class', 'inactiveLink');
    }

    var lastPage  = $('<a href=\"#\" onclick=\"connect(linkMap[\'last\']);\">&raquo;</a>');
    if (typeof linkMap["last"] === "undefined") {
        lastPage.attr('onclick', "return false").attr('class', 'inactiveLink');
    }

    //Append to pagination container
    var paginationContainer = $("<div class='pagination'></div>");
    firstPage.appendTo(paginationContainer);
    prevPage.appendTo(paginationContainer);
    navigation.appendTo(paginationContainer);
    nextPage.appendTo(paginationContainer);
    lastPage.appendTo(paginationContainer);

    var container = $("#articles");
    paginationContainer.appendTo(container);
}

function replacePlaceholdersWithAssets(text, assets) {
    for (let i = 0; i < assets.length; i++) {
        var x = '#asset' + (i + 1) + '#';
        var pattern = new RegExp(x, "g");
        var image = "<img id=\"asset" + i + "\" class='article' src=\"" + assets[i].location + "\">";
        text = text.replace(pattern, image);
    }

    return text;
}
