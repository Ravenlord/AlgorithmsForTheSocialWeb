<?php

/**
 * Algorithms for the Social Web Task 6
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 */

// Require necessary helpers.
foreach (array('helper', 'arithmetic_helper', 'database_helper') as $require) {
  require_once('../' . $require . '.php');
}

function get_top_50() {
  global $mysqli;

  /** @var object $sql_result */
  $sql_result = $mysqli->query(
    'SELECT
      ' . USERID . ' AS "uid"
    FROM ' . TABLE . '
      WHERE ' . RATING . ' IS NOT NULL
      AND ' . RATING . ' != 0
      GROUP BY ' . USERID . '
      ORDER BY COUNT(' . USERID . ') DESC
      LIMIT 50'
  );

  /** @var array $result */
  $result = array();

  /** @var array $row */
  while ($row = $sql_result->fetch_assoc()) {
    $result[$row['uid']] = array();
  }

  $sql_result = $mysqli->query(
    'SELECT
      ' . USERID . ' AS "uid",
      ' . ISBN . ' AS "isbn",
      ' . RATING . ' AS "rating"
    FROM ' . TABLE . '
      WHERE ' . USERID . ' IN(' . implode(',', array_keys($result)) . ')
      AND ' . RATING . ' IS NOT NULL
      AND ' . RATING . ' != 0
      ORDER BY ' . ISBN
  );

  while ($row = $sql_result->fetch_assoc()) {
    $result[$row['uid']][$row['isbn']] = $row['rating'];
  }

  return $result;
}

/**
 * Similarity using Euclidean Distance Score.
 *
 * Function is taken from O'Reilly's Programming Collective Intelligende by Toby
 * Segaran and transcribed from Python to PHP.
 *
 * @link https://en.wikipedia.org/wiki/Euclidean_distance
 * @see https://sites.google.com/site/kristjansiimson/miscellaneous/algorithms/php-similarity-algorithms
 * @param array $user_1
 *   All items with ratings from one user which will be compared to user 2.
 * @param array $user_2
 *   All items with ratings from one user which will be used as comparison for
 *   user 1.
 * @return float
 *   The Euclidean Distance Score as float.
 */
function euclidean_distance_score(array $user_1, array $user_2) {
  /** @var array $shared_items Get list of shared items. */
  $shared_items = array_keys(array_intersect_key($user_1, $user_2));

  // If these users have no ratings in common, return 0.
  if (count($shared_items) === 0) {
    return 0;
  }

  /** @var int $sum_of_squares */
  $sum_of_squares = 0;

  foreach ($shared_items as $shared_item) {
    $sum_of_squares += pow($user_1[$shared_item] - $user_2[$shared_item], 2);
  }

  return 1 / (1 + sqrt($sum_of_squares));
}

/**
 * @todo Write comment!
 *
 * @param array $top50
 * @param int $reference_user_id
 * @param int $neighbor_count
 * @param string $sim
 * @return array
 */
function k_nearest_neighbor(array $top50, $reference_user_id, $neighbor_count = 20, $sim = 'euclidean_distance_score') {
  /** @var array $euclid_score */
  $score = array();

  foreach ($top50 as $user_id => $user_array) {
    if ($user_id != $reference_user_id && !array_key_exists($user_id, $score)) {
      $score[$user_id] = call_user_func($sim, $top50[$reference_user_id], $user_array);
    }
  }

  // Sort the resulting array by the Euclid Score.
  arsort($score);

  // k-NN
  return array_slice($score, 0, $neighbor_count, TRUE);
}

/**
 * @todo Write comment!
 *
 * @param array $user_arrays
 * @param array $neighbor_ids
 * @param string $isbn_to_predict
 * @return float
 */
function average_from_neighbors(array $user_arrays, array $neighbor_ids, $isbn_to_predict) {
  $sum = 0;
  $count = 0;
  foreach ($neighbor_ids as $user_id => $euclid_score) {
    if (!empty($user_arrays[$user_id][$isbn_to_predict])) {
      $sum += $user_arrays[$user_id][$isbn_to_predict];
      ++$count;
    }
  }
  if ($count == 0) {
    return;
  }
  return $sum / $count;
}

/**
 * Slope One algorithm.
 *
 * @link http://sourceforge.net/projects/vogoo/
 * @link http://lemire.me/fr/abstracts/TRD01.html
 * @link http://code.google.com/p/openslopeone/
 * @param array $user_1
 *   All items with ratings from one user which will be compared to user 2.
 * @param array $user_2
 *   All items with ratings from one user which will be used as comparison for
 *   user 1.
 * @param string $isbn_to_predict
 *   The ISBN of the book we should predict a recommendation for.
 * @return float
 *   Predicted rating for given ISBN.
 */
function slope_one_predict(array $user_1, array $user_2, $isbn_to_predict) {
  // Delete the original rating from user 1.
  unset($user_1[$isbn_to_predict]);

  /** @var float $rating_difference */
  $rating_difference = 0;

  /** @var int $count */
  $count = 0;

  // Calculate the average difference between two items.
  foreach ($user_1 as $isbn => $rating) {
    if (!empty($rating) && !empty($user_2[$isbn])) {
      $rating_difference += $rating - $user_2[$isbn];
      ++$count;
    }
  }

  if ($count == 0) {
    return;
  }

  // Calculate the predicted rating for user 1 for item identified by ISBN with
  // average rating deviation.
  return $user_2[$isbn_to_predict] + ($rating_difference / $count);
}

/** @var array $result */
$result = array();

/** @var array $top50 */
$top50 = get_top_50();

/** @var int $top_user */
$top_user = $result['Reference User ID'] = array_keys($top50)[0];

/** @var array $k_nearest_neighbor */
$k_nearest_neighbor = $result['Euclid Score'] = k_nearest_neighbor($top50, $top_user);

$avg_sum = $rmse_avg_count = $slope_sum = $rmse_slope_count = 0;

foreach ($k_nearest_neighbor as $user_id => $euclid_score) {
  $result[$user_id]['Euclid Score'] = $euclid_score;
  $intersected_reference_user_array = array_intersect_keep_values($top50[$user_id], $top50[$top_user]);
  $intersected_user_array = array_intersect_keep_values($top50[$top_user], $top50[$user_id]);
  foreach (array_keys($intersected_reference_user_array) as $isbn) {
    if ($avg_result = average_from_neighbors($top50, $k_nearest_neighbor, $isbn)) {
      $avg_sum += pow($avg_result - $intersected_reference_user_array[$isbn], 2);
      ++$rmse_avg_count;
    }

    if ($slope_result = slope_one_predict($intersected_reference_user_array, $intersected_user_array, $isbn)) {
      $slope_sum += pow($slope_result - $intersected_reference_user_array[$isbn], 2);
      ++$rmse_slope_count;
    }

    $result[$user_id][$isbn] = array(
      'Actual Rating Reference User' => $intersected_reference_user_array[$isbn],
      'Actual Rating User' => $intersected_user_array[$isbn],
      'Average from Neighbors' => $avg_result,
      'Slope One' => $slope_result,
    );
  }
}

$result['Overall RMSE Average'] = sqrt((1 / $rmse_avg_count) * $avg_sum);
$result['Overall RMSE Slope One'] = sqrt((1 / $rmse_slope_count) * $slope_sum);

print_result($result);