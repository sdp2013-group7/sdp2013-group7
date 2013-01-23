/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.neuroph.core.learning;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class SupervisedTrainingElementTest {
    
    double[] input = {0.1, 0.2, 0.3},
             desiredOutput = {0.4, 0.5};
    
    public SupervisedTrainingElementTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {   }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDesiredOutput method, of class SupervisedTrainingElement.
     */
    @Test
    public void testGetDesiredOutput() {
        System.out.println("getDesiredOutput");
        SupervisedTrainingElement instance = new SupervisedTrainingElement(input, desiredOutput);
        double[] result = instance.getDesiredOutput();
        assertEquals(desiredOutput, result);
    }

    /**
     * Test of setDesiredOutput method, of class SupervisedTrainingElement.
     */
    @Test
    public void testSetDesiredOutput() {
        System.out.println("setDesiredOutput");
        SupervisedTrainingElement instance = new SupervisedTrainingElement(input, input);
        instance.setDesiredOutput(desiredOutput);
        assertEquals(desiredOutput, instance.getDesiredOutput());
    }

    /**
     * Test of getIdealArray method, of class SupervisedTrainingElement.
     */
    @Test
    public void testGetIdealArray() {
        System.out.println("getIdealArray");
        SupervisedTrainingElement instance = new SupervisedTrainingElement(input, desiredOutput);
        double[] result = instance.getIdealArray();
        assertEquals(desiredOutput, result);
    }

    /**
     * Test of setIdealArray method, of class SupervisedTrainingElement.
     */
    @Test
    public void testSetIdealArray() {
        System.out.println("setIdealArray");
        SupervisedTrainingElement instance = new SupervisedTrainingElement(input, desiredOutput);
        instance.setIdealArray(desiredOutput);
        assertEquals(desiredOutput, instance.getIdealArray());
    }

    /**
     * Test of isSupervised method, of class SupervisedTrainingElement.
     */
    @Test
    public void testIsSupervised() {
        System.out.println("isSupervised");
        SupervisedTrainingElement instance = new SupervisedTrainingElement(input, desiredOutput);
        boolean expResult = true;
        boolean result = instance.isSupervised();
        assertEquals(expResult, result);
    }
}
