
var RFCX;
RFCX = {};

RFCX.audio = {};
RFCX.audio.sample_rate = 8000;
RFCX.audio.test_file = "test";
RFCX.audio.format = "aiff";
RFCX.audio.file = "test/pcm/"+RFCX.audio.test_file;
RFCX.audio.filename = RFCX.audio.test_file;
RFCX.audio.data = null;
RFCX.audio.buffer_chunks = [];
RFCX.audio.compressed_chunks = [];
RFCX.audio.start_time_execution = (new Date()).valueOf();
RFCX.audio.start_time = RFCX.audio.start_time_execution;

RFCX.fft = {};
RFCX.fft.resolution = 2048;
RFCX.fft.buffer_length = 2*RFCX.fft.resolution;
RFCX.fft.coefficient = RFCX.audio.sample_rate/RFCX.fft.buffer_length;
RFCX.fft.limit_lo = 10;
RFCX.fft.limit_hi = 3990;
RFCX.fft.data = [];
RFCX.fft.freq = [];
RFCX.fft.values = [];
RFCX.fft.object = null;
RFCX.fft.snapshot_times = [];
RFCX.fft.stats = [];
RFCX.fft.stats_str = "";

RFCX.audio.compress_buffers = function() {
  console.log("-> compressing buffers...");
  var compressed_chunk = [];
  for (var i = 0; i < RFCX.audio.buffer_chunks.length; i++) {
    for (var j = 0; j < RFCX.audio.buffer_chunks[i].length; j++) {
      compressed_chunk.push(RFCX.audio.buffer_chunks[i][j]);
      if (compressed_chunk.length == RFCX.fft.buffer_length) {
        RFCX.audio.compressed_chunks.push(compressed_chunk);
        compressed_chunk = [];
      }
    }
  }
}

RFCX.fft.calculate = function() {
  console.log("-> calculating fft...");
  for (var i = 0; i < RFCX.audio.compressed_chunks.length; i++) {
    var fft_output = [];
    RFCX.fft.object.simple(fft_output, RFCX.audio.compressed_chunks[i], "real");
    RFCX.fft.consolidate(fft_output);
  }
}

RFCX.fft.save = function(FS) {
  console.log("-> saving fft... ("+RFCX.fft.data.length+" chunks)");
  var full_spectrum = {};
  for (var i = 0; i < RFCX.fft.data.length; i++) {
    if (!isNaN(RFCX.fft.data[i][0])) {
      for (var j = 0; j < RFCX.fft.data[i].length; j++) {
        RFCX.fft.data[i][j] = Math.round(RFCX.fft.data[i][j]);
      }
      full_spectrum[String(RFCX.audio.start_time + Math.round(i * RFCX.fft.buffer_length * 1000 / RFCX.audio.sample_rate))] = RFCX.fft.data[i];
    }
  }
  FS.writeFile( __dirname+"/../../tmp/spectrum/"+Math.round(RFCX.audio.start_time/1000)+".json", JSON.stringify(full_spectrum), "utf8" );
};

RFCX.fft.consolidate = function(fft_output) {
  var i = RFCX.fft.data.length;
  RFCX.fft.data[i] = [];
  var limits = [Math.floor(RFCX.fft.limit_lo/RFCX.fft.coefficient),Math.ceil(RFCX.fft.limit_hi/RFCX.fft.coefficient)];
  for (var j = 0; j < RFCX.fft.resolution; j++) {
    if ( (j < limits[0]) || (j >= limits[1]) ) {
      RFCX.fft.data[i].push(0);
    } else {
      RFCX.fft.data[i].push(Math.abs(fft_output[j]));
    }
  }
};

RFCX.fft.stats_write = function(FS,initialize) {

  RFCX.fft.stats_str = JSON.stringify(RFCX.fft.stats);
  
  if (initialize) {
    var PATH = require("path");
    PATH.exists(__dirname+"/../../tmp/cumulative.json",function(exists){
      if (!exists) {
        FS.writeFile(__dirname+"/../../tmp/cumulative.json", RFCX.fft.stats_str, function(err){ if (err) throw err; });
      } else {
        FS.readFile(__dirname+"/../../tmp/cumulative.json",function(err,data){
          if (err) throw err;
          RFCX.fft.stats_str = data;
          RFCX.fft.stats = JSON.parse(data);
        });
      }
    });
  } else {
    FS.writeFile(__dirname+"/../../tmp/cumulative.json",RFCX.fft.stats_str, function(err){
      if (err) throw err;
    });
  }
}

