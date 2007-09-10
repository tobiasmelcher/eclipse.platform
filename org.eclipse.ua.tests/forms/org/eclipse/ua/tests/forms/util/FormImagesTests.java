/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 
package org.eclipse.ua.tests.forms.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.forms.widgets.FormImages;

import junit.framework.Assert;
import junit.framework.TestCase;

public class FormImagesTests extends TestCase {
	public void testSingleton() {
		Display display = Display.getCurrent();
		FormImages instance = FormImages.getInstance();
		// ensure the singleton is returning the same instance
		Assert.assertTrue("getInstance() returned a different FormImages instance", instance.equals(FormImages.getInstance()));
		Image gradient = instance.getGradient(display, new Color(display, 1, 1, 1), new Color(display, 7, 7, 7), 21, 21, 0);
		instance.markFinished(gradient);
		// ensure the singleton is returning the same instance after creating and disposing one gradient
		Assert.assertTrue("getInstance() returned a different FormImages instance after creation and disposal of one image", instance.equals(FormImages.getInstance()));
	}
	
	public void testDisposeOne() {
		Display display = Display.getCurrent();
		Image gradient = FormImages.getInstance().getGradient(display, new Color(display, 255, 255, 255), new Color(display, 0, 0, 0), 21, 21, 0);
		FormImages.getInstance().markFinished(gradient);
		// ensure that getting a single gradient and marking it as finished disposed it
		Assert.assertTrue("markFinished(...) did not dispose an image after a single getGradient()", gradient.isDisposed());
	}
	
	public void testMultipleSimpleInstances() {
		Display display = Display.getCurrent();
		Image gradient = FormImages.getInstance().getGradient(display, new Color(display, 200, 200, 200), new Color(display, 0, 0, 0), 30, 16, 3);
		int count;
		// ensure that the same image is returned for many calls with the same parameter
		for (count = 1; count < 20; count ++)
			Assert.assertEquals("getGradient(...) returned a different image for the same params on iteration "+count,
					gradient, FormImages.getInstance().getGradient(display, new Color(display, 200, 200, 200), new Color(display, 0, 0, 0), 30, 16, 3));
		for ( ;count > 0; count--) {
			FormImages.getInstance().markFinished(gradient);
			if (count != 1)
				// ensure that the gradient is not disposed early
				Assert.assertFalse("markFinished(...) disposed a shared image early on iteration "+count,gradient.isDisposed());
			else
				// ensure that the gradient is disposed on the last markFinished
				Assert.assertTrue("markFinished(...) did not dispose a shared image on the last call",gradient.isDisposed());
		}
	}
	
	public void testMultipleComplexInstances() {
		Display display = Display.getCurrent();
		Image gradient = FormImages.getInstance().getGradient(display, new Color[] {new Color(display, 200, 200, 200), new Color(display, 0, 0, 0)},
				new int[] {100}, 31, true, null);
		int count;
		// ensure that the same image is returned for many calls with the same parameter
		for (count = 1; count < 20; count ++)
			Assert.assertEquals("getGradient(...) returned a different image for the same params on iteration "+count,
					gradient, FormImages.getInstance().getGradient(display, new Color[] {new Color(display, 200, 200, 200), new Color(display, 0, 0, 0)},
							new int[] {100}, 31, true, null));
		for ( ;count > 0; count--) {
			FormImages.getInstance().markFinished(gradient);
			if (count != 1)
				// ensure that the gradient is not disposed early
				Assert.assertFalse("markFinished(...) disposed a shared image early on iteration "+count,gradient.isDisposed());
			else
				// ensure that the gradient is disposed on the last markFinished
				Assert.assertTrue("markFinished(...) did not dispose a shared image on the last call",gradient.isDisposed());
		}
	}
	
