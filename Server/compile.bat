SET PATH=$PATH;c:\Programme\Mono-1.0.2;c:\Programme\Mono-1.0.2\bin;
SET MONO_PATH=C:\DOTNET\Server
mcs -lib:c:\DOTNET\Server -r:MDG.dll -r:CortalConsors.TradingApi.dll -r:ICSharpCode.SharpZipLib.dll -r:log4net.dll Server.cs Logger.cs QuoteSystem.cs SubscriptionManager.cs ConsorsGateHandler.cs
