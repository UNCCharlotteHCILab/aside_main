package edu.uncc.aside.codeannotate;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * @author Jun Zhu  (jzhu16 at uncc dot edu)
 * @author Mahmood Mohammadi (mmoham12 at uncc dot edu)
 */

public class PluginConstants {
	////////////// Markers Constants
	
	public static final String MARKER_INPUT_VALIDATION = "edu.uncc.aside.AsideMarker";
	public static final String MARKER_OUTPUT_ENCODING = "edu.uncc.aside.AsideMarker";
	public static final String MARKER_SQL_INJECTION = "edu.uncc.aside.AsideMarker";
	public static final String MARKER_ANNOTATION_CHECKED ="green.check";
	public static final String MARKER_GREEN_CHECK_BOX ="green.check.box";
	public static final String MARKER_ANNOTATION_REQUEST ="yellow.question.box"; // "CodeAnnotate.annotationQuestion"; //"yellow.question.box"; 
	public static final String MARKER_ANNOTATION_ANSWER = "green.diamond"; //"CodeAnnotate.annotationAnswer";
	
	public static final String MARKER_INVISIBALE_ANNOTATION = "edu.uncc.sis.aside.invisibleAnnotation";
	public static final String ICON_DECORATING_OVERLAY ="reddot.jpg";
	public static final String MARKER_ROOT_TYPE = "ASIDE.RootMarker";
		/////////////////////// Markers' Resolution Constants
	public final static String ANNOTATION_RESOLUTION_DESC =  "<p>This warning identifies a method invocation accessing/modifying some sensitive/critical data of the application under development, " +
			"the selected option allows you to select a piece of code that enforces the entity that accesses the sensitive data actually possesses the right/privilege to do so. </p><br/>   <p>If you already have code that does the proper check, " +
			"please annotate the code that can be evaluated to be true or false (e.g. the condition test of a if statement) by first selecting and then pressing Ctrl+0; </p><br/>" +
			"<p>If you do not yet have such code checking the right to access the sensitive data, it is STRONGLY encouraged that you take the time to make up the miss.</p>";

	public final static String CHECK_UNDO_RESOLUTION_DESC = "This option allows you to remove this access control check and restore the state of the path on which this check is annotated. \r\n\r\nThis option is useful in cases where you decide that this check is not annotated on the correct path, or this is a wrong check.";

	public final static String IGNORE_MARKER_RESOLUTION_DESC = "This option allows you to remove the warning from the editor. \r\n\r\nYou should use this option when you are confident that this is not a real issue. For instance, this method invocation is accessing common data that does NOT require access control checks.";

	public final static String READ_MORE_RESOLUTION_DESC = "Click this option will make your browser presenting you a web page that has more detailed information explaining vulnerabilities related to the identified code. \r\n\r\nYou are strongly ENCOURAGED to take this action before others (#2 and #3), unless you already know what this warning actually indicates.";

	/////////////////// Trust Boundary Constants
	public static final String FILE_TRUST_BOUNDARIES = "aside-trust-boundaries.xml";
	public static final String FILE_VALIDATION_RULES = "aside-valiation-rules.xml";

	public static final String DEFAULT_TRUST_BOUNDARIES_FILE = "default_rule_pack"
			+ System.getProperty("file.separator")
			+ "aside-default-trust-boundaries.xml";
	public static final String DEFAULT_VALIDATION_RULES_FILE = "default_rule_pack"
			+ System.getProperty("file.separator")
			+ "aside-default-validation-rules.xml";
	public static final String DEFAULT_SECURE_PROGRAMMING_KNOWLEDGE_BASE = "default_rule_pack"
			+ System.getProperty("file.separator")
			+ "aside-secure-programming-knowledge-base.xml";
	public static final String USER_DEFINED_ASIDE_RULES_FOLDER = "src" + IPath.SEPARATOR + "aside-rules";
	public static final String TRUST_BOUNDARY_RULESET_PATH = USER_DEFINED_ASIDE_RULES_FOLDER + IPath.SEPARATOR + "trust-boundaries.xml";


	
	public static final String ESAPI = "ESAPI";
	public static final int VR = 1;
	public static final int TB = 2;
	public static final int VK = 3;
	
