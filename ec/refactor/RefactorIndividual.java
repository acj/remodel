package ec.refactor;

import ec.vector.*;

public class RefactorIndividual extends IntegerVectorIndividual {
    public String genotypeToStringForHumans()
    {
    String s = "";
    for( int i = 0 ; i < genome.length ; i++ )
        s = s + RefactorCPU.Instruction.values()[genome[i]] + "\n";
    return s;
    }
}