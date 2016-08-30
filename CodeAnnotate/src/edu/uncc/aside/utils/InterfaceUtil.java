package edu.uncc.aside.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
//import org.eclipse.dltk.core.DLTKCore;
//import org.eclipse.dltk.core.IScriptProject;
//import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.php.internal.core.ast.nodes.ITypeBinding;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;
import org.eclipse.ui.texteditor.AnnotationTypeLookup;
import org.eclipse.ui.texteditor.IDocumentProvider;

import edu.uncc.aside.asideInterface.TextSelectionListener;
import edu.uncc.aside.asideInterface.VariablesAndConstants;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.ModelRegistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.mail.*;
//import javax.print.DocFlavor.URL;

//import edu.uncc.aside.codeannotate.NodeFinder;
//import edu.uncc.aside.codeannotate.util.Utils;

public  class InterfaceUtil 
{
	
	private static int[] forbiddenArray = new int[100];
	private static int forbiddenArrayCounter = 0;
	public static String fileName=null;
	public static int markerStart;
	public static int highlightingLength;
	public static String annotatedText=null;
	public static String annotationMarkerName=null;
	public static String randomId=null;
	
	public static Path  prepareHelpFiles()
	{
		URL helpUrls[] = new URL[10];
	    String[] helpFiles = new String[10];
	    
	    helpFiles [0] ="jquery.min.js";
	    helpFiles [1] ="hello.js";
	    helpFiles [2] ="main.css";
	    helpFiles [3] ="ASIDE_Access_Control.html";
	    helpFiles [4] ="ASIDE_Input_Validation.html";
	    helpFiles [5] ="ASIDE_Output_Encoding.html";
	    helpFiles [6] ="ASIDE_SQL_Statement.html";
	    helpFiles [7] ="yellowQuestion.png";
	    helpFiles [8] ="devil-icon.png";
	    
	    int helpFilesCuont= 9;
	    
	    for ( int i=0; i< helpFilesCuont; i++)	    	
	    	helpUrls[i] = Platform.getBundle(PluginConstants.PLUGIN_ID).getEntry("files/" +helpFiles [i]);
	     
	    //System.out.println("urlStr = " + urlStr);
	    final String DEFAULT_TEMP_PATH = System.getProperty("java.io.tmpdir");
	    Path tempPath =  Paths.get(DEFAULT_TEMP_PATH, "ESIDE");
	    
	    try {
	    	//Creating the ESIDE directory in temp dir
	    	
	    	if (! Files.exists(tempPath))	    	
	    		Files.createDirectory(tempPath);
	    	
	    	// Create Help files
	    	for ( int i=0; i< helpFilesCuont; i++)
	    	{
	    	 Path temp= InterfaceUtil.getOrCreateFile(tempPath.toString(), helpFiles[i]);
	    	 InputStream instream = helpUrls[i].openStream();
	    	 
	    	 Files.copy(helpUrls[i].openStream(), temp, StandardCopyOption.REPLACE_EXISTING);
	    	 
	    	 instream.close();
	    	}
	    	    	 
	        
	    } catch(FileAlreadyExistsException e){
	        // the directory already exists.
	    	
	    } catch (IOException e) {
	        //something else went wrong
	        e.printStackTrace();
	    }
	    
	    return tempPath;
	}
	public static Path getOrCreateFile(String filePath, String fileName) {
		Path path = null;
		
		Path directory = Paths.get(filePath);
		
		Path file = directory.resolve(fileName);
		
		if (Files.exists(file)) {
			path = file;
		} else if (Files.notExists(file)) {
			try {
			    // Create the empty file with default permissions
			    path = Files.createFile(file);
			} catch (FileAlreadyExistsException e) {
				
				//logger.error("File already exists (shouldn't happen): " + file, e);
			} catch (IOException e) {
			    // Some other sort of failure, such as permissions.
				//logger.error("Permissions failure for file creation: " + file, e);
			}
		}

		return path;
	}
	//changes a marker type, like a yellow question into a green check. markerType of 0 means it is an annotation request. markerType of 1 means it is an annotation
	public static void changeMarker(String targetMarkerName, int srcIndex, int srcMarkerType, int srcSecondIndex)
	{
		try
		{
		IMarker oldMarker;
		if(srcMarkerType == 0)
		{
			oldMarker = VariablesAndConstants.annotationRequestMarkers[srcIndex];
		}
		else
		{
			System.out.println ("counters in change markers is " + VariablesAndConstants.annotationMarkerCounters[srcIndex]);
			System.out.println("changemarker is " + VariablesAndConstants.annotationMarkers[srcIndex][VariablesAndConstants.annotationMarkerCounters[srcSecondIndex]]);
			oldMarker = VariablesAndConstants.annotationMarkers[srcIndex][VariablesAndConstants.annotationMarkerCounters[srcSecondIndex]];
		}
		
		if(oldMarker != null)
		{
			int markerStart = oldMarker.getAttribute(IMarker.CHAR_START, -1);
			int markerEnd = oldMarker.getAttribute(IMarker.CHAR_END, -1);
			int markerIndex = oldMarker.getAttribute("markerIndex", -1);
			//if the marker attributes are not invalid
			if(markerStart != -1 && markerEnd != -1 && markerIndex != -1)
			{
				//create new marker and delete the old one
				IResource targetResource = oldMarker.getResource();	
				
				Map<String, Object> markerAttributes = new HashMap<String, Object>();	
				markerAttributes.put(IMarker.CHAR_START,
						markerStart);
				markerAttributes.put(IMarker.CHAR_END, markerEnd);
				markerAttributes.put(IMarker.MESSAGE,	"Change Marker: " + srcMarkerType + " To " + targetMarkerName);
				markerAttributes.put("markerIndex", markerIndex);
				markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				
				IMarker newMarker = MarkerAndAnnotationUtil
						.addMarker(targetResource,
								markerAttributes, targetMarkerName);

				/*
 			    IMarker newMarker = targetResource.createMarker(targetMarkerName);
 			   annotationMarkerName=targetMarkerName;
 			    newMarker.setAttribute(IMarker.CHAR_START, markerStart);
 			   	newMarker.setAttribute(IMarker.CHAR_END, markerEnd);
 			   	newMarker.setAttribute("markerIndex", markerIndex);
 			  	newMarker.setAttribute(IMarker.MESSAGE, "Change Marker: " + srcMarkerType + " To " + targetMarkerName);
 			 	newMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
 				newMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
 				*/
 				//System.out.println("Char_start is "+ theMarker.getAttribute(IMarker.CHAR_START));
 				//System.out.println("Char_end is "+ theMarker.getAttribute(IMarker.CHAR_END));
 				
 				//put the new marker back into the array
 				if(srcMarkerType == 0)
 				{
 					VariablesAndConstants.annotationRequestMarkers[srcIndex] = newMarker;
 				}
 				else
 				{
 					//if it is an annotation, give it back its second index
 					newMarker.setAttribute("secondIndex", srcSecondIndex);
 					VariablesAndConstants.annotationMarkers[srcIndex][VariablesAndConstants.annotationMarkerCounters[srcSecondIndex]] = newMarker;
 				}
				    
	   			//now delete what used to be there
 				oldMarker.delete();
				
			}
			else
			{
				System.out.println("marker start and end could not be extracted");
				System.out.println(markerStart + " " + markerEnd + " " + markerIndex);
			}
			
		}
		else
		{
			System.out.println("annotationMarker is null");
		}
		
		}
		catch(Exception e)
		{
			System.out.println("Exception changing markers");
			e.printStackTrace();
		}
	}
	