	public static final int VK_INPUTVALIDATION = 1;
	public static final int VK_OUTPUTENCODING = 2;
	
	
	public static final List<String> JAVA_MAP_TYPES = Arrays.asList(
			"java.util.Map", "java.util.HashMap", "java.util.AbstractMap",
			"java.util.Attributes", "java.util.Hashtable",
			"java.util.IdentityHashMap", "java.util.RenderingHints",
			"java.util.TreeMap", "java.util.WeakHashMap");

	public static final List<String> JAVA_LIST_TYPES = Arrays.asList(
			"java.util.List", "java.util.AbstractList", "java.util.ArrayList",
			"java.util.LinkedList", "java.util.Vector", "java.util.Stack");
//this is the comprehensive version of encoding types
//	public static final String[] ENCODING_TYPES = new String[] { "Base64",
//			"CSS", "DN", "HTML", "HTMLAttribute", "JavaScript", "LDAP", "OS",
//			"SQL", "VBScript", "XML", "XMLAttribute", "XPath" };
//the following ENCODING_TYPES is for the education version use
	/*public static final String[] ENCODING_TYPES = new String[] {"CSS", "HTML", "HTMLAttribute", "JavaScript",
		"OS", "SQL", "XML", "XMLAttribute", "XPath" };*/
	public static final String[] ENCODING_TYPES = new String[] {"CSS", "HTML", "HTMLAttribute", "JavaScript"};
	public static final List<String> JAVA_NUMERIC_TYPES = Arrays
			.asList(new String[] { "java.lang.Integer", "java.lang.Double",
					"java.lang.Short", "java.lang.Float", "java.lang.Long" });

	public static final List<String> JAVA_NUMERIC_METHODS = Arrays
			.asList(new String[] { "parseInt", "parseDouble", "parseShort",
					"parseFloat", "parseLong", "valueOf" });
	
	//newly added
	public static final String outFileNameForPojoResult = "pojoResult.txt";
	public static final String matchStr = ".*(set|get|is)([A-Z]).*"; //set or get or is
	//newly added 2012.01.01
	public static final String AttrIsInput = "input";
	public static final String AttrIsOutput = "output";
	public static final String AttrIsSpecialOutput = "specialOutput";
	public static final String AttrIsWarning = "warning";
	public static final String ReadMore = "Read More";
	
		
	
	/////////////////// Code Generation Constants 
	public static final String DontUseWarning = "<<Warning>> DO NOT USE IT!";
	public static final String BlankLine = "\n";
	public static final String DynamicSQLWarningMsg = "Please do not use dynamic SQL statements, security experts recommend using parameterized SQL statements!";
    public static final String ESAPI_ENCODING_COMMENTS = "// 1 NOTE: code generated by " + Plugin.PLUGIN_NAME + "\n                  ";
	public static final String ESAPI_COMMENTS = "/* 2 NOTE: If the following " + Plugin.PLUGIN_NAME + " generated code detects a problem \n * (e.g., malicious characters entered by user) an exception is thrown.\n * Doing so will skip the rest of the try block code and go directly to\n * (execute) one of the generated catch blocks below.\n * */ "  + "\n";
	
	public static final String ESAPI_SIMPLE_COMMENT =""; // "\n/* 3 NOTE: " + Plugin.PLUGIN_NAME + " has created the following try catch blocks.\n * If the generated input validation code detects a problem\n * (e.g., malicious characters entered by user) an exception is thrown.\n * Doing so will skip the rest of the try block code and go directly to\n * one of the generated catch blocks below.\n * */\n";
   
	public static final String ESAPI_TRY_COMMENT ="\n/* NOTE: " + Plugin.PLUGIN_NAME + " has created the following try catch blocks.\n * If the generated input validation code detects a problem\n * (e.g., malicious characters entered by user) an exception is thrown.\n * Doing so will skip the rest of the try block code and go directly to\n * one of the generated catch blocks below.\n * */";
	public static final String ESAPI_COMMENT2 = "/* NOTE: RR If the following " + Plugin.PLUGIN_NAME + " generated code detects a problem \n * (e.g., malicious characters entered by user) an exception is thrown.\n * Doing so will skip the rest of the try block code and go directly to\n * (execute) one of the generated catch blocks below.\n * */ "  + "\n";

