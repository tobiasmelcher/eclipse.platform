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

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.core.tests.resources.ResourceTestPluginConstants.NATURE_SNOW;
import static org.eclipse.core.tests.resources.ResourceTestPluginConstants.NATURE_WATER;
import static org.eclipse.core.tests.resources.ResourceTestPluginConstants.PI_RESOURCES_TESTS;
import static org.eclipse.core.tests.resources.ResourceTestUtil.createInWorkspace;
import static org.eclipse.core.tests.resources.ResourceTestUtil.createTestMonitor;
import static org.eclipse.core.tests.resources.ResourceTestUtil.setAutoBuilding;
import static org.eclipse.core.tests.resources.ResourceTestUtil.waitForBuild;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import junit.framework.Test;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.tests.internal.builders.SnowBuilder;
import org.eclipse.core.tests.internal.builders.TestBuilder;
import org.eclipse.core.tests.resources.WorkspaceSessionTest;
import org.eclipse.core.tests.session.WorkspaceSessionTestSuite;

/**
 * Tests persistence cases for builders that are missing or disabled.
 */
public class TestMissingBuilder extends WorkspaceSessionTest {
	/**
	 * Returns true if this project's build spec has the given builder,
	 * and false otherwise.
	 */
	protected boolean hasBuilder(IProject project, String builderId) throws CoreException {
		ICommand[] commands = project.getDescription().getBuildSpec();
		for (ICommand command : commands) {
			if (command.getBuilderName().equals(builderId)) {
				return true;
			}
		}
		return false;
	}

	protected InputStream projectFileWithoutWater() {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<projectDescription>\n" + "	<name>P1</name>\n" + "	<comment></comment>\n" + "	<projects>\n" + "	</projects>\n" + "	<buildSpec>\n" + "		<buildCommand>\n" + "			<name>org.eclipse.core.tests.resources.snowbuilder</name>\n" + "			<arguments>\n" + "				<dictionary>\n" + "					<key>BuildID</key>\n" + "					<value>SnowBuild</value>\n" + "				</dictionary>\n" + "			</arguments>\n" + "		</buildCommand>\n" + "	</buildSpec>\n" + "	<natures>\n" + "		<nature>org.eclipse.core.tests.resources.snowNature</nature>\n" + "	</natures>\n" + "</projectDescription>";
		return new ByteArrayInputStream(contents.getBytes());
	}

	/**
	 * Setup.  Create a project that has a disabled builder due to
	 * missing nature prerequisite.
	 */
	public void test1() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("P1");
		createInWorkspace(project);
		setAutoBuilding(true);
		IProjectDescription desc = project.getDescription();
		desc.setNatureIds(new String[] { NATURE_WATER, NATURE_SNOW });
		project.setDescription(desc, IResource.FORCE, createTestMonitor());
		//wait for background build to complete
		waitForBuild();
		//remove the water nature, thus invalidating snow nature
		SnowBuilder builder = SnowBuilder.getInstance();
		builder.reset();
		IFile descFile = project.getFile(IProjectDescription.DESCRIPTION_FILE_NAME);
		// setting description file will also trigger build
		descFile.setContents(projectFileWithoutWater(), IResource.FORCE, createTestMonitor());
		//assert that builder was skipped
		builder.assertLifecycleEvents();

		//assert that the builder is still in the build spec
		assertTrue(hasBuilder(project, SnowBuilder.BUILDER_NAME));

		getWorkspace().save(true, createTestMonitor());
	}

	/**
	 * Now assert that the disabled builder was carried forward and that
	 * it still doesn't build.
	 */
	public void test2() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("P1");

		//assert that the builder is still in the build spec
		assertTrue(hasBuilder(project, SnowBuilder.BUILDER_NAME));

		//perform a build and ensure snow builder isn't called
		getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, createTestMonitor());
		SnowBuilder builder = SnowBuilder.getInstance();
		builder.addExpectedLifecycleEvent(TestBuilder.SET_INITIALIZATION_DATA);
		builder.addExpectedLifecycleEvent(TestBuilder.STARTUP_ON_INITIALIZE);
		builder.assertLifecycleEvents();

		getWorkspace().save(true, createTestMonitor());
	}

	/**
	 * Test again in another workspace.  This ensures that disabled builders
	 * that were never instantiated get carried forward correctly.
	 */
	public void test3() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("P1");

		//assert that the builder is still in the build spec
		assertTrue("1.0", hasBuilder(project, SnowBuilder.BUILDER_NAME));

		//perform a build and ensure snow builder isn't called
		getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, createTestMonitor());
		SnowBuilder builder = SnowBuilder.getInstance();
		builder.addExpectedLifecycleEvent(TestBuilder.SET_INITIALIZATION_DATA);
		builder.addExpectedLifecycleEvent(TestBuilder.STARTUP_ON_INITIALIZE);
		builder.assertLifecycleEvents();

		//now re-enable the nature and ensure that the delta was null
		waitForBuild();
		builder.reset();
		builder.addExpectedLifecycleEvent(SnowBuilder.SNOW_BUILD_EVENT);
		IProjectDescription desc = project.getDescription();
		desc.setNatureIds(new String[] { NATURE_WATER, NATURE_SNOW });
		project.setDescription(desc, IResource.FORCE, createTestMonitor());
		waitForBuild();
		builder.assertLifecycleEvents();
		assertTrue(builder.wasDeltaNull());

	}

	public static Test suite() {
		return new WorkspaceSessionTestSuite(PI_RESOURCES_TESTS, TestMissingBuilder.class);
	}
}
