package edu.uncc.aside.codeannotate.listeners;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
import org.eclipse.jdt.core.BufferChangedEvent;
import org.eclipse.jdt.core.IBufferChangedListener;



/**
 * Coordinate with {@link Messenger} to communicate information with {@link CodeAnnotateElementChangeListener}
 * @author Jing Xie (jxie2@uncc.edu)
 *
 */
public class CodeAnnotateDocumentEditListener implements IBufferChangedListener {
	
	public CodeAnnotateDocumentEditListener(){
		super();
	}

	@Override
	public void bufferChanged(BufferChangedEvent event) {
		Messenger.getInstance().setDocumentEvent(event);
	}
	
	
}
