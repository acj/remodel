= Summary =

REMODEL is a tool that uses genetic programming to automatically
introduce structural [design patterns](http://en.wikipedia.org/wiki/Design_patterns) into UML class diagrams.

= Usage =

1. After pulling the code, install [ECJ](http://cs.gmu.edu/~eclab/projects/ecj/) into the root directory.

2. Edit ec/refactoring/refactoring.inputfile.params to taste.

3. Run ./run.sh [date] [run name]

The two arguments passed to the run.sh script help to package all of the output data into well-named directories.  You can use anything you like, provided that there are no spaces or non-standard characters.

= More Information =

For further reading about what's going on under the hood, please refer to my [thesis](http://www.cse.msu.edu/~acj/papers/jensen2010msthesis.pdf).