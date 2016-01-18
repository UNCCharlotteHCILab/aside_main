package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.ISelectionListener;

public class VariablesAndConstants 
{
		public final static String ANNOTATION_REQUEST_BOUND_RESOLUTION_DESC = "This annotation request has been satisfied with an annotation";
		public final static String ANNOTATION_REQUEST_UNBOUND_RESOLUTION_DESC = "This involves sensitive information and could possibly be an access control vulnerability if access control code is not in place. \n\n Please annotate the access control code for this this sensitive operation by double clicking on \"Annotate Now\" and highlighting the code.";
		public final static String ADD_ANNOTATION_DESC = "Double click this option and highlight code to annotate the access control code.";
		public final static String annotationURL = "https://www.owasp.org/index.php/Access_Control_Cheat_Sheet";
		public final static String CONTEXTUAL_HELP_ROOT = "https://aside-context-help.herokuapp.com";
		//these "chunks" are pieces of the description which are put together with some dynamic content by a method
		public final static String chunkOne = "This annotation is linked to the following security sensitive operation:<P><P>Line ";
		public final static String chunkTwo = "<P><P>Double click \"**********ASIDE Annotation**********\" on the left to navigate to the operation.";
		public final static String ANNOTATION_VULNERABILITY_DESC = "ASIDE believes that this may be vulnerable. This same sensitive operation has appeared before but has different annotations";
		public final static String ANNOTATION_VULNERABILITY_ONE_DESC = "ASIDE believes that this may be vulnerable. This same sensitive operation has appeared before but has different annotations. It has appeared at the following location <P><P> AccountsServlet.java line 66 AccountMapperImpl.updateAcc...";
		public final static String ANNOTATION_VULNERABILITY_TWO_DESC = "ASIDE believes that this may be vulnerable. This same sensitive operation has appeared before but has different annotations. It has appeared at the following location <P><P> TransactionsServlet.java line 46 accounts.getAccount...";
		public final static String ANNOTATION_DELETE_DESC = "Deletes the annotation";
		public final static String ANNOTATION_CHECKED_DELETE_DESC = "Deletes all annotations associated with this annotation request";
		public final static String PLACEHOLDER_TEXT = "Placeholder for description";
		public final static String READ_MORE_DESC = "Go to a web page to learn more about access control vulnerabilities, why you are seeing this marker, and what you can do to fix it";

		public static boolean isAnnotatingNow = false;
		public static IMarker[] annotationRequestMarkers = new IMarker[100];
		public static IMarker[][] annotationMarkers = new IMarker[100][100]; //first dimension is which request is associated with it. The second is which annotation so you can have many annotations per request.
		public static int[] annotationMarkerCounters = new int[100]; //keeps track of how many annotations exist for each request. total amount for each request is this + 1.
		public static String[] methodNames = new String[100];
		public static String[] dynamicCode = new String[100];
		public static int markerStart[] = new int[100];
		
		//count is total amount of annotation request markers;
		public static int count = 0;
		public static int currentIndex = -1;
		public static TextSelectionListener currentSelectionListener;
		public static String previousText = "";
		public static int previousLength = -1;
		public static boolean firstHighlight = true;
		
		
		

}
