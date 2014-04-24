var camera = {
    getPicture: function(title, success, failure, options) {
        options = options || {};
        var quality = options.quality || 100;
        var targetWidth = options.targetWidth || -1;
        var targetHeight = options.targetHeight || -1;
        cordova.exec(success, failure, "Camera", "takePicture", [title, quality, targetWidth, targetHeight]);
    }
};

module.exports = camera;
