#!/usr/bin/env bash
javac -cp .:ec/refactor/jgrapht-jdk1.6.jar ec/refactor/*.java &&
# java -cp .:ec/refactor/jgrapht-jdk1.6.jar ec.Evolve -file refactor.params
java -cp .:ec/refactor/jgrapht-jdk1.6.jar -Xmx512m ec.display.Console -file refactor.params
