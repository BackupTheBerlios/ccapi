package CCAPI;

import java.io.*;

public class PersonalLogger{
	
	String filename;
	public PersonalLogger(String filename){
		this.filename = filename;
	}

	public void log(String text){
		try{
			File f=new File(filename);
			FileWriter bw = new FileWriter(f, true);

			bw.write(text);
			bw.write("\n");
			bw.close();
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		

		
	}

	
}
