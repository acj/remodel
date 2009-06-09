package ec.refactoring;

import java.io.*;
import java.util.TreeMap;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ec.refactoring.AnnotatedEdge.Label;
import ec.refactoring.AnnotatedVertex.VertexType;
import ec.refactoring.AnnotatedVertex.Visibility;
import ec.refactoring.dom.wrappers.*;

public class XMIImport implements IModelImport {

	enum AssociationType {
		AGGREGATION,
		COMPOSITION,
		NONE
	}

    //
    // Constants
    //

    // feature ids

    /** Namespaces feature id (http://xml.org/sax/features/namespaces). */
    protected static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

    /** Validation feature id (http://xml.org/sax/features/validation). */
    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

    /** Schema validation feature id (http://apache.org/xml/features/validation/schema). */
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

    /** Schema full checking feature id (http://apache.org/xml/features/validation/schema-full-checking). */
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";
    
    /** Honour all schema locations feature id (http://apache.org/xml/features/honour-all-schemaLocations). */
    protected static final String HONOUR_ALL_SCHEMA_LOCATIONS_ID = "http://apache.org/xml/features/honour-all-schemaLocations";
    
    /** Validate schema annotations feature id (http://apache.org/xml/features/validate-annotations). */
    protected static final String VALIDATE_ANNOTATIONS_ID = "http://apache.org/xml/features/validate-annotations";
    
    /** Dynamic validation feature id (http://apache.org/xml/features/validation/dynamic). */
    protected static final String DYNAMIC_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/dynamic";
    
    /** XInclude feature id (http://apache.org/xml/features/xinclude). */
    protected static final String XINCLUDE_FEATURE_ID = "http://apache.org/xml/features/xinclude";
    
    /** XInclude fixup base URIs feature id (http://apache.org/xml/features/xinclude/fixup-base-uris). */
    protected static final String XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID = "http://apache.org/xml/features/xinclude/fixup-base-uris";
    
    /** XInclude fixup language feature id (http://apache.org/xml/features/xinclude/fixup-language). */
    protected static final String XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID = "http://apache.org/xml/features/xinclude/fixup-language";

    // default settings

    /** Default parser name (dom.wrappers.Xerces). */
    protected static final String DEFAULT_PARSER_NAME = "ec.refactoring.dom.wrappers.Xerces";
    //protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.DOMParser";

    /** Default element name (*). */
    protected static final String DEFAULT_ELEMENT_NAME = "*";

    /** Default namespaces support (true). */
    protected static final boolean DEFAULT_NAMESPACES = true;

    /** Default validation support (false). */
    protected static final boolean DEFAULT_VALIDATION = false;

    /** Default Schema validation support (false). */
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;

    /** Default Schema full checking support (false). */
    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;
    
    /** Default honour all schema locations (false). */
    protected static final boolean DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS = false;
    
    /** Default validate schema annotations (false). */
    protected static final boolean DEFAULT_VALIDATE_ANNOTATIONS = false;
    
    /** Default dynamic validation support (false). */
    protected static final boolean DEFAULT_DYNAMIC_VALIDATION = false;
    
    /** Default XInclude processing support (false). */
    protected static final boolean DEFAULT_XINCLUDE = false;
    
    /** Default XInclude fixup base URIs support (true). */
    protected static final boolean DEFAULT_XINCLUDE_FIXUP_BASE_URIS = true;
    
    /** Default XInclude fixup language support (true). */
    protected static final boolean DEFAULT_XINCLUDE_FIXUP_LANGUAGE = true;

    //
    // Public static methods
    //

    /** Prints the specified elements in the given document. */
    public static void print(PrintWriter out, Document document,
                             String elementName, String attributeName) {

        // get elements that match
        NodeList elements = document.getElementsByTagName(elementName);

        // is there anything to do?
        if (elements == null) {
            return;
        }

        // print all elements
        if (attributeName == null) {
            int elementCount = elements.getLength();
            for (int i = 0; i < elementCount; i++) {
                Element element = (Element)elements.item(i);
                print(out, element, element.getAttributes());
            }
        }

        // print elements with given attribute name
        else {
            int elementCount = elements.getLength();
            for (int i = 0; i < elementCount; i++) {
                Element      element    = (Element)elements.item(i);
                NamedNodeMap attributes = element.getAttributes();
                if (attributes.getNamedItem(attributeName) != null) {
                    print(out, element, attributes);
                }
            }
        }

    } // print(PrintWriter,Document,String,String)

