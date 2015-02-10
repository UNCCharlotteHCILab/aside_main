package edu.uncc.aside.codeannotate.asideInterface;

import java.lang.reflect.Array;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.php.internal.core.ast.nodes.ITypeBinding;
import org.eclipse.swt.graphics.RGB;
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

//import edu.uncc.aside.codeannotate.NodeFinder;
//import edu.uncc.aside.codeannotate.util.Utils;

public  class InterfaceUtil 
{
	
	private static int[] forbiddenArray = new int[100];
	private static int forbiddenArrayCounter = 0;
	
	//changes a marker type, like a yellow question into a green check. markerType of 0 means it is an annotation request. markerType of 1 means it is an annotation
	public static void changeMarker(String markerName, int theIndex, int markerType, int secondIndex)
	{
		try
		{
		IMarker annotationMarker;
		if(markerType == 0)
		{
			annotationMarker = VariablesAndConstants.annotationRequestMarkers[theIndex];
		}
		else
		{
			System.out.println ("counters in change markers is " + VariablesAndConstants.annotationMarkerCounters[theIndex]);
			System.out.println("changemarker is " + VariablesAndConstants.annotationMarkers[theIndex][VariablesAndConstants.annotationMarkerCounters[secondIndex]]);
			annotationMarker = VariablesAndConstants.annotationMarkers[theIndex][VariablesAndConstants.annotationMarkerCounters[secondIndex]];
		}
		
		if(annotationMarker != null)
		{
			int markerStart = annotationMarker.getAttribute(IMarker.CHAR_START, -1);
			int markerEnd = annotationMarker.getAttribute(IMarker.CHAR_END, -1);
			int markerIndex = annotationMarker.getAttribute("markerIndex", -1);
			//if the marker attributes are not invalid
			if(markerStart != -1 && markerEnd != -1 && markerIndex != -1)
			{
				//create new marker and delete the old one
				IResource targetResource = annotationMarker.getResource();	
 			    IMarker theMarker = targetResource.createMarker(markerName);
 			    theMarker.setAttribute(IMarker.CHAR_START, markerStart);
 			   	theMarker.setAttribute(IMarker.CHAR_END, markerEnd);
 			   	theMarker.setAttribute("markerIndex", markerIndex);
 			  	theMarker.setAttribute(IMarker.MESSAGE, "Access Control Plugin Marker");
 			 	theMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
 				theMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
 				
 				//System.out.println("Char_start is "+ theMarker.getAttribute(IMarker.CHAR_START));
 				//System.out.println("Char_end is "+ theMarker.getAttribute(IMarker.CHAR_END));
 				
 				//put the new marker back into the array
 				if(markerType == 0)
 				{
 					VariablesAndConstants.annotationRequestMarkers[theIndex] = theMarker;
 				}
 				else
 				{
 					//if it is an annotation, give it back its second index
 					theMarker.setAttribute("secondIndex", secondIndex);
 					VariablesAndConstants.annotationMarkers[theIndex][VariablesAndConstants.annotationMarkerCounters[secondIndex]] = theMarker;
 				}
				    
	   			//now delete what used to be there
 				annotationMarker.delete();
 				
				
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
	
	public static void createMarker(String markerType, int charStart, int charEnd, IResource theResource)
	{
		try
		{
			String message = "Where is the access control?";
			if(markerType.equals("yellow.question"))
			{
				message = "Where is the access control?";
			}
			IMarker theMarker = theResource.createMarker(markerType);
			theMarker.setAttribute(IMarker.CHAR_START, charStart);
		   	theMarker.setAttribute(IMarker.CHAR_END, charEnd);
		   	//not setting marker index in this function
		   	//theMarker.setAttribute("markerIndex", markerIndex);
		  	theMarker.setAttribute(IMarker.MESSAGE, message);
		 	theMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			theMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			
			InterfaceUtil.prepareAnnotationRequest(theMarker, theResource);
		}
		catch(Exception e)
		{
			System.out.println("Exception creating marker in createMarker");
		}
	}
	
	//this method prepares each annotation request before it is displayed
	public static void prepareAnnotationRequest(IMarker theMarker, IResource resource)
	{
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
			trimmedText = theLineText.trim();
			
			//save the marker index
			theMarker.setAttribute("markerIndex", VariablesAndConstants.count);
			VariablesAndConstants.annotationRequestMarkers[VariablesAndConstants.count] = theMarker;
			
			//save the marker start 
			VariablesAndConstants.markerStart[VariablesAndConstants.count] = theMarker.getAttribute(IMarker.CHAR_START, -1);
			
			VariablesAndConstants.methodNames[VariablesAndConstants.count] = trimmedText;		
			
			//and advance the count
			VariablesAndConstants.count++;	
			
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
		
		try
		{
			//save the marker index
			theMarker.setAttribute("markerIndex", VariablesAndConstants.count);
			VariablesAndConstants.annotationRequestMarkers[VariablesAndConstants.count] = theMarker;
			
			//save the marker start 
			VariablesAndConstants.markerStart[VariablesAndConstants.count] = theMarker.getAttribute(IMarker.CHAR_START, -1);	
			
			//and advance the count
			VariablesAndConstants.count++;	
			
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
				annotationType = "yellow.question.box";
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
			ISourceModule iSourceModule;
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
			}
			
			IJavaElement jElement = JavaUI.getEditorInputJavaElement(input);

			ICompilationUnit theCompilationUnit = (ICompilationUnit) jElement;
			
			//iSourceModule = (ISourceModule)DLTKCore.create(iFile);
			iResource = theCompilationUnit.getResource();
			
			int char_start = tSelection.getOffset();
			int highlightLength = tSelection.getLength();
			boolean clickedMarkerWhileHighlighting = false;
			
			for(int i = 0; i < VariablesAndConstants.count; i++)
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
					//no annotation exists yet, create one
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
		
	}
	
	//a function to quickly remove and replace markers and adjust their bindings as the user highlights code
	public static void updateBinding(int char_start, int length, IResource iResource)
	{
		try
		{	
			VariablesAndConstants.annotationMarkers[VariablesAndConstants.currentIndex][VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]].delete();
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
			IMarker greenDiamond = iResource.createMarker("green.diamond");
			greenDiamond.setAttribute(IMarker.CHAR_START, char_start);
			greenDiamond.setAttribute(IMarker.CHAR_END, char_end);
			greenDiamond.setAttribute(IMarker.MESSAGE, "Access Control Plugin Marker");
			greenDiamond.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
	   		greenDiamond.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	   		greenDiamond.setAttribute("markerIndex", VariablesAndConstants.currentIndex);
	   		greenDiamond.setAttribute("secondIndex", VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]);
	   
	   		//references the marker in the array, associated with the request
	   		VariablesAndConstants.annotationMarkers[VariablesAndConstants.currentIndex][VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]] = greenDiamond;
	   		System.out.println(VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]);
	   		
		}
		catch(Exception e)
		{
			
		}
	}
	
