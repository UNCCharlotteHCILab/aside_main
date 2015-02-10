package edu.uncc.aside.codeannotate.models;

import java.beans.PropertyChangeListener;

/**
 * This interface is intended to be implemented by all models which are the
 * sources of change.
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * 
 */
public interface IPropertyChangeSource {

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 * 
	 * @param listener
	 */
	public abstract void addPropertyChangeListener(
			PropertyChangeListener listener);

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 * 
	 * @param listener
	 */
	public abstract void removePropertyChangeListener(
			PropertyChangeListener listener);

}
