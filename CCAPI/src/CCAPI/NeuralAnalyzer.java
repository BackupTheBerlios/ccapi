/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

import java.util.*;
import java.io.*;
import net.openai.ai.nn.architecture.*;
import net.openai.ai.nn.error.*;
import net.openai.ai.nn.learning.*;
import net.openai.ai.nn.input.*;
import net.openai.ai.nn.transfer.*;
import net.openai.ai.nn.data.*;
import net.openai.ai.nn.network.*;
import net.openai.ai.nn.terminator.*;
import net.openai.ai.nn.terminator.event.*;
import net.openai.ai.nn.iterator.*;
import net.openai.ai.nn.persistence.*;
import org.apache.log4j.*;


/**
 * This class is an example of how to create a neural net.  This is
 * an awkward way for the moment and will be quickly replaced by XML
 * files for network configuration.
 * 
 */
public class NeuralAnalyzer implements TerminatedEventListener {

    // our handle to the network
    public Network network = null;

    // the collection of layers for the network
    Vector  layers = null;

    /**
     * 
     * This method creates the analyzer network.  The input and output
     * layers have fixed sizes (2 and 1 respectively).  The
     * hiddenlayers are created with the arguments passed in on the
     * command line.  All algorithms are selected by default to be
     * those that we have implemented...
     * 
     * @param args The command line args passed into main.
     */
    public void createNetwork(int inputNeurons, int hiddenNeurons, int outputNeurons) {
	network = new Network();

	// by uncommenting this next line the net will converge much
	// faster but some error messages pop up...need to take care
	// of those in a bias situation.
	//network.setUseBias(true);


	network.setSize(3);

	layers = network.getLayers();

	Layer layer = null;

	// set the parameters for each of the hidden layers.
	for (int i = 0; i < (layers.size()); i++) {
	    layer = (Layer) layers.elementAt(i);

	    if (i == 0) {

		// if this is the first layer, set it's size to 2
		// for the analyzer problem this is fixed.
		layer.setName("Input");
		layer.setSize(inputNeurons);
	    } else if (i == (layers.size() - 1)) {

		// if this is the output layer set it's size to 1
		// again, being the analyzer this is fixed
		layer.setName("Output");
		layer.setSize(outputNeurons);
	    } else {

		// the rest should be hidden layers
		layer.setName("Hidden");
		layer.setSize(hiddenNeurons);
	    } 

	    // for all layers set these default algorithms
	    layer.setInputFunction(new DotProductInputFunction());
	    layer.setLearningRule(new BackPropagationLearningRule());
	    layer.setTransferFunction(new SigmoidTransferFunction());
	    //db(layers.toString());
	} 

	//db(layers.toString());
    } 

    /**
     * This method just sets the overall network parameters
     */
    public void setNetworkAlgorithms() {

	// set the architecture type for the network
	network.setArchitecture(new FeedForwardArchitecture());

	// set the error type
	network.setErrorType(new MeanSquareErrorType());

	// set the learning flag
	network.setLearning(true);

	// set the training data
	network.loadTrainingData("./trainingdata/xor.in", "./trainingdata/xor.out");

	// connect the network
	try {
	    network.connect();
	} catch (Exception e) {
	    db("Could not connect network.");
	    e.printStackTrace();
	} 
    } 

    /**
     * This method sets the criterion and runs the network to that
     * criterion.
     * @param criterion  The error level to train the network to...
     */
    public void train(double criterion) {
	SequentialNetworkIterator iterator = new SequentialNetworkIterator();
	iterator.setIterationTerminator(new TerminateOnCriterion(criterion));
	network.setNetworkIterator(iterator);
	network.addTerminatedEventListener(this);
	try {
	    network.iterateNetwork();
	} catch (NetworkException e) {
	    db("Network could not be iterated further...");
	    e.printStackTrace();
	}
    } 

    /**
     * Handles the terminated event.
     *
     * @param event The TerminatedEvent
     */
    public void handleTerminatedEvent(TerminatedEvent event) {
	if(event instanceof TerminateOnCriterionEvent) {
	    TerminateOnCriterionEvent terminateEvent 
		= (TerminateOnCriterionEvent) event;
	    System.out.println("Training complete.");
	    System.out.println("Error reached: " 
			       + terminateEvent.getErrorReached());
	}
    }


