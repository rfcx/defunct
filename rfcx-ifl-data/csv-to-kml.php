#!/usr/bin/php
<?php
ini_set('memory_limit','512M');

$row = 1;
$valid = 0;
$str = "";
$chunk = 0;
$header_str = "";
$json = "";
$str = "";
$xml = "";

$use_only_tropical_data = true;

if (($handle = fopen("./data/raw/2013-07-world-ifl.csv", "r")) !== FALSE) {
    while (($data = fgetcsv($handle)) !== FALSE) {
        $lat = floatval($data[5]);
        if ((($lat >= -23.4378) && ($lat <= 23.4378)) || !$use_only_tropical_data) {
          $valid++;
           echo "\n{$valid}) ".round(100*(strlen($data[6])/(1024*1024)))/100;
          $json = json_decode($data[6]);

          if (!empty($json->coordinates[0][0])) {
           
            $xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<kml xmlns=\"http://www.opengis.net/kml/2.2\"><Document><Folder><Placemark><MultiGeometry><Polygon><outerBoundaryIs><LinearRing><coordinates>";
            for ($i = 0; $i < count($json->coordinates[0][0]); $i++) {
                $c = $json->coordinates[0][0][$i];
                $xml .= "{$c[0]},{$c[1]} ";
            }
            $xml .= "</coordinates></LinearRing></outerBoundaryIs></Polygon></MultiGeometry></Placemark></Folder></Document></kml>";
           }
                           
          $write_output = file_put_contents("./data/kml/".str_pad(strval($valid),5,"0",STR_PAD_LEFT).".kml",$xml);
          unset($json);
          unset($xml);
          unset($write_output);

        }
    }
    fclose($handle);
//    fclose($writeHandle);
}

?>