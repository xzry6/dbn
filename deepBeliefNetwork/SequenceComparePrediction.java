package deepBeliefNetwork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public class SequenceComparePrediction {
	public static void compare(String oriSeq, String mutSeq) {
		//String oriSeq = args[0];
		//String mutSeq = args[1];
		int p = 0;
		//COMPARE SEQUENCES
		List<Integer> pos = new ArrayList<Integer>();
		List<Character> original = new ArrayList<Character>();
		List<Character> mutated = new ArrayList<Character>();
		while(p<oriSeq.length()&&p<mutSeq.length()) {
			char c1 = oriSeq.charAt(p);
			char c2 = mutSeq.charAt(p);
			if(c1!=c2) {
				pos.add(p+1);
				original.add(c1);
				mutated.add(c2);
			}
			p++;
		}
		//ENCODE MUTATIONs
				String order = "ACDEFGHIKLMNPQRSTVWY";
				double[][] encode = new double[pos.size()][140];
				for(int i=0; i<pos.size(); ++i) {
					if(original.get(i)!=' ') encode[i][order.indexOf(original.get(i))] = -1;
					if(mutated.get(i)!=' ') encode[i][order.indexOf(mutated.get(i))] = 1;
					for(int l=-3; l<4; ++l){
						if(pos.get(i)+l-1<oriSeq.length()&&pos.get(i)+l-1>-1) {
							char temp = oriSeq.charAt(pos.get(i)+l-1);
							int index = order.indexOf(temp);
							if(l<0){
								encode[i][20*(l+4)+index] = 1;
							} else if(l>0){
								encode[i][20*(l+3)+index] = 1;
							}
						}
					}
				}
				
		//RELOAD RBM FILES
				File file = new File("modelW100,60,20.txt");
		        Reader reader = null;
		        List<int[]> layer = new ArrayList<int[]>();
		        try {
		            reader = new InputStreamReader(new FileInputStream(file));
		            int tempchar;
		            int x = 0;
		            int y = 0;
		            int[] temp = new int[2];
		            while((tempchar = reader.read())!=-1) {
		            	if(tempchar==(int)'/') {
		            		temp[1] = y;
		            		layer.add(temp);
		            		temp = new int[2];
		            		y = -1;
		            	}
		            	if(tempchar==(int)'\n') {
		            		temp[0] = x;
		            		y++;
		            		x = 0;
		            	}
		            	if(tempchar==(int)' ') x++;
		            }
		            reader.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        
		        List<double[][]> Ws = new ArrayList<double[][]>();
		        Reader reader1 = null;
		        try {
		            reader1 = new InputStreamReader(new FileInputStream(file));
		            int tempchar;
		            String s = new String();
		            int l = 0;
		            int x = 0;
		            int y = 0;
		            double[][] W = new double[layer.get(l)[0]][layer.get(l)[1]];
		            while((tempchar = reader1.read())!=-1) {
		            	if(tempchar=='/') {
		            		Ws.add(W);
		            		l++;
		            		if(l<layer.size()) W = new double[layer.get(l)[0]][layer.get(l)[1]];
		            		y = -1;
		            	}
		            	if(tempchar=='\n') {
		                	y++;
		            		x = 0;
		            		s = new String();
		            	}
		            	if(tempchar==' ') {
		                	W[x++][y] = Double.parseDouble(s);
		            		s = new String();
		            	}
		            	s+=(char)tempchar;
		            	
		            }
		            reader1.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        
		        
		        List<double[]> Bs = new ArrayList<double[]>();
		        file = new File("modelB100,60,20.txt");
		        Reader reader2 = null;
		        try {
		            reader2 = new InputStreamReader(new FileInputStream(file));
		            int tempchar;
		            String s = new String();
		            int l = 0;
		            int x = 0;
		            double[] B = new double[layer.get(l)[1]];
		            while((tempchar = reader2.read())!=-1) {
		            	if(tempchar=='\n') {
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
		            	s+=(char)tempchar;
		            }
		            reader2.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        
		        //PREDICTION
		        double[] result = new double[encode.length];
		        for(int n=0; n<encode.length; ++n) {
		        	double[] temp = encode[n];
			        for(int l=0; l<Ws.size(); ++l) {
						double[][] W = Ws.get(l);
						double[] B = Bs.get(l);
						double[] sigLayer = new double[W[0].length];
						for(int j=0; j<sigLayer.length; ++j) {
							for(int i=0; i<temp.length; ++i) {
								sigLayer[j]+=W[i][j]*temp[i];
							}
							sigLayer[j] = 1/(1+Math.exp(-sigLayer[j]-B[j]));
							if(l!=Ws.size()-1) sigLayer[j]*=0.6;
						}
						temp = sigLayer;
					}
			        result[n] = temp[0];
		        }
		        
		      //WRITE FILE
		        try{
					FileWriter file1 = new FileWriter("temp1.txt");
					BufferedWriter out = new BufferedWriter(file1);
					for(int i=0; i<result.length; ++i) {
						out.write(pos.get(i)+" ");
						out.write(original.get(i)+" ");
						out.write(mutated.get(i)+" ");
						out.write(Double.toString(result[i]));
						if(i!=result.length-1) {
							out.write((int)'\r');
							out.write((int)'\n');
						}
					}
					out.close();
				} catch(Exception e){
					e.printStackTrace();
				}
		        
	}

	public static void main(String[] args) {
		String ori = "MVPCTLLLLLAAALAPTQTRAGPHSLRYFVTAVSRPGLGEPRYMEVGYVDDTEFVRFDSDAENPRYEPRARWMEQEGPEYWERETQKAKGNEQSFRVDLRTLLGYYNQSKGGSHTIQVISGCEVGSDGRLLRGYQQYAYDGCDYIALNEDLKTWTAADMAALITKHKWEQAGEAERLRAYLEGTCVEWLRRYLKNGNATLLRTDSPKAHVTHHSRPEDKVTLRCWALGFYPADITLTWQLNGEELIQDMELVETRPAGDGTFQKWASVVVPLGKEQYYTCHVYHQGLPEPLTLRWEPPPSTVSNMATVAVLVVLGAAIVTGAVVAFVMKMRRRNTGGKGGDYALAPGSQTSDLSLPDCKVMVHDPHSLA";
		String mut = "MAPCTLLLLLAAALAPTQTRAGPHSLRYFVTAVSRPGFGEPRYMEVGYVDNTEFVRFDSDAENPRYEPRARWMEQEGPEYWERETRKAKGNEQSFRVDLRTLLGYYNQSKGGSHTIQVISGCEVGSDGRLLRGYQQYAYDGCDYIALNEDLKTWTAADMAALITRRKWEQAGEAERLRAYLEGACVEWLRRYLKNGNATLLRTDSPKAHVTHHSRPEDKVTLRCWALGFYPADITLTWQLNGEDLTQDMELVETRPAGDGTFQKWAAVVVPLGKEQYYTCHVYHQGLPEPLTLRWKLPPSTVSNTVIVAVLVVLGAAIVTGAVVAFVMKMRR STGGKGVNYALAPGSQTSDLSLPDCKVMVHDPHSLA";
		compare(ori,mut);
	}
}
