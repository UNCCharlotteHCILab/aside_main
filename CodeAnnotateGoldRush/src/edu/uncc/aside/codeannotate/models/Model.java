package edu.uncc.aside.codeannotate.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import edu.uncc.aside.codeannotate.visitors.IModelVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public abstract class Model implements IPropertyChangeSource{

	private List<Model> children;
	
	// observer pattern
	private PropertyChangeSupport changer = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener){
		changer.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		changer.removePropertyChangeListener(listener);
	} 
	
	/**
	 * @see PropertyChangeSupport#firePropertyChange(String, Object, Object)
	 * 
	 * @param listener
	 */
	protected void fireEvent(String propertyName, Object oldVal, Object newVal){
		changer.firePropertyChange(propertyName, oldVal, newVal);
	}

	// visitor pattern
	
	
	public abstract void accept(IModelVisitor visitor);

	public abstract Model getParent();
	
	protected abstract List<Model> buildChildren();
	
	public List<Model> getChildren(){
		if(children==null)
			children = buildChildren();
		return children;
	}
	
}
