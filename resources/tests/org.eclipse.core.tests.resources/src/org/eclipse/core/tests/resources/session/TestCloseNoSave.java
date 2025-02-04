/*******************************************************************************
 *  Copyright (c) 2000, 2012 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.resources.session;

import static org.eclipse.core.tests.resources.ResourceTestPluginConstants.PI_RESOURCES_TESTS;
import static org.eclipse.core.tests.resources.ResourceTestUtil.createRandomContentsStream;
import static org.eclipse.core.tests.resources.ResourceTestUtil.createTestMonitor;

import junit.framework.Test;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.tests.session.WorkspaceSessionTestSuite;

/**
 * Tests closing a workspace without save.
 */
public class TestCloseNoSave extends WorkspaceSerializationTest {
	public void test1() throws CoreException {
		/* create some resource handles */
		IProject project = workspace.getRoot().getProject(PROJECT);
		project.create(createTestMonitor());
		project.open(createTestMonitor());
		IFolder folder = project.getFolder(FOLDER);
		folder.create(true, true, createTestMonitor());
		IFile file = folder.getFile(FILE);
		file.create(createRandomContentsStream(), true, createTestMonitor());
	}

	public void test2() throws CoreException {
		// projects should exist immediately due to snapshot - files may or
		// may not exist due to snapshot timing. All resources should exist after refresh.
		IResource[] members = workspace.getRoot().members();
		assertEquals("1.0", 1, members.length);
		assertTrue("1.1", members[0].getType() == IResource.PROJECT);
		IProject project = (IProject) members[0];
		assertTrue("1.2", project.exists());
		IFolder folder = project.getFolder(FOLDER);
		IFile file = folder.getFile(FILE);

		//opening the project does an automatic local refresh
		if (!project.isOpen()) {
			project.open(null);
		}

		assertEquals("2.0", 3, project.members().length);
		assertTrue("2.1", folder.exists());
		assertTrue("2.2", file.exists());
	}

	public static Test suite() {
		return new WorkspaceSessionTestSuite(PI_RESOURCES_TESTS, TestCloseNoSave.class);
	}

}
