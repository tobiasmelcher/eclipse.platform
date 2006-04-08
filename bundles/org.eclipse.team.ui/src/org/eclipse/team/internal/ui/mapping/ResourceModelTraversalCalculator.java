/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ui.mapping;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.resources.mapping.ModelProvider;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.diff.IDiff;
import org.eclipse.team.core.mapping.IResourceDiffTree;
import org.eclipse.team.core.mapping.ISynchronizationContext;
import org.eclipse.team.core.mapping.provider.ResourceDiffTree;
import org.eclipse.team.internal.core.subscribers.DiffChangeSet;
import org.eclipse.team.internal.ui.*;

public class ResourceModelTraversalCalculator {

	public static final String PROP_TRAVERSAL_CALCULATOR = "org.eclipse.team.ui.resourceModelraversalCalculator"; //$NON-NLS-1$

	public int getLayoutDepth(IResource resource, TreePath path) {
		if (resource.getType() == IResource.PROJECT)
			return IResource.DEPTH_INFINITE;
		if (resource.getType() == IResource.FILE) 
			return IResource.DEPTH_ZERO;
		if (path != null && hasNonResource(path))
			return IResource.DEPTH_INFINITE;
		if (getLayout().equals(IPreferenceIds.FLAT_LAYOUT)) {
			return IResource.DEPTH_ZERO;
		} else if (getLayout().equals(IPreferenceIds.COMPRESSED_LAYOUT)) {
			return IResource.DEPTH_ONE;
		}
		return IResource.DEPTH_INFINITE;
	}
	
	public String getLayout() {
		return TeamUIPlugin.getPlugin().getPreferenceStore().getString(IPreferenceIds.SYNCVIEW_DEFAULT_LAYOUT);
	}
	
	public Object[] filterChildren(IResourceDiffTree diffTree, IResource resource, Object parentOrPath, Object[] children) {
		if (parentOrPath instanceof TreePath) {
			TreePath tp = (TreePath) parentOrPath;
			if (hasNonResource(tp)) {
				return getTreeChildren(diffTree, resource, children);
			}
		}
		if (getLayout().equals(IPreferenceIds.FLAT_LAYOUT) && resource.getType() == IResource.PROJECT) {
			return getFlatChildren(diffTree, resource);
		} else if (getLayout().equals(IPreferenceIds.COMPRESSED_LAYOUT) && resource.getType() == IResource.PROJECT) {
			return getCompressedChildren(diffTree, (IProject)resource, children);
		} else if (getLayout().equals(IPreferenceIds.COMPRESSED_LAYOUT) && resource.getType() == IResource.FOLDER) {
			return getCompressedChildren(diffTree, (IFolder)resource, children);
		}
		return getTreeChildren(diffTree, resource, children);
	}
	
	private boolean hasNonResource(TreePath parentPath) {
		for (int i = 0; i < parentPath.getSegmentCount(); i++) {
			Object o = parentPath.getSegment(i);
			if (!(o instanceof IResource) && !(o instanceof ModelProvider)) {
				return true;
			}
		}
		return false;
	}
	
	private Object[] getCompressedChildren(IResourceDiffTree diffTree, IProject project, Object[] children) {
		Set result = new HashSet();
		IDiff[] diffs = diffTree.getDiffs(project, IResource.DEPTH_INFINITE);
		for (int i = 0; i < diffs.length; i++) {
			IDiff diff = diffs[i];
			IResource resource = diffTree.getResource(diff);
			if (resource.getType() == IResource.FILE) {
				IContainer parent = resource.getParent();
				if (parent.getType() == IResource.FOLDER)
					result.add(parent);
				else 
					result.add(resource);
			} else if (resource.getType() == IResource.FOLDER)
				result.add(resource);
		}
		return result.toArray();
	}

	/*
	 * Only return the files that are direct children of the folder
	 */
	private Object[] getCompressedChildren(IResourceDiffTree diffTree, IFolder folder, Object[] children) {
		Set result = new HashSet();
		for (int i = 0; i < children.length; i++) {
			Object object = children[i];
			if (object instanceof IResource) {
				IResource resource = (IResource) object;
				if (resource.getType() == IResource.FILE)
					result.add(resource);
			}
		}
		IDiff[] diffs = diffTree.getDiffs(folder, IResource.DEPTH_ONE);
		for (int i = 0; i < diffs.length; i++) {
			IDiff diff = diffs[i];
			IResource resource = diffTree.getResource(diff);
			if (resource.getType() == IResource.FILE)
				result.add(resource);
		}
		return result.toArray();
	}

	private Object[] getFlatChildren(IResourceDiffTree diffTree, IResource resource) {
		Object[] allChildren;
		IDiff[] diffs = diffTree.getDiffs(resource, IResource.DEPTH_INFINITE);
		ArrayList result = new ArrayList();
		for (int i = 0; i < diffs.length; i++) {
			IDiff diff = diffs[i];
			result.add(diffTree.getResource(diff));
		}
		allChildren = result.toArray();
		return allChildren;
	}

	private Object[] getTreeChildren(IResourceDiffTree diffTree, IResource resource, Object[] children) {
		Set result = new HashSet();
		for (int i = 0; i < children.length; i++) {
			Object object = children[i];
			result.add(object);
		}
		IResource[] setChildren = getChildren(diffTree, resource);
		for (int i = 0; i < setChildren.length; i++) {
			IResource child = setChildren[i];
			result.add(child);
		}
		Object[] allChildren = result.toArray(new Object[result.size()]);
		return allChildren;
	}

