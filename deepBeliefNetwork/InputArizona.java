package deepBeliefNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputArizona {
	public static String[][] original;
	public static String[][] mutated;
	public static List<String[]> list;
	public static double[][] encode;
	public static List<double[][]> Ws;
	public static List<double[]> Bs;
	public static String[] label;
	public static void read() {
		File file = new File("Proteins_With_ASE_to_Compare.txt");
		BufferedReader reader = null;
		original = new String[11][2];
		mutated = new String[11][2];
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int flag = 1;
			int line = -1;
			while((tempString = reader.readLine())!=null){
				if(tempString.charAt(0)=='>') {
					int end = tempString.indexOf('#');
					if(flag==1) {
						line++;
						original[line][0] = tempString.substring(1,end-3);
						original[line][1] = "";
						mutated[line][1] = "";
					}
					if(flag==-1) {
						mutated[line][0] = tempString.substring(1, end-3);
					}
					flag = -flag;
					continue;
				}
				if(flag==1) mutated[line][1]+=tempString;
				if(flag==-1) original[line][1]+=tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*for(int i=0; i<original.length; ++i) {
			System.out.println(original[i][0]);
			System.out.println(original[i][1]);
			System.out.println(mutated[i][1]);
		}*/
	}
	public static void compare() {
		list = new ArrayList<String[]>();
		for(int i=0; i<original.length; ++i) {
			int p1 = 0;
			int p2 = 0;
			while(p1<original[i][1].length()&&p2<mutated[i][1].length()) {
				char c1 = original[i][1].charAt(p1);
				char c2 = mutated[i][1].charAt(p2);
				if(c1!=c2) {
					String[] s = new String[6];
					s[0] = original[i][0];
					s[1] = mutated[i][0];
					s[2] = Integer.toString(p1+1);
					s[3] = Character.toString(c1);
					s[4] = Character.toString(c2);
					s[5] = original[i][1];
					list.add(s);
				}
				p1++;
				p2++;
			}
		}
	}
	public static void encode() {
		int size = list.size();
		String order = "ACDEFGHIKLMNPQRSTVWY";
		encode = new double[size][140];
		for(int i=0; i<size; ++i) {
			int position = Integer.parseInt(list.get(i)[2]);
			char ori = list.get(i)[3].charAt(0);
			char mut = list.get(i)[4].charAt(0);
			String sequence = list.get(i)[5];
			if(ori!=' ') encode[i][order.indexOf(ori)] = -1;
			if(mut!=' ') encode[i][order.indexOf(mut)] = 1;
			for(int l=-3; l<4; ++l){
				if(position+l-1<sequence.length()&&position+l-1>-1) {
					char temp = sequence.charAt(position+l-1);
					int index = order.indexOf(temp);
					if(l<0){
						encode[i][20*(l+4)+index] = 1;
					} else if(l>0){
						encode[i][20*(l+3)+index] = 1;
					}
				}
			}
		}
	}
	public static void reload() {
		File file = new File("modelW100,60,20.txt");
        Reader reader1 = null;
        List<int[]> layer = new ArrayList<int[]>();
        try {
            reader1 = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            int x = 0;
            int y = 0;
            int[] temp = new int[2];
            while((tempchar = reader1.read())!=-1) {
            	if(tempchar==(int)'/') {
            		temp[1] = y;
            		layer.add(temp);
            		temp = new int[2];
            		y = -1;
            	}
            	if(tempchar==(int)'\r') {
            		temp[0] = x;
            		y++;
            		x = 0;
            	}
            	if(tempchar==(int)' ') x++;
            }
            reader1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Ws = new ArrayList<double[][]>();
        Reader reader2 = null;
        try {
            reader2 = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            String s = new String();
            int l = 0;
            int x = 0;
            int y = 0;
            double[][] W = new double[layer.get(l)[0]][layer.get(l)[1]];
            while((tempchar = reader2.read())!=-1) {
            	if(tempchar=='/') {
            		Ws.add(W);
            		l++;
            		if(l<layer.size()) W = new double[layer.get(l)[0]][layer.get(l)[1]];
            		y = -1;
            	}
            	if(tempchar=='\r') {
                	y++;
            		x = 0;
            		s = new String();
            	}
            	if(tempchar==' ') {
                	W[x++][y] = Double.parseDouble(s);
            		s = new String();
            	}
            	if(tempchar=='\n') continue;
            	s+=(char)tempchar;
            	
            }
            reader2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        Bs = new ArrayList<double[]>();
        file = new File("modelB100,60,20.txt");
        Reader reader3 = null;
        try {
            reader3 = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            String s = new String();
            int l = 0;
            int x = 0;
            double[] B = new double[layer.get(l)[1]];
            while((tempchar = reader3.read())!=-1) {
            	if(tempchar=='\r') {
            		Bs.add(B);
                	l++;
                	if(l<layer.size()) B = new double[layer.get(l)[1]];
            		x = 0;
            		s = new String();
            	}
            	if(tempchar==' ') {
                	B[x++] = Double.parseDouble(s);
            		s = new String();
            	}
            	if(tempchar=='\n') continue;
            	s+=(char)tempchar;
            }
            reader3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* for(int i=0; i<layer.size(); ++i) {
        	System.out.print(layer.get(i)[0]+" ");
        	System.out.println(layer.get(i)[1]);
        }
        for(int i=0; i<Ws.size(); ++i){
        	double[][] W = Ws.get(i);
        	for(int y=0; y<W[0].length; ++y) {
        		for(int x=0; x<W.length; ++x) {
        			System.out.print(W[x][y]+" ");
        		}
        		System.out.println();
        	}
        	System.out.println("/");
        }
        for(int i=0; i<Bs.size(); ++i) {
        	double[] B = Bs.get(i);
        	for(int x=0; x<B.length; ++x) {
        		System.out.print(B[x]+" ");
        	}
        	System.out.println();
        }*/
	}
	public static void test() {
		label = new String[encode.length];
		for(int instance=0; instance<encode.length; ++instance) {
			double[] temp = encode[instance];
			for(int layer=0; layer<Ws.size(); ++layer) {
				double[][] W = Ws.get(layer);
				double[] B = Bs.get(layer);
				double[] sigLayer = new double[W[0].length];
				for(int j=0; j<sigLayer.length; ++j) {
					for(int i=0; i<temp.length; ++i) {
						sigLayer[j]+=W[i][j]*temp[i];
					}
					sigLayer[j] = 1/(1+Math.exp(-sigLayer[j]-B[j]));
					if(layer!=Ws.size()-1) sigLayer[j]*=0.6;
				}
				temp = sigLayer;
			}
			System.out.println(Arrays.toString(temp));
			//if(temp[0]>=0.5) label[instance] = "+";
			//else label[instance] = "-";
			label[instance] = Double.toString(temp[0]);
		}
	}
	public static void write() {
		try{
			FileWriter file = new FileWriter("Proteins_With_ASE_to_Compare_DBNnew.txt");
			BufferedWriter out = new BufferedWriter(file);
			for(int i=0; i<list.size(); ++i){
				String[] temp = list.get(i);
				out.write(temp[0]);
				out.write((int)'\t');
				out.write(temp[1]);
				out.write((int)'\t');
				out.write(temp[3]+temp[2]+temp[4]);
				out.write((int)'\t');
				out.write(label[i]);
				out.write((int)'\r');
				out.write((int)'\n');
			}
			
			out.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void w() {
		for(int i=0; i<list.size(); ++i) {
			String[] s = list.get(i);
			String temp = "instance";
			if(i<10) temp+="0";
			temp += Integer.toString(i);
			try{
				FileWriter file = new FileWriter(temp);
				BufferedWriter out = new BufferedWriter(file);
				out.write((int)'\r');
				out.write((int)'\n');
				out.write(s[5]);
				out.write((int)'\r');
				out.write((int)'\n');
				out.write(s[2]);
				out.write((int)'\r');
				out.write((int)'\n');
				out.write(s[3]);
				out.write((int)'\r');
				out.write((int)'\n');
				out.write(s[4]);
				out.write((int)'\r');
				out.write((int)'\n');
				out.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		read();
		compare();
		encode();
		for(int i=0; i<encode.length; ++i) {
			System.out.println(Arrays.toString(encode[i]));
		}
		//w();
		reload();
		test();
		write();
	}
}
