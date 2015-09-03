#!/usr/bin/php
<?php
ini_set('memory_limit','512M');
require_once("./../inc/xml2array.inc.php");




scan_kml("./world.kml");

$for_deletion = array();

function scan_kml($filepath) {
  $obj = xml2array(file_get_contents($filepath));
  if (!empty($obj["kml"]["Document"]["Folder"])) {
    foreach($obj["kml"]["Document"]["Folder"] as $n) {
      foreach($n as $n_) { if (!try_fetch_href($n_,$filename)) {
        foreach($n_ as $n__) { if (!try_fetch_href($n__,$filename)) {
          foreach($n__ as $n___) { if (!try_fetch_href($n___,$filename)) {
            foreach($n___ as $n____) { if (!try_fetch_href($n____,$filename)) {
              foreach($n____ as $n_____) { if (!try_fetch_href($n_____,$filename)) {
                foreach($n_____ as $n______) { if (!try_fetch_href($n______,$filename)) {

                }}
              }}
            }}
          }}
        }}
      }}
    }
  }
}



function try_fetch_href($val,$parent_file) {
  if (!empty($val) && is_string($val) && (substr($val,0,4) === "http") && (strtolower(substr($val,-4,4)) === ".kml")) {
    check_cache_kml($val);
    return true;
  }
  return (empty($val) || !is_array($val));
}

function check_cache_kml($href) {
  $dir_cache = "./../data/kml/";
  $filename = $dir_cache . md5($href) . ".kml";
  if (file_put_contents($filename, file_get_contents($href))) {
    echo "saved -> ";
  } else {
    echo "error -> ";
  }
  echo "{$href} -> {$filename}\n";
  scan_kml($filename);

}


?>