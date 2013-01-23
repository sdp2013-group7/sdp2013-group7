package org.neuroph.core.input;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neuroph.core.Connection;
import org.neuroph.core.Neuron;
import static org.junit.Assert.*;

/**
 * 
 * @author Shivanth
 */
public class WeightedSumTest {

    private WeightedSum weightedSum;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		weightedSum = new WeightedSum();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSumWithRandomInput() {
		double[] inputVector = new double[] { 1, 3, 5, 7 };
		double[] weightVector = new double[] { .2, 5, 7, 8 };
		double[] output = WeightedSum.getOutput(inputVector, weightVector);
		assertEquals(.2, output[0], .0001);
	}

	@Test
	public void testOnRandomConnections() {
		// arrange
		Neuron Fromneuron = new Neuron();
		Fromneuron.setInput(.9d);
		Fromneuron.calculate();
		Neuron toneuron1 = new Neuron(), toneuron2 = new Neuron(), toneuron3 = new Neuron();
		List<Connection> inputConnections = new ArrayList<Connection>();
		{
			{
				Connection connection = new Connection(Fromneuron, toneuron1,
						0.5d);
				inputConnections.add(connection);
			}
			{
				Connection connection = new Connection(Fromneuron, toneuron2,
						0.25d);
				inputConnections.add(connection);
			}
			{
				Connection connection = new Connection(Fromneuron, toneuron3,
						-0.25d);
				inputConnections.add(connection);
			}
		}

		double output = weightedSum.getOutput(inputConnections);

		assertEquals(.5, output, .000001);
	}
}
