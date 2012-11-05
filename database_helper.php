<?php

define('DB', 'book-crossing');
define('TABLE', '`bx-book-ratings`');
define('USERID', '`User-ID`');
define('RATING', '`Book-Rating`');
define('ISBN', '`ISBN`');

global $mysqli;

$mysqli = mysqli_init();

$mysqli->real_connect('localhost', 'root', 'QnwDhcpugZHsxK5', DB);

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

function fetch_user_ratings($user_id) {
  global $mysqli;

  $sql_result = $mysqli->query('SELECT `ISBN`, `Book-Rating` FROM `bx-book-ratings` WHERE `User-ID` = "' . $user_id . '" ORDER BY `ISBN`');
  $result = array();
  while ($row = $sql_result->fetch_assoc()) {
    $result[$row['ISBN']] = $row['Book-Rating'];
  }
  return $result;
}