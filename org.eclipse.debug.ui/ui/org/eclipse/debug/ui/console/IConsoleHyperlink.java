/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.ui.console;


/**
 * A hyperlink in the console. Link behavior is implementation dependent.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 2.1
 */
public interface IConsoleHyperlink {
	
	/**
	 * Notification that the mouse has entered this link's region.
	 */
	public void linkEntered();
	
	/**
	 * Notification that the mouse has exited this link's region
	 */
	public void linkExited();
	
	/**
	 * Notification that this link has been activated. Performs
	 * context specific linking.
	 */
	public void linkActivated();

}