		//this method prepares each annotation request before it is displayed
	public static void prepareAnnotationRequest(IMarker theMarker, IResource resource)
	{
		System.err.println("MM");
		try
		{
			
			//now save the method line and get it's text
			IResource theResource = theMarker.getResource();
			IFile theFile = (IFile) theResource;
			int lineNumber = -1;
			int lineLength = -1;
			String theLineText = "";
			String trimmedText = "";
			
			int charStart = theMarker.getAttribute(IMarker.CHAR_START, -1);
			
			IDocumentProvider provider = new TextFileDocumentProvider();
			provider.connect(theFile);
			
			IDocument theDocument = provider.getDocument(theFile);
			provider.disconnect(theFile);
			
			lineNumber = theDocument.getLineOfOffset(charStart);
			lineLength = theDocument.getLineLength(lineNumber);
			theLineText = theDocument.get(charStart, lineLength);
			
			System.out.println("theLineText "+theLineText);
			
			trimmedText = theLineText.trim();
			
			System.out.println("trimmedText "+trimmedText);
			
			//save the marker index
			theMarker.setAttribute("markerIndex", VariablesAndConstants.annotationRequestsCount);
			VariablesAndConstants.annotationRequestMarkers[VariablesAndConstants.annotationRequestsCount] = theMarker;
			
			//save the marker start 
			VariablesAndConstants.markerStart[VariablesAndConstants.annotationRequestsCount] = theMarker.getAttribute(IMarker.CHAR_START, -1);
			
			VariablesAndConstants.methodNames[VariablesAndConstants.annotationRequestsCount] = trimmedText;		
			
			//and advance the count
			VariablesAndConstants.annotationRequestsCount++;	
			
		}
		catch(Exception e)
		{
			System.out.println("Exception preparing annotation request");
			e.printStackTrace();
		}
		
		
	}
	
	
	/*This method is made to prepare markers being placed. It does not handle checking for vulnerabilities
	 */
	public static void prepareAnnotationRequestSimple(IMarker theMarker, IResource resource)
	{
		System.err.println("MM Simple");
		try
		{
			//save the marker index
			theMarker.setAttribute("markerIndex", VariablesAndConstants.annotationRequestsCount);
			VariablesAndConstants.annotationRequestMarkers[VariablesAndConstants.annotationRequestsCount] = theMarker;
			
			//save the marker start 
			VariablesAndConstants.markerStart[VariablesAndConstants.annotationRequestsCount] = theMarker.getAttribute(IMarker.CHAR_START, -1);	
			
			//and advance the count
			VariablesAndConstants.annotationRequestsCount++;	
			
		}
		catch(Exception e)
		{
			System.out.println("Exception preparing annotation request in jsp");
			e.printStackTrace();
		}
		
		
	}
	
