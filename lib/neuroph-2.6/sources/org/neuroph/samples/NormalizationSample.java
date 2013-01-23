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

package org.neuroph.samples;

import java.util.Arrays;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.util.norm.MaxMinNormalizer;

/**
 * This sample shows how to do data normalization in Neuroph.
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class NormalizationSample {

    /**
     * Runs this sample
     */    
    public static void main(String[] args) {
        
        // create training set to normalize
        TrainingSet<SupervisedTrainingElement> trainingSet = new TrainingSet<SupervisedTrainingElement>(2, 1);
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{10, 12}, new double[]{0}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{23, 19}, new double[]{0}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{47, 76}, new double[]{0}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{98, 123}, new double[]{1}));

        trainingSet.normalize();
        // or you can do as below
        //trainingSet.normalize(new MaxMinNormalizer());
        
        // print out normalized training set
        for (SupervisedTrainingElement trainingElement : trainingSet.elements()) {
            System.out.print("Input: " + Arrays.toString(trainingElement.getInput()));
        }
    }
}
