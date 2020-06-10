$(document).ready(function () {
    var assetContainer = $('#assets-container');
    var MAX = 5;
    function addNewInput() {
        for (let i = 1; i <= MAX; i++) {
        var asset = $('<div class="asset"><span>#asset' + i + '#</span><input type="file" name="file" value="' + i + '"/></div>');
            assetContainer.append(asset);
        }
    }

    addNewInput();
});