	public static int[] getCharStartFromLineNumber(int lineNumber, IFile theFile)
	{
		int charPositions[] = new int[2];
		try
		{
		IDocumentProvider provider = new TextFileDocumentProvider();
		provider.connect(theFile);
		IDocument theDocument = provider.getDocument(theFile);
		provider.disconnect(theFile);
		int stringLineOffset;
		int stringLineLength;
		int stringLineEnd;
		stringLineOffset = theDocument.getLineOffset(lineNumber);
		stringLineLength = theDocument.getLineLength(lineNumber);
		stringLineEnd = stringLineOffset + stringLineLength;
		charPositions[0] = stringLineOffset;
		charPositions[1] = stringLineEnd;
		}
		catch(Exception e){
			System.out.println("exception in getCharStartFromLineNumber");
		}
		return charPositions;
	}
	
	public static String getResolutionDescription(IMarker theMarker)
	{
		String chunkOne = VariablesAndConstants.chunkOne;
		String chunkTwo = VariablesAndConstants.chunkTwo;
		String displayedCode = "";
		String completeText = "Sorry, no text available";
		String theFileName = "";
		
		int lineNumber = -1;
		int lineLength = -1;
		int shownLineNumber = -1;
		
		IResource theResource = theMarker.getResource();
		
		//IFile extends IResource so this is possible
		IFile theFile = (IFile) theResource;
		theFileName = theFile.getName();
		
		
		//now convert ifile to idocment so we can convert charstart to line number
		try
		{
			int charStart = theMarker.getAttribute(IMarker.CHAR_START, -1);
			IDocumentProvider provider = new TextFileDocumentProvider();
			provider.connect(theFile);
			IDocument theDocument = provider.getDocument(theFile);
			provider.disconnect(theFile);
			
			lineNumber = theDocument.getLineOfOffset(charStart);
			//the real line number is always the line before what eclipse shows as the line number
			shownLineNumber = lineNumber + 1;
			lineLength = theDocument.getLineLength(lineNumber);
			displayedCode = theDocument.get(charStart, lineLength);
			
			completeText = chunkOne + shownLineNumber + " " + theFileName + "<P>" + displayedCode + chunkTwo;
			
			System.out.println("completeText "+ completeText);
			
		
		}
		catch(Exception e)
		{
			System.out.println("Exception in getResolutionDescription");
		}
		
		
		return completeText;
	}
	
	public static void checkForVulnerabilities()
	{
		/* This method is very complex and took a long time to write and debug so it has been heavily commented.
		 * Directly editing this method is discouraged when possible due to the ease of introducing bugs.
		 * This method has been designed to identify requests for annotation which are exactly the same.
		 * If two requests for annotation are the same and only one has a matching annotation, a vulnerability
		 * has likely been detected. To accomplish this goal, the method finds matching annotation requests and
		 * then throws these into different groups. Once this is done, it goes through each group and changes
		 * the corresponding markers from annotation requests to vulnerability detections.
		 */
		
		
		String tempCompare = ""; //this is a holder for an annotation request which is about to be compared
		String tempCompare2 = ""; //a holder for the other annotation request that it is being compared to
		int[][] matchingGroups = new int[100][100]; //this holds the groups of matching annotation request. The first dimmension is the group, the second is the annotation request reference 
		int secondTierCount = 0; //this holds the amount of annotation request references in a group
		int tempIndex; //this simply makes code look more readable by holding an annotation request reference temporarily
		boolean isForbidden = false; //whether or not an annotation request is forbidden from being thrown into another group (because it is already in a group)
		int matchesInR = 0; //total number of matching annotation requests in a group, needed to detect first match
		forbiddenArray = new int[100]; //list of annotation request references which have already been bound
		forbiddenArrayCounter = 0; //counter for above array
		int counterForI = 0; //total number of groups + 1
		int[] countersForR = new int[100]; //total amounts of annotation requests in each group..so if counterForR[1] = 5 then there are 5 matching requests in the group
		int firstTierCount = 0; //total number of groups
		int iSnapshot = 0; //gets a snapshot of i. used to tell when it changes inside inner loop
		
		for(int i = 0; VariablesAndConstants.methodNames[i] != null; i++) //for every annotation request
		{
			//set variables for inner loop to 0
			secondTierCount = 0; 
			matchesInR = 0;
			tempCompare = VariablesAndConstants.methodNames[i]; //grab the first string to compare
			for(int r = 0; VariablesAndConstants.methodNames[r] != null; r++) //loop over the array
			{
				//System.out.println("inner loop ran. i is " + i + " r is " + r + "value is " +VariablesAndConstants.methodNames[r]);
				isForbidden = checkIfForbidden(r); //see if it is matched
				if(isForbidden == false) //if it isn't already matched
				{	
					tempCompare2 = VariablesAndConstants.methodNames[r]; //grab it to compare
					if(r != i) //if we aren't comparing it to itself
					{
					if(tempCompare.equals(tempCompare2)) //if we have a match
					{
						if(i > iSnapshot) //if the outer loop has changed
						{
							firstTierCount++; //update counter
						}

						matchingGroups[firstTierCount][secondTierCount] = r; //save the match to the appropriate group
						addForbidden(r); //make it forbidden for further matches
						
						
						//System.out.println("matchting groups i is " + i + " r is " + r);
						//System.out.println("saved index is " + matchingGroups[firstTierCount][secondTierCount]);
						tempIndex = matchingGroups[firstTierCount][secondTierCount]; //set this to temp index to make printing simpler
						//System.out.println(VariablesAndConstants.methodNames[tempIndex]);
						
						secondTierCount++; //update individual annotation request position in a group
						
						if(matchesInR == 0) //if this is the first match in this inner loop
						{
							//save the first part of the match too
							matchingGroups[firstTierCount][secondTierCount] = i;
							addForbidden(i); //make sure it isn't in any other groups
							//System.out.println("saved the i. it is " + i);
							tempIndex = matchingGroups[firstTierCount][secondTierCount]; //for simplicity
							//System.out.println("the saved string is " + VariablesAndConstants.methodNames[tempIndex]);
							
							secondTierCount++; //increment total group again	
							matchesInR++; //make sure we don't get back here again in this inner loop
						}
						
						iSnapshot = i; //save this i position. it will be used to see if i changed and advance the group count by one when the inner loop runs again
						
						countersForR[firstTierCount] = secondTierCount; //there are now secondTierCount amount of annotation requests in countersForR[firstTierCount] group
						counterForI = firstTierCount + 1; //update group total
						//System.out.println("counters for r " + firstTierCount + " is " + countersForR[firstTierCount]);
						//System.out.println("counter for i is " + counterForI);
					}
					}
				}
			}
		}
		
		

		int[] missingAnnotations = new int[100]; //array of references of missing annotations in any given group
		int missingAnnotationsCounter = 0; //counter for above array
		boolean annotationFound = false; //whether annotation has been found
		int reference = 0; //used for code simplicity
		String annotationType = ""; //the type of annotation to use
		
		for(int i = 0; i < counterForI; i++) //for every group
		{
			//reset inner loop variables
			annotationFound = false;
			missingAnnotationsCounter = 0;
			missingAnnotations = new int[100];
			
			for(int r = 0; r < countersForR[i]; r++) //for every member of each group
			{			
				reference = matchingGroups[i][r]; //for simplicity
				if(hasAnnotationsRemaining(reference) == false) //if the annotation request has no annotation
				{
					missingAnnotations[missingAnnotationsCounter] = reference; //add the reference to the array
					missingAnnotationsCounter++; //update counter
				}
				else //otherwise
				{
					annotationFound = true; //we found an annotation for the request
				}	
			}
			
			if(annotationFound == true) //if any annotation requests in the group have an annotation
			{
				annotationType = "red.flag.box";
			}
			else
			{
				annotationType = PluginConstants.MARKER_ANNOTATION_REQUEST; //  PluginConstants.MARKER_ANNOTATION_REQUEST;
			}
			//set all missing annotations to red flag or reset them to yellow question
			for(int q = 0; q < missingAnnotationsCounter; q++) //for every missing annotation
			{
				int tempRef = missingAnnotations[q];
				//System.out.println("Changing annotation. i is " + i + " q is " + q + " value is " + tempRef + " annotation request string is " + VariablesAndConstants.annotationRequestMarkers[tempRef]);	
				
				IMarker theMarker = VariablesAndConstants.annotationRequestMarkers[tempRef]; //grab the associated marker
				
				changeMarker(annotationType, theMarker.getAttribute("markerIndex", -1), 0, 0); //change it
			}
			
			
		}
		
		
	}
	
