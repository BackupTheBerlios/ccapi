package CCAPI;

import java.util.*;
import java.io.*;
/*import net.openai.ai.nn.architecture.*;
import net.openai.ai.nn.error.*;
import net.openai.ai.nn.learning.*;
import net.openai.ai.nn.input.*;
import net.openai.ai.nn.transfer.*;
import net.openai.ai.nn.data.*;
import net.openai.ai.nn.network.*;
import net.openai.ai.nn.terminator.*;
import net.openai.ai.nn.terminator.event.*;
import net.openai.ai.nn.iterator.*;
import net.openai.ai.nn.persistence.*;*/
import org.apache.log4j.*;


import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;

/***
 *	good parameter settings.
 *	l:0.9 m:0.5  	20/3/1
 *			20/42/1
 *
 *
 *
 * */



/**
 * This class is an example of how to create a neural net.  This is
 * an awkward way for the moment and will be quickly replaced by XML
 * files for network configuration.
 * 
 */
public class NeuralAnalyzer  implements NeuralNetListener{

	
	DatabaseLayer dbl = new DatabaseLayer();
	
	
    // our handle to the network
    FinancialLibrary fl=new FinancialLibrary();
    
    
    
    
    public void netStopped(NeuralNetEvent e){
	System.out.println("Net stopped.");
	test();
    }
    
     public void cicleTerminated(NeuralNetEvent e) {
	 Monitor mon = (Monitor) e.getSource();
	 long    c = mon.getCurrentCicle();
	                                                                                                                                                                 
	 // We want to print the results every 1000 cycles
	 if (c%100  == 0) {
	     System.out.println(c + " cycles remaining - Error = " + mon.getGlobalError());
	 }
     }
								 		
    
/**
     * Method declaration
          */
      public void netStarted(NeuralNetEvent e) {
              System.out.println("Training...");
      }
			                                                                                                                                                                              
    public void errorChanged(NeuralNetEvent e) {
	//Monitor m=(Monitor)e.getSource();
//	System.out.println("New error: "+m.getGlobalError());
    }
				                                                                                                                                                                              
    public void netStoppedError(NeuralNetEvent e,String error) {
    }
					      
    
    
    
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
     
     SigmoidLayer input, hidden, output;
    public void createNetwork(int inputNeurons, int hiddenNeurons, int outputNeurons) {
    
	input = new SigmoidLayer();
        hidden = new SigmoidLayer();
	output = new SigmoidLayer();
		                                                                                                                                                                                
	input.setLayerName("input");
	hidden.setLayerName("hidden");
	output.setLayerName("output");
					                                                                                                                                                                                
	// sets their dimensions
	input.setRows(inputNeurons);
	hidden.setRows(hiddenNeurons);
	output.setRows(outputNeurons);
									                                                                                                                                                                                
        // Now create the two Synapses
        FullSynapse synapse_IH = new FullSynapse();     /* input -> hidden conn. */
        FullSynapse synapse_HO = new FullSynapse();     /* hidden -> output conn. */
										                                                                                                                                                                                
        synapse_IH.setName("IH");
	synapse_HO.setName("HO");
														                                                                                                                                                                                
        // Connect the input layer whit the hidden layer
        input.addOutputSynapse(synapse_IH);
        hidden.addInputSynapse(synapse_IH);
							                                                                                                                                                                                
        // Connect the hidden layer whit the output layer
        hidden.addOutputSynapse(synapse_HO);
	output.addInputSynapse(synapse_HO);
																				                                                                                                                                                                                
	// Create the Monitor object and set the learning parameters
	monitor = new Monitor();
	                                                                                                                                
	monitor.setLearningRate(0.9);
	monitor.setMomentum(0.5);
	                                                                                                                                                
	// Passe the Monitor to all components
	input.setMonitor(monitor);
	hidden.setMonitor(monitor);
	output.setMonitor(monitor);
														                                                                                                                                                                                
						            // The application registers itself as monitor's listener so it can receive
							            // the notifications of termination from the net.
	monitor.addNeuralNetListener(this);
												                                                                                                                                                                                
	inputStream = new MemoryInputSynapse();
        input.addInputSynapse(inputStream);
	
    }
    Monitor monitor;
    MemoryInputSynapse inputStream;


