/**
* 	(c) Ulrich Staudinger, 2004
**/

using System;
using System.Threading;


using CortalConsors.TradingAPI;
using CortalConsors.TradingAPI.DE;
using CortalConsors.TradingAPI.Events;
using CortalConsors.TradingAPI.ValueObjects;
using CortalConsors.TradingAPI.ValueObjects.DE;


public class QuoteSystem{
	static Logger _log;
	static ConsorsGate _cg;

	static SessionFacade sf;

	// the quote system constructor	
	public QuoteSystem(Logger l, ConsorsGate cg){
		
		//setting the local variables
		_log = l;
		_cg = cg;

		_log.info("QuoteSystem::QuoteSystem", "constructing quote system");

		//initialize the consors system.
		// Create the session facade
		try{

			sf = new SessionFacade();

			// Login
			sf.LoginWithAddOnName("Ulrich Staudinger");

			_log.info("QuoteSystem::QuoteSystem", "logged in.");

			//registering for now, the plain dax values. 

			// Create the parameters that you need for the quote subscription, here the QuoteKey
			QuoteKey qk = sf.CreateQuoteKey();

			// Set all necessary parameters
			qk.SecurityCode = "846900";
			qk.StockexchangeId = "ETR";

			// Acquire the subscription with this QuoteKey
			QuoteSubscription qs = sf.GetQuoteSubscription(qk);

			// Register your event handler on the subscription
			CallbackQuote cbQuote = new CallbackQuote(quoteHandler);
			qs.OnQuoteUpdate += cbQuote;

			_log.info("QuoteSystem::QuoteSystem", "subscribed to dax.");

		}
		catch (Exception ex) 
		{
			_log.fatal("QuoteSystem::QuoteSystem", "Exception while starting QuoteSystem:");
			_log.fatal("QuoteSystem::QuoteSystem", ex.Message);
		}

		Thread constantRunner = new Thread(new ThreadStart(Run));
		constantRunner.Start();
	}
	
	public void Run(){

			while(!_cg._quit){
				System.Threading.Thread.Sleep(10000);
				Console.WriteLine("Still in thread.");
			}



	}

	// the quote handler, needs to call a dispatcher. 	
	private static void quoteHandler(QuoteDataItem qdi, QuoteKey qk, string ErrorMessage)
	{
		try
		{
			if ( ErrorMessage == null ){
				Console.WriteLine( qdi.LastPrice + " " + qdi.LastVolume );
				_cg.getSubscriptionManager().dispatch(" - ", " - ", qdi.LastPrice);
				//_cg.getSubscriptionManager().foo();
			}
			else
				Console.WriteLine( "OnQuoteUpdate() error message: " + ErrorMessage );
		}
		catch( Exception ex )
		{
			_log.fatal( "QuoteSystem::quoteHandler",  "OnQuoteUpdate() exception from " + ex.Source + ": " + ex.Message );
		}
	}


	// function to be used to subscribe to quotes. 
	void subscribeQuotes(string isin, string exchange){
	
	}
	
}
