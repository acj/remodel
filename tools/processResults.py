#!/usr/bin/env pythong
#
# Processes results from the GP.

import os
import sys

result_dir = ""
if (len(sys.argv) < 2):
    print "usage: " + sys.argv[0] + " <result dir>"
    sys.exit(0)
else:
    result_dir = sys.argv[1]

# A dictionary of pattern information.  The key is the name of the model
# being processed, e.g. "TestModel".
model_dict = {}    # Count of pattern types for each model 
qmood_dict = {}    # Initial QMOOD value for each model
bestfit_dict = {}  # Best fitness for each model
totals_dict = {}   # Total number of patterns for each model
treesize_dict = {} # Tree size for each GP program
mtsize_dict = {}   # Number of MTs for each GP program
startgraphsize_dict = {} # Starting graph size for each model
finalgraphsize_dict = {} # Final graph size for each model

for dir in os.listdir(result_dir):
    # Skip "bak" directories that result from repeated runs
    if dir[-3:] == 'bak':
        continue
    
    #print dir
    model_dict[dir] = {}

    num_instances = 0
    output_dir = result_dir + '/' + dir + '/output/'
    if not os.path.isdir(output_dir):
        continue

    # Extract the baseline QMOOD measurement
    if os.path.isfile(output_dir + 'baseline.txt'):
        q_file = open(output_dir + 'baseline.txt', 'r')
    
        qmood_line = q_file.readline()
        qmood_tokens = qmood_line.split(' ')
        qmood_dict[dir] = qmood_tokens[1]
        patterns_line = q_file.readline()
        pattern_inst_line = q_file.readline()
        graphsize_line = q_file.readline()
        graphsize_tokens = graphsize_line.split(' ')
        startgraphsize_dict[dir] = graphsize_tokens[1]
        q_file.close()

    o_file = open(result_dir + '/' + dir + '/run.log', 'r')
    o_lines = o_file.readlines()
    o_file.close()
    o_lines.reverse()

    for line in o_lines:
        if line[0:45] == 'Subpop 0 graph (|V|,|E|) of best individual: ':
            finalgraphsize_dict[dir] = line[45:]
        if line[0:39] == 'Subpop 0 tree size of best individual: ':
            treesize_dict[dir] = line[39:]
        if line[0:33] == 'Subpop 0 MTs in best individual: ':
            mtsize_dict[dir] = line[33:]
        if line[0:39] == 'Subpop 0 best fitness of run: Fitness: ':
            bestfit_dict[dir] = line[39:]
            break
    
    for pattern_file in os.listdir(output_dir):
        if pattern_file[0:7] == 'pattern':
            #print '\t\tPattern: ' + pattern_file
            num_instances += 1

            # Look for the line containing label="..."
            p_file = open(result_dir + '/' + dir + '/output/' + pattern_file, 'r')
            for line in p_file:
                if line[0:6] == 'label=':
                    # Extract the (quoted) name of the pattern instances
                    pattern_name = line[line.find('"')+1:len(line)-2]
                    if not model_dict[dir].has_key(pattern_name):
                        model_dict[dir][pattern_name] = [pattern_file]
                    else:
                        model_dict[dir][pattern_name].append(pattern_file)

            p_file.close()
        
    #print '\t' + str(num_instances) + ' candidate instances found'

print '<html><head><title>GP Results</title></head><body>'

items = model_dict.items()
items.sort()

for (m, p_dict) in items:
    print '<div><h2>' + m + '</h2>'

    if qmood_dict.has_key(m):
        print 'Initial QMOOD value: ' + qmood_dict[m] + '<br/>'
    if bestfit_dict.has_key(m):
        print 'Final fitness value (QMOOD + hasPatterns ? QMOOD*2.0 : 0.0): ' + bestfit_dict[m] + '<br/>'
    if treesize_dict.has_key(m):
        print 'GP tree size: ' + treesize_dict[m] + '<br/>'
    if mtsize_dict.has_key(m):
        print 'MT count: ' + mtsize_dict[m] + '<br/>'
    if startgraphsize_dict.has_key(m):
        print 'Initial graph size (|V|,|E|): ' + startgraphsize_dict[m] + '<br/>'
    if finalgraphsize_dict.has_key(m):
        print 'Final graph size (|V|,|E|): ' + finalgraphsize_dict[m] + '<br/>'
    if len(p_dict.keys()) == 0:
        print 'No patterns found.'
        continue

    print '<table border="1" width="450">'
    print '\t<tr><th>Pattern name</th><th>Qty</th><th></th></tr>'
    for p in p_dict:
        print '\t<tr><td>' + p + '</td><td>' + str(len(p_dict[p])) + '</td><td>'

        ndx = 0
        for pattern_file in p_dict[p]:
            print '<a href="' + m + '/output/' + pattern_file + '.png">' + str(ndx) + '</a> '
            ndx += 1

        print '</td></tr>'

    print '</table></div>'

print '</body></html>'