    void train(){
	System.out.println("**********   B u i l d i n g   t r a i n i n g   s e t  ************");
	
	//populate the data set
	FileDataSourceCSV ds=new FileDataSourceCSV();
	Vector data = dbl.loadHistory("846900", 0);
	System.out.println("candles loaded from disc: "+data.size());
	
	double[][] dataset=new double[400][21];

	int startpos=200;
	int endpos=600;
	
//	for(int i=200;i<data.size()-100;i++){
	for(int i=startpos;i<endpos;i++){
	    Vector v1=new Vector();
	    for(int j=20;j>0;j--){
		Candle c=(Candle)data.elementAt(i+j);
		v1.addElement(c);
	    }
	    v1=normalizeVector(v1);
	    for(int j=20;j>0;j--){
//		Candle c=(Candle)v1.elementAt(j-1);
    		dataset[i-startpos][j-1]=Double.parseDouble((String)v1.elementAt(j-1));
	    }
	    
//	    System.out.println("Dataitems in input: "+inputData.size());
	    Candle c0=(Candle)data.elementAt(i);
	    Candle c1=(Candle)data.elementAt(i-1);
	    Candle c2=(Candle)data.elementAt(i-2);
	    if(c1.close>c0.close &&  c2.close>c1.close){
		dataset[i-startpos][20]=1.0;
	    }
	    else{
		dataset[i-startpos][20]=0.0;
	    }
	    
	    
	    System.out.println("******   building next dataset:  "+i);
//	    System.out.println(" ******* Training done. ********");
	}
		    
	System.out.println("*********  Datasets built.");
	inputStream.setInputArray(dataset);
	inputStream.setAdvancedColumnSelector("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19");

	TeachingSynapse trainer = new TeachingSynapse();
	                                                                                                                                                                             
	trainer.setMonitor(monitor);

	MemoryInputSynapse samples = new MemoryInputSynapse();
		                                                                                                                                                                               
		                                                                                                                                                                               
		           // The output values are on the third column of the file
       samples.setInputArray(dataset);
       samples.setAdvancedColumnSelector("20");
       trainer.setDesired(samples);
						                                                                                                                                                                               
						           // Connects the Teacher to the last layer of the net
	output.addOutputSynapse(trainer);
								                                                                                                                                                                               
						                   /*
						                    * All the layers must be activated invoking their method start;
							                     * the layers are implemented as Runnable objects, then they are
									                      * instanziated on separated threads.
									                       */
	input.start();
	hidden.start();
	output.start();
	monitor.setTrainingPatterns(399); // # of rows (patterns) contained in the input file
	monitor.setTotCicles(5000);            // How many times the net must be trained on the input patterns
	monitor.setLearning(true);              // The net must be trained
	monitor.Go();                                   // The net starts the training job
																									       
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
	    
	System.out.println("************** TEST ");
	input.removeAllInputs();
	output.removeAllOutputs();
	
	
	DirectSynapse memInp=new DirectSynapse();
	input.addInputSynapse(memInp);
	
	
	 DirectSynapse memOut = new DirectSynapse();
         output.addOutputSynapse(memOut);
         // Now we interrogate the net
         monitor.setLearning(false);
        input.start();
	hidden.start();
	output.start();
		
//	memInp.start();
//	memOut.start();
	
	 monitor.Go();

		 
	FileDataSourceCSV ds=new FileDataSourceCSV();
	Vector data=ds.loadCSVFile("/home/us/84690020040805.csv");
	System.out.println("candles loaded from disc: "+data.size());

	int startpos=3;
	int endpos=103;

	double[] drawData=new double[101];
	double[] drawData2=new double[101];
	
	Vector candles=new Vector();

	
	double[][] dataset=new double[100][20];
//	for(int i=200;i<data.size()-100;i++){
	for(int i=endpos-1;i>=startpos;i--){
	    db("\n");
	    
	    Vector v1=new Vector();
	    for(int j=20;j>0;j--){
		Candle c=(Candle)data.elementAt(i+j);
		v1.addElement(c);
	    }
	    v1=normalizeVector(v1);
	    for(int j=20;j>0;j--){
//		Candle c=(Candle)v1.elementAt(j-1);
    		dataset[i-startpos][j-1]=Double.parseDouble((String)v1.elementAt(j-1));
	    }
	   
	    
//	    System.out.println("Dataitems in input: "+inputData.size());
	    Candle c0=(Candle)data.elementAt(i);
	    candles.add(c0);
	    
	    db(c0.toString());
	    Candle c1=(Candle)data.elementAt(i-1);
	    Candle c2=(Candle)data.elementAt(i-2);
	    if(c1.close>c0.close && c2.close>c0.close && c2.close>c1.close){
		//dataset[i-300][20]=1.0;
		db("des: 1.0");
	    }
	    else{
//		dataset[i-300][20]=0.0;
		db("des: 0.0");
	    }
	    
	    
	 
	 d(dataset[i-startpos]);
	 
	  Pattern iPattern = new Pattern(dataset[i-startpos]);
//          iPattern.setCount(i+1-300);
//	iPattern.setCount(1);

			                          // Inrrogate the net
          memInp.fwdPut(iPattern);
//    	d(memInp.getInputVector());					
	  		                          // Read the output pattern and print out it
		if(i!=(endpos-1)){							                      //double[] pattern = memOut.getNextPattern();
		
			db("Put, waiting for get.");
	        	  Pattern pattern = memOut.fwdGet();
			  double val=pattern.getArray()[0];
	        	  System.out.println("Output Pattern #"+(i+1-startpos)+" = "+val);
			  drawData2[i-startpos]=val;

		}
																		      						     	
	}
	


	//draw routines
	//
	Vector normalizedCandles=normalizeVector(candles);
	for(int k=0;k<normalizedCandles.size();k++){
		drawData[k]=Double.parseDouble((String)normalizedCandles.elementAt(k));
	}
	
	ChartWindow cw=new ChartWindow("test");
	cw.draw(drawData);
	cw.draw(drawData2);
	
	//draw that stuff.
    
    }
    void d(double[] in){
	System.out.println("");
	for(int i=0;i<in.length;i++){
	    System.out.print("In"+i+"="+in[i]+"   ");
	}
	System.out.println("");
    }

    void d(Vector in){
	System.out.println("");
	for(int i=0;i<in.size();i++){
	    System.out.print("In"+i+"="+(Object)in.elementAt(i).toString()+"   ");
	}
	System.out.println("");
    }
    public NeuralAnalyzer(String symbol){
	//construct the network.
	
	
	createNetwork(20,7,1);
	
	train();
	
	
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
    	NeuralAnalyzer analyzer = new NeuralAnalyzer("abcd");
    } 

    private static void db(String s) {
	System.err.println("analyzer: " + s);
    } 

}


	
