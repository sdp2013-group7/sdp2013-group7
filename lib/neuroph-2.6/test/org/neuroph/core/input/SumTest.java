
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
public class SumTest {

    private Sum sum;

    public SumTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        sum = new Sum();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSumMultipleInputs() {
        double[] inputVector = new double[]{.1, .4, .7, .9};
        assertEquals(2.1, sum.getOutput(inputVector), .0001);
    }

    @Test
    public void testSumNegatives() {
        double[] inputVector = new double[]{.1, -.4, .7, -.9};
        assertEquals(-.5, sum.getOutput(inputVector), .00001);
    }

    @Test
    public void testNoInput() {
        double[] inputVector = new double[]{};
        assertEquals(0, sum.getOutput(inputVector), .001);
    }
}
