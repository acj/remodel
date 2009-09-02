package ec.refactoring;

/**
 * This class is a container for values that are produced during the
 * evaluation of an individual in the population.
 */
public class StatDataPoint {
	// Individual metrics
	public float designSizeInClasses;	// Design size
	public float avgNumberOfAncestors;	//Abstraction
	public float dataAccessMetric;		// Encapsulation
	public float directClassCoupling;	// Coupling
	public float numberOfMethods;		// Complexity
	public float numberOfPolyMethods;	// Polymorphism
	public float classInterfaceSize;	// Messaging
	public float measureOfAggregation;	// Composition
	public float measureOfFunctionalAbstraction; // Inheritance
	public float numberOfHierarchies;	// Functionality
	
	// Quality aspects, computed in terms of the individual metrics
	public float reusability;
	public float flexibility;
	public float understandability;
	public float functionality;
	public float extendibility;
	public float effectiveness;
	
	// Composite quality value, the simple sum of the quality aspects
	public float qmood;
	
	// Number of design patterns detected in the individual
	public int dpCount;
}