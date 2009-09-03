package ec.refactoring;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SQLDetector implements PatternDetector {

	private Connection conn;
	
	public ArrayList<String> DetectPatterns(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		assert conn != null;
		ArrayList<String> patternInstances = new ArrayList<String>();
		try {
			Statement st = conn.createStatement();

			// Delete indices
			int i;
			st.executeUpdate("DROP INDEX iClasses IF EXISTS");
			st.executeUpdate("DROP INDEX iOperation IF EXISTS");
			st.executeUpdate("DROP INDEX iInterface IF EXISTS");
			st.executeUpdate("DROP INDEX iCall IF EXISTS");
			st.executeUpdate("DROP INDEX iInherit IF EXISTS");
			st.executeUpdate("DROP INDEX iAggregate IF EXISTS");
			st.executeUpdate("DROP INDEX iAssociate IF EXISTS");
			st.executeUpdate("DROP INDEX iCompose IF EXISTS");
			st.executeUpdate("DROP INDEX iInstantiate IF EXISTS");
			st.executeUpdate("DROP INDEX iOwn IF EXISTS");
			st.executeUpdate("DROP INDEX iImplement IF EXISTS");
			//st.executeUpdate("DROP INDEX iReference IF EXISTS");
			// Delete rows
			st.executeUpdate("DELETE FROM  tClass");
			st.executeUpdate("DELETE FROM  tInterface");
			st.executeUpdate("DELETE FROM  tOperation");
			st.executeUpdate("DELETE FROM  tAggregate");
			st.executeUpdate("DELETE FROM  tAssociate");
			st.executeUpdate("DELETE FROM  tCall");
			st.executeUpdate("DELETE FROM  tCompose");
			st.executeUpdate("DELETE FROM  tImplement");
			st.executeUpdate("DELETE FROM  tInherit");
			st.executeUpdate("DELETE FROM  tInstantiate");
			st.executeUpdate("DELETE FROM  tOwn");
			//st.executeUpdate("DELETE FROM  tReference");
			Iterator<AnnotatedVertex> it_v = g.vertexSet().iterator();
			while (it_v.hasNext()) {
				AnnotatedVertex v = (AnnotatedVertex)it_v.next();
				if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
					i = st.executeUpdate("INSERT INTO tClass(name) VALUES('" + v.toString() + "')");
				//} else if (v.getType() == AnnotatedVertex.VertexType.FIELD) {
				//	i = st.executeUpdate("INSERT INTO field(name) VALUES(\"" + v.toString() + "\""");
				} else if (v.getType() == AnnotatedVertex.VertexType.INTERFACE) {
					i = st.executeUpdate("INSERT INTO tInterface(name) VALUES('" + v.toString() + "')");
				} else if (v.getType() == AnnotatedVertex.VertexType.OPERATION) {
					i = st.executeUpdate("INSERT INTO tOperation(name) VALUES('" + v.toString() + "')");
				}
			}
			Iterator<AnnotatedEdge> it_e = g.edgeSet().iterator();
			while (it_e.hasNext()) {
				AnnotatedEdge e = (AnnotatedEdge)it_e.next();
				if (e.getLabel() == AnnotatedEdge.Label.AGGREGATE) {
					i = st.executeUpdate("INSERT INTO tAggregate(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				} else if (e.getLabel() == AnnotatedEdge.Label.ASSOCIATE) {
					i = st.executeUpdate("INSERT INTO tAssociate(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				} else if (e.getLabel() == AnnotatedEdge.Label.CALL) {
					i = st.executeUpdate("INSERT INTO tCall(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				} else if (e.getLabel() == AnnotatedEdge.Label.COMPOSE) {
					i = st.executeUpdate("INSERT INTO tCompose(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				} else if (e.getLabel() == AnnotatedEdge.Label.IMPLEMENT) {
					i = st.executeUpdate("INSERT INTO tImplement(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				} else if (e.getLabel() == AnnotatedEdge.Label.INHERIT) {
					i = st.executeUpdate("INSERT INTO tInherit(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				} else if (e.getLabel() == AnnotatedEdge.Label.INSTANTIATE) {
					i = st.executeUpdate("INSERT INTO tInstantiate(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				} else if (e.getLabel() == AnnotatedEdge.Label.OWN) {
					i = st.executeUpdate("INSERT INTO tOwn(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				}// else if (e.getLabel() == AnnotatedEdge.Label.REFERENCE) {
				//	i = st.executeUpdate("INSERT INTO tReference(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				//}
			}
			
	        st.executeUpdate("CREATE INDEX iClasses ON tClass(name)");
	        st.executeUpdate("CREATE INDEX iOperation ON tOperation(name)");
	        st.executeUpdate("CREATE INDEX iInterface ON tInterface(name)");
	        st.executeUpdate("CREATE INDEX iCall ON tCall(source,sink)");
	        st.executeUpdate("CREATE INDEX iInherit ON tInherit(source,sink)");
	        st.executeUpdate("CREATE INDEX iAggregate ON tAggregate(source,sink)");
	        st.executeUpdate("CREATE INDEX iAssociate ON tAssociate(source,sink)");
	        st.executeUpdate("CREATE INDEX iCompose ON tCompose(source,sink)");
	        st.executeUpdate("CREATE INDEX iInstantiate ON tInstantiate(source,sink)");
	        st.executeUpdate("CREATE INDEX iOwn ON tOwn(source,sink)");
	        st.executeUpdate("CREATE INDEX iImplement ON tImplement(source,sink)");
	        //st.executeUpdate("CREATE INDEX iReference ON tReference(source,sink)");

	        ResultSet rs;
	        /*
	        // Query for checking the execution plan.  Used for profiling and
	        // optimizing queries.
	        rs = st.executeQuery("EXPLAIN PLAN FOR SELECT [...]");
	        
	        ResultSetMetaData meta;
			try {
				meta = rs.getMetaData();
		        int               colmax = meta.getColumnCount();
		        i = 0;
		        Object            o = null;
		        for (; rs.next(); ) {
		            for (i = 0; i < colmax; ++i) {
		                o = rs.getObject(i + 1);    // In SQL the first column is indexed
		                if (o != null) {
		                	System.out.print("[" + meta.getTableName(i+1) + "." + meta.getColumnName(i + 1) + "]" + o.toString() + " ");
		                }
		            }
		            System.out.println("\n");
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			}
			*/
	        
			// Factory Method
			//
			// Original QL:
			//	DP[c,conC,conP,p] = {inherits[conC,c]; uses[conC,conP]; inherits[conP,p]}
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass cAbsProd, tClass cAbsFact, tClass cConProd, tClass cConFact " +
					"JOIN tInherit tInheritFact ON tInheritFact.sink=cAbsFact.name " +
					"JOIN tInstantiate ON tInstantiate.source=tInheritFact.source " +
					"JOIN tInherit tInheritProd ON tInheritProd.source=tInstantiate.sink " +
					"WHERE tInheritFact.sink != tInheritProd.sink AND " +
					"tInheritFact.source != tInheritProd.source AND " +
					"tInheritFact.source = cConFact.name AND " +
					"tInstantiate.sink = cConProd.name AND " +
					"tInheritProd.sink = cAbsProd.name");
			patternInstances.addAll(ReadPatternsFromResult(rs, "FactoryMethod"));

			// Prototype
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass protAbs, tClass protCon, tClass cInstClass " +
					"JOIN tInherit ON tInherit.source=protCon.name " +
					"JOIN tOwn ON tOwn.source=tInherit.sink " +
					"JOIN tOperation ON tOperation.name=tOwn.sink " +
					"JOIN tInstantiate ON (tInstantiate.source=cInstClass.name AND tInstantiate.sink=protAbs.name) " +
					"WHERE tInherit.sink = protAbs.name");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Prototype"));

			// Adapter
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass cClient, tClass cAdapter, tClass cAdaptee, tClass cTarget " +
					"JOIN tCall callClientTarget ON (cClient.name=callClientTarget.source AND cTarget.name=callClientTarget.sink) " +
					"JOIN tInherit inheritAdapterTarget ON (inheritAdapterTarget.sink=cTarget.name AND inheritAdapterTarget.source=cAdapter.name) " +
					"JOIN tCall callAdapterAdaptee ON (callAdapterAdaptee.source=cAdapter.name AND callAdapterAdaptee.sink=cAdaptee.name)" +
					"WHERE cAdapter.name != cClient.name AND cAdaptee.name != cClient.name AND cAdaptee.name != cAdapter.name AND cTarget.name != cClient.name AND cTarget.name != cAdapter.name AND cTarget.name != cAdaptee.name");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Adapter"));
			
			// Bridge
			// information in the annotated graph.
			//
			// rfAbs  -> refinedAbstraction
			// abs    -> abstraction
			// imp    -> implementer
			// conImp -> concreteImplementer
			// DP[rfAbs,abs,imp,conImp] = {inherits[rfAbs,abs]; uses[abs,imp]; inherits[conImp,imp]}
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass cAbstraction, tClass cRefinedAbstraction, tClass cImplementor, tClass cConcreteImplementor " +
					"JOIN tInherit inheritRefinedAbstractionAbstraction ON (inheritRefinedAbstractionAbstraction.source=cRefinedAbstraction.name AND inheritRefinedAbstractionAbstraction.sink=cAbstraction.name) " +
					"JOIN tInherit inheritConcreteImplementor ON (inheritConcreteImplementor.source=cConcreteImplementor.name AND inheritConcreteImplementor.sink=cImplementor.name) " +
					"JOIN tAggregate aggregateAbstractionImplementor ON (aggregateAbstractionImplementor.source=cAbstraction.name AND aggregateAbstractionImplementor.sink=cImplementor.name) " +
					"WHERE cRefinedAbstraction.name != cAbstraction.name AND cImplementor.name != cRefinedAbstraction.name AND cConcreteImplementor.name != cAbstraction.name ");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Bridge"));
			
			// Composite
			// l  -> leaf
			// c  -> component
			// cp -> composite
			//DP[l,c,cp] = {inherits[l,c]; inherits[cp,c]; uses[cp,c]}
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass cClient, tClass cComponent, tClass cLeaf, tClass cComposite " +
					"JOIN tCall callClientComponent ON (callClientComponent.source=cClient.name AND callClientComponent.sink=cComponent.name) " +
					"JOIN tInherit inheritLeafComponent ON (inheritLeafComponent.source=cLeaf.name AND inheritLeafComponent.sink=cComponent.name) " +
					"JOIN tInherit inheritCompositeComponent ON (inheritCompositeComponent.source=cComposite.name AND inheritCompositeComponent.sink=cComponent.name) " +
					"JOIN tAggregate aggregateCompositeComponent ON (aggregateCompositeComponent.source=cComposite.name AND aggregateCompositeComponent.sink=cComponent.name)" +
					"WHERE cComponent.name != cClient.name AND cLeaf.name != cClient.name AND cLeaf.name != cComponent.name AND cComposite.name != cClient.name AND cComposite.name != cLeaf.name AND cComposite.name != cComponent.name");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Composite"));
			
			// Proxy
			//
			// rs -> realSubject
			// s  -> subject
			// p  -> proxy
			//DP[rs,s,p] = {inherits[rs,s]; inherits[p,s]; uses[p,rs]}
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass cRealSubject, tClass cSubject, tClass cProxy " +
					"JOIN tInherit inheritRealSubjectSubject ON (inheritRealSubjectSubject.source=cRealSubject.name AND inheritRealSubjectSubject.sink=cSubject.name) " +
					"JOIN tCall callProxyRealSubject ON (callProxyRealSubject.source=cProxy.name AND callProxyRealSubject.sink=cRealSubject.name)" +
					"WHERE cSubject.name != cRealSubject.name AND cProxy.name != cRealSubject.name AND cSubject.name != cRealSubject.name");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Proxy"));
			
			// Decorator
			//
			// cc -> concreteComponent
			// c  -> component
			// d  -> decorator
			// cd -> concreteDecorator
			// DP[cc,c,d,cd] = {inherits[cc,c]; inherits[d,c]; uses[d,c]; inherits[cd,d]}
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass cComponent, tClass cConcreteComponent, tClass cDecorator, tClass cConcreteDecorator " +
					"JOIN tInherit inheritConcreteComponentComponent ON (inheritConcreteComponentComponent.source=cConcreteComponent.name AND inheritConcreteComponentComponent.sink=cComponent.name) " +
					"JOIN tInherit inheritDecoratorComponent ON (inheritDecoratorComponent.source=cDecorator.name AND inheritDecoratorComponent.sink=cComponent.name) " +
					"JOIN tInherit inheritConcreteDecoratorDecorator ON (inheritConcreteDecoratorDecorator.source=cConcreteDecorator.name AND inheritConcreteDecoratorDecorator.sink=cDecorator.name)" +
					"WHERE cConcreteComponent.name != cComponent.name AND cDecorator.name != cComponent.name AND cDecorator.name != cConcreteComponent.name AND cConcreteDecorator.name != cComponent.name AND cConcreteDecorator.name != cConcreteComponent.name AND cConcreteDecorator.name != cDecorator.name");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Decorator"));
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return patternInstances;
	}
	
	ArrayList<String> ReadPatternsFromResult(ResultSet rs, String patternName) {
		ArrayList<String> patternInstances = new ArrayList<String>();
        ResultSetMetaData meta;
		try {
			meta = rs.getMetaData();
	        int               colmax = meta.getColumnCount();
	        int i = 0;
	        Object            o = null;
	        for (; rs.next(); ) {
	        	String patternInstance = patternName + " ";
	            for (i = 0; i < colmax; ++i) {
	                o = rs.getObject(i + 1);    // In SQL the first column is indexed
	                if (o != null) {
	                	//System.out.print("[" + meta.getTableName(i+1) + "." + meta.getColumnName(i + 1) + "]" + o.toString() + " ");
	                	patternInstance += o.toString() + " ";
	                }
	            }
	            //System.out.println(" ");
	            patternInstances.add(patternInstance);
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return patternInstances;
	}

	public void Setup() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
			Statement st = conn.createStatement();    // statements

	        int i = st.executeUpdate("CREATE TABLE tClass (name varchar(128));");
	        if (i == -1) { System.out.println("db error : class"); }
	        i = st.executeUpdate("CREATE TABLE tOperation (name varchar(128));");
	        if (i == -1) { System.out.println("db error : operation"); }
	        i = st.executeUpdate("CREATE TABLE tInterface (name varchar(128));");
	        if (i == -1) { System.out.println("db error : interface"); }
	        i = st.executeUpdate("CREATE TABLE tCall (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : call"); }
	        i = st.executeUpdate("CREATE TABLE tAssociate (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : associate"); }
	        i = st.executeUpdate("CREATE TABLE tAggregate (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : aggregate"); }
	        i = st.executeUpdate("CREATE TABLE tCompose (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : compose"); }
	        i = st.executeUpdate("CREATE TABLE tInstantiate (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : instantiate"); }
	        i = st.executeUpdate("CREATE TABLE tInherit (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : inherit"); }
	        i = st.executeUpdate("CREATE TABLE tOwn (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : own"); }
	        i = st.executeUpdate("CREATE TABLE tImplement (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : implement"); }
	        //i = st.executeUpdate("CREATE TABLE tReference (source varchar(128), sink varchar(128));");
	        //if (i == -1) { System.out.println("db error : reference"); }
	        st.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
