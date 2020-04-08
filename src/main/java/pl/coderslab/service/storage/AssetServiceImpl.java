package pl.coderslab.service.storage;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.config.StorageProperties;
import pl.coderslab.dto.AssetDto;
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
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class AssetServiceImpl implements AssetService {

    private final Path rootLocation;
    private final AssetRepository assetRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AssetServiceImpl(StorageProperties properties, AssetRepository assetRepository, ModelMapper modelMapper) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.assetRepository = assetRepository;
        this.modelMapper = modelMapper;
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
    public Asset store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file" + file.getOriginalFilename());
            }
            String fileType = getFileExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString() + fileType;

            //create a new asset
            Asset asset = new Asset();
            asset.setName(filename);
            asset.setType(fileType);
            asset.setLocation(rootLocation.toFile().getAbsolutePath());

            //copy a asset to rootLocation with the original name
            Files.copy(file.getInputStream() , this.rootLocation.resolve(filename));

            //save asset
            assetRepository.save(asset);
            return asset;
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
                       .map(f -> f.substring(filename.lastIndexOf(".")))
                       .orElse("");
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public AssetDto convertToDto(Asset asset) {
        return modelMapper.map(asset, AssetDto.class);
    }

    @Override
    public Asset convertToEntity(AssetDto assetDto) {
        return modelMapper.map(assetDto, Asset.class);
    }
}