	public static final String VALIDATION_EXCEPTION_COMMENTS = "/* This catch block is executed when " + Plugin.PLUGIN_NAME + " finds input that did\n * not match validation rules (e.g., bad user input). */ \n"; // When this happens,\n * the developer should maintain usability in this catch block\n * (e.g., response.sendRedirect(\"Login.jsp\"); with an accompanying\n * unsuccessful attempt indication presented to the user).\n * */ \n\n // Note: Default return generated by " + Plugin.PLUGIN_NAME + "";
    
    public static final String INTRUTION_EXCEPTION_COMMENTS = "/* This catch block will be executed when advanced \n * intrusion behavior is detected in " + Plugin.PLUGIN_NAME + "'s try block statement. */ \n"; // This exception should also be handled by the \n * developer similar to the ValidationException block.\n */ \n  // NOTE: default return generated by " + Plugin.PLUGIN_NAME + "";
   
    public static final String ESAPI_COMMENT = "\n// NOTE: code generated by " + Plugin.PLUGIN_NAME + "";
   
    public static final List<String> VALIDATION_TYPES_TO_BE_DELETED = Arrays.asList(new String[]{"HTTPContextPath", "HTTPCookieName",
    	"HTTPCookieValue", "HTTPHeaderName", "HTTPHeaderValue", "HTTPJSESSIONID", "HTTPScheme", "HTTPServerName",
    	"HTTPServerPath", "HTTPURI", "HTTPURL", "AcceptLenientDates", "DirectoryName",  "Redirect", "AccountName", "IPAddress", "HTTPQueryString", "HTTPParameterName", "RoleName", "SystemCommand", "HTTPPath", "ConfigurationFile", "HTTPServletPath"});
    public static final String HostUrl = "http://hci.uncc.edu/tomcat/" + Plugin.PLUGIN_NAME + "/"; //"http://www.google.com/#q=";
   
	public static String[] returnTypeCategories = new String[]{"false", "void", "0", "null"};
	
	public static final int OUTPUT_IGNORE_RANK_NUM = 6;
	public static final int OUTPUT_NOTICE_LABEL_RANK_NUM = 7;
	public static final int INPUT_IGNORE_RANK_NUM = 8;
	public static final int INPUT_NOTICE_LABEL_RANK_NUM = 9;
	public static final String TOOL_TIP ="" + Plugin.PLUGIN_NAME + " potential security vulnerability: accessing unvalidated input";// "ASIDE has flagged this code as potentially vulnerable. Left click the icon to find out why.";
	public static final String LOG_SEND_END_DATE = "2020/05/19 24:00:00";
	public static final String USERIDLIST_EMAIL_SUBJECT = "UNCC " + Plugin.PLUGIN_NAME + " userId list";
	public static final List<String> ALLOWED_USERID_EMAIL_SENDER = Arrays
			.asList(new String[] { "\"Mahmoud \" <mmoham12@uncc.edu>"});
	
	public static final String ESAPI_IMPORT = "org.owasp.esapi.ESAPI";
	public static final String ESAPI_VALIDATION_EXCEPTION_IMPORT = "org.owasp.esapi.errors.IntrusionException";
	public static final String ESAPI_INTRUSION_EXCEPTION_IMPORT = "org.owasp.esapi.errors.ValidationException";
	
	public static final String EXCEPTION_VARIABLE_NAME = "e";
	
	public static final String VALIDATION_EXCEPTION_TYPE = "ValidationException";
	public static final String INTRUSION_EXCEPTION_TYPE = "IntrusionException";
	public static final String ESAPI_VALIDATOR = "validator";
	public static final String ESAPI_ENCODER = "encoder";
	public static final String ESAPI_VALIDATOR_GETVALIDINPUT = "getValidInput";
	public static final String ESAPI_CONTEXT_PLACEHOLDER = "replace ME with validation context";
	//private static final String ESAPI_TRY_COMMENT = "/* NOTE: ASIDE has created the following try catch blocks.\n * If the generated input validation code detects a problem\n * (e.g., malicious characters entered by user) an exception is thrown.\n * Doing so will skip the rest of the try block code and go directly to * one of the generated catch blocks below.\n * */\n";
	//  NOTE: try catch and validation code within was generated by ASIDE";
	//private static final String ESAPI_COMMENT = "// NOTE: code generated by ASIDE";
	public static final String ESAPI_DEFAULT_LENGTH = "200"; // TODO	
	
	
	/////////////////////// Plugin Class Constants
	public static final String PLUGIN_ID = "ESIDE";
	public static final String ANNOTATION_RELATIONSHIP_VIEW_ID = "relationships";
	public static final String SENSITIVE_OPERATIONS_CONFIG_FILE = "SensitiveInfoAccessors.xml";
	public static final String ASIDE_NODE_PROP_START = "aside_start";
	public static final String ASIDE_NODE_PROP_END = "aside_end";
	public static final String LOG_PROPERTIES_FILE = "logger.properties";
	public static final String ASIDE_USERID_FILE = "userID.txt";
	public static final String JAVA_WEB_APP_NATURE = "org.eclipse.jdt.core.javanature";
	
