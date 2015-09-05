package deepBeliefNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FineTune {
	double[][] topW;
	double[] topB;
	double[][] class1;
	double[][] class2;
	double[][] label1;
	double[][] label2;
	
	static void softmax(double[] error) {
		int num = error.length;
		double temp = 0;
		double sum = 0;
		for(int i=0; i<num; ++i) {
			if(temp < error[i]) {
				temp = error[i];
			}
		}
		for(int i=0; i<num; ++i) {
			error[i] = Math.exp(error[i] - temp);
			sum += error[i];
		}
		for(int i=0; i<num; ++i) {
			error[i] = error[i]/sum;
		}
	}
	
	static double[] calculateError(double[][] W, 
			double[] bias, double[] currentError, double[] sigLayer){
		double[] nextError = new double[W.length];
		for(int i=0; i<W.length; ++i){
			for(int j=0; j<W[0].length; ++j){
				nextError[i] += W[i][j]*currentError[j];
			}
			if(bias!=null){
				nextError[i] += bias[i];
			}
			nextError[i] *= sigLayer[i]*(1-sigLayer[i])*4*2;
		}
		return nextError;
	}
	
	
	
	static void updateTheta(double[][] W, double[] bias, 
			double[] currentError, double[] sigLayer, double learningRate, int N){
		for(int j=0; j<bias.length; ++j){
			for(int i=0; i<W.length; ++i){
				W[i][j] += learningRate*currentError[j]*sigLayer[i]/N;
			}
			bias[j] += learningRate*currentError[j]/N;
		}
	}
	
	void tellFromClass(double[][] train, double[][] label){
		List<double[]> Class1 = new ArrayList<double[]>();
		List<double[]> Class2 = new ArrayList<double[]>();
		List<double[]> Label1 = new ArrayList<double[]>();
		List<double[]> Label2 = new ArrayList<double[]>();
		for(int n=0; n<train.length; ++n){
			if(label[n][0]>0){
				Class2.add(train[n]);
				Label2.add(label[n]);
			} else{
				Class1.add(train[n]);
				Label1.add(label[n]);
			}
		}
		class1 = new double[Class1.size()][train[0].length];
		class2 = new double[Class2.size()][train[0].length];
		label1 = new double[Class1.size()][label[0].length];
		label2 = new double[Class2.size()][label[0].length];
		for(int n=0; n<class1.length; ++n){
			System.arraycopy(Class1.get(n), 0, class1[n], 0, train[0].length);
			System.arraycopy(Label1.get(n), 0, label1[n], 0, label[0].length);
		}

		for(int n=0; n<class2.length; ++n){
			System.arraycopy(Class2.get(n), 0, class2[n], 0, train[0].length);
			System.arraycopy(Label2.get(n), 0, label2[n], 0, label[0].length);
		}
		System.out.println("positive class has "+ class2.length+ " instances");
		System.out.println("negative class has "+ class1.length+ " instances");
	}
	
	public void fineTune(double[][] train, double[] prelabel, 
			List<MyRBM> rbmList, int epoche, double learningRate){
		int num = train.length;//number of training samples
		int length = rbmList.get(rbmList.size()-1).hidNum;//number of output units
		int DBNlayer = rbmList.size();//number of DBN layers
		
		double[][] label = new double[num][1];
		for(int n=0; n<num; ++n){
			label[n][0] = prelabel[n];
		}
		topW = new double[length][label[0].length];
		topB = new double[label[0].length];
		tellFromClass(train, label);
		
		for(int e=0; e<epoche; ++e){
			for(int n=0; n<num; ++n){

				List<double[]> sigList = new ArrayList<double[]>();
				double[] sigLayer = new double[train[n].length];
				double[] tempLayer  = new double[train[n].length];
				System.arraycopy(train[n], 0, tempLayer, 0, train[n].length);
				sigList.add(tempLayer);
				
				double[] topError = new double[label[0].length];
				double[] error = new double[length];
				double[] nextError;
				
				//form a binary classifier
				for(int l=0; l<DBNlayer; ++l){
					MyRBM rbm = new MyRBM();
					rbm.getRBM(rbmList.get(l));
					sigLayer = rbm.sigmoidHgivenV(tempLayer,1,0);
					
					double[] dropRate = rbm.simpleRate(0.6);
					//rbm.dropOut(sigLayer, rbm.sigmoidHgivenV(tempLayer,2,3), true);
					rbm.dropOut(sigLayer, dropRate, true);
					
					sigList.add(sigLayer);
					tempLayer = sigLayer;
				}
				
				//calculate supervised-learning-layer error
				for(int j=0; j<label[0].length; ++j){
					double temp = 0;
					for(int i=0; i<length; ++i){
						temp += tempLayer[i]*topW[i][j];
					}
					topError[j] = temp+topB[j];
				}
				if(label[0].length!=1){
					softmax(topError);
					for(int i=0; i<label[n].length; ++i){
						topError[i] = label[n][i]-topError[i];
					}
				} else{
					topError[0] = 1.0/(1.0+Math.exp(-topError[0]));
					topError[0] = (label[n][0]-topError[0]);//*topError[0]*(1-topError[0])*4*2;
				}
				
				
				//update top-layer theta then calculate previous layer error
				updateTheta(topW, topB, topError, sigLayer, learningRate, num);
				if(epoche>=10){
					error = calculateError(topW, null, topError, sigLayer);
						
					for(int l=DBNlayer-1; l>-1; --l){
						MyRBM rbm = new MyRBM();
						rbm = rbmList.get(l);
						nextError = new double[rbm.visNum];
						nextError = calculateError(rbm.W, null, error, sigList.get(l));
						updateTheta(rbm.W, rbm.hidBias, error, sigList.get(l), learningRate, num);
						error = nextError;
					}
				}
			}
		}
		
	}
	
	double[] test(double[][] train, double[] prelabel, List<MyRBM> rbmList){
		
		int DBNlayer = rbmList.size();
		double[][] label = new double[prelabel.length][1];
		for(int n=0; n<prelabel.length; ++n){
			label[n][0] = prelabel[n];
		}
		
		double[][] output = new double[label.length][label[0].length];
		
		for(int n=0; n<train.length; ++n){
			double[] currentLayer = new double[train[0].length];
			double[] nextLayer;
			for(int i=0; i<train[0].length; ++i){
				currentLayer[i] = train[n][i];
			}
			
			for(int l=0; l<DBNlayer; ++l){
				MyRBM rbm = new MyRBM();
				rbm.getRBM(rbmList.get(l));
				nextLayer = rbm.sigmoidHgivenV(currentLayer,1,0);
				double[] dropRate = rbm.simpleRate(0.6);
				rbm.dropOut(nextLayer, dropRate, false);
				//rbm.dropOut(nextLayer, rbm.sigmoidHgivenV(currentLayer,2,3), false);
				currentLayer = nextLayer;
			}
			
			for(int j=0; j<label[0].length; ++j){
				for(int i=0; i<rbmList.get(rbmList.size()-1).hidNum; ++i){
					output[n][j] += currentLayer[i]*topW[i][j];
				}
				output[n][j] = output[n][j]+topB[j];
			}
			if(label[0].length!=1){
				softmax(output[n]);
			} else{
				output[n][0] = 1.0/(1.0+Math.exp(-output[n][0]));
			}
		}
		System.out.println(Arrays.deepToString(label));
		System.out.println(Arrays.deepToString(output));
		
		double[] mylabel = new double[prelabel.length];
		for(int n=0; n<prelabel.length; ++n){
			if(output[n][0]>=0.5){
				mylabel[n] = 1;
			}
		}
		return mylabel;
	}
}
