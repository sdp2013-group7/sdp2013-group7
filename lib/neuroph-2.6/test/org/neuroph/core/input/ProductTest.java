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
public class ProductTest {

	private Product product;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		product = new Product();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testProductPositives() {
		double inputVector[] = new double[] { .5, .10 };
		assertEquals(.050, product.getOutput(inputVector), .0001);
	}

	@Test
	public void testProductMixed() {
		double inputVector[] = new double[] { -.1, .10 };
		assertEquals(-.010, product.getOutput(inputVector), .0001);
	}

	@Test
	public void testProductNegativess() {
		double inputVector[] = new double[] { -1, -.5, -.10 };
		assertEquals(-.050, product.getOutput(inputVector), .0001);
	}

	@Test
	public void testProductMultiple() {
		double inputVector[] = new double[] { 1, .5, .10, .9 };
		assertEquals(.045, product.getOutput(inputVector), .0001);
	}

	@Test
	public void testNZeroes() {
		double inputVector[] = new double[] { 1, 2, 4, 0 };
		assertEquals(0, product.getOutput(inputVector), .00001);
	}

	@Test
	public void testEmptyInput() {
		double inputVector[] = new double[] {};
		assertEquals(0, product.getOutput(inputVector), .00001);
	}
}
