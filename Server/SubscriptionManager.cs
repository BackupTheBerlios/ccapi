/**
*	(c) 2004, Ulrich Staudinger
*/

using System;
using System.Collections;

public class SubscriptionManager{

	Logger _log;
	ConsorsGate _cg;
	static ArrayList _connections;
	

	//the plain constructor	
	public SubscriptionManager(Logger log, ConsorsGate cg){
		_log = log;
		_cg = cg;
		_connections = new ArrayList();
		
		_log.info("SubscriptionManager::SubscriptionManager", "constructing.");
	}

	public void add(ConsorsGateHandler cgh){
		_connections.Add(cgh);
	}

	//adding a subscription.
	public void addSubscription(string isin, string exchange, string cgatehandler){
		_log.info("SubscriptionManager::addSubscription", "adding a subscription.");
	}

	public void dispatch(string isin, string exchange, double value){
		try{
			for(int i=0;i<_connections.Count;i++){
				try{
					ConsorsGateHandler cgh = (ConsorsGateHandler)_connections[i];
					if(cgh._loggedIn)cgh.rawSend(isin+" @ "+exchange+ " : "+value);
				}
				catch(Exception e){
					_log.fatal("SubscriptionManager::dispatch", "inner for: "+e.Message);
				}	
			}
		}
		catch(Exception e){
			_log.fatal("SubscriptionManager::dispatch", e.Message);
		}
	}
	
	public void foo(){}
}