	//changes annotation requests
	public static void bindRequest()
	{
		InterfaceUtil.changeMarker("green.check", VariablesAndConstants.currentIndex, 0, VariablesAndConstants.annotationMarkerCounters[VariablesAndConstants.currentIndex]);
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
	    	clearAndSetHighlighting(0, null);
			
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
	public static void clearAndSetHighlighting(int markerNumber, IMarker callingMarker)
	{

		IMarker tempRequestMarker;
		int tempIndex = -1;
		String markerType;
		int callingIndex = -5; //-5 because -1 is used below
		int secondIndex = -5;
		if(callingMarker != null) //calling marker is null when this is called after annotation is completed
		{
			callingIndex = callingMarker.getAttribute("markerIndex", -1);
			secondIndex = callingMarker.getAttribute("secondIndex", -1); //this will be -1 if the marker is an annotation request. This is ok since changeAnnotationHighlighting handles it properly
		}
		
		//first make everything a box instead of highlighted
		for(int i = 0; i < VariablesAndConstants.count; i ++)
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
					if(markerType.equals("green.check") == true)
					{
						changeMarker("green.check.box", tempIndex, 0, 0);
					}
					else if(markerType.equals("red.flag") == true)
					{
						changeMarker("red.flag.box", tempIndex, 0, 0);
					}
					else if(markerType.equals("yellow.question") == true)
					{
						changeMarker("yellow.question.box", tempIndex, 0, 0);
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
		
			switch (markerNumber)
			{
			case 0:
			{
				//case 0 means green diamond called it so change the check
				changeMarker("green.check", callingIndex, 0, 0);
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
	
	public static void fakeVulnerabilities()
	{
		/* do nothing for now
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
	 
	
}
