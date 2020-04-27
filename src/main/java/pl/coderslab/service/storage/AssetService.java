package pl.coderslab.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.dto.ArticleDto;
import pl.coderslab.dto.AssetDto;
import pl.coderslab.model.Article;
import pl.coderslab.model.Asset;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface AssetService {
    void init();

    Asset store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    String getFileExtension(String filename);

    void deleteAll();

    AssetDto convertToDto(Asset asset);

    Asset convertToEntity(AssetDto assetDto);

    Asset findById(long id);

    List<Asset> findAll();

    void deleteById(long id);

    void deleteStoredFile(String filename);
}
