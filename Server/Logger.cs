using System;
using System.IO;

public class Logger{
	
	StreamWriter writer;
	int index=0;
	
	public Logger(String logfile){
		writer=new StreamWriter(logfile, true);
	}

	public void debug(String origin, String text){
		print("DEBUG", origin, text);
	}
	
	public void info(String origin, String text){
		print("INFO", origin, text);
	}
	
	public void fatal(String origin, String text){
		print("FATAL", origin, text);
	}

	public void print(String level, String origin, String text){
		DateTime e = DateTime.Now;
		String line=e.ToString() + " ["+level+"] "+origin+" - "+text;
		Console.WriteLine(line);
		writer.WriteLine(line);		
		writer.Flush();
	}
}
