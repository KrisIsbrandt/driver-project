package pl.coderslab.service.storage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.config.StorageConfig;
import pl.coderslab.dto.AssetDto;
import pl.coderslab.exception.StorageException;
import pl.coderslab.model.Asset;
import pl.coderslab.repository.AssetRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Primary
public class AssetServiceImplWithCloudStorage implements AssetService {

    private final AmazonS3 s3client;
    private final String bucketName;
    private final String region;
    private final AssetRepository assetRepository;
    private final ModelMapper modelMapper;
    private TransferManager transferManager;
    private String URI;
    private String[] allowedFormats;

    @Autowired
    public AssetServiceImplWithCloudStorage(AmazonS3 s3client,
                                            @Value("${storage.bucketName}") String bucketName,
                                            @Value("${storage.region}") String region,
                                            @Value("${storage.allowedFormats}") String[] allowedFormats,
                                            AssetRepository assetRepository,
                                            ModelMapper modelMapper) {
        this.s3client = s3client;
        this.bucketName = bucketName;
        this.region = region;
        this.allowedFormats = allowedFormats;
        this.assetRepository = assetRepository;
        this.modelMapper = modelMapper;
        init();
    }

    @Override
    public void init() {
        transferManager = TransferManagerBuilder
                .standard()
                .withS3Client(s3client)
                .build();

        URI = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
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
            if (!isAllowedFormat(fileFormat)) {
                throw new StorageException(fileFormat + " is not allowed");
            }

            //create a new asset
            Asset asset = new Asset();
            asset.setName(filename);
            asset.setType(fileFormat);
            asset.setLocation(URI + filename);

            //upload to cloud storage with set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());

            PutObjectRequest request = new PutObjectRequest(bucketName, filename, file.getInputStream(), metadata);
            Upload upload = transferManager.upload(request);
            upload.waitForUploadResult();
            //save asset to database
            assetRepository.save(asset);
            return asset;
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        } catch (AmazonServiceException | InterruptedException e) {
            throw new StorageException("Failed to store file in s3 " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
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
