/*
 * Created on Oct 11, 2003
 *
 * Part of Stocks! 1.0
 *
 */

package CCAPI;

/**
 * this class is contains a chart window
 *
 * @author uls
 *
 */

import java.awt.*;
import java.util.*;
import java.text.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.*;
import org.jfree.data.time.*;
import org.jfree.data.*;


public class ChartWindow extends JFrame {
	String symbol=""; //contains the symbol for this chartwindow
	JPanel toolbar, toolbarleft, mainpanel; //a panel supposed to be a toolbar
	JComboBox jcb; //the dropdown containing the symbols
	JComboBox timerange; //the dropdown for the complete timerange
	int timerangeInMilliseconds;

	JComboBox compression; //the dropdown for the duration
	int timerangems;

	public String code="";
	JLabel statusLine;

	JFreeChart chart;
	//JFreeChart datasets

	CombinedRangeXYPlot parent;

	public TimeSeriesCollection dataset1=new TimeSeriesCollection(); // XYSeriesCollection (see ttp://www.jfree.org/jfreechart/javadoc/org/jfree/data/XYSeriesCollection.html)
	public TimeSeriesCollection dataset2=new TimeSeriesCollection(); // XYSeriesCollection (see ttp://www.jfree.org/jfreechart/javadoc/org/jfree/data/XYSeriesCollection.html)
	public TimeSeriesCollection dataset3=new TimeSeriesCollection(); // XYSeriesCollection (see ttp://www.jfree.org/jfreechart/javadoc/org/jfree/data/XYSeriesCollection.html)
	public VectorHighLowDataset hld=new VectorHighLowDataset("time");


	TimeSeries x1;
	TimeSeries bollinger_top;
	TimeSeries bollinger_bottom;

	TimeSeries env1, env2;

	TimeSeries average1;
	TimeSeries average2;
	ChartPanel chartPanel;
	Vector components=new Vector(); //contains plugin components

	//helper variables
	int av1=5;


	/**
	 * constructor. Constructs the chart window.
	 * @param s
	 * @param symbol
	 */

	ChartWindow thisp;
	public ChartWindow(String symbol){

		this.symbol=symbol;
		thisp=this;
		//initialize the main layout
		setTitle(symbol);
		mainpanel=new JPanel();
		GridBagLayout gbl=new GridBagLayout();
		GridBagConstraints gbc=new GridBagConstraints();
		mainpanel.setLayout(gbl);

		//build a toolbar
//		add a plain status bar
		this.statusLine=new JLabel("Done");
		this.getContentPane().add(statusLine, BorderLayout.SOUTH);

		statusLine.setBackground(new Color(0,0,0));
		statusLine.setForeground(new Color(255,255,255));
		statusLine.setOpaque(true);


		//
		x1=new TimeSeries("symbol", FixedMillisecond.class);
		dataset1.addSeries(x1);

		System.out.println("Populated.");

		//
		chart = createChart(dataset1);
		System.out.println("constr.");

		//chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);

		chart.setAntiAlias(false);

		chartPanel = new ChartPanel(chart);

		//

		int i=0;
		gbc.anchor=gbc.NORTHWEST;
		gbc.fill=gbc.BOTH;
		gbc.weightx=1;
		gbc.gridx=0;

		gbc.weighty=1;
		gbc.gridy=i;
		gbl.setConstraints(chartPanel, gbc);

		mainpanel.add(chartPanel);
		//System.out.println("add");
		setVisible(true);
		setSize(new Dimension(400,300));

		//chartPanel.setPopupMenu(buildPopupMenu());
		chartPanel.setMouseZoomable(true);
		chartPanel.setHorizontalAxisTrace(true);
		chartPanel.setVerticalAxisTrace(true);
		chartPanel.setHorizontalZoom(true);

		chartPanel.setOpaque(true);
		chartPanel.setBackground(new Color(0,0,0));



		this.getContentPane().add(mainpanel, BorderLayout.CENTER);

		this.setSize(600,400);
		this.toFront();
		this.show();
	}


	/**
	 * temporal helper function
	 *
	 * @param dataset
	 * @return
	 */
	private JFreeChart createChart(TimeSeriesCollection dataset) {


		NumberAxis axis=new NumberAxis(null);
		axis.setAutoRangeIncludesZero(false);


		//parent=new CombinedRangeXYPlot(axis);

		//chart = null;

		//XYPlot plot2=new XYPlot(dataset2, new DateAxis(null), null, new StandardXYItemRenderer());
		//XYPlot subplot2=new XYPldt(dataset2, new DateAxis("Date 2"), null, )


		//parent.add(subplot1);
		//parent.add(subplot2);

		//chart=new JFreeChart(null, null, parent, false);

		chart=ChartFactory.createTimeSeriesChart(null, "", "", dataset, false, false, false);

		XYPlot plot1=chart.getXYPlot();

		plot1.setDataset(dataset);
		plot1.setRenderer(new StandardXYItemRenderer());

		plot1.setSecondaryDataset(0, hld);
		CandlestickRenderer c1=new CandlestickRenderer();



	//c1.setAutoWidthFactor(1.0);
	//c1.setAutoWidthGap(0.1);
	c1.setBasePaint(new Color(255,255,255));
	c1.setBaseOutlinePaint(new Color(255,255,255));

	c1.setPaint(new Color(255,255,255));

	c1.setUpPaint(new Color(255,0,0,80));
	c1.setDownPaint(new Color(0,255,0,80));



		plot1.setSecondaryRenderer(0, c1);

		//plot1.setSecondaryDataset(0, dataset2);

		XYDotRenderer xd1=new XYDotRenderer();
		//plot1.setSecondaryRenderer(0, new AreaXYRenderer(AreaXYRenderer.AREA_AND_SHAPES));
		//plot1.setSecondaryRenderer(0, xd1);


		//chart=new JFreeChart("", null, plot1, false);



		chart.setBackgroundPaint(new Color(0,0,0));

	return chart;

	}


