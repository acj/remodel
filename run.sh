#!/usr/bin/env bash
#JAVAC=/usr/bin/javac
#JAVA=/usr/bin/java
JAVAC=~/research/java6/bin/javac
JAVA=~/research/java6/bin/java

# Clean up
rm -f output/*

#$JAVAC -g -cp .:ec/refactoring/jgrapht-jdk1.6.jar ec/refactoring/*.java && $JAVA -cp .:ec/refactoring/jgrapht-jdk1.6.jar:tijmp.jar -agentlib:tijmp -Xmx768m -enableassertions ec.Evolve -file ec/refactoring/refactoring.params
$JAVAC -g -cp .:ec/refactoring/jgrapht-jdk1.6.jar ec/refactoring/*.java && $JAVA -cp .:ec/refactoring/jgrapht-jdk1.6.jar:hsqldb/lib/hsqldb.jar:tijmp.jar -Xmx1024m -enableassertions ec.Evolve -file ec/refactoring/refactoring.params

echo "Processing .dot files..."
for i in `ls output/*.dot`
do
    dot -Tpng $i -o $i.png
done