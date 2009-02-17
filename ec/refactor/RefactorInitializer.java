package ec.refactor;
import ec.Initializer;
import ec.EvolutionState;
import ec.util.Parameter;
import ec.Population;

/* 
 * RefactorInitializer.java
 * 
 * Created: Mon Feb 16 2009
 * By: Adam Jensen
 */

/**
 * RefactorInitializer is a default Initializer which initializes a Population
 * by calling the Population's populate(...) method.  For most applications,
 * this should suffice.
 *
 * @author Adam Jensen
 * @version 1.0 
 */

public class RefactorInitializer extends Initializer
    {
    public void setup(final EvolutionState state, final Parameter base)
        { 
        }

    /** Creates, populates, and returns a new population by making a new
        population, calling setup(...) on it, and calling populate(...)
        on it, assuming an unthreaded environment (thread 0).
        Obviously, this is an expensive method.  It should only
        be called once typically in a run. */

    public Population initialPopulation(final EvolutionState state, int thread)
        {
        Population p = setupPopulation(state, thread); 
        p.populate(state, thread);
        return p;
        }
                
    public Population setupPopulation(final EvolutionState state, int thread)
        {
        Parameter base = new Parameter(P_POP);
        Population p = (Population) state.parameters.getInstanceForParameterEq(base,null,Population.class);  // Population.class is fine
        p.setup(state,base);
        return p;
        }
    }
