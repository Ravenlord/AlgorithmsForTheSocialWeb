<?php

error_reporting(E_ALL | E_DEPRECATED);
ini_set('display-errors', 1);
ini_set('html_errors', 0);

if (!isset($_GET['html']) && !isset($_GET['csv'])) {
  header('content-type: text/plain; charset=utf-8');
}

global $result;

$result = array();

function print_result(array $a) {
  print_r($a);
  exit;
}

function array_intersect_keep_values(array $x, array $y) {
  $return = array();
  foreach ($x as $k => $v) {
    if (array_key_exists($k, $y)) {
      $return[$k] = $y[$k];
    }
  }
  return $return;
}

function to_csv($identifier, $data_array) {
  return 'identifier="' . $identifier . '";' . implode(';', $data_array) . "\n";
}