	///////////////////////  ESAPI Config Process
	public final static String ESAPI_CONFIG_DIR_NAME = "ESAPI-Lib";
	public final static String ASIDE_ESAPI_CONTAINER = "ESAPI Libraries";
	public final static String PROJECT_LIB_PATH = "WebContent"	+ IPath.SEPARATOR + "WEB-INF" + IPath.SEPARATOR + "lib";
	public final static String PROJECT_WEBINF_PATH = "src" ;//"WebContent" + IPath.SEPARATOR + "WEB-INF";
	public final static String ESAPI_VM_ARG = "-Dorg.owasp.esapi.resources";

	public static final String userHome = System.getProperty("user.home");
	/** The name of the ESAPI property file */
	public static final String RESOURCE_FILE = "ESAPI.properties";
	public static final String VALIDATION_PROPERTIES = "Validator.ConfigurationFile";
	public static final String VALIDATOR_KEY_WORD = "Validator";
	
	/////////// AsideNature
	/**
	 * ID of this corresponding project builder
	 */
	public static final String ASIDE_BUILDER_ID = "edu.uncc.sis.aside.AsideBuilder";


	///////////////////////// DynamicMenuContributionItem
	public static final String CONTRIBUTION_ITEM_ID = "edu.uncc.sis.aside.auxiliary.dynamicMenuContributionItem";
	public static final String CONTRIBUTION_COMMAND_ID = "edu.uncc.sis.aside.auxiliary.dynamicCommand";
	public static final String EXPLANATION_VIEW_ID = "edu.uncc.sis.aside.views.complimentaryExplanationView";
	
		
	//////////////////AddAsideRulesAction
	public static final String ASIDE_NATURE_ID = "edu.uncc.sis.aside.AsideNature";
	
	//////////IPreferenceConstants
	// The identifiers for the preferences
	public static final String ASIDE_VR_PREFERENCE = "asideValidationRules";
	public static final String PROJECT_VR_PREFERENCE = "projectValidationRules";
	public static final String EXTERNAL_VR_PREFERENCE = "externalValidationRules";
	public static final String EXTERNAL_VR_PATH_PREFERENCE = "externalPaths";
	public static final String ASIDE_TB_PREFERENCE = "asideTrustBoundaries";
	public static final String PROJECT_TB_PREFERENCE = "projectTrustBoundaries";
	public static final String EXTERNAL_TB_PREFERENCE = "externalTrustBoundaries";
	public static final String EXTERNAL_TB_PATH_PREFERENCE = "externalPaths";
	// The default values for the preferences
	public static final String EXTERNAL_VR_PATH_PREFERENCE_DEFAULT = "";
	public static final String EXTERNAL_TB_PATH_PREFERENCE_DEFAULT = "";
	// The path delimiter
	public static final String PATH_DELIMITER = ";";
	// List Control size
	public static final int VERTICAL_DIALOG_UNITS_PER_CHAR = 8;
	public static final int LIST_HEIGHT_IN_CHARS = 5;
	public static final int LIST_WIDTH_IN_CHARS = 25;
	public static final int LIST_HEIGHT_IN_DLUS = LIST_HEIGHT_IN_CHARS
	* VERTICAL_DIALOG_UNITS_PER_CHAR;
	public static final int LIST_WIDTH_IN_DLUS = LIST_WIDTH_IN_CHARS
	* VERTICAL_DIALOG_UNITS_PER_CHAR;
		
	/////////////// ValidationRulesView
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String VIEW_ID = "edu.uncc.sis.aside.views.ValidationRulesView";
	public static final String DELIMITER = "\\p{Punct}";
	
	


}
