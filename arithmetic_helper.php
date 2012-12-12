<?php

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

function pearson_correlation(array $x, array $y) {
  $n_x = count($x);
  $n_y = count($y);

  // TEST {{{
  if ($n_x !== $n_y) {
    trigger_error('FAIL: arrays must be of same length.');
    exit;
  }
  // }}}

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

/**
 * Get the metric of two items based on Jaccard index.
 *
 * @link https://en.wikipedia.org/wiki/Jaccard_index
 * @param array $x
 *   User ratings array one.
 * @param array $y
 *   User ratings array two.
 * @param array $intersect_count
 *   Element count of intersected array.
 * @return float
 *   Returns the similarity metric (between 0 and 1).
 */
function jaccard_index(array $x, array $y, $intersect_count = 0) {
  if ($intersect_count == 0) {
    return 0;
  }
  $array_union = count($x + $y);
  if ($array_union == 0) {
    return 0;
  }
  return ($array_union - $intersect_count) / $array_union;
}