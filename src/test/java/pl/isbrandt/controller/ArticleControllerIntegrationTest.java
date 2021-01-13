package pl.isbrandt.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import pl.isbrandt.dto.ArticleDto;
import pl.isbrandt.dto.AssetDto;
import pl.isbrandt.exception.ResourceNotFoundException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArticleControllerIntegrationTest {

    @LocalServerPort
    private int port;
    private static String API_ARTICLE_ROOT;
    private static String API_ASSET_ROOT;
    private static int testArticleId;
    private static int testAssetId;

    @PostConstruct
    void init() {
        API_ARTICLE_ROOT = "http://localhost:" + port + "/api/v1/articles";
        API_ASSET_ROOT = "http://localhost:" + port + "/api/v1/assets";
    }

    @Test
    @Order(1)
    public void whenGetAllArticles_thenOK() {
        when()
                .get(API_ARTICLE_ROOT).
                then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    public void givenArticleWithoutFile_whenMakingPostRequestToArticleEndpoint_thenNewArticleIsCreated() {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("title", "test title");
        requestParameters.put("body", "test body");

        int articleId = given()
                            .log().all().
                        with()
                            .params(requestParameters).
                        when()
                            .post(API_ARTICLE_ROOT).
                        then()
                            .log().body()
                            .assertThat()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract()
                            .path("id");
        testArticleId = articleId;
    }

    @Test
    @Order(3)
    public void givenArticleId_whenMakingGetRequestToArticleEndpoint_thenReturnArticle() {
        int articleId = testArticleId;

        given()
                .log().all()
                .pathParam("id", articleId).
        when()
                .get(API_ARTICLE_ROOT + "/{id}").
        then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(articleId))
                .body("title", equalTo("test title"))
                .body("body", equalTo("test body"));
    }

    @Test
    @Order(4)
    public void givenFakeArticleId_whenMakingGetRequestToArticleEndpoint_thenReturnNotFoundException() throws ResourceNotFoundException {
        int articleId = 0;

        given()
                .log().all()
                .pathParam("id", articleId).
        when()
                .get(API_ARTICLE_ROOT + "/{id}").
        then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Resource not found"));
    }

    @Test
    @Order(5)
    public void givenArticleWithoutFile_whenMakingPutRequestToArticleEndpoint_thenArticleIsUpdated() {
        int articleId = testArticleId;

        //GET article to modify
        ArticleDto expected = given()
                                .log().all()
                                .pathParam("id", articleId).
                            when()
                                .get(API_ARTICLE_ROOT + "/{id}").
                            then()
                                .extract()
                                .as(ArticleDto.class);
        expected.setTitle("Updated test title");
        expected.setBody("Updated test title");

        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("id", String.valueOf(expected.getId()));
        requestParameters.put("title", expected.getTitle());
        requestParameters.put("body", expected.getBody());

        //Update article
        given()
                .log().all()
                .pathParam("id", expected.getId()).
        with()
                .params(requestParameters).
        when()
                .put(API_ARTICLE_ROOT + "/{id}");

        //GET updated article to compare
        ArticleDto actual =  given()
                                    .log().all()
                                    .pathParam("id", expected.getId()).
                             when()
                                    .get(API_ARTICLE_ROOT + "/{id}").
                             then()
                                    .extract()
                                    .as(ArticleDto.class);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getBody(), actual.getBody());
        assertEquals(expected.getAssets(), actual.getAssets());
    }

    @Test
    @Order(6)
    public void givenArticle_whenMakingAddAction_thenArticleHasNewAsset() throws IOException {
        int articleId = testArticleId;
        File file = createPixel("test file");

        //Add a new file to given article
        given()
                .log().all().
        with()
                .pathParam("id", articleId)
                .multiPart(file).
        when()
                .post(API_ARTICLE_ROOT + "/{id}/assets/add");

        //Get the article
        ArticleDto actual =
                given()
                        .log().all()
                        .pathParam("id", articleId).
                when()
                        .get(API_ARTICLE_ROOT + "/{id}").
                then()
                        .extract()
                        .as(ArticleDto.class);
        assertEquals(1, actual.getAssets().size());

        //Re-using asset id for other tests
        testAssetId = actual.getAssets().stream()
                                        .map(x -> (int) x.getId())
                                        .findFirst().get();
        file.delete();
    }

    @Test
    @Order(7)
    public void givenArticleWithFile_whenMakingRemoveAction_thenAssetIsRemoved() {
        int articleId = testArticleId;
        int assetId = testAssetId;
        given()
                .log().all()
                .pathParam("id", articleId)
                .param("assetId", assetId).
        when()
                .post(API_ARTICLE_ROOT + "/{id}/assets/remove");

        ArticleDto article =
            given()
                    .log().all()
                    .pathParam("id", articleId).
            when()
                    .get(API_ARTICLE_ROOT + "/{id}").
            then()
                    .extract()
                    .as(ArticleDto.class);

        article.getAssets().forEach(actualAsset -> {
            assertThat(actualAsset.getId()).isNotEqualTo(assetId);
        });
    }

    @Test
    @Order(8)
    public void givenArticleWithoutFile_whenMakingAssignAction_thenArticleHasAssignedAsset() {
        int articleId = testArticleId;
        int assetId = testAssetId;

        given()
                .log().all().
        with()
                .pathParam("id", articleId)
                .param("assetId", assetId).
        when()
                .post(API_ARTICLE_ROOT + "/{id}/assets/assign");

        given()
                .log().all()
                .pathParam("id", articleId).
        when()
                .get(API_ARTICLE_ROOT + "/{id}").
        then()
                .assertThat()
                .body("assets", is(notNullValue()));
    }

    @Test
    @Order(9)
    public void givenArticleWithFile_whenMakingGetRequestForAssets_thenAssetsListIsNotEmpty() {
        int articleId = testArticleId;

        given()
                .log().all()
                .pathParam("id", articleId).
        when()
                .get(API_ARTICLE_ROOT + "/{id}/assets").
        then()
                .assertThat()
                .body("size()", equalTo(1));
    }

    @Test
    @Order(100)
    public void givenListOfTestObjects_whenMakingDeleteRequest_thenAllTestEntriesAreDeleted() {
        int articleId = testArticleId;
        int assetId = testAssetId;

        //Delete asset from storage
        ArticleDto article =
                given()
                        .pathParam("id", articleId).
                when()
                        .get(API_ARTICLE_ROOT + "/{id}").
                then()
                        .extract()
                        .as(ArticleDto.class);

        for (AssetDto asset : article.getAssets()) {
            File file = new File(asset.getLocation() + "/" + asset.getName());
            file.delete();
        }

        //Delete article
        given()
                .pathParam("id", articleId).
        when()
                .delete(API_ARTICLE_ROOT + "/{id}");

        given()
                .log().all()
                .pathParam("id", articleId).
        when()
                .get(API_ARTICLE_ROOT + "/{id}").
        then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Resource not found"));

        //Delete asset
        given()
                .pathParam("id", assetId).
        when()
                .delete(API_ASSET_ROOT + "/{id}");

        given()
                .log().all()
                .pathParam("id", assetId).
        when()
                .get(API_ASSET_ROOT + "/{id}").
        then()
                .log().body()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Resource not found"));
    }

    private File createPixel(String filename) throws IOException {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0,0, Integer.MAX_VALUE);

        File file = new File(filename + ".png");
        ImageIO.write(img,"PNG",  file);
        return file;
    }
}

