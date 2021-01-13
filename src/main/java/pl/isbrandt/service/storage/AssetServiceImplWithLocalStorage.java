package pl.isbrandt.service.storage;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.isbrandt.dto.AssetDto;
import pl.isbrandt.exception.StorageException;
import pl.isbrandt.exception.StorageFileNotFoundException;
import pl.isbrandt.model.Asset;
import pl.isbrandt.repository.AssetRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class AssetServiceImplWithLocalStorage implements AssetService {

    private final AssetRepository assetRepository;
    private final ModelMapper modelMapper;
    private Path rootLocation;
    private String[] allowedFormats;

    @Autowired
    public AssetServiceImplWithLocalStorage(AssetRepository assetRepository,
                                            ModelMapper modelMapper,
                                            @Value("${storage.location}") String location,
                                            @Value("${storage.allowedFormats}") String[] allowedFormats) {
        this.assetRepository = assetRepository;
        this.modelMapper = modelMapper;
        this.rootLocation = Paths.get(location);
        this.allowedFormats = allowedFormats;
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
            String fileFormat = getFileExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString() + fileFormat;

            //check if allowed fileFormat
            if(!isAllowedFormat(fileFormat)) {
                throw new StorageException(fileFormat + " is not allowed");
            }

            //create a new asset
            Asset asset = new Asset();
            asset.setName(filename);
            asset.setType(fileFormat);
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

    @Override
    public Asset findById(long id) {
        return assetRepository.findById(id).orElse(null);
    }

    @Override
    public List<Asset> findAll() {
        return assetRepository.findAll();
    }

    @Override
    public void deleteById(long id) {
        assetRepository.deleteById(id);
    }

    @Override
    public void deleteStoredFile(String filename) {
        try {
            Files.delete(this.rootLocation.resolve(filename));
        } catch (IOException e) {
            throw new StorageException("Failed to delete stored file" + filename, e);
        }
    }

    private boolean isAllowedFormat(String fileFormat) {
        for (String allowedFormat : allowedFormats) {
            if (allowedFormat.equals(fileFormat)) {
                return true;
            }
        }
        return false;
    }
}
