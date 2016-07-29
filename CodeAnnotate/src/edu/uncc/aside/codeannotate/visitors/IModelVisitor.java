package edu.uncc.aside.codeannotate.visitors;

import edu.uncc.aside.codeannotate.models.InputValidationPoint;
import edu.uncc.aside.codeannotate.models.OutputEncodingPoint;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
import edu.uncc.aside.codeannotate.models.SQLInjectionPoint;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public interface IModelVisitor {

	public void visit(ModelCollector modelCollector);
	public void visit(Path path);
	public void visit(AccessControlPoint accessControlPoint);
	public void visit(InputValidationPoint inputValidationPoint);
	public void visit(OutputEncodingPoint outputEncodingPoint);
	public void visit(SQLInjectionPoint sqlInjectionPoint);
	
}
