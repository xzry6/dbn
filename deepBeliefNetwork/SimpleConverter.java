package deepBeliefNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleConverter {
	public static void main(String[] args) {
		try {
			File file = new File("labelR1615.txt");
			FileWriter writer = new FileWriter("labelC1615.txt");
			BufferedWriter out = new BufferedWriter(writer);
			BufferedReader reader = new BufferedReader(new FileReader(file));
		    String tempString = null;
		    while ((tempString = reader.readLine()) != null) {
		    	double num = Double.parseDouble(tempString);
		    	if(num>0) {
		    		out.write("1.0\r\n");
		    	} else out.write("0.0\r\n");
		    }
		    out.close();
		    reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
