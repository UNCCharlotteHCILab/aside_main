package edu.uncc.aside.codeannotate.visitors;

import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.PathCollector;
import edu.uncc.aside.codeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public interface IModelVisitor {

	public void visit(PathCollector pathCollector);
	public void visit(Path path);
	public void visit(Point point);
	
}
