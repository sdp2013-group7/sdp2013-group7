

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
public class OrTest {
    private Or or;


    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        or = new Or();
    }

    @After
    public void tearDown() {
    }

   @Test
	public void testZeroUpperBoundary() {
		Or or = new Or();
		double[] inputVector = new double[] { 0.49999999d };
		double actualOutput = or.getOutput(inputVector);
		assertEquals(0d, actualOutput, 0.000001d);
	}

	@Test
	public void testZeroLowerBoundary() {
		Or or = new Or();
		double[] inputVector = new double[] { 0d };
		double actualOutput = or.getOutput(inputVector);
		assertEquals(0d, actualOutput, 0.000001d);
	}

	@Test
	public void testOneLowerBoundary() {
		Or or = new Or();
		double[] inputVector = new double[] { 0.5d };
		double actualOutput = or.getOutput(inputVector);
		assertEquals(1d, actualOutput, 0.000001d);
	}

	@Test
	public void testOneUpperBoundary() {
		Or or = new Or();
		double[] inputVector = new double[] { 1d };
		double actualOutput = or.getOutput(inputVector);
		assertEquals(1d, actualOutput, 0.000001d);
	}

	@Test
	public void testOneForManyInputs() {
		Or or = new Or();
		double[] inputVector = new double[] { 0.52d, 0.53d, 0.54d, 0.5d, 0.6d, 0.83d };
		double actualOutput = or.getOutput(inputVector);
		assertEquals(1d, actualOutput, 0.000001d);
	}

	@Test
	public void testZeroForManyInputs() {
		Or or = new Or();
		double[] inputVector = new double[] { 0.32d, 0.44d, 0.33d, 0.5d, 0.4d, 0.23d };
		double actualOutput = or.getOutput(inputVector);
		assertEquals(1d, actualOutput, 0.000001d);
	}

	@Test
	public void testNoInputs() {
		Or or = new Or();
		double[] inputVector = new double[] {};
		double actualOutput = or.getOutput(inputVector);
                
		assertEquals(0d, actualOutput, 0.000001d);
	}
}