package pl.coderslab.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.config.StorageProperties;
import pl.coderslab.exception.StorageException;
import pl.coderslab.exception.StorageFileNotFoundException;
import pl.coderslab.model.Asset;
import pl.coderslab.repository.AssetRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;
    private final AssetRepository assetRepository;

    @Autowired
    public StorageServiceImpl(StorageProperties properties, AssetRepository assetRepository) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.assetRepository = assetRepository;
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file" + file.getOriginalFilename());
            }
            //create a new asset
            Asset asset = new Asset();
            asset.setAssetName(file.getOriginalFilename());
            asset.setAssetType(getFileExtension(file.getOriginalFilename()));
            asset.setAssetLocation(rootLocation.toFile().getAbsolutePath());

            //copy a asset to rootLocation with the original name
            Files.copy(file.getInputStream() , this.rootLocation.resolve(file.getOriginalFilename()));

            //save asset
            assetRepository.save(asset);
        } catch (IOException e) {
            throw new StorageException("Failed to store file" + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            //Return Stream of all files found in the rootLocation dictionary, but not on the same level sa the dictionary
            return Files.walk(this.rootLocation, 1)
                        .filter(path -> !path.equals(this.rootLocation))
                        .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public String getFileExtension(String filename) {
        //return substring after last dot or empty string
        return Optional.of(filename)
                       .filter(f -> f.contains("."))
                       .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                       .orElse("");
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
