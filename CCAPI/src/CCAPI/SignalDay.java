package CCAPI;

/**
* 	a helper class file containing all the signals for one day. 
*/

public class SignalDay{
	
	String date="";
	java.util.HashMap signals=new java.util.HashMap();

	public SignalDay(){
	}	

	public void addSignal(String signal, String value){
		signals.put(signal, value);
	}
	
	public java.util.HashMap getSignals(){
		return signals;
	}	

	public String getDate(){return date;}

}
