/*
 * Created on 28.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */
package CCAPI.GeneticSystems;

import java.util.*;

/**
 * need to find a way to put this into words at first. 
 * 
 * survival of the fittest is the word
 * first generation does a backtest on the whole history, best program is taken and mutated. 
 * 
 * 
 */

public class GeneticTrader {

	/**
	 * contains the candles
	 */
	Vector candles;
	
	/**
	 * number of programs stored in memory. 
	 */
	int programs = 100;
	
	/**
	 * actively sets the candles data set
	 * @param candles
	 */
	public void setDataSet(Vector candles){
		this.candles = candles;
	}
	
	/**
	 * our plain constructor
	 *
	 */
	public GeneticTrader(){
		// initialize the core
	}
	/**
	 * actually evolve something 
	 *
	 */
	public void evolve(){
		
	}
	
	public void test(){
		
	}
	
	/**
	 * the obligatory main 
	 * @param args
	 */
	public static void main(String[] args) {
		 GeneticTrader gt = new GeneticTrader();
	}
}