    //
    // Protected static methods
    //

    /** Prints the specified element. */
    protected static void print(PrintWriter out,
                                Element element, NamedNodeMap attributes) {

        out.print('<');
        out.print(element.getNodeName());
        if (attributes != null) {
            int attributeCount = attributes.getLength();
            for (int i = 0; i < attributeCount; i++) {
                Attr attribute = (Attr)attributes.item(i);
                out.print(' ');
                out.print(attribute.getNodeName());
                out.print("=\"");
                out.print(normalize(attribute.getNodeValue()));
                out.print('"');
            }
        }
        out.println('>');
        out.flush();

    } // print(PrintWriter,Element,NamedNodeMap)

    /** Normalizes the given string. */
    protected static String normalize(String s) {
        StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
            case '<': {
                    str.append("&lt;");
                    break;
                }
            case '>': {
                    str.append("&gt;");
                    break;
                }
            case '&': {
                    str.append("&amp;");
                    break;
                }
            case '"': {
                    str.append("&quot;");
                    break;
                }
            case '\r':
            case '\n': {
                    str.append("&#");
                    str.append(Integer.toString(ch));
                    str.append(';');
                    break;
                }
            default: {
                    str.append(ch);
                }
            }
        }

        return str.toString();

    } // normalize(String):String

	public void Import(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g,
			String Filename) throws FileNotFoundException {
        // variables
        PrintWriter out = new PrintWriter(System.out);
        Xerces parser = null;
        String elementName = DEFAULT_ELEMENT_NAME;
        String attributeName = null;
        boolean namespaces = DEFAULT_NAMESPACES;
        boolean validation = DEFAULT_VALIDATION;
        boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
        boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
        boolean honourAllSchemaLocations = DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean dynamicValidation = DEFAULT_DYNAMIC_VALIDATION;
        boolean xincludeProcessing = DEFAULT_XINCLUDE;
        boolean xincludeFixupBaseURIs = DEFAULT_XINCLUDE_FIXUP_BASE_URIS;
        boolean xincludeFixupLanguage = DEFAULT_XINCLUDE_FIXUP_LANGUAGE;

        // use default parser?
        if (parser == null) {

            // create parser
            try {
                //parser = (ParserWrapper)Class.forName("ec.refactoring.dom.wrappers.Xerces").newInstance();
            	parser = new Xerces();
            }
            catch (Exception e) {
                System.err.println("error: Unable to instantiate parser ("+DEFAULT_PARSER_NAME+")");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        // set parser features
        try {
            parser.setFeature(NAMESPACES_FEATURE_ID, namespaces);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+NAMESPACES_FEATURE_ID+")");
        }
        try {
            parser.setFeature(VALIDATION_FEATURE_ID, validation);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+VALIDATION_FEATURE_ID+")");
        }
        try {
            parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, schemaValidation);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+SCHEMA_VALIDATION_FEATURE_ID+")");
        }
        try {
            parser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+SCHEMA_FULL_CHECKING_FEATURE_ID+")");
        }
        try {
            parser.setFeature(HONOUR_ALL_SCHEMA_LOCATIONS_ID, honourAllSchemaLocations);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+HONOUR_ALL_SCHEMA_LOCATIONS_ID+")");
        }
        try {
            parser.setFeature(VALIDATE_ANNOTATIONS_ID, validateAnnotations);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+VALIDATE_ANNOTATIONS_ID+")");
        }
        try {
            parser.setFeature(DYNAMIC_VALIDATION_FEATURE_ID, dynamicValidation);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+DYNAMIC_VALIDATION_FEATURE_ID+")");
        }
        try {
            parser.setFeature(XINCLUDE_FEATURE_ID, xincludeProcessing);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+XINCLUDE_FEATURE_ID+")");
        }
        try {
            parser.setFeature(XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID, xincludeFixupBaseURIs);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID+")");
        }
        try {
            parser.setFeature(XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID, xincludeFixupLanguage);
        }
        catch (SAXException e) {
            System.err.println("warning: Parser does not support feature ("+XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID+")");
        }

        // parse file
        try {
            Vector<Node> classNodes = new Vector<Node>();
            Vector<Node> interfaceNodes = new Vector<Node>();
            TreeMap<String, AnnotatedVertex> xmiIdMap = new TreeMap<String, AnnotatedVertex>();
            
            Document document = parser.parse(Filename);
            //print(out, document, elementName, attributeName);
            
            // Extract classes from the model
            NodeList allClassNodes = document.getElementsByTagName("UML:Class");
            for (int ndx=0; ndx<allClassNodes.getLength(); ++ndx) {
            	if (allClassNodes.item(ndx).getAttributes().getNamedItem("name") != null) {
            		classNodes.add(allClassNodes.item(ndx));
            		
            		// Add a class for this vertex to the graph
            		Node n = allClassNodes.item(ndx);
            		AnnotatedVertex v = new AnnotatedVertex(n.getAttributes().getNamedItem("name").getNodeValue(), VertexType.CLASS, Visibility.PUBLIC);
            		g.addVertex(v);
            		xmiIdMap.put(allClassNodes.item(ndx).getAttributes().getNamedItem("xmi.id").getNodeValue(), v);
            	}
            }
            
            // Extract classes from the model
            NodeList allInterfaceNodes = document.getElementsByTagName("UML:Interface");
            for (int ndx=0; ndx<allInterfaceNodes.getLength(); ++ndx) {
            	if (allInterfaceNodes.item(ndx).getAttributes().getNamedItem("name") != null) {
            		interfaceNodes.add(allInterfaceNodes.item(ndx));
            		
            		// Add a class for this vertex to the graph
            		Node n = allInterfaceNodes.item(ndx);
            		AnnotatedVertex v = new AnnotatedVertex(n.getAttributes().getNamedItem("name").getNodeValue(), VertexType.INTERFACE, Visibility.PUBLIC);
            		g.addVertex(v);
            		xmiIdMap.put(allInterfaceNodes.item(ndx).getAttributes().getNamedItem("xmi.id").getNodeValue(), v);
            	}
            }
            
            // Extract associations.  This is a bit tricky.  The 
            // UML:Association tag is used for "generic" associations as well
            // as aggregations and compositions.  Which one of these describes
            // the current tag is denoted by the "aggregation" attribute of
            // each individual "UML:AssociationEnd" tag.  This attribute takes
            // on values of "aggregate" or "composite" to mark the
            // association as being aggregation or composition, respectively.
            NodeList associationNodes = document.getElementsByTagName("UML:Association");
            for (int ndx=0; ndx<associationNodes.getLength(); ++ndx) {
            	AssociationType assocType = AssociationType.NONE;
            	AnnotatedVertex parent = null;
            	Vector<AnnotatedVertex> children = new Vector<AnnotatedVertex>();
            	NodeList childNodes = associationNodes.item(ndx).getChildNodes();
            	for (int child_ndx=0; child_ndx<childNodes.getLength(); ++child_ndx) {
            		if (childNodes.item(child_ndx).getNodeName() == "UML:Association.connection") {
            			NodeList endNodes = childNodes.item(child_ndx).getChildNodes();
            			for (int end_ndx=0; end_ndx<endNodes.getLength(); ++end_ndx) {
            				if (endNodes.item(end_ndx).getNodeName() == "UML:AssociationEnd") {
            					// What type of association is this?  We
            					// default to none.  If aggregation or
            					// composition is found, though, we need to
            					// process this as a part-whole structure.
            					String endType = endNodes.item(end_ndx).getAttributes().getNamedItem("aggregation").getNodeValue();
            					if (endType.equals("composite")) {
            						assocType = AssociationType.COMPOSITION;
            						parent = xmiIdMap.get(endNodes.item(end_ndx).getChildNodes().item(3).getChildNodes().item(1).getAttributes().getNamedItem("xmi.idref").getNodeValue());
            					} else if (endType.equals("aggregate")) {
            						assocType = AssociationType.AGGREGATION;
            						parent = xmiIdMap.get(endNodes.item(end_ndx).getChildNodes().item(3).getChildNodes().item(1).getAttributes().getNamedItem("xmi.idref").getNodeValue());
            					} else if (endType.equals("none")) {
            						children.add(xmiIdMap.get(endNodes.item(end_ndx).getChildNodes().item(3).getChildNodes().item(1).getAttributes().getNamedItem("xmi.idref").getNodeValue()));
            					} else {
            						System.err.println("Unknown association type: \"" + endType + "\"");
            						break;
            					}
            					System.out.println(endNodes.item(end_ndx).getChildNodes().item(1).getNodeName());
            					
            				}
            			}
            			break; // Assumption: only one UML:Association.connection tag
            		}
            	}
            	// Now process all of the ends that we just found.
            	if (parent != null) {
	            	if (assocType == AssociationType.AGGREGATION) {
	            		System.out.println("\tAggregation: " + associationNodes.item(ndx));
	            		for (int child_ndx=0; child_ndx<children.size(); ++child_ndx) {
	            			g.addEdge(parent, children.get(child_ndx), new AnnotatedEdge(Label.AGGREGATE));
	            		}
	            	} else if (assocType == AssociationType.COMPOSITION) {
	            		System.out.println("\tComposition");
	            		for (int child_ndx=0; child_ndx<children.size(); ++child_ndx) {
	            			g.addEdge(parent, children.get(child_ndx), new AnnotatedEdge(Label.COMPOSE));
	            		}
	            	}
            	} else {
            		assert children.size() >= 2;
            		System.out.println("\tAssociation");
            		if (children.size() > 2) {
            			System.err.println("\t\tWarning: Expected two children, found " + children.size());
            		}
            		g.addEdge(children.get(0), children.get(1), new AnnotatedEdge(Label.ASSOCIATE));
            	}
            }
            NodeList generalizationNodes = document.getElementsByTagName("UML:Generalization");
            for (int ndx=0; ndx<generalizationNodes.getLength(); ++ndx) {
            	System.out.println(generalizationNodes.item(ndx));
            	AnnotatedVertex parent = null;
            	AnnotatedVertex child = null;
            	NodeList childNodes = generalizationNodes.item(ndx).getChildNodes();
            	// Look for the parent (UML:Generalization.parent).  The
            	// class that the generalization relationship refers to will
            	// be the second child node.  The first child is usually an
            	// empty text node.
            	for (int child_ndx=0; child_ndx<childNodes.getLength(); ++child_ndx) {
            		if (childNodes.item(child_ndx).getNodeName() == "UML:Generalization.parent") {
            			System.out.println(childNodes.item(child_ndx).getChildNodes().item(1).getNodeName());
            			parent = xmiIdMap.get(childNodes.item(child_ndx).getChildNodes().item(1).getAttributes().getNamedItem("xmi.idref").getNodeValue());
            			break;
            		}
            	}
            	// Now look for the child (UML:Generalization.child).  Same
            	// deal with the child node.
            	for (int child_ndx=0; child_ndx<childNodes.getLength(); ++child_ndx) {
            		if (childNodes.item(child_ndx).getNodeName() == "UML:Generalization.child") {
            			child = xmiIdMap.get(childNodes.item(child_ndx).getChildNodes().item(1).getAttributes().getNamedItem("xmi.idref").getNodeValue());
            			break;
            		}
            	}
            	if (parent != null && child != null) {
            		g.addEdge(child, parent, new AnnotatedEdge(Label.INHERIT));
            	} else {
            		System.out.println("Error: missing one of " + parent + ", " + child);
            	}
            }
            
            // Extract operations from each class
            for (int cl_ndx=0; cl_ndx<classNodes.size(); ++cl_ndx) {
            	String className = classNodes.get(cl_ndx).getAttributes().getNamedItem("name").getNodeValue();
        		System.out.println("Processing class: "
        				+ className);
            	NodeList opNodes = classNodes.get(cl_ndx).getChildNodes();
            	AnnotatedVertex classVertex = xmiIdMap.get(classNodes.get(cl_ndx).getAttributes().getNamedItem("xmi.id").getNodeValue());
            	int numFeatures = opNodes.getLength();
            	for (int op_ndx=0; op_ndx<numFeatures; ++op_ndx) {
	            	if (opNodes.item(op_ndx).getNodeName() == "UML:Classifier.feature") {
	            		NodeList elementNodes = opNodes.item(op_ndx).getChildNodes();
	            		// Now extract operations and attributes
	            		int numElements = elementNodes.getLength();
	            		for (int element_ndx=0; element_ndx<numElements; ++element_ndx) {
	            			if (elementNodes.item(element_ndx).getNodeName() == "UML:Operation") {
	            				// If an operation returns an instance of a class in our graph,
	            				// then create an instantiation relationship between the owning class
	            				// and the instantiated class.
	            				NodeList operChildNodes = elementNodes.item(element_ndx).getChildNodes();
	            				for (int elchild_ndx=0; elchild_ndx<operChildNodes.getLength(); ++elchild_ndx) {
	            					if (operChildNodes.item(elchild_ndx).getNodeName() == "UML:BehavioralFeature.parameter") {
	            						NodeList paramChildNodes = operChildNodes.item(elchild_ndx).getChildNodes();
	            						// Find the parameter that specifies the return type
	            						for (int operparam_ndx=0; operparam_ndx<paramChildNodes.getLength(); ++operparam_ndx) {
	            							Node paramNode = paramChildNodes.item(operparam_ndx);
	            							if (paramNode.getNodeName() == "UML:Parameter" &&
	            									paramNode.getAttributes().getNamedItem("kind").getNodeValue().equals("return")) {
	            								// Finally, extract the class whose instance is being returned
	            								assert paramNode.hasChildNodes();
	            								String returnClassName = paramNode.getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem("xmi.idref").getNodeValue();
	            								if (xmiIdMap.containsKey(returnClassName)) {
	            									AnnotatedVertex returnClass = xmiIdMap.get(returnClassName); 
	            									g.addEdge(classVertex, returnClass, new AnnotatedEdge(Label.INSTANTIATE));
	            								} else {
	            									// Bail out - the return type is not a class in our annotated graph.
	            									break;
	            								}
	            							}
	            						}
	            					}
	            				}
	            			} else if (elementNodes.item(element_ndx).getNodeName() == "UML:Attribute") {
	            				// If an attribute has a data type that is a class in our
	            				// annotated graph, then create an instantiation relationship
	            				// between the owning class and the instantiated class.
	            				NodeList attribChildNodes = elementNodes.item(element_ndx).getChildNodes();
	            				for (int type_ndx=0; type_ndx<attribChildNodes.getLength(); ++type_ndx) {
	            					Node typeNode = attribChildNodes.item(type_ndx);
	            					if (typeNode.getNodeName() == "UML:StructuralFeature.type" &&
	            							typeNode.hasChildNodes() &&
	            							typeNode.getChildNodes().item(1).getNodeName() == "UML:Class") {
	            						String attribTypeName = typeNode.getChildNodes().item(1).getAttributes().getNamedItem("xmi.idref").getNodeValue();
	            						if (xmiIdMap.containsKey(attribTypeName)) {
		            						AnnotatedVertex attribTypeVertex = xmiIdMap.get(attribTypeName);
		            						g.addEdge(classVertex, attribTypeVertex, new AnnotatedEdge(Label.INSTANTIATE));
	            						} else {
	            							// Bail out of this attribute.  The
	            							// attribute data type is not in
	            							// our annotated graph.
	            							break;
	            						}
	            					}
	            				}
	            			}
	            		}
	            	}
            	}
            }
            //print(out, document, "UML:Class", attributeName);
        }
        catch (SAXParseException e) {
            // ignore
        }
        catch (Exception e) {
            System.err.println("error: Parse error occurred - "+e.getMessage());
            if (e instanceof SAXException) {
                Exception nested = ((SAXException)e).getException();
                if (nested != null) {
                    e = nested;
                }
            }
            e.printStackTrace(System.err);
            System.exit(-1);
        }
	}

}