	public static IResource[] getChildren(IResourceDiffTree diffTree, IResource resource) {
		Set result = new HashSet();
		IPath[] childPaths = diffTree.getChildren(resource.getFullPath());
		for (int i = 0; i < childPaths.length; i++) {
			IPath path = childPaths[i];
			IDiff delta = diffTree.getDiff(path);
			IResource child;
			if (delta == null) {
				// the path has descendent deltas so it must be a folder
				if (path.segmentCount() == 1) {
					child = ((IWorkspaceRoot)resource).getProject(path.lastSegment());
				} else {
					child = ((IContainer)resource).getFolder(new Path(path.lastSegment()));
				}
			} else {
				child = diffTree.getResource(delta);
			}
			result.add(child);
		}
		return (IResource[]) result.toArray(new IResource[result.size()]);
	}

	public ResourceTraversal[] getTraversals(DiffChangeSet dcs, TreePath tp) {
		IResource[] resources = getResource(dcs, tp);
		return new ResourceTraversal[] { new ResourceTraversal(resources, IResource.DEPTH_ZERO, IResource.NONE) };
	}

	private IResource[] getResource(DiffChangeSet dcs, TreePath tp) {
		if (tp.getSegmentCount() == 1 && tp.getFirstSegment() == dcs) {
			return dcs.getResources();
		}
		Set result = new HashSet();
		Object o = tp.getLastSegment();
		if (o instanceof IResource) {
			IResource resource = (IResource) o;
			int depth = getLayoutDepth(resource, tp);
			IDiff[] diffs = dcs.getDiffTree().getDiffs(resource, depth);
			for (int i = 0; i < diffs.length; i++) {
				IDiff diff = diffs[i];
				IResource r = ResourceDiffTree.getResourceFor(diff);
				if (r != null)
					result.add(r);
			}
		}
		return (IResource[]) result.toArray(new IResource[result.size()]);
	}

	public ResourceTraversal[] getTraversals(IResource resource, TreePath tp) {
		return new ResourceTraversal[] { new ResourceTraversal(new IResource[] { resource }, getLayoutDepth(resource, tp), IResource.NONE) };
	}

	public boolean isResourcePath(TreePath path) {
		for (int i = 0; i < path.getSegmentCount(); i++) {
			Object o = path.getSegment(i);
			if (!(o instanceof IResource)) {
				return false;
			}
		}
		return true;
	}
	
	public String getLabel(Object elementOrPath) {
		if (elementOrPath instanceof TreePath && hasNonResource((TreePath)elementOrPath)) {
			return null;
		}
		Object element = internalGetElement(elementOrPath);
		Object parent = internalGetElementParent(elementOrPath);
		if (element instanceof IResource) {
			IResource resource = (IResource) element;			
			if (getLayout().equals(IPreferenceIds.COMPRESSED_LAYOUT) 
					&& resource.getType() == IResource.FOLDER
					&& (parent == null || parent instanceof IProject)) {
				return resource.getProjectRelativePath().toString();
			}
			if (getLayout().equals(IPreferenceIds.FLAT_LAYOUT) 
					&& resource.getType() == IResource.FILE
					&& (parent == null || parent instanceof IProject)) {
				IPath parentPath = resource.getProjectRelativePath().removeLastSegments(1);
				if (!parentPath.isEmpty())
					return NLS.bind(TeamUIMessages.ResourceModelLabelProvider_0, resource.getName(), parentPath.toString());
			}
		}
		return null;
	}
	
	public boolean isCompressedFolder(Object elementOrPath) {
		if (elementOrPath instanceof TreePath && hasNonResource((TreePath)elementOrPath)) {
			return false;
		}
		Object element = internalGetElement(elementOrPath);
		Object parent = internalGetElementParent(elementOrPath);
		if (element instanceof IResource) {
			IResource resource = (IResource) element;
			// Only use the compressed folder icon if the parent is not known
			// or the parent is a project
			return getLayout().equals(IPreferenceIds.COMPRESSED_LAYOUT) 
				&& resource.getType() == IResource.FOLDER
				&& (parent == null || parent instanceof IProject);
		}
		return false;
	}
	
	private TreePath internalGetPath(Object elementOrPath) {
		if (elementOrPath instanceof TreePath) {
			return (TreePath) elementOrPath;
		}
		return null;
	}
	
	private Object internalGetElement(Object elementOrPath) {
		if (elementOrPath instanceof TreePath) {
			TreePath tp = (TreePath) elementOrPath;
			return tp.getLastSegment();
		}
		return elementOrPath;
	}
	
	private Object internalGetElementParent(Object elementOrPath) {
		if (elementOrPath instanceof TreePath) {
			TreePath tp = (TreePath) elementOrPath;
			if (tp.getSegmentCount() > 1) {
				return tp.getSegment(tp.getSegmentCount() - 2);
			}
			
		}
		return null;
	}

	public boolean hasChildren(ISynchronizationContext context, Object elementOrPath) {
		Object element = internalGetElement(elementOrPath);
		if (element instanceof IContainer) {
			IContainer container = (IContainer) element;
			// For containers check to see if the delta contains any children
			if (context != null) {
				int depth = getLayoutDepth(container, internalGetPath(elementOrPath));
				if (depth == IResource.DEPTH_ZERO)
					return false;
				IResourceDiffTree tree = context.getDiffTree();
				IResource[] members = tree.members(container);
				if (members.length > 0) {
					if (depth == IResource.DEPTH_INFINITE)
						return true;
					for (int i = 0; i < members.length; i++) {
						IResource resource = members[i];
						if (resource.getType() == IResource.FILE)
							return true;
					}
				}
			}
		}
		return false;
	}
}
