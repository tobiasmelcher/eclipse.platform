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
 *     Broadcom Corp. - build configurations
 *******************************************************************************/
package org.eclipse.core.tests.internal.builders;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ //
		AutoBuildJobTest.class, //
		BuildConfigurationsTest.class, //
		BuildContextTest.class, //
		BuildDeltaVerificationTest.class, //
		BuilderCycleTest.class, //
		BuilderEventTest.class, //
		BuilderNatureTest.class, //
		BuilderTest.class, //
		ComputeProjectOrderTest.class, //
		CustomBuildTriggerTest.class, //
		EmptyDeltaTest.class, //
		MultiProjectBuildTest.class, //
		ParallelBuildChainTest.class, //
		RebuildTest.class, //
		RelaxedSchedRuleBuilderTest.class, //
})
public class AllBuilderTests {
}
