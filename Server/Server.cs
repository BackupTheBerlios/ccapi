/**
* 	(C) Ulrich Staudinger, 20004
**/


using System;
using System.Net.Sockets;
using System.IO;
using System.Threading;
using System.Collections;


public class ConsorsGate{
	
	Logger _log;
	public bool _quit;
	
	Thread _thread;
	
	public static QuoteSystem _qs;
	public SubscriptionManager _sm;

	public static void Main(){
		ConsorsGate cg=new ConsorsGate(new Logger("test.log"));
	}
	
	public SubscriptionManager getSubscriptionManager(){
		return _sm;
	}
	
	
	public ConsorsGate(Logger l){
		
		_log=l;
		_log.info("ConsorsGate::ConsorsGate", "constructing.");
		_quit=false;
		_thread=new Thread(new ThreadStart(Run));
		_thread.Start();

	
		//initializing the core elements
		
		_sm = new SubscriptionManager(_log, this);
		_qs = new QuoteSystem(_log, this);
	}
	
	private void Run(){
		_log.info("ConsorsGate::Run", "starting TCP listener on port 65000.");
		TcpListener tcpl=new TcpListener(65000);
		tcpl.Start();

		while(!_quit){
			Socket socket=tcpl.AcceptSocket();
			if(socket.Connected){
				_log.info("ConsorsGate::Run", "new socket connected.");
				ConsorsGateHandler cgh=new ConsorsGateHandler(_log, socket);
				_sm.add(cgh);	
			}
		}
		_log.info("ConsorsGate::Run", "Leaving now.");
	}
	
	public void foo(){}
	
	
	
}