	// data recipient interfaces
	/**
	 * implements DataRecipients data function
	  * @see jahangir.DataRecipient#data(jahangir.Tick)
	 */

	int i=0;
	long candletime=0;

	Candle current=new Candle();

	Vector ticks=new Vector();
	/*
	public void data(Tick t){

		try{
			//System.out.println(t.date.toString());

			if(candletime==0)candletime=ms;

			//statusLine.setText(""+t.symbol+" - "+t.value+" - "+t.amount+ " - "+t.change);

			try{
				x1.add(new FixedMillisecond(t.date), t.value);
			}
			catch(Exception e){
				System.out.println("Unresolved error");
				//e.printStackTrace();
			}

			//env1.add(new FixedMillisecond(t.date), t.value*1.01);
			//env2.add(new FixedMillisecond(t.date), t.value*0.99);


			if(timerangems!=-1){
				if(ms<candletime+timerangems){
					//check hi, lows, closes
					if(current.open==-1){
						current.open=t.value;
					}
					if(t.value>current.hi)current.hi=t.value;
					if(t.value<current.low)current.low=t.value;

					current.close=t.value;

				}
				else{
					//add current to candle vector
					current.date=new Date(ms+1);
					hld.addCandle(current);
					current=new Candle();
					candletime=0;
				}
			}
			//calcAverage1(0,t.date);


			i++;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	*/


	/**
	 * implements DataRecipient's getSymbol function
	 * @see jahangir.DataRecipient#getSymbol()
	 *
	 */
	public String getSymbol(){
		return symbol;
	}

	public void calcAverage1(int d, Date timestamp){
		try{
			double v=0;

			//List l1=x1.getItems();

			for(int i=d;i<d+av1;i++){
				v+=x1.getValue(x1.getItemCount()-(av1-i)).doubleValue();
			}
			average1.add(new FixedMillisecond(timestamp), v/av1);


		}
		catch(Exception e){
			//e.printStackTrace();
			//System.out.println(timestamp.toString());
		}
	}


	/**
	 * actually recalculates the first average (necessary when we change the average settings)
	 * @param d
	 */
	public void recalculateAverage1(){

		//average1=new TimeSeries("av1", FixedMillisecond.class);
		average1.delete(0, average1.getItemCount()-1);

		for(int z=0;z<x1.getItemCount();z++){
			try{
					double v=0;

					//List l1=x1.getItems();

					for(int i=z;i<z+av1;i++){
						v+=x1.getValue(x1.getItemCount()-(av1-i)).doubleValue();

					}
					average1.add(x1.getTimePeriod(z), v/av1);

				}
				catch(Exception e){
					//e.printStackTrace();
					//System.out.println(timestamp.toString());
				}
		}

	}

	public void draw(double[] v){
		TimeSeries data=new TimeSeries("symbol", FixedMillisecond.class);
		dataset1.addSeries(data);

		for(int i=0;i<v.length;i++){
			try{
				data.add(new FixedMillisecond(i), v[i]);
			}
			catch(Exception e){
				System.out.println("Unresolved error");
				//e.printStackTrace();
			}


		}
	}
	/*
	public void addMiniChart(Plugin p){
		components.add(p);

		rebuild();
	}

	public void removeMiniChart(Plugin p){
		components.remove(p);
		rebuild();

	}
	*/
	public void rebuild(){
		// remove everything from main panel
		mainpanel.removeAll();

		GridBagLayout gbl=new GridBagLayout();
		GridBagConstraints gbc=new GridBagConstraints();
		mainpanel.setLayout(gbl);

		gbc.anchor=gbc.NORTHWEST;
		gbc.fill=gbc.BOTH;
		gbc.weightx=1;
		gbc.gridx=0;

		int i=0;
	//add chart

		gbc.weighty=1;
		gbc.gridy=i;
		gbl.setConstraints(chartPanel, gbc);
		mainpanel.add(chartPanel);

		//add all other plugins/components
		validate();

	}






	/**
	 * converts data into a candlestick chart
	 *
	 */
	public void convert(){


		chart.setAntiAlias(false);
		chartPanel.setChart(chart);

	}


	/**
	 * wipes all data
	 *
	 */
	public void wipeAll(){

		//wipes all

		dataset1.removeAllSeries();
		x1=new TimeSeries("symbol", FixedMillisecond.class);
		dataset1.addSeries(x1);

		current=new Candle();

		candletime=-1;
		hld.wipe();
	}

	/**
	 * returns if this chart should retrieve realtime data
	 */
	boolean rr=false;
	public boolean retrieveRealtime(){
		return rr;
	}



	//JTextArea scripttext;
	/**
	 * opens a very simple script editor
	 *
	 */
	/*
	public void editScript(){
		String scr=script.getScript();
		scripttext=new JTextArea();
		scripttext.setText(scr);
		JFrame f=new JFrame("Script editor - press set in chartwindow to set.");
		f.setIconImage((new ImageIcon("script.jpg")).getImage());
		f.getContentPane().add(new JScrollPane(scripttext), BorderLayout.CENTER);
		f.setSize(300,400);
		f.show();

	}
	*/

	public static void main(String[] args){
		ChartWindow cw=new ChartWindow("test");

		double[] c1=new double[100];
		for(int i=0;i<c1.length;i++){
			c1[i]=Math.random();
		}

		cw.draw(c1);


		double[] c2=new double[100];
		for(int i=0;i<c2.length;i++){
			c2[i]=Math.random();
		}

		cw.draw(c2);


	}
}