RFCX.fft.post_process = function(FS) {

  console.log("-> calculate frequencies...");
  for (var i = 0; i < RFCX.fft.resolution; i++) {
      var freq = String(Math.round((i+1)*RFCX.fft.coefficient));
      RFCX.fft.freq.push(freq);
      RFCX.fft.values[i] = [];
      RFCX.fft.stats[i] = {"freq":freq,"cnt":0,"min":0,"max":0,"mean":0,"std_dev_squ":0,"std_dev":0};
  }
  RFCX.fft.stats_write(FS,true);

  console.log("-> appending frequency values...");
  FS.readFile(__dirname+"/../../tmp/spectrum/"+Math.round(RFCX.audio.start_time/1000)+".json","utf8",function(err,data){
    var snapshots = JSON.parse(data);
    for (timestamp in snapshots) {
        var snapshot = snapshots[timestamp];
        RFCX.fft.snapshot_times.push(parseInt(timestamp));
        for (var i = 0; i < RFCX.fft.resolution; i++) {
            RFCX.fft.values[i].push(snapshot[i]);
        }
    }
    RFCX.fft.append_stats();
    RFCX.fft.end(FS);
  });

}

RFCX.fft.end = function(FS) {

  RFCX.fft.stats_write(FS,false);
  RFCX.fft.filter_results(FS);

  if (RFCX.audio.filename != RFCX.audio.test_file) {
    FS.unlink(__dirname+"/../"+RFCX.audio.file +"."+RFCX.audio.format,function(err){  });
  }

}

RFCX.fft.append_stats = function() {

  for (var i = 0; i < RFCX.fft.resolution; i++) {
    var values = RFCX.fft.values[i];
    var len = values.length;
    RFCX.fft.stats[i].cnt = RFCX.fft.stats[i].cnt + len;
    if (RFCX.fft.stats[i].min == 0) { RFCX.fft.stats[i].min = 999999999999; }
    var total = 0;
    for (var j = 0; j < len; j++) {
      total = total + values[j];
      if (values[j] < RFCX.fft.stats[i].min) { RFCX.fft.stats[i].min = values[j]; }
      if (values[j] > RFCX.fft.stats[i].max) { RFCX.fft.stats[i].max = values[j]; }
    }
    // calculate mean and store as float
    RFCX.fft.stats[i].mean = total / len; var mean = RFCX.fft.stats[i].mean;

    // calculate sum of squares of deviations
    var devs = []; for (var j = 0; j < len; j++) { devs.push(values[j]-mean); }
    var devs_squ = []; for (var j = 0; j < len; j++) { devs_squ.push(devs[j]*devs[j]); }
    var devs_squ_ttl = 0; for (var j = 0; j < len; j++) { devs_squ_ttl = devs_squ_ttl+devs_squ[j]; }
    RFCX.fft.stats[i].std_dev_squ = RFCX.fft.stats[i].std_dev_squ+devs_squ_ttl;

    RFCX.fft.stats[i].std_dev = Math.sqrt( RFCX.fft.stats[i].std_dev_squ / (RFCX.fft.stats[i].cnt - 1) );
  }
}


RFCX.fft.filter_results = function(FS) {

  var values_to_store = [];
  for (var i = 0; i < RFCX.fft.resolution; i++) {
    var limit = RFCX.fft.stats[i].mean + (2 * RFCX.fft.stats[i].std_dev);
    var values = [];
    for (var j = 0; j < RFCX.fft.values[i].length; j++) {
      if (RFCX.fft.values[i][j] > limit) {
        values.push(j.toString(16)+","+RFCX.fft.values[i][j].toString(16));
      }
    }
    if (values.length > 0) {
      values_to_store.push(parseInt(RFCX.fft.stats[i].freq).toString(16)+":"+values.join(";"));
    }
  }

  FS.writeFile( __dirname+"/../../tmp/frequency/"+RFCX.audio.filename+".json", values_to_store.join("*"), function(err){
    if (err) throw err;
    console.log("-> execution time: "+Math.round(((new Date()).valueOf() - RFCX.audio.start_time_execution) / 100)/10 + "s ("+RFCX.fft.values[0].length+" chunks)");
  });

}

module.exports = RFCX;
