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
public class TrainingElementTest {
    
     double[] input = {0.1, 0.2, 0.3};
    
    public TrainingElementTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getInput method, of class TrainingElement.
     */
    @Test
    public void testGetInput() {
        System.out.println("getInput");
        TrainingElement instance = new TrainingElement(input);
        double[] expResult = input;
        double[] result = instance.getInput();
        assertEquals(expResult, result);
    }

    /**
     * Test of setInput method, of class TrainingElement.
     */
    @Test
    public void testSetInput() {
        System.out.println("setInput");
        TrainingElement instance = new TrainingElement(new  double[]{0.7, 0.8});
        instance.setInput(input);
        assertEquals(input, instance.getInput());        
    }

    /**
     * Test of getLabel method, of class TrainingElement.
     */
    @Test
    public void testGetLabel() {
        System.out.println("getLabel");
        TrainingElement instance = new TrainingElement(input);
        instance.setLabel("TestLabel");
        String expResult = "TestLabel";
        String result = instance.getLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLabel method, of class TrainingElement.
     */
    @Test
    public void testSetLabel() {
        System.out.println("setLabel");
        TrainingElement instance = new TrainingElement(input);
        instance.setLabel("TestLabel");
        String result = instance.getLabel();
        String expResult = "TestLabel";
        assertEquals(expResult, result);
    }


}
