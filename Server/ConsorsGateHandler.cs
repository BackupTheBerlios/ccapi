/** (c) Ulrich Staudinger **/

using System;
using System.Net.Sockets;
using System.IO;
using System.Threading;
using System.Collections;


public class ConsorsGateHandler{
	Socket _s;
	Logger _log;
	Thread _thread;
	string _clientid="";
	StreamReader _reader;
	StreamWriter _writer;
	
	public bool _loggedIn = false;
	
	public ConsorsGateHandler(Logger log, Socket s){
		this._s=s;
		this._log=log;
		_thread=new Thread(new ThreadStart(handle));
		_thread.Start();
	}
	
	public void handle(){
		_log.info("ConsorsGateHandler::handle", "handling connection.");
		//constructing the stream reader.
		try{
			NetworkStream netStream=new NetworkStream(_s);
			_reader=new StreamReader(netStream);
			_writer=new StreamWriter(netStream);
			_clientid=_reader.ReadLine();
			_log.info("ConsorsGateHandler::handle", "Client with clientid "+_clientid+" registered.");
			rawSend("ok");
			
			//mark the connection as logged in. 
			_loggedIn = true;
			
			string l = _reader.ReadLine();
			while ( l != "quit" ){
				l = _reader.ReadLine();		
			}
			_loggedIn = false;
		}
		catch{
			_loggedIn = false;
			_log.info("ConsorsGateHandler::handle", "caugh exception!");	
		}
		_s.Close();
	}
	
	public void rawSend(string text){
		try{
			_writer.WriteLine(text);
			_writer.Flush();		
		}
		catch{
			_log.info("ConsorsGateHandler::rawSend", "exception caught, closing connection");
		}
	}
}