    /**
     * Method declaration
     *
     *
     * @param args
     *
     * @see
     */
    public static void main(String[] args) {
	//  start log4j
	BasicConfigurator.configure();


	int i=2;
	int h=3;
	int o=1;
	

	NeuralAnalyzer analyzer = new NeuralAnalyzer();

	analyzer.createNetwork(i,h,o);
	analyzer.setNetworkAlgorithms();



	//function to train the network! 

	analyzer.train(0.1);

	//now i want to pipe one pattern into the net.
	
	analyzer.network.setLearning(false);


	DataSet data=new DataSet();
	DataElement dataelement=new DataElement();
	Vector v=new Vector();
	v.addElement(""+0.1);
	v.addElement(""+0.0);
	dataelement.setInput(v);	
	Vector v1=new Vector();
//	v1.add(""+0.0);
	
	dataelement.setDesired(v1);
	data.addElement(dataelement);
	
	
	analyzer.network.setDataSet(data);	
	try{
        	analyzer.network.iterateNetwork();
		
	}
	catch(Exception e){
	    e.printStackTrace();
	}

	Layer out=analyzer.network.getOutputLayer();

	System.out.println("out:"+out.toString());

    } 


	/**
	 * An example of marshalling/unmarshalling NN objects
	 */
	public void xmlTrainingDataTest(){
		db("*****************************************");
		db("Marshalling training set . . .");
		db("TR(original)\n "+network.getDataSet().toString());
		Persistence.store(network.getDataSet(),"tr_data");

		DataSet trs = (DataSet) Persistence.retrieve("tr_data",DataSet.class);
		db("TR(from XML)\n "+trs.toString());
	}

	/**
	 * This is an example of how you can store NN
	 * to hard drive and restore it back.
	 */
	public void xmlDumpTest(){
		
		Network nn = null; 
		FeedForwardArchitecture ta = new FeedForwardArchitecture();
		MeanSquareErrorType te = new MeanSquareErrorType();

		//db(ta.printNetworkConnections(network));	
		//db(network.toString());

		db("*****************************************");
	    db("Marshalling network (saving to XML) . . .");
	    Persistence.store(network,"nn");
				
		try {
	    	db("Unmarshalling (loading from XML) network . . .");
			nn = 
				(Network)Persistence.retrieve("nn",Network.class);
			nn.reconnect();
	    	
		} catch (Exception e) {
	    	db("Could not load and/or re-connect network.");
	    	e.printStackTrace();
		}

		
		//db(nn.toString());
		//db(ta.printNetworkConnections(nn));	

		db("Error (pre-serialization): "+network.getError());
		db("Error (post de-serialization 1): "+nn.getError());

		te.calculateError(nn);
		db("Error (1) before iteration: "+nn.getError());
		//  broken for now...we need a new IterationTerminator
		//  that takes a iteration limit.
		//nn.iterate(1);
		db("Error after 1 iteration: "+nn.getError());
		//db(nn.toString());
		
		Persistence.store(nn,"nn2");

		/*
		try {
	    db("Unmarshalling (loading from XML) network . . .");
		nn = 
			Persistence.networkUnmarshaller("nn2", 
			"conf/openai_nn.map");
	    	nn.reconnect();	
		} catch (Exception e) {
	    	db("Could not load and/or re-connect network.");
	    	e.printStackTrace();
		}
		db("Error (post de-serialization 1): "+nn.getError());
		te.calculateError(nn);
		db("Error (1) before iterations: "+nn.getError());
		nn.iterate(1);//ToCriterion();
		te.calculateError(nn);
		db("Error after 1 iteration (2): "+nn.getError());
		*/
	}
	
	/**
	 * NN serialization test
	 */
	public void serializationDumpTest(){
		db("*****************************************");
	    db("Serializing network (saving to binary) . . .");
		
		db("Error (pre-serialization): "+network.getError());
		Persistence.serialize(network, "dash.ai");
				
	    db("De-serializing (loading from binary) network . . .");
		Network nn = (Network) 
			Persistence.deserialize("dash.ai");
		
		db("Error (post de-serialization) before iterations: "+nn.getError());

		//  broken for now...we need a new IterationTerminator
		//  that takes a iteration limit.
		//nn.iterate(1);
		db("Error (post de-serialization) after iterations: "+nn.getError());
		Persistence.serialize(network, "dash2.ai");
	}
	
    /**
     * Method declaration
     *
     *
     * @param s
     *
     * @see
     */
    private static void db(String s) {
	System.err.println("analyzer: " + s);
    } 

}



/*--- formatting done in "Sun Java Convention" style on 02-14-2002 ---*/
	