	//adds annotation request to list of matched annotation requests
	public static void addForbidden(int r)
	{
		forbiddenArray[forbiddenArrayCounter] = r;
		forbiddenArrayCounter++;
	}
	
	//checks if annotation request has already been matched
	public static boolean checkIfForbidden(int r)
	{
		boolean isForbidden = false;
		for(int i = 0; i < forbiddenArrayCounter; i++)
		{
			if(forbiddenArray[i] == r)
			{
				isForbidden = true;
				break;
			}
		}
		
		return isForbidden;
	}
	
	public static void processAnnotationChanges(IWorkbenchPart theWorkbenchPart, ITextSelection tSelection)
	{
			IEditorPart target;
			//ISourceModule iSourceModule;
			IResource iResource;
			IFile iFile;
			
			target = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			IEditorInput input = target.getEditorInput();
			iFile = ((IFileEditorInput)input).getFile();
			if(iFile == null)
			{
				System.out.println("iFile is null");
			}
			else
			{
				System.out.println("iFile present. name is " + iFile.getName());
				fileName=iFile.getName();
			}
			
			IJavaElement jElement = JavaUI.getEditorInputJavaElement(input);

			ICompilationUnit theCompilationUnit = (ICompilationUnit) jElement;
			
			//iSourceModule = (ISourceModule)DLTKCore.create(iFile);
			iResource = theCompilationUnit.getResource();
			
			int char_start = tSelection.getOffset();
			int highlightLength = tSelection.getLength();
			markerStart=char_start;
			
			boolean clickedMarkerWhileHighlighting = false;
			
			for(int i = 0; i < VariablesAndConstants.annotationRequestsCount; i++)
			{
				//if the starting point of this highlight is the same as the starting point of any saved request marker
				if(char_start == VariablesAndConstants.markerStart[i])
				{
					//they clicked a marker while highlighting
					clickedMarkerWhileHighlighting = true;
					break;
				}
			}
			
			//if they just clicked to highlight
			if(VariablesAndConstants.firstHighlight == true)
			{
				VariablesAndConstants.previousLength = highlightLength;
				VariablesAndConstants.firstHighlight = false;	
			}
			else if((highlightLength == 0 && VariablesAndConstants.firstHighlight == false) || clickedMarkerWhileHighlighting == true)
			{
				//length of text is 0 and it isn't their first click. OR they have clicked a marker. They are probably done annotating.
				exitAnnotationMode();
			}
			else
			{
				//they have already clicked once
				//add a new binding or update existing binding
				IMarker existingAnnotation = VariablesAndConstants.annotationMarkers[VariablesAndConstants.currentIndex][VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]];
				if(existingAnnotation == null)
				{
					//no annotation exists yet, create one based on Highlighted area= Annotation Answer
					createAnnotation(char_start, highlightLength, iResource);
					
					//now change the annotation request
					bindRequest();
				}
				else
				{
					//annotation exists, update binding
					updateBinding(char_start, highlightLength, iResource);
				}
					
				VariablesAndConstants.previousLength = highlightLength;
				VariablesAndConstants.firstHighlight = false;
			}
			
