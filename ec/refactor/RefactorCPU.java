package ec.refactor;

import java.util.*;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class RefactorCPU {
	private Stack<Integer> stackS;
	private Stack<Integer> stackT;
	private int regAx;
	private int regBx;
	private int instPtr;
	private int instCount;
	private int graphSrcPtr;	// Pointers to vertices in our
	private int graphSnkPtr;	// graph representation of the source code.
	private int[] genome;
	
	public enum Instruction {
		NOPA,
		NOPB,
		STORE0,
		PUSHS,
		PUSHT,
		POPS,
		POPT,
		INCR,
		DECR,
		ADD,
		SUB,
		MULT,
		DIV,
		IFEQU,
		IFLESS,
		IFGTR,
		IFNAMESEQU,
		IFNAMESNEQU,
		SELSRCVTX,
		SELSNKVTX,
		NEWCLASS,
		NEWOPER,
		MOVEOPER,
		ADDINHERIT,
		ADDIMPLEMENT,
		ADDOWN,
		INHERIT,
		OWN,
		IMPLEMENT,
		ASSOCIATE
	}
	
	private final static int MAX_INST = 256; // Max. instructions executed per run
	
	public RefactorCPU() {
		Reset();
	}
	/**
	 * Resets the CPU to its default state with an empty genome.
	 */
	public void Reset() {
		stackS = new Stack<Integer>();
		stackT = new Stack<Integer>();
		regAx = 0;
		regBx = 0;
		instPtr = 0;
		instCount = 0;
		graphSrcPtr = 0;
		graphSnkPtr = 0;
		genome = null;
	}
	public void SetGenome(int[] g) {
		genome = g;
	}
	/**
	 * A private helper function that retrieves the numerical value of the next
	 * instruction to be executed.
	 * @return Integer value of next instruction.
	 */
	private int GetNextInstruction() {
		if (instPtr == genome.length - 1) {
			return -1;
		} else {
			return genome[instPtr+1];
		}
	}
	/** 
	 * A private helper function that moves the instruction pointer to the
	 * next non-conditional instruction.  This is used to skip a series of
	 * if-* instructions when the first such statement evaluates to false.
	 */
	private void FalseIfHandler() {
		if (genome[instPtr+1] >= 13 && genome[instPtr+1] <= 17) {
			// Skip the series of if-* instructions that follow
			instPtr = GetNextNonConditionalInstruction();
		} else {
			// Just skip the next instruction
			instPtr += 2;
		}
	}
	/**
	 * A private helper function that returns the index within the genome of the 
	 * next non-conditional instruction.  The current instruction (i.e., the
	 * position stored in the instruction pointer) is not considered.  This
	 * is used to skip a series of subsequent if-* instructions when the first
	 * one evaluates to false.
	 * @return Position of next non-conditional instruction in the genome.
	 */
	private int GetNextNonConditionalInstruction() {
		// FIXME: Ugly!
		int nextInstPos = instPtr + 1;
		int nextInst = genome[nextInstPos];
		while (nextInst >= 13 && nextInst <= 17) {
			++nextInstPos;
			nextInst = genome[nextInstPos]; 
		}
		return nextInstPos;
	}

	public void SimulateGenome(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		while (instCount < MAX_INST) {
			switch (genome[instPtr]) {
				// nopA
				case 0: 
				{
					++instPtr;
					break;
				}
				// nopB
				case 1:
				{
					++instPtr;
					break;
				}
				// store-0
				// Default behavior is to write 0 into register Ax.  If followed
				// by a nop-B, then 0 is written to register Bx instead.
				case 2:
				{
					if (GetNextInstruction() == 1) {
						regBx = 0;
					} else {
						regAx = 0;
					}
					++instPtr;
					break;
				}
				// push-S
				// Default behavior is to push register Ax's value onto the S stack.
				// If followed by a nop-B, then register Bx's value is pushed instead.
				case 3:
				{
					if (GetNextInstruction() == 1) {
						stackS.push(regBx);
					} else {
						stackS.push(regAx);
					}
					++instPtr;
					break;
				}
				// push-T
				// Default behavior is to push register Ax's value onto the T stack.
				// If followed by a nop-B, then register Bx's value is pushed instead.
				case 4:
				{
					if (GetNextInstruction() == 1) {
						stackT.push(regBx);
					} else {
						stackT.push(regAx);
					}
					++instPtr;
					break;
				}
				// pop-S
				// Default behavior is to pop a value from the S stack and place it
				// in register Ax.  If followed by a nop-B, then the value is placed
				// in register Bx instead.
				case 5:
				{
					if (stackS.empty()) { break; }
					if (GetNextInstruction() == 1) {
						regBx = stackS.pop();
					} else {
						regAx = stackS.pop();
					}
					++instPtr;
					break;
				}
				// pop-T
				// Default behavior is to pop a value from the T stack and place it
				// in register Ax.  If followed by a nop-B, then the value is placed
				// in register Bx instead.
				case 6:
				{
					if (stackT.empty()) { break; }
					if (GetNextInstruction() == 1) {
						regBx = stackT.pop();
					} else {
						regAx = stackT.pop();
					}
					++instPtr;
				}
				// incr
				// Default behavior is to increment the value in register Ax by 1.  If
				// followed by a nop-B, then the value in register Bx is incremented
				// by 1 instead.
				case 7:
				{
					if (GetNextInstruction() == 1) {
						regBx += 1;
					} else {
						regAx += 1;
					}
					++instPtr;
					break;
				}
				// decr
				// Default behavior is to decrement the value in register Ax by 1.  If
				// followed by a nop-B, then the value in register Bx is decremented
				// by 1 instead.
				case 8:
				{
					if (GetNextInstruction() == 1) {
						regBx -= 1;
					} else {
						regAx -= 1;
					}
					++instPtr;
					break;
				}
				// add
				// Default behavior is to add the value stored in register Ax to the
				// value stored in register Bx and store the sum in register Ax.  If
				// followed by a nop-B, then the sum is stored in register Bx. 
				case 9:
				{
					int sum = regAx + regBx;
					if (GetNextInstruction() == 1) {
						regBx = sum;
					} else {
						regAx = sum;
					}
					++instPtr;
					break;
				}
				// sub
				// Default behavior is to substract the value stored in register Ax
				// from the value stored in register Bx and store the difference in
				// register Ax.  If followed by a nop-B, then the difference is stored
				// in register Bx.
				case 10:
				{
					int diff = regAx - regBx;
					if (GetNextInstruction() == 1) {
						regBx = diff;
					} else {
						regAx = diff;
					}
					++instPtr;
					break;
				}
				// mult
				// Default behavior is to multiply the value stored in register Ax by
				// the value stored in register Bx and store the product in register
				// Ax.  If followed by a nop-B, then the difference is stored in
				// register Bx.
				case 11:
				{
					int product = regAx * regBx;
					if (GetNextInstruction() == 1) {
						regBx = product;
					} else {
						regAx = product;
					}
					++instPtr;
					break;
				}
				// div
				// Divide the value stored in register Ax by
				// the value stored in register Bx and store the (integer) quotient
				// in register Ax.  The remainder is stored in register Bx.  This
				// instruction is treated as a true nop if register Bx contains 0.
				case 12:
				{
					if (regBx == 0) { break; }
					int quotient = regAx / regBx;
					int remainder = regAx % regBx;
					regAx = quotient;
					regBx = remainder;
					++instPtr;
					break;
				}
				// if-equ
				// If the values stored in registers Ax and Bx are equal, then execute
				// the next instruction.  Otherwise, skip the next instruction.
				case 13:
				{
					if (regAx == regBx) {
						break;
					} else {
						// Skip to next non-conditional instruction
						FalseIfHandler();
					}
					break;
				}
				// if-less
				// If the value stored in register Ax is less than the value stored
				// in register Bx, then execute the next instruction.  Otherwise,
				// skip the next instruction.
				case 14:
				{
					if (regAx < regBx) {
						break;
					} else {
						// Skip to next non-conditional instruction
						FalseIfHandler();
					}
					++instPtr;
					break;
				}
				// if-gtr
				// If the value stored in register Ax is greater than the value stored
				// in register Bx, then execute the next instruction.  Otherwise,
				// skip the next instruction.
				case 15:
				{
					if (regAx > regBx) {
						break;
					} else {
						// Skip to next non-conditional instruction
						FalseIfHandler();
					}
					++instPtr;
					break;
				}
				// if-names-equ
				// If the name of the source and sink graph entities (classes or
				// operations) are equal, then execute the next instruction.
				// Otherwise, skip the next instruction.
				case 16:
				{
					if (g.vertexSet().toArray()[graphSrcPtr] == g.vertexSet().toArray()[graphSnkPtr]) {
						break;
					} else {
						// Skip to the next non-conditional instruction
						FalseIfHandler();
					}
					++instPtr;
					break;
				}
				// if-names-nequ
				case 17:
				{					
					if (g.vertexSet().toArray()[graphSrcPtr] != g.vertexSet().toArray()[graphSnkPtr]) {
						break;
					} else {
						// Skip to the next non-conditional instruction
						FalseIfHandler();
					}
					++instPtr;
					break;
				}
				// sel-src-vtx
				// Changes the graph source pointer to the value stored in register
				// Ax.  If followed by a nop-B, then the value in register Bx is
				// used instead.  Since this value is interpreted as an index,
				// any negative values will cause this instruction to behave
				// as a nop.
				case 18:
				{
					if (GetNextInstruction() == 1 && regBx >= 0) {
						graphSrcPtr = regBx % g.vertexSet().size();
					} else if (regAx >= 0) {
						graphSrcPtr = regAx % g.vertexSet().size();
					}
					++instPtr;
					break;
				}
				// sel-snk-vtx
				// Changes the graph sink pointer to the value stored in register
				// Ax.  If followed by a nop-B, then the value in register Bx is
				// used instead.  Since this value is interpreted as an index,
				// any negative values will cause this instruction to behave
				// as a nop.
				case 19:
				{
					if (GetNextInstruction() == 1 && regBx >= 0) {
						graphSnkPtr = regBx % g.vertexSet().size();
					} else if (regAx >= 0) {
						graphSnkPtr = regAx % g.vertexSet().size();
					}
					++instPtr;
					break;
				}
				// new-class
				// Adds a new class (vertex) to the graph and makes it the
				// currently selected sink class.  If followed by a nop-B,
				// then the new class will be the currently selected source
				// class.
				case 20:
				{
					// FIXME: Need to pull the class name from somewhere.
					final int new_index = g.getSize();
					g.addVertex(new SourceVertex("NewClass" + new_index, SourceVertex.VertexType.CLASS));
					if (GetNextInstruction() == 1) {
						graphSrcPtr = new_index;
					} else {
						graphSnkPtr = new_index;
					}
					break;
				}
				// new-oper
				// Adds a new operation and creates an ownership association
				// between the new operation and the currently selected source
				// class.  If followed by a nop-B, then an association is created
				// with the sink class instead.
				case 21:
				{
					// FIXME: Need to pull the operation name from somewhere.
					final int new_index = g.vertexSet().size();
					final String owning_class; 
					if (GetNextInstruction() == 1) {
						owning_class = g.getVertex(graphSnkPtr).toString();
					} else {
						owning_class = g.getVertex(graphSrcPtr).toString(); 
					}
					final String new_oper = owning_class + "__NewOper" + new_index;
					g.addVertex(new SourceVertex(new_oper, SourceVertex.VertexType.OPERATION));
					SourceEdge e = new SourceEdge(SourceEdge.Label.OWN);
					g.addEdge(g.getVertex(owning_class), g.getVertex(new_oper), e);
					break;
				}
				// move-oper (source class to sink class)
				// If the source vertex is an operation and the sink vertex
				// is a class, then move the operation to the class.
				case 22:
				{
					SourceVertex src = g.getVertex(graphSrcPtr);
					SourceVertex snk = g.getVertex(graphSnkPtr); 
					if (src.getType() == SourceVertex.VertexType.OPERATION &&
							snk.getType() == SourceVertex.VertexType.CLASS) {
						// Find the old operation-to-class ownership edge and
						// replace it with an edge to the new owning class.
						Set<SourceEdge> edges = g.edgesOf(src);
						Iterator<SourceEdge> it = edges.iterator();
						while (it.hasNext()) {
							SourceEdge edge = it.next();
							if (edge.getLabel() == SourceEdge.Label.OWN) {
								g.removeEdge(edge);
								g.addEdge(src, snk, new SourceEdge(SourceEdge.Label.OWN));
								break;
							}
						}
						break;
					} else {
						// Type mismatch - fall back to nop behavior.
						break;
					}
				}
				// add-inherit
				case 23:
				{
					SourceVertex src = g.getVertex(graphSrcPtr);
					SourceVertex snk = g.getVertex(graphSnkPtr);
					if (src.getType() == SourceVertex.VertexType.CLASS &&
							snk.getType() == SourceVertex.VertexType.CLASS) {
						g.addEdge(src, snk, new SourceEdge(SourceEdge.Label.INHERIT));
					}
					break;
				}
				// add-implement
				case 24:
				{
					SourceVertex src = g.getVertex(graphSrcPtr);
					SourceVertex snk = g.getVertex(graphSnkPtr);
					if (src.getType() == SourceVertex.VertexType.CLASS &&
							snk.getType() == SourceVertex.VertexType.CLASS) {
						g.addEdge(src, snk, new SourceEdge(SourceEdge.Label.IMPLEMENT));
					}
					break;
				}
				// add-own
				case 25:
				{
					SourceVertex src = g.getVertex(graphSrcPtr);
					SourceVertex snk = g.getVertex(graphSnkPtr);
					if (src.getType() == SourceVertex.VertexType.CLASS &&
							snk.getType() == SourceVertex.VertexType.OPERATION) {
						g.addEdge(src, snk, new SourceEdge(SourceEdge.Label.OWN));
					}
					break;
				}
				// add-associate
				case 26:
				{
					SourceVertex src = g.getVertex(graphSrcPtr);
					SourceVertex snk = g.getVertex(graphSnkPtr);
					if (src.getType() == SourceVertex.VertexType.CLASS &&
							snk.getType() == SourceVertex.VertexType.CLASS) {
						g.addEdge(src, snk, new SourceEdge(SourceEdge.Label.ASSOCIATE));
					}
					break;
				}
				default:
				{
					System.err.println("Invalid instruction!");
					System.exit(1);
				}
			}
			++instCount;
		}
	}
}
