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
 * This class is an example of how to create a neural net. This is an awkward
 * way for the moment and will be quickly replaced by XML files for network
 * configuration.
 *  
 */

public class NeuralAnalyzer implements NeuralNetListener {

	/**
	 * NETWORK SETTINGS START
	 */

	//int inputN = 81, hiddenN = 39, outputN = 12;
	int inputN = 20, hiddenN = 3, outputN = 1;

	int trainingCycles = 300;

	/**
	 * NETWORK SETTINGS END
	 */

	double[][] trainingInput;

	double[][] trainingOutput;

	// 
	DatabaseLayer dbl = new DatabaseLayer();

	// our handle to the network
	FinancialLibrary fl = new FinancialLibrary();

	public void netStopped(NeuralNetEvent e) {
		System.out.println("Net stopped.");
		
		
		test();
		
		
	}

	public void cicleTerminated(NeuralNetEvent e) {
		Monitor mon = (Monitor) e.getSource();
		long c = mon.getCurrentCicle();

		// We want to print the results every 100 cycles
		if (c % 100 == 0) {
			System.out.println(c + " cycles remaining - Error = "
					+ mon.getGlobalError());
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

	public void netStoppedError(NeuralNetEvent e, String error) {
	}

	/**
	 * 
	 * This method creates the analyzer network. The input and output layers
	 * have fixed sizes (2 and 1 respectively). The hiddenlayers are created
	 * with the arguments passed in on the command line. All algorithms are
	 * selected by default to be those that we have implemented...
	 * 
	 * @param args
	 *            The command line args passed into main.
	 */

	SigmoidLayer input, hidden, output;

	public void createNetwork(int inputNeurons, int hiddenNeurons,
			int outputNeurons) {

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
		FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden conn. */
		FullSynapse synapse_HO = new FullSynapse(); /* hidden -> output conn. */

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

		// The application registers itself as monitor's listener so it can
		// receive
		// the notifications of termination from the net.
		monitor.addNeuralNetListener(this);

		inputStream = new MemoryInputSynapse();
		input.addInputSynapse(inputStream);

	}

	Monitor monitor;

	MemoryInputSynapse inputStream;

	MemoryInputSynapse outputStream;

	void trainSetCandleSticks() {

		// build training sets

		// Training set will contain:
		// 10 historic candles ( means 40 sets )
		// 10 historic ADX values ( means 10 values)
		// 10 historic MACD + Trigger values ( means 20 values )
		// 10 historic SMA 5 ( means 10 values )
		// 10 historic MOMENTUM ( means 10)

		// complete: 80 values per entry.

		// need to load the symbol data from our dataset.

		Vector data = dbl.loadHistory("846900", 0);
		System.out.println("candles loaded from disc: " + data.size());

		// startcandle is the latest candle which we will actually use to build
		// training data.
		int startcandle = 300;
		int stopcandle = 2000;

		trainingInput = new double[stopcandle - startcandle][81];

		trainingOutput = new double[stopcandle - startcandle][12];

		// build the layers.

		for (int i = 0; i < trainingInput.length; i++) {
			// iterate over the different patterns

			for (int j = 0; j < 10; j++) {

				Candle c = (Candle) data.elementAt(startcandle + i + j);

				// contains temporary values
				Vector temporaryVector = new Vector();
				for (int k = 0; k < 100; k++) {
					temporaryVector.addElement(data.elementAt(startcandle + i
							+ j + k));
				}
				// temporary adding done.

				// ok, now we populate the input vector with our candles.

				trainingInput[i][(j * 8) + 0] = c.open;
				trainingInput[i][(j * 8) + 1] = c.hi;
				trainingInput[i][(j * 8) + 2] = c.low;
				trainingInput[i][(j * 8) + 3] = c.close;

				// starting at four the various indicators are filled in.
				trainingInput[i][(j * 8) + 4] = fl.adx(14, temporaryVector, 0);

				trainingInput[i][(j * 8) + 5] = fl.MACD(12, 26, 9, temporaryVector,
						0)[0];
				trainingInput[i][(j * 8) + 6] = fl.MACD(12, 26, 9, temporaryVector,
						0)[1];

				trainingInput[i][(j * 8) + 7] = fl.SMA(5, temporaryVector, 0);
				trainingInput[i][(j * 8) + 8] = fl.Momentum(10, temporaryVector, 0);

			}

			// ok, input patterns built.
			// build the input patterns

			Candle c1 = (Candle) data.elementAt(startcandle + i - 1);
			Candle c2 = (Candle) data.elementAt(startcandle + i - 2);
			Candle c3 = (Candle) data.elementAt(startcandle + i - 3);

			trainingOutput[i][0] = c1.open;
			trainingOutput[i][1] = c1.hi;
			trainingOutput[i][2] = c1.low;
			trainingOutput[i][3] = c1.close;

			trainingOutput[i][4] = c2.open;
			trainingOutput[i][5] = c2.hi;
			trainingOutput[i][6] = c2.low;
			trainingOutput[i][7] = c2.close;

			trainingOutput[i][8] = c3.open;
			trainingOutput[i][9] = c3.hi;
			trainingOutput[i][10] = c3.low;
			trainingOutput[i][11] = c3.close;

			// ok. output patterns built.
		}

		inputStream.setInputArray(trainingInput);
		
		String ret = "";
		for(int n = 1; n < 82; n++){
			ret += n;
			if(n!=81)ret +=",";
		}
		
		// specifying which input is used from the dataset in the inpustream ...
		inputStream.setAdvancedColumnSelector(ret);

		//// the desired values.
		outputStream = new MemoryInputSynapse();

		// The output values are on the third column of the file
		outputStream.setInputArray(trainingOutput);
		outputStream.setAdvancedColumnSelector("1,2,3,4,5,6,7,8,9,10,11,12");
		
		
	}

	void buildTrainingSetSimple() {
		System.out
				.println("**********   B u i l d i n g   t r a i n i n g   s e t  ************");

		//populate the data set
		Vector data = dbl.loadHistory("846900", 0);
		System.out.println("candles loaded from disc: " + data.size());

		trainingInput = new double[2000][20];
		trainingOutput = new double[2000][1];
		int startpos = 200;
		int endpos = 2200;

		//	for(int i=200;i<data.size()-100;i++){
		for (int i = startpos; i < endpos; i++) {


			double[] tempIn = new double[20];
			tempIn[0] = 0;
			Candle c0  = (Candle)data.elementAt(i);
			for (int j = 1; j < 20; j++) {
				Candle c = (Candle) data.elementAt(i + j);
				tempIn[j] = (c.close - c0.close);
			}
			
			normalizeArray(tempIn);
			
			for (int j = 0; j < 20; j++) {
				//Candle c=(Candle)v1.elementAt(j);
				trainingInput[i - startpos][j] = tempIn[j];
				//dataset[i-startpos][j] = 0;
			}
			// building input vector END

			//	    System.out.println("Dataitems in input: "+inputData.size());
			c0 = (Candle) data.elementAt(i);
			Candle c1 = (Candle) data.elementAt(i - 1);
			Candle c2 = (Candle) data.elementAt(i - 2);
			Candle c4 = (Candle) data.elementAt(i - 3);
			Candle c5 = (Candle) data.elementAt(i - 4);
			Candle c6 = (Candle) data.elementAt(i - 5);

			// building the expected output set. - training for a three day rise
			/*
			 * if (c1.close > c0.close && c2.close > c1.close && c1.close >
			 * c0.close*1.01 && c2.close > c1.close*1.01) { dataset[i -
			 * startpos][20] = 1.0; } else { dataset[i - startpos][20] = 0.0; }
			 */

			if (c1.close > c0.close && c2.close > c1.close) {
				trainingOutput[i - startpos][0] = 1.0;

			} else {
				trainingOutput[i - startpos][0] = 0.0;
			}

			System.out.println("******   building next dataset:  " + i);
			//	    System.out.println(" ******* Training done. ********");
		}

		inputStream.setInputArray(trainingInput);
		// specifying which input is used from the dataset in the inpustream ...
		inputStream
				.setAdvancedColumnSelector("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");

		//// the desired values.
		outputStream = new MemoryInputSynapse();

		// The output values are on the third column of the file
		outputStream.setInputArray(trainingOutput);
		outputStream.setAdvancedColumnSelector("1");

	}

	void train() {

		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setMonitor(monitor);

		trainer.setDesired(outputStream);

		// Connects the Teacher to the last layer of the net
		output.addOutputSynapse(trainer);

		/*
		 * All the layers must be activated invoking their method start; the
		 * layers are implemented as Runnable objects, then they are
		 * instanziated on separated threads.
		 */
		input.start();
		hidden.start();
		output.start();
		// start done.

		monitor.setTrainingPatterns(trainingInput.length); // # of rows (patterns) contained in
		// the input file
		monitor.setTotCicles(trainingCycles); // How many times the net must be
											  // trained on
		// the input patterns
		monitor.setLearning(true); // The net must be trained
		monitor.Go(); // The net starts the training job

	}

	/**
	 * normalizes and returns doubles.
	 * 
	 * @param input
	 * @return
	 */
	Vector normalizeVector(Vector input) {
		Vector ret = new Vector();

		double min = fl.min(input, 0, input.size());
		double max = fl.max(input, 0, input.size());

		for (int i = 0; i < input.size(); i++) {
			Candle c = (Candle) input.elementAt(i);
			ret.addElement("" + ((c.close - min) / (max - min)));
		}

		return ret;

	}

	/**
	 * normalizes an double array
	 * 
	 * @param input
	 */
	void normalizeArray(double[] input) {

		double min = 1000000000;
		double max = -111111111;
		for (int i = 0; i < input.length; i++) {
			if (input[i] < min)
				min = input[i];
			if (input[i] > max)
				max = input[i];
		}

		// do normalization
		for (int j = 0; j < input.length; j++) {
			input[j] = ((input[j] - min) / (max - min));
		}

	}

	public void test() {

		System.out.println("************** TEST ");
		input.removeAllInputs();
		output.removeAllOutputs();

		DirectSynapse memInp = new DirectSynapse();
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

		Vector data = dbl.loadHistory("846900", 0);

		System.out.println("candles loaded from disc: " + data.size());

		int startpos = 0;
		int endpos = 200;

		double[] drawData = new double[200];
		double[] drawData2 = new double[200];

		Vector candles = new Vector();

		double[][] dataset = new double[200][20];
		//	for(int i=200;i<data.size()-100;i++){
		for (int i = endpos - 1; i >= startpos; i--) {
			db("\n");

			
			double[] tempIn = new double[20];
			tempIn[0] = 0;
			Candle c0  = (Candle)data.elementAt(i);
			for (int j = 1; j < 20; j++) {
				Candle c = (Candle) data.elementAt(i + j);
				tempIn[j] = (c.close - c0.close);
			}
			
			normalizeArray(tempIn);

			for (int j = 20; j > 0; j--) {
				//		Candle c=(Candle)v1.elementAt(j-1);
				dataset[i - startpos][j - 1] = tempIn[j-1];
			}

			//	    System.out.println("Dataitems in input: "+inputData.size());
		
			candles.add(c0);

			db(c0.toString());

			/*
			 * Candle c1 = (Candle) data.elementAt(i - 1); Candle c2 = (Candle)
			 * data.elementAt(i - 2); if (c1.close > c0.close && c2.close >
			 * c0.close && c2.close > c1.close) { //dataset[i-300][20]=1.0;
			 * db("des: 1.0"); } else { // dataset[i-300][20]=0.0; db("des:
			 * 0.0"); }
			 * 
			 * d(dataset[i - startpos]);
			 */

			Pattern iPattern = new Pattern(dataset[i - startpos]);
			//          iPattern.setCount(i+1-300);
			//	iPattern.setCount(1);

			// Inrrogate the net
			memInp.fwdPut(iPattern);
			//    	d(memInp.getInputVector());
			// Read the output pattern and print out it
			//if (i != (endpos - 1)) { //double[] pattern =
			// memOut.getNextPattern();

			if (i != (endpos)) {

				db("Put, waiting for get.");
				Pattern pattern = memOut.fwdGet();
				double val = pattern.getArray()[0];
				System.out.println("Output Pattern #" + (i + 1 - startpos)
						+ " = " + val);
				drawData2[i - startpos] = val;

			}

		}

		//draw routines
		//
		Vector normalizedCandles = normalizeVector(candles);
		for (int k = 0; k < normalizedCandles.size(); k++) {
			drawData[k] = Double.parseDouble((String) normalizedCandles
					.elementAt(k));
		}

		ChartWindow cw = new ChartWindow("test");
		cw.draw(drawData);
		cw.draw(drawData2);

		//draw that stuff.

	}

	void d(double[] in) {
		System.out.println("");
		for (int i = 0; i < in.length; i++) {
			System.out.print("In" + i + "=" + in[i] + "   ");
		}
		System.out.println("");
	}

	void d(Vector in) {
		System.out.println("");
		for (int i = 0; i < in.size(); i++) {
			System.out.print("In" + i + "="
					+ (Object) in.elementAt(i).toString() + "   ");
		}
		System.out.println("");
	}

	public NeuralAnalyzer(String symbol) {
		//construct the network.

		createNetwork(inputN, hiddenN, outputN);
		buildTrainingSetSimple();
		//trainSetCandleSticks();
		train();

		//trainSetCandleSticks();

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
		System.out.println("analyzer: " + s);
	}

}

