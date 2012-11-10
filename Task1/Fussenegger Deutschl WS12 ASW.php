<?php

error_reporting(E_ALL | E_DEPRECATED);
ini_set('display-errors', 1);
ini_set('html_errors', 0);
header('content-type: text/plain; charset=utf-8');

global $mysqli;

$mysqli = mysqli_init();

$mysqli->real_connect('localhost', 'root', 'QnwDhcpugZHsxK5', 'book-crossing');

function fetch_val($query) {
  global $mysqli;

  $result = $mysqli->query($query);
  while ($row = $result->fetch_assoc()) {
    return $row['field'];
  }
}

function fetch_array($query) {
  global $mysqli;

  $result = $mysqli->query($query);
  $return = array();
  while ($row = $result->fetch_assoc()) {
    $return[] = $row['field'];
  }
  return $return;
}

function print_result(array $a) {
  print_r($a);
  exit;
}

function fetch_user_ratings($user_id) {
  global $mysqli;

  $sql_result = $mysqli->query('SELECT `ISBN`, `Book-Rating` FROM `bx-book-ratings` WHERE `User-ID` = "' . $user_id . '" ORDER BY `ISBN`');
  $result = array();
  while ($row = $sql_result->fetch_assoc()) {
    $result[$row['ISBN']] = $row['Book-Rating'];
  }
  return $result;
}

function median(array $a) {
  $n = count($a);
  $m = $n / 2;
  if ($n === 2 * $m) {
    return ($a[$m - 1] + $a[$m]) / 2;
  } else {
    return $a[floor($m)];
  }
}

function pquantil($p, array $a) {
  $n = count($a);
  $np = $n * $p;
  if (is_int($np)) {
    return ($a[$np - 1] + $a[$np]) / 2;
  } else {
    return $a[floor($np)];
  }
}

