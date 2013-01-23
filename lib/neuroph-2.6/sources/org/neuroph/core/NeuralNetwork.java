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

package org.neuroph.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import org.neuroph.core.exceptions.VectorSizeMismatchException;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.util.NeuralNetworkType;
import org.neuroph.util.plugins.LabelsPlugin;
import org.neuroph.util.plugins.PluginBase;
import org.neuroph.util.random.RangeRandomizer;
import org.neuroph.util.random.WeightsRandomizer;


/**
 *<pre>
 * Base class for artificial neural networks. It provides generic structure and functionality
 * for the neural networks. Neural network contains a collection of neuron layers and learning rule.
 * Custom neural networks are created by deriving from this class, creating layers of interconnected network specific neurons,
 * and setting network specific learning rule.
 *</pre>
 * 
 * @see Layer
 * @see LearningRule
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class NeuralNetwork extends Observable implements Serializable {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */
	private static final long serialVersionUID = 4L;

	/**
	 * Network type id (see neuroph.util.NeuralNetworkType)
	 */
	private NeuralNetworkType type;

	/**
	 * Neural network
	 */
	private List<Layer> layers;

	/**
	 * Reference to network input neurons
	 */
	private List<Neuron> inputNeurons;

	/**
	 * Reference to newtwork output neurons
	 */
	private List<Neuron> outputNeurons;

	/**
	 * Learning rule for this network
	 */
	private LearningRule learningRule; // learning algorithme

	/**
	 * Separate thread for learning rule
	 */
	private transient Thread learningThread; // thread for learning rule
	
	/**
	 * Plugins collection
	 */	
	private Map<Class, PluginBase> plugins;

        /**
         *  Label for this network
         */
        private String label="";

	/**
	 * Creates an instance of empty neural network.
	 */
	public NeuralNetwork() {
		this.layers = new ArrayList<Layer>();
		this.plugins = new HashMap<Class, PluginBase>();
		this.addPlugin(new LabelsPlugin());	
	}

	/**
	 * Adds layer to neural network
	 * 
	 * @param layer
	 *            layer to add
	 */
	public void addLayer(Layer layer) {
		layer.setParentNetwork(this);
		this.layers.add(layer);
	}

	/**
	 * Adds layer to specified index position in network
	 * 
	 * @param idx
	 *            index position to add layer
	 * @param layer
	 *            layer to add
	 */
	public void addLayer(int idx, Layer layer) {
		layer.setParentNetwork(this);
		this.layers.add(idx, layer);
	}

	/**
	 * Removes specified layer from network
	 * 
	 * @param layer
	 *            layer to remove
	 */
	public void removeLayer(Layer layer) {
		this.layers.remove(layer);
	}

	/**
	 * Removes layer at specified index position from net
	 * 
	 * @param idx
	 *            int value represents index postion of layer which should be
	 *            removed
	 */
	public void removeLayerAt(int idx) {
		this.layers.remove(idx);
	}

	/**
	 * Returns interface for iterating layers
	 * 
	 * @return iterator interface for network getLayersIterator
	 */
	public Iterator<Layer> getLayersIterator() {
		return this.layers.iterator();
	}

	/**
	 * Returns layers collection
	 * 
	 * @return layers collection
	 */
	public List<Layer> getLayers() {
		return this.layers;
	}

	/**
	 * Returns layer at specified index
	 * 
	 * @param idx
	 *            layer index position
	 * @return layer at specified index position
	 */
	public Layer getLayerAt(int idx) {
		return this.layers.get(idx);
	}

	/**
	 * Returns index position of the specified layer
	 * 
	 * @param layer
	 *            requested Layer object
	 * @return layer position index
	 */
	public int indexOf(Layer layer) {
		return this.layers.indexOf(layer);
	}

	/**
	 * Returns number of layers in network
	 * 
	 * @return number of layes in net
	 */
	public int getLayersCount() {
		return this.layers.size();
	}

   	/**
	 * Sets network input. Input is an array of double values.
	 *
	 * @param inputVector
	 *            network input as double array
	 */
	public void setInput(double ... inputVector) throws VectorSizeMismatchException   {
        if (inputVector.length != inputNeurons.size())
            throw new VectorSizeMismatchException("Input vector size does not match network input dimension!");

                int i = 0;
		for(Neuron neuron : this.inputNeurons) {
			neuron.setInput(inputVector[i]); // set input to the coresponding neuron
                        i++;
		}

	}

	/**
	 * Returns network output Vector. Output Vector is a collection of Double
	 * values.
	 * 
	 * @return network output Vector
	 */
	public double[] getOutput() {
		double[] outputVector = new double[outputNeurons.size()];

                int i = 0;
		for(Neuron neuron : this.outputNeurons) {
			outputVector[i] = neuron.getOutput();
                        i++;
		}		
		
		return outputVector;
	}

	/**
	 * Performs calculation on whole network
	 */
	public void calculate() {
		for(Layer layer : this.layers) {
			layer.calculate();
		}		
	}

	/**
	 * Resets the activation levels for whole network
	 */
	public void reset() {
		for(Layer layer : this.layers) {
			layer.reset();
		}		
	}


        /**
         * Starts learning in a new thread to learn the specified training set,
         * and immediately returns from method to the current thread execution
         * @param trainingSetToLearn
         *              set of training elements to learn
         */
        public void learnInNewThread(TrainingSet trainingSetToLearn) {
                    learningRule.setTrainingSet(trainingSetToLearn);
                    learningThread = new Thread(learningRule);
                    learningRule.setStarted();
                    learningThread.start();
        }

        /**
         * Starts learning with specified learning rule in new thread to learn the
         * specified training set, and immediately returns from method to the current thread execution
         * @param trainingSetToLearn
         *              set of training elements to learn
         * @param learningRule
         *              learning algorithm
         */
        public void learnInNewThread(TrainingSet trainingSetToLearn, LearningRule learningRule) {
            setLearningRule(learningRule);
            learningRule.setTrainingSet(trainingSetToLearn);
            learningThread = new Thread(learningRule);
            learningRule.setStarted();
            learningThread.start();
        }

        /**
         * Starts the learning in the current running thread to learn the specified
         * training set, and returns from method when network is done learning.
         * This method is deprecated, see learn method which does the same
         * @param trainingSetToLearn
         *              set of training elements to learn
         */
        @Deprecated
        public void learnInSameThread(TrainingSet trainingSetToLearn) {
            learningRule.setTrainingSet(trainingSetToLearn);
            learningRule.setStarted();
            learningRule.run();
        }
        

        /**
         * Learn the specified training set
         * @param trainingSet
         *              set of training elements to learn
         */        
        public void learn(TrainingSet trainingSet) {
            learningRule.setTrainingSet(trainingSet);
            learningRule.setStarted();
            learningRule.run();
        }        
        
        /**
         * Learn the specified training set, using specified learning rule
         * @param trainingSetToLearn
         *              set of training elements to learn
         * @param learningRule 
         *              instance of learning rule to use for learning
         */           
        public void learn(TrainingSet trainingSetToLearn, LearningRule learningRule) {
            setLearningRule(learningRule);
            learningRule.setTrainingSet(trainingSetToLearn);
            learningRule.setStarted();
            learningRule.run();
        }        
        
        /**
         * Starts the learning with specified learning rule in the current running
         * thread to learn the specified training set, and returns from method when network is done learning
         * This method is deprecated, see learn method which does the same
         * @param trainingSetToLearn
         *              set of training elements to learn
         * @param learningRule
         *              learning algorithm
         * *
         */
        @Deprecated
        public void learnInSameThread(TrainingSet trainingSetToLearn, LearningRule learningRule) {
            setLearningRule(learningRule);
            learningRule.setTrainingSet(trainingSetToLearn);
            learningRule.setStarted();
            learningRule.run();
        }

	/**
	 * Stops learning
	 */
	public void stopLearning() {
		learningRule.stopLearning();
	}

        /**
         * Pause the learning - puts learning thread in wait state.
         * Makes sense only wen learning is done in new thread with learnInNewThread() method
         */
        public void pauseLearning() {
            if ( learningRule instanceof IterativeLearning)
                   ((IterativeLearning)learningRule).pause();
        }

        /**
         * Resumes paused learning - notifies the learning rule to continue
         */
       public void resumeLearning() {
            if ( learningRule instanceof IterativeLearning)
                   ((IterativeLearning)learningRule).resume();
        }

	/**
	 * Randomizes connection weights for the whole network
	 */
	public void randomizeWeights() {
            randomizeWeights(new WeightsRandomizer());
	}

	/**
	 * Randomizes connection weights for the whole network within specified value range
	 */
	public void randomizeWeights(double minWeight, double maxWeight) {
            randomizeWeights(new RangeRandomizer(minWeight, maxWeight));
        }
        
      	/**
	 * Randomizes connection weights for the whole network using specified random generator
	 */
	public void randomizeWeights(Random random) {
            randomizeWeights(new WeightsRandomizer(random));
	}
        
        /**
         * Randomizes connection weights for the whole network using specified randomizer
         * @param randomizer random weight generator to use
         */
        public void randomizeWeights(WeightsRandomizer randomizer) {
            randomizer.randomize(this);
        }

        /**
         * Initialize connection weights for the whole network to a value
         *
         * @param value the weight value
         */
        public void initializeWeights(double value) {
              for(Layer layer : this.layers) {
                  layer.initializeWeights(value);
              }
        }

        /**
         * Initialize connection weights for the whole network using a
         * random number generator
         * call the randomizeWeights... - they are same...
         * or use randomizeWeights(Random random) if you need to specify random generator
         * @param generator the random number generator
         * @deprecated 
         */
        public void initializeWeights(Random generator) {
              for(Layer layer : this.layers) {
                   layer.initializeWeights(generator);
              }
        }

        /**
         * Use randomizeWeights(double minWeight, double maxWeight) instead
         * @param min
         * @param max 
         * @deprecated 
         */
        public void initializeWeights(double min, double max) {
            randomizeWeights(min, max);
        }

	/**
	 * Returns type of this network
	 * 
	 * @return network type
	 */
	public NeuralNetworkType getNetworkType() {
		return type;
	}

	/**
	 * Sets type for this network
	 * 
	 * @param type network type
	 */
	public void setNetworkType(NeuralNetworkType type) {
		this.type = type;
	}

	/**
	 * Gets reference to input neurons Vector.
	 * 
	 * @return input neurons Vector
	 */
	public List<Neuron> getInputNeurons() {
		return this.inputNeurons;
	}

	/**
	 * Sets reference to input neurons Vector
	 * 
	 * @param inputNeurons
	 *            input neurons collection
	 */
	public void setInputNeurons(List<Neuron> inputNeurons) {
		this.inputNeurons = inputNeurons;
	}

	/**
	 * Returns reference to output neurons Vector.
	 * 
	 * @return output neurons Vector
	 */
	public List<Neuron> getOutputNeurons() {
		return this.outputNeurons;
	}

	/**
	 * Sets reference to output neurons Vector.
	 * 
	 * @param outputNeurons
	 *            output neurons collection
	 */
	public void setOutputNeurons(List<Neuron> outputNeurons) {
		this.outputNeurons = outputNeurons;
	}

	/**
	 * Returns the learning algorithm of this network
	 * 
	 * @return algorithm for network training
	 */
	public LearningRule getLearningRule() {
		return this.learningRule;
	}

	/**
	 * Sets learning algorithm for this network
	 * 
	 * @param learningRule learning algorithm for this network
	 */
	public void setLearningRule(LearningRule learningRule) {
                learningRule.setNeuralNetwork(this);
		this.learningRule = learningRule;
	}

        /**
         * Returns the current learning thread (if it is learning in the new thread
         * Check what happens if it learns in the same thread)
         */
        public Thread getLearningThread() {
            return learningThread;
        }

	/**
	 * Notifies observers about some change
	 */
	public void notifyChange() {
		setChanged();
		notifyObservers();
		clearChanged();
	}

	/**
	 * Creates connection with specified weight value between specified neurons
	 * 
	 * @param fromNeuron neuron to connect
         * @param toNeuron neuron to connect to
	 * @param weightVal connection weight value
	 */	
	public void createConnection(Neuron fromNeuron, Neuron toNeuron, double weightVal) {
		Connection connection = new Connection(fromNeuron, toNeuron, weightVal);
		toNeuron.addInputConnection(connection);
	}

	
	@Override
	public String toString() {
        	if (label!=null) 
                    return label;
		
		return super.toString();
	}

	/**
	 * Saves neural network into the specified file.
	 * 
	 * @param filePath
	 *		file path to save network into
	 */
	public void save(String filePath) {
		ObjectOutputStream out = null;
		try {
			File file = new File(filePath);
			out = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream(file)));
			out.writeObject(this);
			out.flush();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Loads neural network from the specified file.
	 * 
	 * @param filePath
	 *		file path to load network from
	 * @return loaded neural network as NeuralNetwork object
	 */	
	public static NeuralNetwork load(String filePath) {
		ObjectInputStream oistream = null;
		
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new FileNotFoundException("Cannot find file: " + filePath);
			}

			oistream = new ObjectInputStream( new BufferedInputStream(new FileInputStream(filePath)));
			NeuralNetwork nnet = (NeuralNetwork) oistream.readObject();

			return nnet;

		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} finally {
			if(oistream != null) {
				try {
					oistream.close();
				} catch (IOException ioe) {
				}
			}
		}

		return null;
	}

	/**
	 * Loads neural network from the specified InputStream.
	 *
	 * @param inputStream
	 *		input stream to load network from
	 * @return loaded neural network as NeuralNetwork object
	 */
	public static NeuralNetwork load(InputStream inputStream) {
		ObjectInputStream oistream = null;

		try {
			oistream = new ObjectInputStream(new BufferedInputStream(inputStream));
			NeuralNetwork nnet = (NeuralNetwork) oistream.readObject();

			return nnet;

		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} finally {
			if(oistream != null) {
				try {
					oistream.close();
				} catch (IOException ioe) {
				}
			}
		}

		return null;
	}

	/**
	 * Adds plugin to neural network
	 * @param plugin neural network plugin to add
	 */
	public void addPlugin(PluginBase plugin) {
		plugin.setParentNetwork(this);
		this.plugins.put(plugin.getClass(), plugin);
	}
	
	/**
	 * Returns the requested plugin
	 * @param pluginClass class of the plugin to get
	 * @return instance of specified plugin class
	 */
	public PluginBase getPlugin(Class pluginClass) {
		return this.plugins.get(pluginClass);
	}
	
	/**
	 * Removes the plugin with specified name
	 * @param pluginClass class of the plugin to remove
	 */
	public void removePlugin(Class pluginClass) {
		this.plugins.remove(pluginClass);
	}

        /**
         * Get network label
         * @return network label
         */
        public String getLabel() {
            return label;
        }

        /**
         * Set network label
         * @param label network label to set
         */
        public void setLabel(String label) {
            this.label = label;
        }


	

}
