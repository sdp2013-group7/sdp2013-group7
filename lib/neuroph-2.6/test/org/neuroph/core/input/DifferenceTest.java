package org.neuroph.core.input;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.neuroph.core.Connection;
import org.neuroph.core.Neuron;

public class DifferenceTest {

    Difference diference;

    @Before
    public void setUp() {
        diference = new Difference();
    }

    @Test
    public void testEmptyArrayInput() {
        List<Connection> inputConnections = new ArrayList<Connection>();
        double[] output = diference.getOutput(inputConnections);
        assertEquals(0, output.length);
    }

    @Test
    public void testOnRandomConnections() {
        Neuron Fromneuron = new Neuron();
        List<Connection> inputConnections = new ArrayList<Connection>();
        {
            {
                Connection connection = new Connection(Fromneuron, new Neuron(), 0.5d);
                inputConnections.add(connection);
            }
            {
                Connection connection = new Connection(Fromneuron, new Neuron(), 0.25d);
                inputConnections.add(connection);
            }
            {
                Connection connection = new Connection(Fromneuron, new Neuron(), -0.25d);
                inputConnections.add(connection);
            }
        }

        // act
        Difference diference = new Difference();
        double[] output = diference.getOutput(inputConnections);

        // assert
        assertEquals(3, output.length);
        assertEquals(-0.5d, output[0], 0.0000001d);
        assertEquals(-0.25d, output[1], 0.0000001d);
        assertEquals(0.25d, output[2], 0.0000001d);
    }
}
