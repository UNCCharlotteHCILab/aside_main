package edu.uncc.aside.codeannotate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jdt.core.IMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLConfig {
	private static boolean TRACE = false;
	static Map<IMethod, SinkDescription> method2desc = new HashMap<IMethod, SinkDescription>();

	public static class SinkDescription {
		
		String id, typeName, methodName, categoryName;
		private int vulnerableParameter;		
		
		protected SinkDescription(String id, String typeName, String methodName, String categoryName, 
				int parameterCount, int vulnerableParameter) 
		{
			this.id = id;
			this.typeName = typeName;
			this.methodName = methodName;
			this.categoryName = categoryName;
			this.vulnerableParameter = vulnerableParameter; 
		}

		public SinkDescription(String id, String categoryName, int vulnerableParameter){
			this.id = id;
			
			int right_bracker_idx = id.lastIndexOf(')');
			int left_bracker_idx = id.lastIndexOf('(');
			String param = id.substring(left_bracker_idx+1, right_bracker_idx);
			int comma_count = 0;
			for(int i=0; i < param.length(); i++) {
			    char c = param.charAt(i);
			    if(c == ',') {
			        comma_count++;
			    }
			}
			int dot_index = id.lastIndexOf('.'); 
			
			this.typeName = id.substring(0, dot_index);
			this.methodName = id.substring(0, left_bracker_idx);
			this.categoryName = categoryName;
			this.vulnerableParameter = vulnerableParameter; 
		}

		public String toString(){
			return "Sink: " + (typeName + ", " + methodName + ", " + categoryName);
		}
		public String getCategoryName() {
			return categoryName;
		}
		public String getMethodName() {
			return methodName;
		}
		public String getTypeName() {
			return typeName;
		}
		public String getID() {
			return id;
		}
		
		public int getVulnerableParameter() {
			return vulnerableParameter;
		}
	}
	
	public static class SourceDescription {
		String typeName, methodName, categoryName, id;
		
		public String getCategoryName() {
			return categoryName;
		}
		public String getMethodName() {
			return methodName;
		}
		public String getTypeName() {
			return typeName;
		}
		protected SourceDescription(String id, String typeName, String methodName, String categoryName) {
			this.id = id;
			this.typeName = typeName;
			this.methodName = methodName;
			this.categoryName = categoryName;
		}
		
		public SourceDescription(String id, String categoryName){
			this.id = id;
			
			//int right_bracker_idx = id.lastIndexOf(')');
			int left_bracker_idx = id.lastIndexOf('(');
			//String param = id.substring(left_bracker_idx+1, right_bracker_idx);
			int dot_index = id.lastIndexOf('.'); 
			
			this.typeName = id.substring(0, dot_index);
			this.methodName = id.substring(0, left_bracker_idx);
			this.categoryName = categoryName; 
		}

		public String toString(){
			return "Source: " + (typeName + ", " + methodName + ", " + categoryName);
		}
		public String getID() {
			return this.id;
		}		
	}


    public static void main(String argv []){
        TRACE = true;

    	Collection<SinkDescription> sinks 	            = readSinks("SensitiveInfoAccessors.xml", "");
    	
    	
        if(TRACE) {
           
            System.out.println(sinks);
            
        }
    }

	public static Collection<SinkDescription> readSinks(String fileName, String base) {
		LinkedList<SinkDescription> result = new LinkedList<SinkDescription>(); 
		
		try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(base + fileName));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            if(TRACE) System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            NodeList accessors = doc.getElementsByTagName("accessor");
            if(TRACE) System.out.println("Total no of accessors: " + accessors.getLength());

            for(int s=0; s < accessors.getLength() ; s++){                
                if(accessors.item(s).getNodeType() == Node.ELEMENT_NODE){
                	Element sinkNode = (Element) accessors.item(s);
                	Assert.isNotNull(sinkNode);
                	String id = sinkNode.getAttribute("id");
                	Assert.isNotNull(id);                	
                	
                    //String typeName 	= sinkNode.getElementsByTagName("type").item(0).getChildNodes().item(0).getNodeValue().trim();
                    //String methodName 	= sinkNode.getElementsByTagName("method").item(0).getChildNodes().item(0).getNodeValue().trim();
                    String categoryName = sinkNode.getElementsByTagName("category").item(0).getChildNodes().item(0).getNodeValue().trim();
                    int parameterCount  = new Integer(sinkNode.getElementsByTagName("paramCount").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue();
                    int vulnerableParameter = new Integer(sinkNode.getElementsByTagName("vulnParam").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue();
                    
                    SinkDescription desc = new SinkDescription(id, categoryName, vulnerableParameter);
                    result.add(desc);
                    if(TRACE) System.out.println(desc);
                }//end of if clause
            }//end of for loop with s var
        } catch (SAXParseException err) {
	        System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
	        System.out.println(" " + err.getMessage ());
	        return null;
        }catch (SAXException e) {
	        Exception x = e.getException ();
	        ((x == null) ? e : x).printStackTrace ();
	        return null;
        }catch (Throwable t) {
        	t.printStackTrace ();
        	return null;  
        }
        
        return result;
	}
	
	public static Collection<SinkDescription> readSinks(String fileName) {
		return readSinks("SensitiveInfoAccessors.xml", getPlugingBasePath());
	}
	
	
	public static String getPlugingBasePath(){
		Plugin plugin = Plugin.getDefault();
		URL base = plugin.getBundle().getEntry("/");
		
		try {
			return FileLocator.toFileURL(base).getFile() + "/";
		} catch (IOException e) {		
			e.printStackTrace();
			return "";
		}	
	}
	
//	private static void saveDescription(IMethod method, SinkDescription desc) {
//		method2desc.put(method, desc);		
//	}
	
	public static SinkDescription getDescription(IMethod method) {
		return method2desc.get(method);
	}
}
