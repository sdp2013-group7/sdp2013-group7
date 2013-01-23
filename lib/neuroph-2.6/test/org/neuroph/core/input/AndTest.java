package org.neuroph.core.input;

import static org.junit.Assert.assertEquals;
import org.junit.Before;

import org.junit.Test;

public class AndTest {

    private And and;

    @Before
    public void setUp() {
        and = new And();
    }

    @Test
    public void testZeroUpperBoundary() {
        double[] inputVector = new double[]{0.49999999d};
        double actualOutput = and.getOutput(inputVector);
        assertEquals(0d, actualOutput, 0.000001d);
    }

    @Test
    public void testZeroLowerBoundary() {
        double[] inputVector = new double[]{0d};
        double actualOutput = and.getOutput(inputVector);
        assertEquals(0d, actualOutput, 0.000001d);
    }

    @Test
    public void testOneLowerBoundary() {
        double[] inputVector = new double[]{0.5d};
        double actualOutput = and.getOutput(inputVector);
        assertEquals(1d, actualOutput, 0.000001d);
    }

    @Test
    public void testOneUpperBoundary() {
        double[] inputVector = new double[]{1d};
        double actualOutput = and.getOutput(inputVector);
        assertEquals(1d, actualOutput, 0.000001d);
    }

    @Test
    public void testOneForManyInputs() {
        double[] inputVector = new double[]{0.52d, 0.53d, 0.54d, 0.5d, 0.6d, 0.83d};
        double actualOutput = and.getOutput(inputVector);
        assertEquals(1d, actualOutput, 0.000001d);
    }

    @Test
    public void testZeroForManyInputs() {
        double[] inputVector = new double[]{0.52d, 0.53d, 0.33d, 0.5d, 0.6d, 0.83d};
        double actualOutput = and.getOutput(inputVector);
        assertEquals(0d, actualOutput, 0.000001d);
    }

    @Test
    public void testNoInputs() {
        double[] inputVector = new double[]{};
        double actualOutput = and.getOutput(inputVector);
        assertEquals(0d, actualOutput, 0.000001d);
    }
}
