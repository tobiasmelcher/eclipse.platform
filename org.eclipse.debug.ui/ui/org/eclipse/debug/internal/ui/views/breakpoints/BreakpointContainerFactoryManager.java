/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.views.breakpoints;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Manager which provides access to the breakpoint organizers
 * contributed via the org.eclipse.debug.ui.breakpointOrganizers
 * extension point.
 * <p>
 * Manages the default breakpoint working set and places newly
 * create breakpoints in to that set.
 * </p>
 * @since 3.1
 */
public class BreakpointContainerFactoryManager {
	
	private static BreakpointContainerFactoryManager fgManager;
	
    private Map fOrganizers = new HashMap();    

	/**
	 * Returns the singleton instance of the breakpoint container
	 * factory manager.
	 */
	public static BreakpointContainerFactoryManager getDefault() {
		if (fgManager == null) {
			fgManager= new BreakpointContainerFactoryManager();
		}
		return fgManager;
	}
	
	/**
	 * Creates and initializes a new breakpoint container factory.
	 */
	private BreakpointContainerFactoryManager() {
        loadOrganizers();
        // force the working set organizer to initialize its listeners
        IBreakpointOrganizer organizer = getOrganizer("org.eclipse.debug.ui.breakpointWorkingSetOrganizer"); //$NON-NLS-1$
        IPropertyChangeListener listener = new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
            }
        };
        organizer.addPropertyChangeListener(listener);
        organizer.removePropertyChangeListener(listener);
	}
    
    /**
     * Loads all contributed breakpoint organizers.
     */
    private void loadOrganizers() {
        IExtensionPoint extensionPoint= Platform.getExtensionRegistry().getExtensionPoint(DebugUIPlugin.getUniqueIdentifier(), IDebugUIConstants.EXTENSION_POINT_BREAKPOINT_ORGANIZERS);
        IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
        for (int i = 0; i < configurationElements.length; i++) {
            IConfigurationElement element= configurationElements[i];
            IBreakpointOrganizer organizer = new BreakpointOrganizerExtension(element);
            if (validateOrganizer(organizer)) {
                fOrganizers.put(organizer.getIdentifier(), organizer);
            }
        }
    }    
	
    /**
     * Validates the given organizer. Checks that certain required attributes
     * are available.
     * @param extension the organizer to validate
     * @return whether the given organizer is valid
     */
    protected static boolean validateOrganizer(IBreakpointOrganizer organizer) {
        String id = organizer.getIdentifier();
        String label = organizer.getLabel();
        return id != null && id.length() > 0 && label != null && label.length() > 0;
    }    
	
    /**
     * Returns all contributed breakpoint organizers.
     * 
     * @return all contributed breakpoint organizers
     */
    public IBreakpointOrganizer[] getOrganizers() {
        Collection collection = fOrganizers.values();
        return (IBreakpointOrganizer[]) collection.toArray(new IBreakpointOrganizer[collection.size()]);
    }
    
    /**
     * Returns the specified breakpoint organizer or <code>null</code>
     * @param id organizer identifier
     * @return breakpoint organizer or <code>null</code>
     */
    public IBreakpointOrganizer getOrganizer(String id) {
        return (IBreakpointOrganizer) fOrganizers.get(id);
    }
    
    /**
     * Shuts down the organizer manager, disposing organizers.
     */
    public void shutdown() {
        IBreakpointOrganizer[] organizers = getOrganizers();
        for (int i = 0; i < organizers.length; i++) {
            IBreakpointOrganizer organizer = organizers[i];
            organizer.dispose();
        }
    }

}
