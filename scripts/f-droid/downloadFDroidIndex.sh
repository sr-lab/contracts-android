#!/bin/bash

################################################################################
# Code related with actions and colours
################################################################################
RES_COL=60
MOVE_TO_COL="printf \\033[${RES_COL}G"

RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
NORMAL=$(tput sgr0)

SETCOLOR_SUCCESS=$GREEN
SETCOLOR_FAILURE=$RED
SETCOLOR_NORMAL=$NORMAL

echo_success() {
  $MOVE_TO_COL
  printf "["
  printf $SETCOLOR_SUCCESS
  printf $"  OK  "
  printf $SETCOLOR_NORMAL
  printf "]"
  printf "\r"
  return 0
}

echo_failure() {
  $MOVE_TO_COL
  printf "["
  printf $SETCOLOR_FAILURE
  printf $"FAILED"
  printf $SETCOLOR_NORMAL
  printf "]"
  printf "\r"
  return 1
}

action() {
  local STRING rc

  STRING=$1
  printf "$STRING "
  shift
  "$@" && echo_success $"$STRING" || echo_failure $"$STRING"
  rc=$?
  echo
  return $rc
}

################################################################################
# Code related with F-Droid index.xml
################################################################################

DATE=$(date +"%Y%m%d_%H%M")
URL="https://f-droid.org/repo/index.xml"
OUTPUT="index_${DATE}.xml"

action "Downloading index.xml (as $OUTPUT)" wget $URL -O $OUTPUT 2>/dev/null 

