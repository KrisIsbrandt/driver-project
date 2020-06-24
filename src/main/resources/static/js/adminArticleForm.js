var ROOT;
if (location.port !== "8080") {
    ROOT = location.protocol + '//'+ location.hostname + '/';
} else {
    ROOT = "http://localhost:8080/";
}

var articleId = $('input[type=hidden]').attr('value');
var assetContainer = $('#assets-container');
var MAX = 5;

function clearAssetContainer() {
    assetContainer.empty();
}

function addNewInput(start, end) {
    for (let i = start; i <= end; i++) {
        var asset = $('<div class="asset"><span>#asset' + (i + 1) + '#</span><input type="file" name="file" value="' + (i + 1) + '"/></div>');
        assetContainer.append(asset);
    }
}

function showCurrentAssets(assets) {
    let length = assets.length;
    for (let i = 0; i < length; i++) {
        let assetId = assets[i].id;
        var image = $('<img style=\"width:90%; height: 90%\">');
        image.attr("src", assets[i].location);

        var removeBtn = $('<button class="btn btn-danger btn-circle" title="Delete"><svg class="svg-inline--fa fa-trash fa-w-14" aria-hidden="true" focusable="false" data-prefix="fas" data-icon="trash" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512" data-fa-i2svg=""><path fill="currentColor" d="M432 32H312l-9.4-18.7A24 24 0 0 0 281.1 0H166.8a23.72 23.72 0 0 0-21.4 13.3L136 32H16A16 16 0 0 0 0 48v32a16 16 0 0 0 16 16h416a16 16 0 0 0 16-16V48a16 16 0 0 0-16-16zM53.2 467a48 48 0 0 0 47.9 45h245.8a48 48 0 0 0 47.9-45L416 128H32z"></path></svg>Delete</button>');
        removeBtn.on('click', function(e) {
            confirmRemove(articleId, assetId);
        });

        var asset = $('<div class="asset"><span>#asset' + (i + 1) + '#</span></div>');
        asset.append(image);
        asset.append(removeBtn);
        assetContainer.append(asset);
    }
}

function confirmRemove(articleId, assetId) {
    removeAsset(articleId, assetId);
    clearAssetContainer();
    updateAssetList();
    showCurrentAssets(assets);
    addNewInput(assets.length, (MAX - assets.length));
}

function updateAssetList() {
    let link = ROOT + "api/v1/articles/" + articleId + "/assets";
    assets = $.get(link);
}

function removeAsset(articleId, assetId) {
    let link = ROOT + "api/v1/articles/" + articleId + "/assets/remove?assetId=" + assetId;
    $.post(link);
}

if (typeof articleId !== 'undefined') {
    showCurrentAssets(assets);
    addNewInput(assets.length, (MAX - assets.length));
} else {
    addNewInput(1, MAX);
}
