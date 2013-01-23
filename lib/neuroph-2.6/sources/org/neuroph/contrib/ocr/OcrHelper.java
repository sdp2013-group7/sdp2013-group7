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

package org.neuroph.contrib.ocr;

import java.util.List;
import org.neuroph.contrib.imgrec.ColorMode;
import org.neuroph.contrib.imgrec.ImageRecognitionHelper;
import org.neuroph.contrib.imgrec.image.Dimension;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.util.TransferFunctionType;


/**
 * Provides methods to create Neural Network and Training set for OCR.
 * @author zoran
 */
public class OcrHelper extends ImageRecognitionHelper {

    /**
     * Creates neural network for OCR, which contains OCR plugin. OCR plugin provides interface for character recognition.
     * @param label neural network label
     * @param samplingResolution character size in pixels (all characters will be scaled to this dimensions during recognition)
     * @param colorMode color mode used fr recognition
     * @param characterLabels character labels for output neurons
     * @param layersNeuronsCount number of neurons ih hidden layers
     * @param transferFunctionType neurons transfer function type
     * @return returns NeuralNetwork with the OCR plugin
     */
    public static NeuralNetwork createNewNeuralNetwork(String label, Dimension samplingResolution, ColorMode colorMode, List<String> characterLabels,  List<Integer> layersNeuronsCount, TransferFunctionType transferFunctionType) {
        NeuralNetwork neuralNetwork = ImageRecognitionHelper.createNewNeuralNetwork(label, samplingResolution, colorMode, characterLabels, layersNeuronsCount, transferFunctionType);
        neuralNetwork.addPlugin(new OcrPlugin(samplingResolution, colorMode));

        return neuralNetwork;
    }
}