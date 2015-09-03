
var AV = require("av");
var FFT = require("fft");
var FS = require("fs");
var RFCX = require(__dirname+"/rfcx/rfcx.js");
var ARGV = require("optimist").argv;

if ((typeof ARGV.format !== "boolean") && (typeof ARGV.format != "undefined")) {
  RFCX.audio.format = ARGV.format;
}

if ((typeof ARGV.file !== "boolean") && (typeof ARGV.file != "undefined")) {
  RFCX.audio.file = "../tmp/pcm/"+ARGV.file;
  RFCX.audio.filename = ARGV.file;
  if (!isNaN(parseInt(ARGV.file))) { RFCX.audio.start_time = 1000*parseInt(ARGV.file); }
}

console.log("-> loading audio file ("+RFCX.audio.file+"."+RFCX.audio.format+")");
RFCX.audio.data = AV.Asset.fromFile(__dirname+"/"+RFCX.audio.file +"."+RFCX.audio.format);

RFCX.fft.object = new FFT.complex(RFCX.fft.resolution, false);

RFCX.audio.data.on("decodeStart", function() {
  RFCX.audio.data.on("data", function(buffer) {
    RFCX.audio.buffer_chunks.push(buffer);
    RFCX.audio.data.decoder.readChunk();
  });
  console.log("-> generating fft data...");
  RFCX.audio.data.decoder.readChunk();
});

RFCX.audio.data.source.on("end",function(){
  setTimeout(function(){

    RFCX.audio.compress_buffers();
    RFCX.fft.calculate();
    RFCX.fft.save(FS);
    RFCX.fft.post_process(FS);

  },1000);
});

RFCX.audio.data.start();
