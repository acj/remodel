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

			// First, translate the graph into tuples in SQL
			int i;
			st.executeUpdate("DELETE FROM  tClass");
			st.executeUpdate("DELETE FROM  tInterface");
			st.executeUpdate("DELETE FROM  tOperation");
			st.executeUpdate("DELETE FROM  tAggregate");
			//st.executeUpdate("DELETE FROM  tAssociate");
			st.executeUpdate("DELETE FROM  tCall");
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
				} else if (e.getLabel() == AnnotatedEdge.Label.REFERENCE) {
					i = st.executeUpdate("INSERT INTO tReference(source,sink) VALUES('" + e.getSourceVertex().toString() + "','" + e.getSinkVertex().toString() + "')");
				}
			}
			
			// Factory Method
			ResultSet rs = st.executeQuery("SELECT DISTINCT * FROM tClass " +
					"JOIN tInherit tInherit1 ON tClass.name=tInherit1.sink " +
					"JOIN tOwn ON tInherit1.source=tOwn.source " +
					"JOIN tInstantiate ON tOwn.sink=tInstantiate.source " +
					"JOIN tInherit tInherit2 ON tInstantiate.sink=tInherit2.source " +
					"JOIN tClass tClass2 ON tInherit2.sink=tClass2.name " +
					"WHERE tInherit1.sink != tInherit2.sink AND " +
					"tInherit1.source != tInherit2.source");
			patternInstances.addAll(ReadPatternsFromResult(rs, "FactoryMethod"));

			// Prototype
			//PT[c,protAbs,protCon,protAbs_meth] = {classes[c]; classes[protAbs];
			// classes[protCon]; operations[protAbs_meth]; inherits[protCon,protAbs];
			// owns[protAbs,protAbs_meth]
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass protCon " +
					"JOIN tInherit ON protCon.name=tInherit.source " +
					"JOIN tOwn ON tInherit.sink=tOwn.source " +
					"JOIN tOperation ON tOwn.sink=tOperation.name");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Prototype"));

			// Adapter
	        //AD[client,adapter_meth,adaptee_meth,adapter,adaptee,target] =
	        // {opers[client]; opers[adapter_meth]; opers[adaptee_meth]; 
			// classes[adapter]; classes[adaptee]; classes[target];
			// owns[adapter,adapter_meth]; calls[client,adapter_meth]; 
			// inherits[adapter,target]; owns[adaptee,adaptee_meth]; 
			// calls[adapter_meth,adaptee_meth];}
			
			// tClass1 == adapter
			// tOperation1 == adapter_meth
			// tOperation2 == adaptee_meth
			// tCall1 == tClass1 calls tOperation1
			// tCall2 == tOperation1 calls tOperation2
			// tOwn1 == tClass1 owns tOperation1
			// tOwn2 == tClass2 owns tOperation2
			// tInherit1 == tClass1 inherits tClass2
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass tClass1 " +
					"JOIN tOwn tOwn1 ON tClass1.name=tOwn1.source " +
					"JOIN tOperation tOperation1 ON tOwn1.sink=tOperation1.name " +
					"JOIN tCall tCall1 ON (tClass1.name=tCall1.source AND tOperation1.name=tCall1.sink) " +
					"JOIN tClass tClass2 ON tClass1.name!=tClass2.name " +
					"JOIN tInherit tInherit1 ON (tInherit1.source=tClass1.name AND tInherit1.sink=tClass2.name) " +
					"JOIN tOperation tOperation2 ON tOperation1.name!=tOperation2.name " +
					"JOIN tCall tCall2 ON (tCall2.source=tOperation1.name AND tCall2.sink=tOperation2.name) " +
					"JOIN tOwn tOwn2 ON (tCall2.source=tOperation1.name AND tCall2.sink=tOperation2.name) ");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Adapter"));
			
			// Bridge
			// TODO: We can't do this one yet because we don't have aggregation
			// information in the annotated graph.
			//
			// rfAbs  -> refinedAbstraction
			// abs    -> abstraction
			// imp    -> implementer
			// conImp -> concreteImplementer
			// DP[rfAbs,abs,imp,conImp] = {inherits[rfAbs,abs]; uses[abs,imp]; inherits[conImp,imp]}
			//rs = st.executeQuery("SELECT DISTINCT * FROM ");
			//patternInstances.addAll(ReadPatternsFromResult(rs, "Bridge"));
			
			// Composite
			// TODO: Need aggregation
			// l  -> leaf
			// c  -> component
			// cp -> composite
			//DP[l,c,cp] = {inherits[l,c]; inherits[cp,c]; uses[cp,c]}
			
			
			// Proxy
			//
			// rs -> realSubject
			// s  -> subject
			// p  -> proxy
			//DP[rs,s,p] = {inherits[rs,s]; inherits[p,s]; uses[p,rs]}
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass rs " +
					"JOIN tClass s ON s.name!=rs.name " +
					"JOIN tInherit tInherit1 ON (tInherit1.source=rs.name AND tInherit1.sink=s.name) " +
					"JOIN tClass p ON (p.name!=s.name AND p.name!=rs.name) " +
					"JOIN tInherit tInherit2 ON (tInherit2.source=p.name AND tInherit2.sink=s.name) " +
					"JOIN tOperation op1 ON op1.name!=null " +
					"JOIN tOperation op2 ON op2.name!=op1.name " +
					"JOIN tOwn tOwn1 ON (tOwn1.source=p.name AND tOwn1.sink=op1.name) " +
					"JOIN tOwn tOwn2 ON (tOwn2.source=rs.name AND tOwn2.sink=op2.name) " +
					"JOIN tCall ON (tCall.source=op1.name AND tCall.source=op2.name)");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Proxy"));
			
			// Decorator
			//
			// cc -> concreteComponent
			// c  -> component
			// d  -> decorator
			// cd -> concreteDecorator
			// DP[cc,c,d,cd] = {inherits[cc,c]; inherits[d,c]; uses[d,c]; inherits[cd,d]}
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass cc " +
					"JOIN tClass c ON c.name!=cc.name " +
					"JOIN tInherit tInherit1 ON (tInherit1.source=cc.name AND tInherit1.sink=c.name) " +
					"JOIN tClass d ON (d.name!=c.name AND d.name!=cc.name) " +
					"JOIN tInherit tInherit2 ON (tInherit2.source=d.name AND tInherit2.sink=c.name) " +
					"JOIN tOperation d_op ON d_op.name!=null " +
					"JOIN tOperation c_op ON c_op.name!=d_op.name " +
					"JOIN tCall ON (tCall.source=c_op.name AND tCall.sink=d_op.name) " +
					"JOIN tClass cd ON (cd.name!=c.name AND cd.name!=cc.name AND cd.name!=d.name) " +
					"JOIN tInherit tInherit3 ON (tInherit3.source=cd.name AND tInherit3.sink=d.name)");
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
	        i = st.executeUpdate("CREATE TABLE tReference (source varchar(128), sink varchar(128));");
	        if (i == -1) { System.out.println("db error : reference"); }

	        st.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
