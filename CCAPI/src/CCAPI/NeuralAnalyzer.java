package CCAPI;

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


import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;




/**
 * This class is an example of how to create a neural net.  This is
 * an awkward way for the moment and will be quickly replaced by XML
 * files for network configuration.
 * 
 */
public class NeuralAnalyzer implements TerminatedEventListener {

	

	public NeuralAnalyzer(){
		
	}
    // our handle to the network
    public Network network = null;

    // the collection of layers for the network
    Vector  layers = null;
    FinancialLibrary fl=new FinancialLibrary();
    
    
    
    
    
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


	network.setSize(2);

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
	    BackPropagationLearningRule rule=new BackPropagationLearningRule();
	    rule.setAlpha(0.9);
	    rule.setBeta(0.9);
	    layer.setLearningRule(rule);
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
	//network.setLearning(true);

	// set the training data
	//network.loadTrainingData("./trainingdata/xor.in", "./trainingdata/xor.out");

	// connect the network
	try {
	    network.connect();
	    network.randomize();
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
	System.out.println("Event: "+event.toString());
	if(event instanceof TerminateOnCriterionEvent) {
	    TerminateOnCriterionEvent terminateEvent 
		= (TerminateOnCriterionEvent) event;
	    System.out.println("Training complete.");
	    System.out.println("Error reached: " 
			       + terminateEvent.getErrorReached());
	}
    }
    
    
   

    void train(){
	System.out.println("**********   B u i l d i n g   t r a i n i n g   s e t  ************");
	//build the training set
	DataSet traindata=new DataSet();
	traindata.addOutputCategory("buy");
	
	for(int i=0;i<20;i++){
	    traindata.addInputCategory("Input"+i);
	}
	
	//populate the data set
	DataSource ds=new DataSource();
	Vector data=ds.loadCSVFile("/home/us/84690020040805.csv");
	System.out.println("candles loaded from disc: "+data.size());
	

//	for(int i=200;i<data.size()-100;i++){
	for(int i=200;i<300;i++){
	    DataElement de=new DataElement();
	    Vector inputData=new Vector();
	    
	    for(int j=20;j>0;j--){
		Candle c=(Candle)data.elementAt(i+j);
		inputData.addElement(c);
	    }
//	    System.out.println("Dataitems in input: "+inputData.size());
	    de.setInput(normalizeVector(inputData));
	    
	    Candle c0=(Candle)data.elementAt(i);
	    Candle c1=(Candle)data.elementAt(i-1);
	    Candle c2=(Candle)data.elementAt(i-2);
	    Vector desiredData=new Vector();
	    if(c1.close>c0.close && c2.close>c0.close && c2.close>c1.close){
		desiredData.addElement(""+1.0);
	    }
	    else{
		desiredData.addElement(""+0.0);
	    }
	    
	    //DataElement trainelement=new DataElement(inputData, desiredData);
	    
	    de.setDesired(desiredData);
	    de.setOutput(desiredData);
	    
	    //de.setOutput(desiredData);
	    
	    traindata.addElement(de);
	    
	    System.out.println("******   building next dataset:  "+i);
//	    System.out.println(" ******* Training done. ********");
	}
		    
	

	//set the dataset
	network.setDataSet(traindata);
	
	//train the network.
	network.setLearning(true);
	train(5.0);
	network.setLearning(false);
	
    }


    Vector normalizeVector(Vector input){
	Vector ret=new Vector();
	
	double min=fl.min(input,0, input.size());
	double max=fl.max(input,0,input.size());
	
	for(int i=0;i<input.size();i++){
	    Candle c=(Candle)input.elementAt(i);	
	    ret.addElement(""+((c.close-min)/(max-min)));
	}
	
	return ret;
	
    }

    public void test(){
    	network.setLearning(false);

	BatchNetworkIterator batch=new BatchNetworkIterator();

//populate the data set
	DataSource ds=new DataSource();
	Vector data=ds.loadCSVFile("/home/us/84690020040805.csv");
	System.out.println("candles loaded from disc: "+data.size());
	
	

	for(int i=10;i<200;i++){
	    DataSet dataset=new DataSet();
	    dataset.addOutputCategory("buy");
	
	    for(int k=0;k<20;k++){
		dataset.addInputCategory("Input"+i);
	    }
	
	    DataElement de=new DataElement();
	    Vector inputData=new Vector();
	    for(int j=20;j>0;j--){
		Candle c=(Candle)data.elementAt(i+j);
		inputData.addElement(c);
	    }
	    
	    de.setInput(normalizeVector(inputData));
	    
	    Candle c0=(Candle)data.elementAt(i);
	    Candle c1=(Candle)data.elementAt(i-1);
	    Candle c2=(Candle)data.elementAt(i-2);
	    
	    
	    //de.setDesired(new Vector());
	    Vector o=new Vector();
	    o.addElement(""+0);
    	    de.setOutput(o);
	    
	    dataset.addElement(de);
	    
	    network.setDataSet(dataset);
	    //network.setDataElement(de);

	    
	    
	    try{
        	Layer in=network.getLayerAt(0);
		Layer hid=network.getLayerAt(1);
    		Layer out=network.getLayerAt(2);
	    
	
		//in.calculate();
		//hid.calculate();
		//out.calculate();    
	    
		batch.iterate(network);
	    
		//network.iterateNetwork();
		//network.iterateNetwork();	
	    }	
	    catch(Exception e){
		e.printStackTrace();
	    }
	    
	    Layer out=network.getOutputLayer();
	    
	    
	    System.out.println("-----");
	    System.out.println(c0.toString());
	    System.out.println(c1.toString());
	    System.out.println(c2.toString());
	    System.out.println("out:"+out.toString());
	    
	}
	

    
    }


    public NeuralAnalyzer(String symbol){
	//construct the network.
	
	
	createNetwork(20,2,1);
	
	//set network algorithms.
	setNetworkAlgorithms();
	train();
	
	test();	
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


	int i=20;
	int h=3;
	int o=1;
	

	NeuralAnalyzer analyzer = new NeuralAnalyzer("abcd");
/*
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
*/
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
	
