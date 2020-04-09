package pl.coderslab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.dto.AssetDto;
import pl.coderslab.model.Asset;
import pl.coderslab.service.storage.AssetService;

import java.util.List;
import java.util.stream.Collectors;

import static pl.coderslab.service.RestPredictions.checkFound;
import static pl.coderslab.service.RestPredictions.checkNotNull;

@RestController
@RequestMapping("/assets")
public class AssetController {

    private final AssetService assetService;

    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public List<AssetDto> findAll() {
        List<Asset> assets = checkFound(assetService.findAll());
        return assets.stream()
                     .map(assetService::convertToDto)
                     .collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public AssetDto findById(@PathVariable ("id") long id) {
        Asset asset = checkFound(assetService.findById(id));
        return assetService.convertToDto(asset);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetDto create(@RequestParam("file") MultipartFile file) {
        Asset asset = assetService.store(file);
        checkNotNull(asset);
        return assetService.convertToDto(asset);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable ("id") long id) {
        checkFound(assetService.findById(id));
        assetService.deleteById(id);
    }
}
