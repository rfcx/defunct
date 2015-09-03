
var tj = require('togeojson'),
    fs = require('fs'),
    d3 = require('d3'),
    jsdom = require('jsdom').jsdom,
    kmlDir = "./../data/kml/";

fs.readdir(kmlDir,function(err,files){

  for (var i = 0; i < files.length; i++) {
    
    var geoJson = tj.kml(jsdom(fs.readFileSync(kmlDir+files[i],"utf8")));

    if (typeof geoJson.features[0] != "undefined") {

      var pathFeature = geoJson.features[0];

      var mapWidth = 600;
      var mapHeight = 800;
      var mapCenter = d3.geo.centroid(pathFeature);
      var mapScale  = 10000;
      var mapOffset = [mapWidth/2, mapHeight/2];
      var mapProjection = d3.geo.mercator().scale(mapScale).center(mapCenter).translate(mapOffset);

      var pathObj = d3.geo.path().projection(mapProjection);
      var pathAreaPixels = pathObj.area(pathFeature);

      console.log( Math.round(pathAreaPixels));

    } else {
      console.log("bad file: "+files[i]);
    }
  }
});