	public void testMultipleUniqueInstances() {
		Display display = Display.getCurrent();
		Image[] images = new Image[24];
		images[0] = FormImages.getInstance().getGradient(display, new Color(display, 1, 0, 0), new Color(display, 100, 100, 100), 25, 23, 1);
		images[1] = FormImages.getInstance().getGradient(display, new Color(display, 0, 1, 0), new Color(display, 100, 100, 100), 25, 23, 1);
		images[2] = FormImages.getInstance().getGradient(display, new Color(display, 0, 0, 1), new Color(display, 100, 100, 100), 25, 23, 1);
		images[3] = FormImages.getInstance().getGradient(display, new Color(display, 0, 0, 0), new Color(display, 101, 100, 100), 25, 23, 1);
		images[4] = FormImages.getInstance().getGradient(display, new Color(display, 0, 0, 0), new Color(display, 100, 101, 100), 25, 23, 1);
		images[5] = FormImages.getInstance().getGradient(display, new Color(display, 0, 0, 0), new Color(display, 100, 100, 101), 25, 23, 1);
		images[6] = FormImages.getInstance().getGradient(display, new Color(display, 0, 0, 0), new Color(display, 100, 100, 100), 20, 23, 1);
		images[7] = FormImages.getInstance().getGradient(display, new Color(display, 0, 0, 0), new Color(display, 100, 100, 100), 25, 10, 1);
		images[8] = FormImages.getInstance().getGradient(display, new Color(display, 0, 0, 0), new Color(display, 100, 100, 100), 25, 23, 2);
		images[9] = FormImages.getInstance().getGradient(display, new Color(display, 1, 1, 1), new Color(display, 101, 101, 101), 20, 10, 2);
		images[10] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0)}, new int[] {}, 31, true, null);
		images[11] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,1,1,1)},
				new int[] {80}, 31, true, new Color(display,255,255,255));
		images[12] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,1,1,1)},
				new int[] {80}, 31, true, new Color(display,0,0,0));
		images[13] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,100,100,100)},
				new int[] {100}, 31, true, null);
		images[14] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,1,0,0), new Color(display,100,100,100)},
				new int[] {100}, 31, true, null);
		images[15] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,1,0), new Color(display,100,100,100)},
				new int[] {100}, 31, true, null);
		images[16] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,1), new Color(display,100,100,100)},
				new int[] {100}, 31, true, null);
		images[17] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,101,100,100)},
				new int[] {100}, 31, true, null);
		images[18] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,100,101,100)},
				new int[] {100}, 31, true, null);
		images[19] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,100,100,101)},
				new int[] {100}, 31, true, null);
		images[20] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,100,100,100)},
				new int[] {100}, 20, true, null);
		images[21] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,100,100,100)},
				new int[] {100}, 31, false, null);
		images[22] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,0,0,0), new Color(display,100,100,100)},
				new int[] {50}, 31, true, new Color(display,1,1,1));
		images[23] = FormImages.getInstance().getGradient(display, new Color[]{new Color(display,1,1,1), new Color(display,101,101,101)},
				new int[] {50}, 20, false, new Color(display,1,1,1));
		// ensure none of the images are the same
		for (int i = 0; i < images.length - 1; i++) {
			for (int j = i+1; j < images.length; j++) {
				Assert.assertNotSame("getGradient(...) returned the same image for different parameters: i = " + i + "; j = " + j, images[i], images[j]);
			}
		}
		// ensure all of the images are disposed with one call to markFinished
		for (int i = 0; i < images.length; i++) {
			FormImages.getInstance().markFinished(images[i]);
			Assert.assertTrue("markFinished(...) did not dispose an image that was only requested once: i = " + i, images[i].isDisposed());
		}
	}
	
	public void testComplexEquality() {
		Display display = Display.getCurrent();
		Image image1 = FormImages.getInstance().getGradient(display, new Color[] {new Color(display,0,0,0), new Color(display,255,255,255)},
				new int[] {100}, 20, true, new Color(display,100,100,100));
		Image image2 = FormImages.getInstance().getGradient(display, new Color[] {new Color(display,0,0,0), new Color(display,255,255,255)},
				new int[] {100}, 20, true, new Color(display,0,0,0));
		Assert.assertEquals("different images were created with only the background color differing when that difference is irrelevant", image1, image2);
		FormImages.getInstance().markFinished(image1);
		FormImages.getInstance().markFinished(image2);
		image1 = FormImages.getInstance().getGradient(display, new Color[] {new Color(display,0,0,0), new Color(display,255,255,255)},
				new int[] {80}, 20, true, new Color(display,100,100,100));
		image2 = FormImages.getInstance().getGradient(display, new Color[] {new Color(display,0,0,0), new Color(display,255,255,255)},
				new int[] {80}, 20, true, new Color(display,0,0,0));
		Assert.assertNotSame("the same image was used when different background colors were specified", image1, image2);
		FormImages.getInstance().markFinished(image1);
		FormImages.getInstance().markFinished(image2);
	}
	
	public void testDisposeUnknown() {
		Display display = Display.getCurrent();
		Image image = new Image(display, 10, 10);
		FormImages.getInstance().markFinished(image);
		Assert.assertTrue("markFinished(...) did not dispose of an unknown image", image.isDisposed());
	}
}
