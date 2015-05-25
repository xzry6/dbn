package deepBeliefNetwork;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import libsvm.*;

public class SVM {
	svm_model model;
	
	public void train(double[][] train, double[] label, 
			List<MyRBM> rbmList, int epoche){
		int num = train.length;//number of training samples
		int length = rbmList.get(rbmList.size()-1).hidNum;//number of output units
		int DBNlayer = rbmList.size();//number of DBN layers

		epoche = 1;
		double[][] output = new double[num*epoche][length];
		
		
		
		double[] svmlabel = new double[num*epoche];
		
		for(int e=0; e<epoche; ++e){
			for(int n=0; n<num; ++n){
				double[] nextLayer;
				double[] temp = new double[train[n].length];
				System.arraycopy(train[n], 0, temp, 0, train[n].length);
				
				//form a binary classifier
				for(int l=0; l<DBNlayer; ++l){
					MyRBM rbm = new MyRBM();
					rbm.getRBM(rbmList.get(l));
					nextLayer = new double[rbm.hidNum];
					nextLayer = (rbm.sigmoidHgivenV(temp,1,0));
					temp = nextLayer;
				}
				MyRBM rbm = new MyRBM();
				System.arraycopy((temp), 0, output[e*num+n], 0, temp.length);
			}
		}
		
		try{
			FileWriter file = new FileWriter("droppedinput001.txt");
			BufferedWriter out = new BufferedWriter(file);
			for(int i=0; i<output.length; ++i){
				for(int j=0; j<output[i].length; ++j){
					out.write(Float.toString((float)output[i][j]));
					out.write((int)' ');
				}
				out.write((int)'\r');
				out.write((int)'\n');
			}
			
			out.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
		svm_node[][] data = new svm_node[num*epoche][length];
		for(int c=0; c<num*epoche; ++c){
			svm_node[] record = new svm_node[length];
			for(int l=0; l<length; ++l){
				svm_node temp = new svm_node();
				temp.value = (float) output[c][l];
				temp.index = l+1;
				record[l] = temp;
			}
			//record[length].index = -1;
			System.arraycopy(record, 0, data[c], 0, record.length);
				//data[n] = record;
		}
		for(int e=0; e<epoche; ++e){
			for(int n=0; n<num; ++n){
				if(label[n]==1){
					svmlabel[e*num+n] = label[n];
				} else{
					svmlabel[e*num+n] = -1;
				}
			}
		}
		/*try{
			FileWriter file = new FileWriter("svmlabel.txt");
			BufferedWriter out = new BufferedWriter(file);
			for(int i=0; i<label.length; ++i){
				out.write(Double.toString(label[i]));
				out.write((int)'\r');
				out.write((int)'\n');
			}
			
			out.close();
		} catch(Exception e){
			e.printStackTrace();
		}*/
		
		
		
		/*svm_problem problem = new svm_problem();
		problem.l = epoche*num;
		problem.x = data;
		problem.y = svmlabel;
		
		svm_parameter parameter = new svm_parameter();
		parameter.svm_type = svm_parameter.C_SVC;
		parameter.kernel_type = svm_parameter.RBF;
		parameter.cache_size = 10000;
		parameter.eps = 0.1;
		parameter.C = 1;
		
		System.out.println(svm.svm_check_parameter(problem, parameter));
		model = svm.svm_train(problem, parameter);
		*/
		
	}
		
	public double[] test(double[][] test, svm_model model, List<MyRBM> rbmList){
		int DBNlayer = rbmList.size();
		int num = test.length;
		int length = rbmList.get(rbmList.size()-1).hidNum;//number of output units
		double[][] output = new double[num][length];
		
		for(int n=0; n<test.length; ++n){
			double[] currentLayer = new double[test[0].length];
			double[] nextLayer;
			System.arraycopy(test[n], 0, currentLayer, 0, test[n].length);
			
			for(int l=0; l<DBNlayer; ++l){
				MyRBM rbm = new MyRBM();
				rbm.getRBM(rbmList.get(l));
				nextLayer = new double[rbm.hidNum];
				nextLayer = (rbm.sigmoidHgivenV(currentLayer,1,0));
				currentLayer = nextLayer;
			}
			System.arraycopy(currentLayer, 0, output[n], 0, currentLayer.length);
		}
		

		double[] result = new double[num];
		//svm_node[][] data = new svm_node[num][length];
		for(int n=0; n<num; ++n){
			svm_node[] record = new svm_node[length];
			for(int l=0; l<length; ++l){
				svm_node temp = new svm_node();
				temp.value = (float) output[n][l];
				temp.index = l+1;
				record[l] = temp;
			}
			//record[length].index = -1;
			result[n] = svm.svm_predict(model, record);
			System.out.println(result[n]);
			//System.arraycopy(record, 0, data[n], 0, record.length);
		}
		return result;
	}
		
		
		
}
