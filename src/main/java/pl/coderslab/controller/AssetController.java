package pl.coderslab.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RequestMapping("/api/v1/assets")
@Api(value = "Asset Management Operations")
public class AssetController {

    private final AssetService assetService;

    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @ApiOperation(value = "View a list of available assets", response = List.class)
    @GetMapping
    public List<AssetDto> findAll() {
        List<Asset> assets = checkFound(assetService.findAll());
        return assets.stream()
                     .map(assetService::convertToDto)
                     .collect(Collectors.toList());
    }

    @ApiOperation(value = "Get an asset by Id", response = AssetDto.class)
    @GetMapping(value = "/{id}")
    public AssetDto findById(@ApiParam(value = "Asset Id from which asset object will retrieve", required = true) @PathVariable ("id") long id) {
        Asset asset = checkFound(assetService.findById(id));
        return assetService.convertToDto(asset);
    }

    @ApiOperation(value = "Add an asset")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetDto create(@ApiParam(value = "File provided via multipart/form-data to be stored and converted to asset", required = true) @RequestParam("file") MultipartFile file) {
        Asset asset = assetService.store(file);
        checkNotNull(asset);
        return assetService.convertToDto(asset);
    }

    @ApiOperation(value = "Delete an asset")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@ApiParam(value = "Asset Id from which asset object will delete from database", required = true) @PathVariable ("id") long id) {
        checkFound(assetService.findById(id));
        assetService.deleteById(id);
    }
}
