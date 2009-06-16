#!/usr/bin/env bash
#
# Usage: ./run.sh [date] [run name]
#
# e.g., ./run.sh 20090616 ReMoDD-Model
JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
#JAVAC=~/research/java6/bin/javac
#JAVA=~/research/java6/bin/java

# Clean up
mkdir -p output
rm -f output/*

JAVACP=.:ec/refactoring/jgrapht-jdk1.6.jar:nsuml1_4/lib/nsmdf.jar:nsuml1_4/build/nsuml1_3.jar:hsqldb/lib/hsqldb.jar:xerces-2_9_1/xercesImpl.jar:xerces-2_9_1/xml-apis.jar

#$JAVAC -g -cp .:ec/refactoring/jgrapht-jdk1.6.jar ec/refactoring/*.java && $JAVA -cp .:ec/refactoring/jgrapht-jdk1.6.jar:tijmp.jar -agentlib:tijmp -Xmx768m -enableassertions ec.Evolve -file ec/refactoring/refactoring.params
$JAVAC -g -cp $JAVACP ec/refactoring/*.java && $JAVA -cp $JAVACP -Xmx3000m -enableassertions ec.Evolve -file ec/refactoring/refactoring.params

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