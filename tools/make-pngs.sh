#!/usr/bin/env bash

TARGET_DIR=$1
echo "Processing .dot files"
for x in `find $TARGET_DIR -name "pattern.*.dot"`
do
  dot -Tpng $x -o $x.png
  echo -n "."
done