function standard_deviation(array $a) {
  $n = count($a);
  $sum = 0;
  $avg = array_sum($a) / $n;
  foreach ($a as $x) {
    $sum += pow($x - $avg, 2);
  }
  return sqrt(1 / ($n - 1) * $sum);
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

function pearson_correlation(array $x, array $y) {
  $n_x = count($x);
  $n_y = count($y);
  if ($n_x !== $n_y) {
    trigger_error('Arrays must be of same length!');
    return -1;
  }
  $avg_x = array_sum($x) / $n_x;
  $avg_y = array_sum($y) / $n_y;
  $s_xy = 0;
  foreach ($x as $k => $v) {
    $s_xy += ($v - $avg_x) * ($y[$k] - $avg_y);
  }
  if (($temp = (float) standard_deviation($x) * standard_deviation($y)) === 0.0) {
    return 0;
  }
  // Do not forget about the 1 / ($n_x - 1) because we are using our standard_deviation function
  // where they are not reduced.
  return ((1 / ($n_x - 1)) * $s_xy) / ($temp);
}

function spearman_correlation_ranking(array $a) {
  $rating_array = array();
  $i = 0;
  foreach ($a as $isbn => $rating) {
    $rating_array[$rating][$isbn] = ++$i;
  }
  $isbn_array = array();
  foreach ($rating_array as $rating => $isbn_ranking_array) {
    $avg = array_sum($isbn_ranking_array) / count($isbn_ranking_array);
    foreach ($isbn_ranking_array as $isbn => $ranking) {
      $isbn_array[$isbn] = $avg;
    }
  }
  return $isbn_array;
}

function spearman_correlation(array $x, array $y) {
  if (count($x) !== count($y)) {
    trigger_error('Arrays must be of same length!');
    return -1;
  }

  array_multisort($x, SORT_NUMERIC);
  $x = spearman_correlation_ranking($x);

  array_multisort($y, SORT_NUMERIC);
  $y = spearman_correlation_ranking($y);

  return pearson_correlation($x, $y);
}

function cosine_similarity_vector_abs(array $a) {
  $r = 0;
  foreach (array_values($a) as $v) {
    $r += $v * $v;
  }
  return sqrt($r);
}

function cosine_similarity(array $x, array $y) {
  $dot_product = 0;
  foreach ($x as $k => $v) {
    $dot_product += $v * $y[$k];
  }
  $abs_x = cosine_similarity_vector_abs($x);
  $abs_y = cosine_similarity_vector_abs($y);
  if (($abs_xy = (float) $abs_x * $abs_y) === 0.0) {
    return 0;
  }
  return $dot_product / $abs_xy;
}

$result = array();

foreach (array('0316095648', '0971880107', '0446610038') as $isbn) {
  $result[$isbn]['Average'] = fetch_val('SELECT AVG(`Book-Rating`) AS "field" FROM `bx-book-ratings` WHERE `ISBN` = "' . $isbn . '"');
  $result[$isbn]['Ratings'] = fetch_array('SELECT `Book-Rating` AS "field" FROM `bx-book-ratings` WHERE `ISBN` = "' . $isbn . '" ORDER BY `Book-Rating`');
  $result[$isbn]['Median'] = median($result[$isbn]['Ratings']);
  $result[$isbn]['p-Quantil'] = pquantil(0.25, $result[$isbn]['Ratings']);
  $result[$isbn]['Ratings'] = array_count_values($result[$isbn]['Ratings']);
  $result[$isbn]['Ratings']['total'] = array_sum($result[$isbn]['Ratings']);
}

foreach (array('1903', '2033', '2766') as $user_id) {
  $result[$user_id]['Average'] = fetch_val('SELECT AVG(`Book-Rating`) AS "field" FROM `bx-book-ratings` WHERE `User-ID` = "' . $user_id . '"');
  $result[$user_id]['Ratings'] = fetch_array('SELECT `Book-Rating` AS "field" FROM `bx-book-ratings` WHERE `User-ID` = "' . $user_id . '" ORDER BY `Book-Rating`');
  $result[$user_id]['Standard Deviation'] = standard_deviation($result[$user_id]['Ratings']);
  $result[$user_id]['Ratings'] = array_count_values($result[$user_id]['Ratings']);
  $result[$user_id]['Ratings']['total'] = array_sum($result[$user_id]['Ratings']);
}

$spec_user_id = '276688';
$result[$spec_user_id]['Ratings'] = fetch_user_ratings($spec_user_id);
$result[$spec_user_id]['Common Users'] = array();
$sql_result = $mysqli->query(
  'SELECT `br1`.`User-ID` FROM `bx-book-ratings` `br1` WHERE `br1`.`ISBN` IN ("' . implode('","', array_keys($result[$spec_user_id]['Ratings'])) . '") AND (
    SELECT `br2`.`Book-Rating` FROM `bx-book-ratings` `br2` WHERE `br2`.`ISBN` = `br1`.`ISBN` AND `br2`.`User-ID` = "' . $spec_user_id . '"
  ) = `br1`.`Book-Rating` AND `br1`.`User-ID` != "' . $spec_user_id . '" GROUP BY `br1`.`User-ID` HAVING COUNT(`br1`.`User-ID`) >= 7'
);
while ($row = $sql_result->fetch_assoc()) {
  $intersected_user_rating = $result[$spec_user_id]['Common Users'][$row['User-ID']] = array_intersect_keep_values($result[$spec_user_id]['Ratings'], fetch_user_ratings($row['User-ID']));
  $intersected_spec_user_rating = array_intersect_keep_values($intersected_user_rating, $result[$spec_user_id]['Ratings']);

  $result[$spec_user_id]['Common Users'][$row['User-ID']]['Pearson Correlation'] = pearson_correlation($intersected_spec_user_rating, $intersected_user_rating);
  $result[$spec_user_id]['Common Users'][$row['User-ID']]['Spearman Correlation'] = spearman_correlation($intersected_spec_user_rating, $intersected_user_rating);
  $result[$spec_user_id]['Common Users'][$row['User-ID']]['Cosine Similarity'] = cosine_similarity($intersected_spec_user_rating, $intersected_user_rating);
  $result[$spec_user_id]['Common Users'][$row['User-ID']]['csv'] = "\n" .
    'spec_user_id="' . $spec_user_id . '";' . implode(';', $intersected_spec_user_rating) . "\n" .
    'user_id="' . $row['User-ID'] . '";' . implode(';', $intersected_user_rating);
}

print_result($result);