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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.encog.engine.data.EngineData;
import org.encog.engine.data.EngineIndexableSet;
import org.neuroph.core.exceptions.VectorSizeMismatchException;
import org.neuroph.util.norm.MaxNormalizer;
import org.neuroph.util.norm.Normalizer;

/**
 * A set of training elements for training neural network.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class TrainingSet<E extends TrainingElement> implements Serializable, EngineIndexableSet {

    /**
     * The class fingerprint that is set to indicate serialization compatibility
     * with a previous version of the class
     */
    private static final long serialVersionUID = 2L;
    /**
     * Collection of training elements
     */
    private List<E> elements;
    private int inputVectorSize = 0;
    private int outputVectorSize = 0;
    /**
     * Label for this training set
     */
    private String label;
    /**
     * Full file path including file name
     */
    private transient String filePath;

    /**
     * Creates an instance of new empty training set
     */
    public TrainingSet() {
        this.elements = new ArrayList<E>();
    }

    /**
     * Creates an instance of new empty training set with given label
     * 
     * @param label
     *            training set label
     */
    public TrainingSet(String label) {
        this.label = label;
        this.elements = new ArrayList<E>();
    }

    /**
     * Creates an instance of new empty training set
     * 
     * @param inputVectorSize
     */
    public TrainingSet(int inputVectorSize) {
        this.elements = new ArrayList<E>();
        this.inputVectorSize = inputVectorSize;
    }

    /**
     * Creates an instance of new empty training set
     * 
     * @param inputVectorSize
     * @param outputVectorSize
     */
    public TrainingSet(int inputVectorSize, int outputVectorSize) {
        this.elements = new ArrayList<E>();
        this.inputVectorSize = inputVectorSize;
        this.outputVectorSize = outputVectorSize;
    }

    /**
     * Adds new training element to this training set
     * 
     * @param el
     *            training element to add
     */
    public void addElement(E el)
            throws VectorSizeMismatchException {
        // check input vector size if it is predefined
        if ((this.inputVectorSize != 0)
                && (el.getInput().length != this.inputVectorSize)) {
            throw new VectorSizeMismatchException(
                    "Input vector size does not match training set!");
        }
        // check output vector size if it is predefined
        if (el instanceof SupervisedTrainingElement) {
            SupervisedTrainingElement sel = (SupervisedTrainingElement) el;
            if ((this.outputVectorSize != 0)
                    && (sel.getDesiredOutput().length != this.outputVectorSize)) {
                throw new VectorSizeMismatchException(
                        "Output vector size does not match training set!");
            }
        }
        // if everything went ok add training element
        this.elements.add(el);
    }

    /**
     * Removes training element at specified index position
     * 
     * @param idx
     *            position of element to remove
     */
    public void removeElementAt(int idx) {
        this.elements.remove(idx);
    }

    /**
     * Returns Iterator for iterating training elements collection
     * 
     * @return Iterator for iterating training elements collection
     */
    public Iterator<E> iterator() {
        return this.elements.iterator();
    }

    /**
     * Returns training elements collection
     * 
     * @return training elements collection
     */
    @Deprecated
    public List<E> trainingElements() {
        return this.elements;
    }

    /**
     * Returns elements of this training set
     * 
     * @return training elements
     */
    public List<E> elements() {
        return this.elements;
    }

    /**
     * Returns training element at specified index position
     * 
     * @param idx
     *            index position of training element to return
     * @return training element at specified index position
     */
    public TrainingElement elementAt(int idx) {
        return this.elements.get(idx);
    }

    /**
     * Removes all alements from training set
     */
    public void clear() {
        this.elements.clear();
    }

    /**
     * Returns true if training set is empty, false otherwise
     * 
     * @return true if training set is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /**
     * Returns number of training elements in this training set set
     * 
     * @return number of training elements in this training set set
     */
    public int size() {
        return this.elements.size();
    }

    /**
     * Returns label for this training set
     * 
     * @return label for this training set
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label for this training set
     * 
     * @param label
     *            label for this training set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Sets full file path for this training set
     * 
     * @param filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns full file path for this training set
     * 
     * @return full file path for this training set
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns label of this training set
     * 
     * @return label of this training set
     */
    @Override
    public String toString() {
        return this.label;
    }

    /**
     * Saves this training set to the specified file
     * 
     * @param filePath
     */
    public void save(String filePath) {
        this.filePath = filePath;
        this.save();
    }

    /**
     * Saves this training set to file specified in its filePath field
     */
    public void save() {
        ObjectOutputStream out = null;

        try {
            File file = new File(this.filePath);
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(this);
            out.flush();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioe) {
                }
            }
        }
    }

    public void saveAsTxt(String filePath, String delimiter) {
        if ((delimiter == null) || delimiter.equals("")) {
            delimiter = " ";
        }

        PrintWriter out = null;

        try {
            out = new PrintWriter(new FileWriter(new File(filePath)));

            for (E element : this.elements) {
                double[] input = element.getInput();
                for (int i = 0; i < input.length; i++) {
                    out.print(input[i] + delimiter);
                }

                if (element instanceof SupervisedTrainingElement) {
                    double[] output = ((SupervisedTrainingElement) element).getDesiredOutput();
                    for (int j = 0; j < output.length; j++) {
                        out.print(output[j] + delimiter);
                    }
                }
                out.println();
            }

            out.flush();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Loads training set from the specified file
     * 
     * @param filePath
     *            training set file
     * @return loded training set
     */
    public static TrainingSet load(String filePath) {
        ObjectInputStream oistream = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("Cannot find file: " + filePath);
            }

            oistream = new ObjectInputStream(new FileInputStream(filePath));
            TrainingSet tSet = (TrainingSet) oistream.readObject();

            return tSet;

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } finally {
            if (oistream != null) {
                try {
                    oistream.close();
                } catch (IOException ioe) {
                }
            }
        }

        return null;
    }

    public static TrainingSet createFromFile(String filePath, int inputsCount, int outputsCount, String delimiter) {
        FileReader fileReader = null;

        try {
            TrainingSet trainingSet = new TrainingSet();
            trainingSet.setInputSize(inputsCount);
            trainingSet.setOutputSize(outputsCount);
            fileReader = new FileReader(new File(filePath));
            BufferedReader reader = new BufferedReader(fileReader);

            String line = "";

            while ((line = reader.readLine()) != null) {
                double[] inputs = new double[inputsCount];
                double[] outputs = new double[outputsCount];
                String[] values = line.split(delimiter);

                if (values[0].equals("")) {
                    continue; // skip if line was empty
                }
                for (int i = 0; i < inputsCount; i++) {
                    inputs[i] = Double.parseDouble(values[i]);
                }

                for (int i = 0; i < outputsCount; i++) {
                    outputs[i] = Double.parseDouble(values[inputsCount + i]);
                }

                if (outputsCount > 0) {
                    trainingSet.addElement(new SupervisedTrainingElement(inputs, outputs));
                } else {
                    trainingSet.addElement(new TrainingElement(inputs));
                }
            }

            return trainingSet;

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ex1) {
                }
            }
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ex1) {
                }
            }
            ex.printStackTrace();
            throw ex;
        }
        
        return null;
    }

    public void normalize() {
        this.normalize(new MaxNormalizer());
    }

    public void normalize(Normalizer normalizer) {
        normalizer.normalize(this);
    }

    /**
     * Returns output vector size of training elements in this training set This
     * method is implementation of EngineIndexableSet interface, and it is added
     * to provide compatibility with Encog data sets and FlatNetwork
     */
    @Override
    public int getIdealSize() {
        return this.outputVectorSize;
    }

    public void setInputSize(int inputVectorSize) {
        this.inputVectorSize = inputVectorSize;
    }

    public void setOutputSize(int outputVectorSize) {
        this.outputVectorSize = outputVectorSize;
    }

    // http://java.about.com/od/javautil/a/uniquerandomnum.htm
    public TrainingSet[] createTrainingAndTestSubsets(int trainSetPercent, int testSetPercent) {
        TrainingSet[] trainAndTestSet = new TrainingSet[2];

        ArrayList<Integer> randoms = new ArrayList<Integer>();
        for (int i = 0; i < this.size(); i++) {
            randoms.add(i);
        }

        Collections.shuffle(elements);

        // create training set
        trainAndTestSet[0] = new TrainingSet();
        int trainingElementsCount = this.size() * trainSetPercent / 100;
        for (int i = 0; i < trainingElementsCount; i++) {
            int idx = randoms.get(i);
            trainAndTestSet[0].addElement(this.elements.get(idx));
        }


        // create test set
        trainAndTestSet[1] = new TrainingSet();
        int testElementsCount = this.size() - trainingElementsCount;
        for (int i = 0; i < testElementsCount; i++) {
            int idx = randoms.get(trainingElementsCount + i);
            trainAndTestSet[1].addElement(this.elements.get(idx));
        }

        return trainAndTestSet;
    }

    /**
     * Returns output vector size of training elements in this training set.
     */
    public int getOutputSize() {
        return this.outputVectorSize;
    }

    /**
     * Returns input vector size of training elements in this training set This
     * method is implementation of EngineIndexableSet interface, and it is added
     * to provide compatibility with Encog data sets and FlatNetwork
     */
    @Override
    public int getInputSize() {
        return this.inputVectorSize;
    }

    public void shuffle() {
        Collections.shuffle(elements);
    }

    /**
     * Returns true if training set contains supervised training elements This
     * method is implementation of EngineIndexableSet interface, and it is added
     * to provide compatibility with Encog data sets and FlatNetwork
     */
    @Override
    public boolean isSupervised() {
        return this.outputVectorSize > 0;
    }

    /**
     * Gets training data/record at specified index position. This method is
     * implementation of EngineIndexableSet interface. It is added for
     * Encog-Engine compatibility.
     */
    @Override
    public void getRecord(long index, EngineData pair) {
        EngineData item = this.elements.get((int) index);
        pair.setInputArray(item.getInputArray());
        pair.setIdealArray(item.getIdealArray());
    }

    /**
     * Returns training elements/records count This method is implementation of
     * EngineIndexableSet interface. It is added for Encog-Engine compatibility.
     */
    @Override
    public long getRecordCount() {
        return this.elements.size();
    }

    /**
     * This method is implementation of EngineIndexableSet interface, and it is
     * added to provide compatibility with Encog data sets and FlatNetwork.
     * 
     * Some datasets are not memory based, they may make use of a SQL connection
     * or a binary flat file. Because of this these datasets need to be cloned
     * for multi-threaded training or performance will greatly suffer. Because
     * this is a memory-based dataset, no cloning takes place and the "this"
     * object is returned.
     */
    @Override
    public EngineIndexableSet openAdditional() {
        return this;
    }
}