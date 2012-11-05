<?php

// Require necessary helpers.
foreach (array('helper', 'arithmetic_helper', 'database_helper') as $require) {
  require_once('../' . $require . '.php');
}

// Identifizieren Sie die 50 Benutzer mit den meisten Bewertungen (!= 0). {{{
  $sql_result = $mysqli->query(
    'SELECT
      ' . USERID . ' AS "id",
      COUNT(' . USERID . ') AS "count"
    FROM ' . TABLE . '
      WHERE ' . RATING . ' != 0
      GROUP BY ' . USERID . '
      ORDER BY COUNT(' . USERID . ') DESC
      LIMIT 50'
  );

  while ($row = $sql_result->fetch_assoc()) {
    $result['top_50_users'][$row['id']] = $row['count'];
  }

  $result['total']['top_50_users'] = array_sum($result['top_50_users']);
// }}}

$sql_in_top_50_users = USERID . ' IN (' . implode(', ', array_keys($result['top_50_users'])) . ')';

// Schätzen Sie die Wahrscheinlichkeit für jede Bewertung (1 bis 10) durch Berechnung der entsprechenden relativen Häufigkeit. {{{
  $result['top_50_users_ratings'] = fetch_array(
    'SELECT
      COUNT(' . RATING . ') AS "field"
    FROM '. TABLE . '
      WHERE ' . RATING . ' != 0
      AND ' . $sql_in_top_50_users . '
      GROUP BY ' . RATING
  );

  $result['total']['top_50_users_ratings'] = array_sum($result['top_50_users_ratings']);

  foreach ($result['top_50_users_ratings'] as $i => $v) {
    $result['top_50_users_relative_rate'][$i] = $v / $result['total']['top_50_users_ratings'];
  }

  foreach ($result['top_50_users_relative_rate'] as $i => $v) {
    $result['top_50_users_relative_rate_bins'][$i] = array_sum(array_slice($result['top_50_users_relative_rate'], 0, $i + 1));
  }

  $result['total']['top_50_users_relative_rate'] = array_sum($result['top_50_users_relative_rate']);
// }}}

// TEST {{{
  if ($result['total']['top_50_users'] != $result['total']['top_50_users_ratings']) {
    trigger_error('FAIL: total user ratings not equal total book ratings.');
    exit;
  }
// }}}

// Ersetzen sie nun die fehlenden Werte durch Zufallswerte, die der geschätzten Verteilung folgen. {{{
  function rating_rand(array $bins) {
    $mt_rand = mt_rand(0,1);
    foreach ($bins as $i => $bin) {
      if ($mt_rand <= $bin) return $i + 1;
    }
  }

  /** @var array $isbns ISBNs for all books rated by our top50 users. */
  $isbns = fetch_array('SELECT ' . ISBN . ' AS "field" FROM ' . TABLE . ' WHERE ' . $sql_in_top_50_users);

  /** @var array $user_id_isbns_ratings Ratings for all books of the top50 users. */
  $user_id_isbns_ratings = array();

  foreach ($result['top_50_users'] as $user_id => $total_ratings) {

    // Get all ratings from this top50 user.
    $sql_result = $mysqli->query('SELECT ' . RATING . ' AS "rating", ' . ISBN . ' AS "isbn" FROM ' . TABLE . ' WHERE ' . USERID . ' = ' . $user_id);

    // Put them into our new array.
    while ($row = $sql_result->fetch_assoc()) {
      $user_id_isbns_ratings[$user_id][$row['isbn']] = $row['rating'];
    }

    // Rate all books this user hasn't rated.
    foreach ($isbns as $i => $isbn) {
      if (
        !array_key_exists($isbn, $user_id_isbns_ratings[$user_id]) ||           // If this book doesn't exist at all
        $user_id_isbns_ratings[$user_id][$isbn] <= 0 ||                         // If the rating is below (illegal) or equal 0
        $user_id_isbns_ratings[$user_id][$isbn] > 10                            // If the rating is above 10 (illegal)
      ) {
        // Calculate random rating for this book.
        $user_id_isbns_ratings[$user_id][$isbn] = rating_rand($result['top_50_users_relative_rate_bins']);

        // TEST {{{
        if ($user_id_isbns_ratings[$user_id][$isbn] < 0 || $user_id_isbns_ratings[$user_id][$isbn] > 10) {
          trigger_error('FAIL: rating_rand() returned illegal value.');
          exit;
        }
        // }}}

      }
    }

  }

  // Example output for single user (more then one user is simply too much).
  /*
  foreach ($user_id_isbns_ratings as $user_ratings) {
    $result['example_user_ratings'] = $user_ratings;
    break;
  }
  */
// }}}

// Identifizieren das Paar von Benutzern mit der höchsten paarweisen Pearson-Korrelation. {{{

  // Prepare array for highest pearson calculation.
  $result['pearson_max'] = array('pearson' => NULL);

  // Iterate over all users.
  foreach ($user_id_isbns_ratings as $user_id => $isbns_ratings_array) {
    // Iterate again over all users.
    foreach ($user_id_isbns_ratings as $other_user_id => $other_isbns_ratings_array) {
      // Calculate the pearson correlation for all 50 users.
      $pearson_correlation[$user_id][$other_user_id] = pearson_correlation ($isbns_ratings_array, $other_isbns_ratings_array);

      // Identifizieren das Paar von Benutzern mit der höchsten paarweisen Pearson-Korrelation. {{{
        if (abs($pearson_correlation[$user_id][$other_user_id]) > abs($result['pearson_max']['pearson']) && $user_id != $other_user_id) {
          $result['pearson_max'] = array(
            'user_id' => $user_id,
            'other_user_id' => $other_user_id,
            'pearson' => $pearson_correlation[$user_id][$other_user_id]
          );
        }
      // }}}

    }
  }
// }}}

// Laden Sie die erstellte Daten-Matrix, die Matrix der paarweisen Korrelationskoeffizienten (50*50), den Source Code und einen Report ins Wiki. {{{
  $csv = to_csv('', array_keys($pearson_correlation));
  foreach ($pearson_correlation as $user_id => $other_users_array) {
    $csv .= to_csv($user_id, $other_users_array);
  }
  file_put_contents('matrix.csv', $csv);
// }}}

print_result($result);