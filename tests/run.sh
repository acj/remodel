#!/usr/bin/env bash
#
# Test suite for search-based refactoring GP
# Usage: ./run.sh
JAVA=/usr/bin/java
JAVACP=.:sbr.jar:lib/jgrapht-jdk1.6.jar:lib/hsqldb.jar:lib/xercesImpl.jar:lib/xml-apis.jar

rm -f test.log
rm -rf results
mkdir results

for f in `ls Models/*.xmi`
do
	# Clean up
	mkdir -p output
	rm -f output/*

	2>&1
	echo -n "Running GP on $f: "
	echo "ec.refactoring.inputfile = $f" > ec/refactoring/refactoring.inputfile.params
	echo "seed.0 = 1" >> ec/refactoring/refactoring.inputfile.params
	$JAVA -cp $JAVACP -Xmx1000m -enableassertions ec.Evolve -file ec/refactoring/refactoring.params &> output/run.log

	SHORTNAME=`echo $f | sed -e 's/Models\/\(.*\)\.xmi/\1/'`
	mv output results/$SHORTNAME
	
	# Determine whether we detected the DP that is expected
	DETECTED=`grep -r "{.*$SHORTNAME.*}" results/$SHORTNAME/run.log`
	if [ -z "$DETECTED" ]; then
		echo "failed!"
	else
		echo "passed"
	fi
done



