var fs = require("fs");
var restify = require("restify");
var zip = new require('node-zip')();

var client = restify.createJsonClient({
  url: "http://localhost:8080",
  connectTimeout: 10000,
  headers: { "x-rfcx-unit": "abcd" },
  retry: { "retries": 0 }
});

fs.readFile("test.json","utf8",function(e,d){
  zip.file('test.json', d);
  var data = zip.generate({base64:true,compression:'DEFLATE'});
  console.log(data.length);
  client.post("/freq", {
      time: 1354436455,
      data: data
    }, function(err,req,res,obj){
      if (err) throw err;
      console.log(JSON.stringify(obj, null, 2));
  });
});