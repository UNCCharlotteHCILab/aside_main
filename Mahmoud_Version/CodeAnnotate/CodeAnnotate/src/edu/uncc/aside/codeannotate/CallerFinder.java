package edu.uncc.aside.codeannotate;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import edu.uncc.aside.codeannotate.MethodSearchRequestor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.search.JavaSearchScope;

/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * 
 */
public class CallerFinder {

	
	private static IJavaSearchScope fSearchScope;
	
	public static Collection/*<MethodUnitPair>*/ findCallers(IProgressMonitor progressMonitor, String methodName, IJavaProject project, boolean isConstructor) {
        
		try {
			
			if(progressMonitor == null)
				progressMonitor = new NullProgressMonitor();
			
			MethodSearchRequestor.initializeParserMap();
			
        	SearchRequestor searchRequestor =  (SearchRequestor) new MethodSearchRequestor.MethodReferencesSearchRequestor();
			
            SearchEngine searchEngine = new SearchEngine();

            IProgressMonitor monitor = new SubProgressMonitor(progressMonitor, 5, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
            
            monitor.beginTask("Searching for calls to " + methodName + (project != null ? " in " + project.getProject().getName() : ""), 100);
            
         //   System.out.println("\n MM.."+ "Searching for calls to " + methodName + (project != null ? " in " + project.getProject().getName() : "")+ ": ");
            
            IJavaSearchScope searchScope = getSearchScope(project);
            
            int matchType = !isConstructor ? IJavaSearchConstants.METHOD : IJavaSearchConstants.CONSTRUCTOR;
            
            SearchPattern pattern = SearchPattern.createPattern(
            		methodName, 
					matchType,
					IJavaSearchConstants.REFERENCES,
					SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
            
            searchEngine.search(
            		pattern, 
					new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
                    searchScope, 
					searchRequestor, 
					monitor
			);

            if(searchRequestor instanceof MethodSearchRequestor.MethodDeclarationsSearchRequestor){                
                return ((MethodSearchRequestor.MethodDeclarationsSearchRequestor)searchRequestor).getMethodUnitPairs();
            }else{
                return ((MethodSearchRequestor.MethodReferencesSearchRequestor)searchRequestor).getMethodUnitPairs();
            }
            
        } catch (CoreException e) {

            return new LinkedList();
        }
    }
    
    public static Collection/*<MethodUnitPair>*/ findDeclarations(IProgressMonitor progressMonitor, String methodName, IJavaProject project, boolean isConstructor) {
        try {
            SearchRequestor searchRequestor = new MethodSearchRequestor.MethodDeclarationsSearchRequestor(); 
            SearchEngine searchEngine = new SearchEngine();

            IProgressMonitor monitor = new SubProgressMonitor(
                    progressMonitor, 5, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
            monitor.beginTask("Searching for calls to " + 
                    methodName + (project != null ? " in " + project.getProject().getName() : ""), 100);            
            IJavaSearchScope searchScope = getSearchScope(project);
           
            int matchType = !isConstructor ? IJavaSearchConstants.METHOD : IJavaSearchConstants.CONSTRUCTOR;
            SearchPattern pattern = SearchPattern.createPattern(
                    methodName, 
                    matchType,
                    IJavaSearchConstants.DECLARATIONS,
                    SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE );
            
            searchEngine.search(
                    pattern, 
                    new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() },
                    searchScope, 
                    searchRequestor, 
                    monitor
                    );

            if(searchRequestor instanceof MethodSearchRequestor.MethodDeclarationsSearchRequestor){                
                return ((MethodSearchRequestor.MethodDeclarationsSearchRequestor)searchRequestor).getMethodUnitPairs();
            }else{
                return ((MethodSearchRequestor.MethodReferencesSearchRequestor)searchRequestor).getMethodUnitPairs();
            }
        } catch (CoreException e) {

            return new LinkedList();
        }
    }
    
    public static IJavaSearchScope getSearchScope(IJavaProject project) {
    	if (fSearchScope == null) {
            fSearchScope = SearchEngine.createWorkspaceScope();
        	//fSearchScope = SearchEngine.createJavaSearchScope(new IResource[] {method.getResource()});
        }
//    	return fSearchScope;

    	if(project == null) {	        
	        return fSearchScope;
    	} else {
    		JavaSearchScope js = new JavaSearchScope();
    		try {
    			int includeMask = 
    				JavaSearchScope.SOURCES | 
					JavaSearchScope.APPLICATION_LIBRARIES | 
					JavaSearchScope.SYSTEM_LIBRARIES ;
				js.add((JavaProject) project, includeMask, new HashSet());
			} catch (JavaModelException e) {
				return fSearchScope;
			}
    		return js;
    	} 
    }    	
}
