all:
	export JAVAHOME=~/jdk1.6
	export PATH=~/jdk1.6/bin:$PATH
	make -f Makefile.ecj
	javac -g -d bin -classpath .:lib/jgrapht-jdk1.6.jar:lib/hsqldb.jar:lib/xercesImpl.jar:lib/xml-apis.jar ec/refactoring/*.java
	cd bin && jar cf ../sbr.jar ec && cd ..

install:
	mkdir -p ../sbr-runtime/lib
	cp sbr.jar ../sbr-runtime/lib
	cp lib/*.jar ../sbr-runtime/lib