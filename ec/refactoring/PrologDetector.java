package ec.refactoring;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import ubc.cs.JLog.Foundation.jPrologAPI;

public class PrologDetector implements PatternDetector {
	private String prologPredicatesQueries;
	private jPrologAPI prolog;
	
	public ArrayList<String> DetectPatterns(
			AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		Iterator<AnnotatedVertex> it_v = g.vertexSet().iterator();
		StringBuilder prologFacts = new StringBuilder();
		
		// Setup the fact database
		prologFacts.append("cls(''). interface(''). operation('').");
		while (it_v.hasNext()) {
			AnnotatedVertex v = (AnnotatedVertex)it_v.next();
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				prologFacts.append("cls(" + v.toString() + ").");
			} else if (v.getType() == AnnotatedVertex.VertexType.INTERFACE) {
				prologFacts.append("interface(" + v.toString() + ").");
			} else if (v.getType() == AnnotatedVertex.VertexType.OPERATION) {
				prologFacts.append("operation(" + v.toString() + ").");
			}
		}
		// Ensure that all of the relations exist, even if they are empty
		prologFacts.append("aggregate('','').associate('','')." +
				"fcall('','').compose('','').implement('','')." +
				"inherit('','').instantiate('','').own('','')." +
				"reference('','').");
		Iterator<AnnotatedEdge> it_e = g.edgeSet().iterator();
		while (it_e.hasNext()) {
			AnnotatedEdge e = (AnnotatedEdge)it_e.next();
			if (e.getLabel() == AnnotatedEdge.Label.AGGREGATE) {
				prologFacts.append("aggregate('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "').");
			} else if (e.getLabel() == AnnotatedEdge.Label.ASSOCIATE) {
				prologFacts.append("associate('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "').");
			} else if (e.getLabel() == AnnotatedEdge.Label.CALL) {
				prologFacts.append("fcall('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "').");
			} else if (e.getLabel() == AnnotatedEdge.Label.COMPOSE) {
				prologFacts.append("compose('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "').");
			} else if (e.getLabel() == AnnotatedEdge.Label.IMPLEMENT) {
				prologFacts.append("implement('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "').");
			} else if (e.getLabel() == AnnotatedEdge.Label.INHERIT) {
				prologFacts.append("inherit('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "').");
			} else if (e.getLabel() == AnnotatedEdge.Label.INSTANTIATE) {
				prologFacts.append("instantiate('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "').");
			} else if (e.getLabel() == AnnotatedEdge.Label.OWN) {
				prologFacts.append("own('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "').");
			}
		}
		
		// Start looking for patterns
		try {
			String prologInput = prologFacts.toString() + "\n" + prologPredicatesQueries;
			ByteArrayInputStream bs = new ByteArrayInputStream(prologInput.getBytes());
			prolog = new jPrologAPI(bs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> detectedPatterns = new ArrayList<String>();		
		Hashtable varz;
		
		// Abstract Factory
		varz = prolog.query("abstract_factory(A,B,C,D,E).");
		while (varz != null) {
			detectedPatterns.add(varz.get("A") + " " + varz.get("B") +
					" " + varz.get("C") + " " + varz.get("D") + " " +
					varz.get("E"));
			varz = prolog.retry();
		}
		
		// Adapter
		varz = prolog.query("adapter(A,B,C,D).");
		while (varz != null) {
			detectedPatterns.add(varz.get("A") + " " + varz.get("B") +
					" " + varz.get("C") + " " + varz.get("D"));
			varz = prolog.retry();
		}
		
		// Bridge
		varz = prolog.query("bridge(A,B,C,D).");
		while (varz != null) {
			detectedPatterns.add(varz.get("A") + " " + varz.get("B") +
					" " + varz.get("C") + " " + varz.get("D"));
			varz = prolog.retry();
		}
		
		// Composite
		varz = prolog.query("composite(A,B,C,D).");
		while (varz != null) {
			detectedPatterns.add(varz.get("A") + " " + varz.get("B") +
					" " + varz.get("C") + " " + varz.get("D"));
			varz = prolog.retry();
		}
		
		// Decorator
		varz = prolog.query("decorator(A,B,C,D).");
		while (varz != null) {
			detectedPatterns.add(varz.get("A") + " " + varz.get("B") +
					" " + varz.get("C") + " " + varz.get("D"));
			varz = prolog.retry();
		}
		
		// Prototype
		varz = prolog.query("prototype(A,B,C,D).");
		while (varz != null) {
			detectedPatterns.add(varz.get("A") + " " + varz.get("B") +
					" " + varz.get("C") + " " + varz.get("D"));
			varz = prolog.retry();
		}
		
		// Proxy
		varz = prolog.query("proxy(A,B,C).");
		while (varz != null) {
			detectedPatterns.add(varz.get("A") + " " + varz.get("B") +
					" " + varz.get("C"));
			varz = prolog.retry();
		}
		
		prolog.stop();
		
		return detectedPatterns;
	}

	public void Setup() {
		// What follows is a set of predicates that look for specific
		// Gamma design patterns.  They are taken more-or-less directly
		// from Birkner's MS thesis and are translated from QL.
		
		// Abstract Factory
		prologPredicatesQueries = 
			"abstract_factory(AFact,CFact,AProd,CProd,Client) :-" +
				"cls(AFact), cls(CFact), cls(AProd), cls(CProd), " +
				"cls(Client), inherit(CFact,AFact), inherit(CProd,AProd), " +
				"instantiate(CFact,CProd), fcall(Client,CProd), AFact \\= CFact, " +
				"AProd \\= CProd, AFact \\= Client, CFact \\= Client, " +
				"AProd \\= Client, CProd \\= Client, AFact \\= AProd, AProd \\= CFact.\n";
		
		// Adapter
		prologPredicatesQueries +=
			"adapter(Client, Adapter, Adaptee, Target) :-" +
				"cls(Client), cls(Adapter), cls(Adaptee), cls(Target), " +
				"fcall(Client, Target), inherit(Adapter,Target), " +
				"fcall(Adapter, Adaptee), Client \\= Adapter, " +
				"Client \\= Adaptee, Client \\= Target, " +
				"Adapter \\= Adaptee, Adapter \\= Target, " +
				"Adaptee \\= Target.\n";
		
		// Bridge
		prologPredicatesQueries +=
			"bridge(Abstraction, RefAbstraction, Implementor, ConcImplementor) :-" +
				"inherit(RefAbstraction, Abstraction), " +
				"inherit(ConcImplementor, Implementor), " +
				"aggregate(Abstraction, Implementor), " +
				"Abstraction \\= RefAbstraction, " +
				"Abstraction \\= Implementor, " +
				"Abstraction \\= ConcImplementor, " +
				"RefAbstraction \\= Implementor, " +
				"RefAbstraction \\= ConcImplementor, " +
				"Implementor \\= ConcImplementor.\n";
		
		// Composite
		prologPredicatesQueries +=
			"composite(Client, Component, Leaf, Composite) :-" +
				"fcall(Client,Component), inherit(Leaf, Component), " +
				"inherit(Composite, Component), " +
				"aggregate(Composite, Component), " +
				"Client \\= Component, " +
				"Client \\= Leaf, " +
				"Client \\= Composite, " +
				"Component \\= Leaf, " +
				"Component \\= Composite, " +
				"Leaf \\= Composite.\n";
		
		// Decorator
		prologPredicatesQueries +=
			"decorator(Component, ConcComponent, Decorator, ConcDecorator) :-" +
				"inherit(ConcComponent, Component), " +
				"inherit(Decorator, Component), " +
				"inherit(ConcDecorator, Decorator)," +
				"Component \\= ConcComponent, " +
				"Component \\= Decorator, " +
				"Component \\= ConcDecorator, " +
				"ConcComponent \\= Decorator, " +
				"ConcComponent \\= ConcDecorator," +
				"Decorator \\= ConcDecorator.\n";
		
		// Prototype
		prologPredicatesQueries += 
			"prototype(ProtAbs, ProtCon, InstClass, Oper) :-" +
				"cls(ProtAbs), cls(ProtCon), cls(InstClass), " +
				"inherit(ProtCon, ProtAbs), operation(Oper), " +
				"own(ProtAbs,Oper), instantiate(InstClass, ProtAbs), " +
				"ProtAbs \\= ProtCon, ProtAbs \\= InstClass, " +
				"ProtCon \\= InstClass.\n";
		
		// Proxy
		prologPredicatesQueries +=
			"proxy(RealSubject, Subject, Proxy) :-" +
				"inherit(RealSubject, Subject), " +
				"fcall(Proxy, RealSubject), " +
				"RealSubject \\= Subject, " +
				"RealSubject \\= Proxy, " +
				"Subject \\= Proxy.\n";
	}
}
