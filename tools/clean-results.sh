#!/usr/bin/env bash
#
#

TARGET_DIR=$1

echo "Cleaning results"
find $TARGET_DIR -name "lib" | xargs rm -rf
find $TARGET_DIR -name "Models" | xargs rm -rf
echo "Uncompressing files"
find $TARGET_DIR -name "*.gz" | xargs gunzip

echo "Processing .dot files"
for x in `find $TARGET_DIR -name "pattern.*.dot"`
do
  dot -Tpng $x -o $x.png
  echo -n "."
done