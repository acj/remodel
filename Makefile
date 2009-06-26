all:
	export JAVAHOME=~/jdk1.6
	export PATH=~/jdk1.6/bin:$PATH
	make -f Makefile.ecj
	javac -g -classpath .:ec/refactoring/jgrapht-jdk1.6.jar:nsuml1_4/lib/nsmdf.jar:nsuml1_4/build/nsuml1_3.jar:hsqldb/lib/hsqldb.jar:xerces-2_9_1/xercesImpl.jar:xerces-2_9_1/xml-apis.jar ec/refactoring/*.java
	cd bin && jar cf ../sbr.jar ec && cd ..

install:
	mkdir -p ../sbr-runtime/lib
	cp sbr.jar ../sbr-runtime/lib
	cp ec/refactoring/jgrapht-jdk1.6.jar ../sbr-runtime/lib
	cp nsuml1_4/lib/nsmdf.jar ../sbr-runtime/lib
	cp nsuml1_4/build/nsuml1_3.jar ../sbr-runtime/lib
	cp hsqldb/lib/hsqldb.jar ../sbr-runtime/lib
	cp xerces-2_9_1/xercesImpl.jar ../sbr-runtime/lib
	cp xerces-2_9_1/xml-apis.jar ../sbr-runtime/lib