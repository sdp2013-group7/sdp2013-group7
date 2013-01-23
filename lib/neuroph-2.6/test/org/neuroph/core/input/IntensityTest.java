/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.neuroph.core.input;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author Shivanth
 */
public class IntensityTest {
    Intensity intensity;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
            intensity = new Intensity();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testIntensity() {
		

			double[] inputVector = new double[] { .5, 5 };
			assertEquals(Math.sqrt(25.25), intensity.getOutput(inputVector), .000001d);

			inputVector = new double[] { .5, -5 };
			assertEquals(Math.sqrt(25.25), intensity.getOutput(inputVector), .000001d);

			inputVector = new double[] { .5, 0 };
			assertEquals(Math.sqrt(.25), intensity.getOutput(inputVector), .000001d);

			inputVector = new double[] { 0, 0 };
			assertEquals(0d, intensity.getOutput(inputVector), .000001d);

			inputVector = new double[] { -.5, -5 };
			assertEquals(Math.sqrt(25.25), intensity.getOutput(inputVector), .000001d);

	}

	@Test
	public void testNoInput() {
		Intensity i = new Intensity();
		double[] inputVector = new double[] {};
		assertEquals(0, i.getOutput(inputVector), .000001d);
	}

}