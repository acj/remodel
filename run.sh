#!/usr/bin/env bash
#
# Usage: ./run.sh [date] [run name]
#
# e.g., ./run.sh 20090616 ReMoDD-Model
JAVAC=/usr/bin/javac
JAVA=/usr/bin/java

# Clean up
mkdir -p output
rm -f output/*

JAVACP=.:lib/jgrapht-jdk1.6.jar:lib/hsqldb.jar:lib/xercesImpl.jar:lib/xml-apis.jar:lib/jlogic.jar:lib/builtinsLib.jar
$JAVAC -cp $JAVACP ec/refactoring/*.java && $JAVA -cp $JAVACP -Xmx1000m -enableassertions ec.Evolve -file ec/refactoring/refactoring.params

if [ -n "$1" ]; then
    if [ -n "$2" ]; then
	mkdir -p results/$1/$2
	cp out.stat output/* results/$1/$2
    fi
fi

echo "Processing .dot files..."
for i in `ls output/pattern*.dot`
do
    dot -Tpng $i -o $i.png
done