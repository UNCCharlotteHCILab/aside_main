package edu.uncc.aside.codeannotate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.corext.refactoring.structure.ASTNodeSearchUtil;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class MethodSearchRequestor {

	static Map<ICompilationUnit, CompilationUnit> parserMap;

	// Settings
	final static boolean SKIP_NOT_SOURCE = false;
	final static boolean SKIP_BINARY = true;

	static CompilationUnit retrieveCompilationUnit(ICompilationUnit unit) {
		CompilationUnit cu = (CompilationUnit) parserMap.get(unit);
		if (cu == null) {
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(unit);
			parser.setResolveBindings(true);
			cu = (CompilationUnit) parser.createAST(null);

			parserMap.put(unit, cu);
		}

		return cu;
	}

	static void initializeParserMap() {

		parserMap = new HashMap<ICompilationUnit, CompilationUnit>();

	}

	public static class MethodDeclarationsSearchRequestor extends
			SearchRequestor {

		public class MethodDeclarationlResultCollector {
			ArrayList<Utils.MethodDeclarationUnitPair> _methodDeclarationUnitPairs;

			MethodDeclarationlResultCollector() {
				_methodDeclarationUnitPairs = new ArrayList<Utils.MethodDeclarationUnitPair>();
			}

			public void addCaller(MethodDeclaration mi, CompilationUnit cu,
					IResource resource, IMember member) {
				_methodDeclarationUnitPairs
						.add(new Utils.MethodDeclarationUnitPair(mi, cu,
								resource, member));
			}

			public Collection<Utils.MethodDeclarationUnitPair> getMethodDeclarationUnitPairs() {
				return _methodDeclarationUnitPairs;
			}
		}

		private MethodDeclarationlResultCollector fSearchResults;
		private boolean fRequireExactMatch = true;

		public MethodDeclarationsSearchRequestor() {
			fSearchResults = new MethodDeclarationlResultCollector();
		}

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {

			if (fRequireExactMatch
					&& (match.getAccuracy() != SearchMatch.A_ACCURATE)) {
				return;
			}

			if (match.isInsideDocComment()) {
				return;
			}

			if (match.getElement() != null
					&& match.getElement() instanceof IMember) {
				IMember member = (IMember) match.getElement();
				if (SKIP_BINARY && member.isBinary()) {
					return;
				}

				if (member.getCompilationUnit() == null) {

					if (!SKIP_NOT_SOURCE) {
						fSearchResults.addCaller(null, null,
								match.getResource(), member);
					}
					return;
				}

				CompilationUnit cu = retrieveCompilationUnit(member
						.getCompilationUnit());
				ASTNode node = ASTNodeSearchUtil.getAstNode(match, cu);

				MethodDeclaration md = null;

				if (node != null) {
					if (node instanceof MethodDeclaration) {
						md = (MethodDeclaration) node;
					} else if (node.getParent() instanceof MethodDeclaration) {
						md = (MethodDeclaration) node.getParent();
					} else {
						try {
							System.err
									.println("MethodDeclarationsSearchRequestor: Skipping node that appears in the search: "
											+ node
											+ " of type "
											+ node.getClass()
											+ " at line "
											+ member.getCorrespondingResource()
											+ ":"
											+ cu.getLineNumber(node
													.getStartPosition()));
						} catch (JavaModelException e) {

							return;
						}
						return;
					}
				}

				IResource resource = match.getResource();
				if (resource == null) {
					System.err.println("No resource for " + match);
					return;
				}
				fSearchResults.addCaller(md, cu, resource, member);

			}
		}

		public Collection<Utils.MethodDeclarationUnitPair> getMethodUnitPairs() {

			return fSearchResults.getMethodDeclarationUnitPairs();
		}

	}

	public static class MethodReferencesSearchRequestor extends SearchRequestor {

		public class MethodCallResultCollector {
			ArrayList<Utils.ExprUnitResourceMember> _methodUnitPairs;

			MethodCallResultCollector() {
				_methodUnitPairs = new ArrayList<Utils.ExprUnitResourceMember>();
			}

			public void addCaller(Expression expr, CompilationUnit cu,
					IResource resource, IMember member) {
				_methodUnitPairs.add(new Utils.ExprUnitResourceMember(expr, cu,
						resource, member));
			}

			public Collection<Utils.ExprUnitResourceMember> getMethodUnitPairs() {
				return _methodUnitPairs;
			}
		}

		private MethodCallResultCollector fSearchResults;
		private boolean fRequireExactMatch = true;

		public MethodReferencesSearchRequestor() {
			fSearchResults = new MethodCallResultCollector();
		}

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			if (fRequireExactMatch
					&& (match.getAccuracy() != SearchMatch.A_ACCURATE)) {

				return;
			}

			if (match.isInsideDocComment()) {

				return;
			}

			if (match.getElement() != null
					&& match.getElement() instanceof IMember) {
				IMember member = (IMember) match.getElement();

				if (SKIP_BINARY && member.isBinary()) {

					return;
				}

				if (member.getCompilationUnit() == null) {

					if (!SKIP_NOT_SOURCE) {
						fSearchResults.addCaller(null, null,
								match.getResource(), member);
					}

					return;

				}

				CompilationUnit cuNode = retrieveCompilationUnit(member
						.getCompilationUnit());
				ASTNode node = ASTNodeSearchUtil.getAstNode(match, cuNode);
				Expression expr = null;

				if (node != null) {

					if (node instanceof MethodInvocation) {
						expr = (MethodInvocation) node;
					} else if (node.getParent() instanceof MethodInvocation) {
						expr = (MethodInvocation) node.getParent();
					} else if (node instanceof ClassInstanceCreation) {
						expr = (ClassInstanceCreation) node;
					} else if (node.getParent() instanceof ClassInstanceCreation) {
						expr = (ClassInstanceCreation) node.getParent();
					} else {
						System.err.println("Unknown match type: " + node
								+ " of type " + node.getClass());
						try {
							System.err
									.println("MethodReferencesSearchRequestor: Skipping node that appears in the search: "
											+ node
											+ " of type "
											+ node.getClass()
											+ " at line "
											+ member.getCorrespondingResource()
											+ ":"
											+ cuNode.getLineNumber(node
													.getStartPosition()));
						} catch (JavaModelException e) {
							e.printStackTrace();
							return;
						}

						return;
					}
				}

				IResource resource = match.getResource();
				if (resource == null) {
					System.err.println("the match is not associated with a IResource instance...");
					return;
				}

				if (expr == null)
					System.err.println("the match is not associated with a Expression Node instance...");

				fSearchResults.addCaller(expr, cuNode, resource, member);
			} else {
				System.err.println("Skipping match: " + match);
			}

		}

		public Collection<Utils.ExprUnitResourceMember> getMethodUnitPairs() {

			return fSearchResults.getMethodUnitPairs();
		}

	}

}
