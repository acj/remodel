package ec.refactoring;

import java.util.ArrayList;
import java.util.Iterator;

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
			state.output.message("=== Begin pattern list ===");
			ArrayList<String> patternList = ((RefactorIndividual)best_of_run[x]).GetPatternList(); 
			Iterator<String> p_it = patternList.iterator();
			while (p_it.hasNext()) {
				state.output.message(p_it.next() + "\n");
			}
			state.output.message("=== End pattern list ===");
	        state.output.message("Subpop " + x + " best fitness of run: " + best_of_run[x].fitness.fitnessToStringForHumans());
	        //state.output.message("Genotype (graphviz):\n" + ((RefactorIndividual)best_of_run[x]).GetGraph().ToGraphViz());
	
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
