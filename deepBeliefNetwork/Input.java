package deepBeliefNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Input {
	public double[][] data;
	public double[] labelC;
	double[] labelR;
	public Input(String dataName, String labelName, int length){
		File file = new File(dataName);
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            int count = 0;
        	String temp = new String();
            data = new double[length][140];
            	while ((tempchar = reader.read()) != -1) {
            	if(tempchar!=(int)' '&&tempchar!=(int)'\r'&&tempchar!=(int)'\n'){
            		temp += (char) tempchar;
            	} else if(tempchar==(int)' '){
            		data[count/140][count%140] = Double.parseDouble(temp);
            		temp = new String();
            		count++;
            		}
            	} 
            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        
        File lfile = new File(labelName);
        Reader lreader = null;
        try {
            lreader = new InputStreamReader(new FileInputStream(lfile));
            int tempchar;
            int count = 0;
        	String temp = new String();
            labelC = new double[length];
            labelR = new double[length];
            	while ((tempchar = lreader.read()) != -1) {
            	if(tempchar!=(int)'\r'&&tempchar!=(int)'\n'){
            		temp += (char) tempchar;
            	} else if(tempchar==(int)'\r'){
            		labelR[count] = Double.parseDouble(temp);
            		temp = new String();
            		if(labelR[count]>=0){
            			labelC[count] = 1;
            		}
            		count++;
            		}
            	}
            	
            lreader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
}
