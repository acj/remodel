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
			
			// Abstract Factory
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
			rs = st.executeQuery("SELECT DISTINCT * FROM tClass tClass1 " +
					"JOIN tOwn tOwn1 ON tClass1.name=tOwn1.source " +
					"JOIN tOperation tOperation1 ON tOwn1.sink=tOperation1.name " +
					"JOIN tCall tCall1 ON tOperation1.name=tCall1.sink " +
					"JOIN tInherit tInherit1 ON (tInherit1.source=tClass1.name AND tInherit1.sink=tOperation1.name) " +
					"JOIN tCall tCall2 ON tCall2.source=tOperation1.name " +
					"JOIN tOwn tOwn2 ON tCall2.sink!=tOperation1.name ");
			patternInstances.addAll(ReadPatternsFromResult(rs, "Adapter"));
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
