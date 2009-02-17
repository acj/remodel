package ec.refactor;
import ec.util.Parameter;
import ec.*;

/* 
 * RefactorDefaults.java
 * 
 * Created: Mon Feb 16 2009
 * By: Adam Jensen
 */

/**
 * @author Adam Jensen
 * @version 1.0 
 */

public final class RefactorDefaults implements DefaultsForm 
    {
    public static final String P_REFACTOR = "refactor";

    /** Returns the default base. */
    public static final Parameter base()
        {
        return new Parameter(P_REFACTOR);
        }
    }
