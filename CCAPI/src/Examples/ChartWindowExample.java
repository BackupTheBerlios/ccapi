/*
 * Created on 25.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */
package Examples;

import CCAPI.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class ChartWindowExample {

	ChartWindowExample(){
		ChartWindow cw = new ChartWindow("Example");
		double[] u = new double[100];
		for(int i=0;i<u.length;i++){
			u[i]=Math.random();
		}
		cw.draw(u);
		
		double[] v = new double[100];
		for(int i=0;i<v.length;i++){
			v[i]=Math.random();
		}
		
		cw.draw(v);
		
		double[] w = new double[100];
		for(int i=0;i<w.length;i++){
			w[i]=Math.random();
		}
		
		cw.draw(w);
		
		// but you need to add a window listener
		cw.addWindowListener(new WindowAdapter(){public void WindowClosed(WindowEvent e){
			System.exit(0);
		}});
		
	}
	
	public static void main(String[] args){
		// maining
		ChartWindowExample cwe = new ChartWindowExample();
	}
}
