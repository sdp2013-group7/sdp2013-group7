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
public class SumSqrTest {
	private SumSqr sumSqr;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		sumSqr = new SumSqr();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSumMultipleInputs() {
		double[] inputVector = new double[] { .1, .4, .7, .9 };
		assertEquals(1.47, sumSqr.getOutput(inputVector), .00001);
	}

	@Test
	public void testNegatives() {
		double[] inputVector = new double[] { .1, -.4, .7, -.9 };
		assertEquals(1.47, sumSqr.getOutput(inputVector), .00001);
	}

	@Test
	public void testNoInput() {
		double[] inputVector = new double[] {};
		assertEquals(0, sumSqr.getOutput(inputVector), .001);

	}

}