			highlightingLength= highlightLength;
			annotatedText= tSelection.getText();
		
	}
	
	//a function to quickly remove and replace markers and adjust their bindings as the user highlights code
	public static void updateBinding(int char_start, int length, IResource iResource)
	{
		try
		{	
			VariablesAndConstants.annotationMarkers[VariablesAndConstants.currentIndex]
					[VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]]
							.delete();
			
			createAnnotation(char_start, length, iResource);
			
			bindRequest();
			
			System.out.println("update " + VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]);
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in updateBinding");
		}
	}
	
	//creates annotation
	public static void createAnnotation(int char_start, int length, IResource iResource)
	{
		try
		{
			int char_end = char_start + length;
			
			Map<String, Object> markerAttributes = new HashMap<String, Object>();	
			markerAttributes.put(IMarker.CHAR_START,
					char_start);
			markerAttributes.put(IMarker.CHAR_END, char_end);
			markerAttributes.put(IMarker.MESSAGE,	PluginConstants.MARKER_ANNOTATION_ANSWER + " Access Control Plugin Marker");
		
			markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			markerAttributes.put("markerIndex", VariablesAndConstants.currentIndex);
			markerAttributes.put("secondIndex", VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]);
	  
			
			IMarker marker = MarkerAndAnnotationUtil
					.addMarker(iResource,
							markerAttributes, PluginConstants.MARKER_ANNOTATION_ANSWER);
			
			//MM Add annotation to the ASIDE windows
			
		//	NodeFinder nodeFinder = new NodeFinder(astRoot, char_start, length);
			
			AccessControlPoint accessControlPoint= new AccessControlPoint(null, null, iResource);
			
			
			/*
			IMarker marker = iResource.createMarker(PluginConstants.ANNOTATION_ANSWER); //"green.diamond");
			
			marker.setAttribute(IMarker.CHAR_START, char_start);
			marker.setAttribute(IMarker.CHAR_END, char_end);
			marker.setAttribute(IMarker.MESSAGE, PluginConstants.ANNOTATION_ANSWER + " Access Control Plugin Marker");
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
	   		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	   		marker.setAttribute("markerIndex", VariablesAndConstants.currentIndex);
	   		marker.setAttribute("secondIndex", VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]);
	   */
	   		//references the marker in the array, associated with the request
	   		VariablesAndConstants.annotationMarkers[VariablesAndConstants.currentIndex][VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]] = marker;
	   		
	   		System.out.println(VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]);
	   		
		}
		catch(Exception e)
		{
			
		}
	}
	
	//changes annotation requests
	public static void bindRequest()
	{
		InterfaceUtil.changeMarker(PluginConstants.MARKER_ANNOTATION_CHECKED , VariablesAndConstants.currentIndex, 0, VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]);
   		
		InterfaceUtil.checkForVulnerabilities();
	}
	
	public static void exitAnnotationMode()
	{
		try
		{		   
			VariablesAndConstants.previousLength = 0;
			VariablesAndConstants.firstHighlight = true;
			VariablesAndConstants.isAnnotatingNow = false;
			
			//update annotations per request count
	    	VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]++;
	    	
	    	//set highlighting back to box
	    	clearAndSetHighlighting(0, null,randomId);
			
	   		//now dispose of selection listener
	   		ISelectionService theSelectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
	   		if(theSelectionService != null && VariablesAndConstants.currentSelectionListener != null)
	   		{
	   			theSelectionService.removeSelectionListener(VariablesAndConstants.currentSelectionListener);
	   		}
		}
		catch(Exception e)
		{
			System.out.println("EXCEPTION leaving annotation mode");
		}
	}
	
	//marker number is the type of marker which is calling the function. for now, only 0 and 1 are used, but more can be added in the switch statement
	//this method sets all markers to boxes and then sets highlighting for active markers
	public static void clearAndSetHighlighting(int markerNumber, IMarker callingMarker,String randomID)
	{
		// Maybe commenting out all lines removes the problem
		randomId=randomID;

		
		IMarker tempRequestMarker;
		int tempIndex = -1;
		String markerType;
		int callingIndex = -5; //-5 because -1 is used below
		int secondIndex = -5;
		if(callingMarker != null) //calling marker is null when this is called after annotation is completed
		{
			// Default = -1 it cannot detect the markerindex attribute 
			callingIndex = callingMarker.getAttribute("markerIndex", -1);
			secondIndex = callingMarker.getAttribute("secondIndex", -1); //this will be -1 if the marker is an annotation request. This is ok since changeAnnotationHighlighting handles it properly
			
			IResource theResource = callingMarker.getResource();
			IFile theFile = (IFile) theResource;
			fileName = theFile.getName();
			
			markerStart = callingMarker.getAttribute(IMarker.CHAR_START, -1);
			int markerEnd=callingMarker.getAttribute(IMarker.CHAR_END, -1);
			highlightingLength=markerEnd- markerStart;
		}
		
		//first make everything a box instead of highlighted
		for(int i = 0; i < VariablesAndConstants.annotationRequestsCount; i ++)
		{
			tempRequestMarker = VariablesAndConstants.annotationRequestMarkers[i];
			tempIndex = tempRequestMarker.getAttribute("markerIndex", -1);
				
			if(callingMarker != null)
			{
				changeAnnotationHighlighting("green.diamond.box", tempIndex, secondIndex);
			}
			else
			{
				changeAnnotationHighlighting("green.diamond.box", tempIndex, -5);
			}
			
			if(callingIndex != tempIndex)
			{		
				try
				{
					markerType = VariablesAndConstants.annotationRequestMarkers[i].getType();
										
					if(markerType.equals(PluginConstants.MARKER_ANNOTATION_CHECKED) == true)
					{
						changeMarker(PluginConstants.MARKER_GREEN_CHECK_BOX, tempIndex, 0, 0);
						
					}
					else if(markerType.equals("red.flag") == true)
					{
						changeMarker("red.flag.box", tempIndex, 0, 0);
						
					}
					else if(markerType.equals("yellow.question") == true)
					{
						changeMarker(PluginConstants.MARKER_ANNOTATION_REQUEST, tempIndex, 0, 0);
						
					}
					
					
				}	
				catch(Exception e)
				{
					System.out.println("Exception in clearAndSetHighlighting");
				}	
			}
			else
			{
				//don't do anything to the selected marker to avoid breaking the resolutions menu
			}
		}
		if(callingMarker != null)
		{
		try {
			String markerName=callingMarker.getType();
			System.out.println("MarkerNameCheck"+markerName);
			
			if(markerName.equals(PluginConstants.MARKER_ANNOTATION_REQUEST) == true)
				annotationMarkerName=PluginConstants.MARKER_GREEN_CHECK_BOX;
			
			else if(markerName.equals("red.question.box") == true)
				annotationMarkerName="red.flag.box";
			
		//	saveAnnotationsToCSV();
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if(callingMarker != null)
		{
		
			switch (markerNumber)
			{
			case 0:
			{
				//case 0 means green diamond called it so change the check
				changeMarker(PluginConstants.MARKER_ANNOTATION_CHECKED, callingIndex, 0, 0);
				break;
			}
			case 1:
			{
				//case 1 means green check called it so change the diamond
				changeAnnotationHighlighting("green.diamond", callingIndex, secondIndex);
				break;
			}
			default:
			{
				//nothing for now
				break;
			}
			}
		}
			
	}
	
	//This function checks to see whether the an annotation request (firstIndex) has any remaining annotations
	public static boolean hasAnnotationsRemaining(int firstIndex)
	{
		boolean hasAnnotations = false;
		IMarker tempMarker = null;
		for(int i = 0; i <= VariablesAndConstants.annotationMarkerCounters[firstIndex]; i++) //for every possible annotation for this request
		{
			tempMarker = VariablesAndConstants.annotationMarkers[firstIndex][i]; //grab the annotation
			if(tempMarker != null) //if one is found to not be null
			{
				System.out.println("Annotation found at position " + i);
				hasAnnotations = true; //the annotation request has at least one annotation associated with it.
				break;
			}
		}
		
		return hasAnnotations;
	}
	
	//changes all annotation highlighting for a given request marker reference except the selected annoation marker if an annotation marker is selected.
	//if you know that no annotation marker is selected, pass in a negative value for selectedMarkerSecondIndex
	public static void changeAnnotationHighlighting(String markerName, int firstIndex, int selectedMarkerSecondIndex)
	{ 
		//RETURN TO THIS LATER. SOME BUG HERE
		//issue with resseting highlighting on multiple annotations on same request. The first annotation seems to "break" and when deleting and recreating annotations
		
		IMarker tempMarker = null;
		for(int i = 0; i <= VariablesAndConstants.annotationMarkerCounters[firstIndex]; i++) //for every possible annotation for this request
		{
			if(selectedMarkerSecondIndex != i)
			{
				tempMarker = VariablesAndConstants.annotationMarkers[firstIndex][i]; //grab the annotation
				if(tempMarker != null)
				{
					if(tempMarker.exists() == true)
					{
						try
						{
						changeMarker(markerName, firstIndex, 1, i);
						System.out.println("changing marker first index is " + VariablesAndConstants.annotationMarkers[firstIndex][i].getAttribute("markerIndex") );
						System.out.println("Annotation changed at position i" + i);
						System.out.println("i is " + i);
						System.out.println("Selected marker index is " + selectedMarkerSecondIndex);
						
						// saveAnnotationsToCSV();
						}
						catch(Exception e)
						{
							System.out.println("Exception in changeAnnotationHighlighting");
						}
					}
					else
					{
						System.out.println("marker does not exist");
					}
				}
				else
				{
					System.out.println("tempmarker is null");
				}
			}
			else
			{
				System.out.println("annotation matches i");
				//do nothing. the selected marker is the same one about to be changed. This would make the annotation vanish.
			}
			System.out.println("end of loop i is " + i);
		}
		
		
	}
	private static void saveAnnotationsToCSV() throws IOException {
		//String csv = "C:\\Users\\Nasheen Nur\\Desktop\\AnnotationCSV.csv";
		System.out.println("Before CSV write "+annotationMarkerName+" "+fileName+" " +markerStart+" " +highlightingLength+" "+annotatedText);
		try {
			java.net.URL location = InterfaceUtil.class.getProtectionDomain().getCodeSource().getLocation();

		    FileWriter writer = new FileWriter(location.getFile()+"AnnotationCSV.csv", true);
		    
		    writer.append(annotationMarkerName);
		    writer.append(',');
		    writer.append(fileName);
		    writer.append(',');
		    writer.append(Integer.toString(markerStart));
		    writer.append(',');
		    writer.append(Integer.toString(highlightingLength));
		    writer.append(',');
		    writer.append(annotatedText);
		    writer.append(',');
		    writer.append(randomId);
		    writer.append('\n');
		    writer.flush();
		    writer.close();
		} catch (Exception e) {} 
	
	    System.out.println("done!");
	}
	public static void fakeVulnerabilities()
	{
		/* do nothing for now. Use this for studies
		changeMarker("red.flag.box", 3, 0, 0);
		changeMarker("red.flag.box", 6, 0, 0);
		
		IMarker firstMarker = VariablesAndConstants.annotationRequestMarkers[3];
		IMarker secondMarker = VariablesAndConstants.annotationRequestMarkers[6];
		
		//now set the marker ids so that they can be identified for the faked warning messages
		try
		{
			firstMarker.setAttribute("markerIdentifier", 1);
			secondMarker.setAttribute("markerIdentifier", 2);
		}
		catch(Exception e)
		{
			System.out.println("Exception setting marker id's in fakeVulnerabilities method");
		}
		
		*/
	}

	public static void removeMarker(IMarker marker)
	{
		try {
			marker.delete();
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int removeAssociatedAnnotations(IMarker marker)
	{
		int markerIndex = -1;
		try
		{
			markerIndex = (int)marker.getAttribute("markerIndex", -1);
			if( markerIndex == -1 ) return -1;
			
			for(int i = 0; i < VariablesAndConstants.annotationMarkerCounters[markerIndex]; i++)
			{
				IMarker theAnnotation = VariablesAndConstants.annotationMarkers[markerIndex][i];
				if(theAnnotation != null)
				{
					theAnnotation.delete();
	    			VariablesAndConstants.annotationMarkers[markerIndex][i] = null; //so checkForVulnerabilities will work
				}
				
			}
			
		}
		catch(Exception e)
		{
			System.out.println("exception deleting marker");
		}
		
		return markerIndex;
		
	}

	public static void deleteWorkspaceMarkers(String markerType){
		IJavaProject javaProject ;
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
	
		for(IProject project : allProjects){
			if(project.isOpen() && !project.getName().equalsIgnoreCase("RemoteSystemsTempFiles"))
			{
				javaProject= null;
				javaProject = JavaCore.create(project);  
	
				if(javaProject == null )				
					continue;
	
				InterfaceUtil.deleteProjectMarkers(javaProject, markerType);
			}
		}
		
		/* NOT WORKED */
		VariablesAndConstants.isAnnotatingNow = false;
		 VariablesAndConstants.annotationRequestMarkers = new IMarker[100];
		 VariablesAndConstants.annotationMarkers = new IMarker[100][100]; //first dimension is which request is associated with it. The second is which annotation so you can have many annotations per request.
		 VariablesAndConstants.annotationMarkerCounters = new int[100]; //keeps track of how many annotations exist for each request. total amount for each request is this + 1.
		 VariablesAndConstants.methodNames = new String[100];
		 VariablesAndConstants.dynamicCode = new String[100];
		 VariablesAndConstants.markerStart = new int[100];
		
		//count is total amount of annotation request markers;
		 VariablesAndConstants.annotationRequestsCount = 0;
		 VariablesAndConstants.currentIndex = -1;
	
		 VariablesAndConstants.previousText = "";
		 VariablesAndConstants.previousLength = -1;
		 VariablesAndConstants.firstHighlight = true;
		
		
		/**/
	
	}

	public static void deleteProjectMarkers(IJavaProject project, String markerType){
		
		/*
	    if(project != null){
	    	try{
	    		IMarker[] markers = project.getCorrespondingResource().findMarkers(markerType, true, IResource.DEPTH_INFINITE);
	    		
	    		project.getCorrespondingResource().deleteMarkers( //PluginConstants.ASIDE_MARKER_TYPE
	    				markerType, true,
	    				IResource.DEPTH_INFINITE);
	
	    		ModelCollector model=  ModelRegistry.getPathCollectorForProject(project.getProject());
	    		model.removeAllInputValidation();
	    		
	    	}catch(Exception e){
	    		System.err.println("project.get1");
	    		e.printStackTrace();
	    	}
	    }
	
	*/
		try {
			
		
		for (IPackageFragment fragment : project.getPackageFragments()) {
			
			for (ICompilationUnit unit : fragment.getCompilationUnits()) {
				
				InterfaceUtil.deleteCompilationUnitMarkers(unit, markerType);
			}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			System.err.println("project.get2");
			//e.printStackTrace();
			return;
		}
	}
	
	
	public static void clearArrays()
	{
	VariablesAndConstants.annotationRequestsCount = 0;
	VariablesAndConstants.currentIndex = -1;
	
	VariablesAndConstants.previousText = "";
	VariablesAndConstants.previousLength = -1;
	VariablesAndConstants.firstHighlight = true;
	
	VariablesAndConstants.isAnnotatingNow = false;
	VariablesAndConstants.annotationRequestMarkers = new IMarker[100];
	VariablesAndConstants.annotationMarkers = new IMarker[100][100]; //first dimension is which request is associated with it. The second is which annotation so you can have many annotations per request.
	VariablesAndConstants.annotationMarkerCounters = new int[100]; //keeps track of how many annotations exist for each request. total amount for each request is this + 1.
	VariablesAndConstants.methodNames = new String[100];
	VariablesAndConstants.dynamicCode = new String[100];
	VariablesAndConstants.markerStart = new int[100];
	
	
	}

	public static void deleteCompilationUnitMarkers(ICompilationUnit unit, String markerType){
			try {
				IResource resource = unit.getUnderlyingResource();
				//markerType="edu.uncc.sis.aside.AsideMarker"
				IMarker[] markers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
				for(IMarker marker :markers){
					
					marker.delete();
					
					
					
				}
				clearArrays();
				
				//MM Markers should be deleted from Tyler's code
				
				markers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
				
				System.out.println("Markers in " + unit.getElementName() + " is " + markers.length);
			
			} catch (JavaModelException e) {
				System.err.println("project.get3");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				System.err.println("project.get4");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
	/*
		public static void saveMarkersToFile(String markerType,String fileName){
	
		for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
			
				if(p.isOpen() && !p.getName().equalsIgnoreCase("RemoteSystemsTempFiles"))
				{
					try {
						IPath location = p.getLocation();
						
						FileWriter writer = new FileWriter(location.append(fileName).toString(), false);
	
						for( IMarker marker : p.findMarkers(markerType, true, IResource.DEPTH_INFINITE) )
						{
							writer.append(marker.getType());
							writer.append(',');
							writer.append(marker.getResource().getProjectRelativePath().toString());
							
							writer.append(',');
							writer.append(marker.getAttribute(IMarker.CHAR_START).toString());
							
							writer.append(',');
							writer.append(marker.getAttribute(IMarker.CHAR_END).toString());
							
							writer.append(',');
							writer.append(marker.getAttribute(IMarker.LINE_NUMBER).toString());
							
							writer.append(',');
							writer.append(marker.getAttribute(IMarker.SEVERITY).toString());
							
							writer.append(',');
							writer.append(marker.getAttribute(IMarker.PRIORITY).toString());
							
							writer.append(',');
							writer.append(marker.getAttribute(IMarker.MESSAGE).toString());	
							
							writer.append(',');
							writer.append( marker.getAttribute("creationTime").toString());	
							
							writer.append(',');
							writer.append( marker.getAttribute("userID").toString());
							
							writer.append('\n');
						}
						writer.flush();
						writer.close();
						
	
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				
				}//if   
			}
		}
	
		public static void restoreMarkersFromFile(String fileName)
		{
			String line;
			String[] fields ;
			IMarker marker;
			IPath location;
			FileReader reader ;
			BufferedReader bufferedReader ;
		
			for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
				
				if(p.isOpen() && !p.getName().equalsIgnoreCase("RemoteSystemsTempFiles"))
				{
					try {
						location = p.getLocation();
						
						reader = new FileReader(location.append(fileName).toString());
						
						bufferedReader = new BufferedReader(reader);
											
						while ((line = bufferedReader.readLine()) != null) {
	
							fields = line.trim().split(",");
							if( (fields== null) || (fields.length < 10)) continue;
							//ResourcesPlugin.getWorkspace().getRoot().get
							IJavaProject javaProject = JavaCore.create(p);
							IResource resource;
							String name;
							
							for (IPackageFragment fragment : javaProject.getPackageFragments()) {							
								for (ICompilationUnit unit : fragment.getCompilationUnits()) {
									
									name= unit.getResource().getProjectRelativePath().toString();
									
									if(fields[1].equalsIgnoreCase(name))
									{
										Map<String, Object> markerAttributes = new HashMap<String, Object>();	
										
										markerAttributes.put(IMarker.CHAR_START,Integer.parseInt(fields[2]) );
										markerAttributes.put(IMarker.CHAR_END,Integer.parseInt(fields[3]) );
										markerAttributes.put(IMarker.LINE_NUMBER,	Integer.parseInt(fields[4]));
										markerAttributes.put(IMarker.MESSAGE,fields[7]);
										markerAttributes.put(IMarker.SEVERITY, Integer.parseInt(fields[5]));
										markerAttributes.put(IMarker.PRIORITY, Integer.parseInt(fields[6]));
										markerAttributes.put("CurrentTime", fields[8]);
										markerAttributes.put("UserId",fields[9]);
										
										MarkerAndAnnotationUtil
												.addMarker(unit.getResource(),
														markerAttributes, fields[0]);
										
									}
	
	
								}
							}
						}//while
						bufferedReader.close();
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				
				}//if   
			}//for projects
	
		}//function
	*/
	 
	
}
