<%@ WebService Language="c#" Codebehind="ServerGate.asmx.cs" Class="ServerGate" %>
<%@ Assembly Name="Logger" %>
<%@ Assembly Name="Server" %>

using System;
using System.Collections;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Web;

[WebServiceAttribute (Description="Gate to the ccapi.net server. Provides various functions for access to the soap service.")]
public class ServerGate : System.Web.Services.WebService, IHttpHandler
{

    
    static Logger _log;
    static ConsorsGate _gate;
    static ServerGate ()
    {
        _log = new Logger("test.log");
        _log.info("ServerGate::ServerGate()", "constructing web service...");
	_gate = new ConsorsGate(_log);
	_log.info("ServerGate::ServerGate()", "ConsorsGate constructed.");
    }

    //http handler?	
    public void ProcessRequest(System.Web.HttpContext context){
	HttpResponse objRes=context.Response;
	HttpSessionSate objSes= context.Session;
	objRes.Write("<html><body>test</body></html>");
    }
    public bool IsReusable(){
	return true;
    }
    
    [WebMethod (Description="Sample function")]
//	[SoapHeaderAttribute("string", Direction = SoapHeaderDirection.Out)]
    public void Login (string a)
    {
        _log.info("ServerGate::Login", "Logging in.");
    	//userInfo = new UserInfo ();
    	//userInfo.userId = ++userCount;
    }
		
    [WebMethod (Description="Function to actually get a quote from the system.")]
    public void getQuote(string isin, string exchange){
        _log.info("ServerGate::getQuote", "retrieving quote: "+isin+"@"+exchange);	
    }
    
    [WebMethod (Description="use this to place a buy order without a limit")]
    public void buy(string isin, string exchange, int amount){
	
    }
    
    [WebMethod (Description="use this to place a sell order without a limit")]
    public void sell(string isin, string exchange, int amount){
    
    }
    

    [WebMethod (Description="use this to place a buy order with a limit")]
    public void limitBuy(string isin, string exchange, int amout, double price){
    
    }

    [WebMethod (Description="place a sell order with a limit")]
    public void limitSell(string isin, string exchange, int amount, double limit){
    
    }
    
    
    [WebMethod (Description="use this to place a stopbuy order with a limit")]
    public void stopBuy(string isin, string exchange, int amout, double price){
    
    }

    [WebMethod (Description="place a stop loss order")]
    public void stopLoss(string isin, string exchange, int amount, double price){
    
    }    
    
}

