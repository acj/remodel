#!/usr/bin/env bash
JAVAC=/usr/lib/jvm/java-6-sun-1.6.0.12/bin/javac
JAVA=/usr/bin/java
#JAVAC=~/research/java6/bin/javac
#JAVA=~/research/java6/bin/java

$JAVAC -g -cp .:ec/refactoring/jgrapht-jdk1.6.jar ec/refactoring/*.java
$JAVA -cp .:ec/refactoring/jgrapht-jdk1.6.jar ec.Evolve -file ec/refactoring/refactoring.params