package ec.refactoring;

import java.io.*;
import java.util.*;

import ec.EvolutionState;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;
import ec.util.Output;

public class RefactorStatistics extends SimpleStatistics {
	private static final long serialVersionUID = 580669123049234310L;

	@Override
	public void finalStatistics(EvolutionState state, int result) {
        super.finalStatistics(state,result);
        
        // for now we just print the best fitness 
        
        state.output.println("\nBest Individual of Run:",Output.V_NO_GENERAL,statisticslog);
        for(int x=0;x<state.population.subpops.length;x++ )
        {
	        best_of_run[x].printIndividualForHumans(state,statisticslog,Output.V_NO_GENERAL);
	        
	        next_best_of_run[x].printIndividualForHumans(state,statisticslog,Output.V_NO_GENERAL);
			
	        // Best individual
	        state.output.message("Subpop " + x + " best fitness of run: " +
	        		best_of_run[x].fitness.fitnessToStringForHumans());
	        state.output.message("Subpop " + x + " tree size of best individual: " +
	        		((RefactorIndividual)best_of_run[x]).GetNodeCount());
	        state.output.message("Subpop " + x + " graph (|V|,|E|) of best individual: (" +
	        		((RefactorIndividual)best_of_run[x]).GetGraph().getSize() + "," +
	        		((RefactorIndividual)best_of_run[x]).GetGraph().edgeSet().size());
	        // Next-best individual
	        state.output.message("Subpop " + x + " second-best fitness of run: " +
	        		next_best_of_run[x].fitness.fitnessToStringForHumans());
	        state.output.message("Subpop " + x + " tree size of second-best individual: " +
	        		((RefactorIndividual)next_best_of_run[x]).GetNodeCount());
	        state.output.message("Subpop " + x + " graph (|V|,|E|) of best individual: (" +
	        		((RefactorIndividual)next_best_of_run[x]).GetGraph().getSize() + "," +
	        		((RefactorIndividual)next_best_of_run[x]).GetGraph().edgeSet().size());
	
	        ArrayList<String> patternList = ((RefactorIndividual)best_of_run[x]).GetPatternList();
		        
			try {
			    BufferedWriter out = new BufferedWriter(new FileWriter("output/patterns.end"));
				 
				Iterator<String> p_it = patternList.iterator();
			    while (p_it.hasNext()) {
			    	out.write(p_it.next() + "\n");
			    }
			    out.flush();
			    out.close();
			    
			    //out = new BufferedWriter(new FileWriter("output/graphfinal.facts"));
			    //out.write(((RefactorIndividual)best_of_run[x]).GetGraph().ToFacts());
			    //out.flush();
			    //out.close();
			    
			    out = new BufferedWriter(new FileWriter("output/graphfinal.dot"));
			    out.write(((RefactorIndividual)best_of_run[x]).GetGraph().ToGraphViz("Final"));
			    out.flush();
			    out.close();
			    
			    // TODO: Print all patterns, not just the first
		        if (patternList.size() > 0) {
        			for (int ndx=0; ndx<patternList.size(); ++ndx) {
						AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> graph =
							((RefactorIndividual)best_of_run[x]).GetGraph().GetPatternSubgraph(patternList.get(ndx), false);
						out = new BufferedWriter(new FileWriter("output/pattern." + ndx + ".dot"));
						out.write(graph.ToGraphViz(patternList.get(ndx).split(" ")[0]));
						out.flush();
						out.close();
				    }
		        }
			} catch (IOException e) {
				System.err.println("Could not export pattern to graphviz!");
			}
	        System.out.println("New pattern instances in best individual: " + patternList.size());
	        // finally describe the winner if there is a description
	        if (state.evaluator.p_problem instanceof SimpleProblemForm)
	            ((SimpleProblemForm)(state.evaluator.p_problem.clone())).describe(best_of_run[x], state, x, 0, statisticslog,Output.V_NO_GENERAL);
        }
    }

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);
		//state.output.message("Genotype (graphviz):\n" + ((RefactorIndividual)best_of_run[0]).GetGraph().ToGraphViz());
	}
}
