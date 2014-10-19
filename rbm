package deepBeliefNetwork;

import java.util.Arrays;
import java.util.Random;

public class MyRBM {
	int num;
	int visNum;
	int hidNum;
	double[][] W;
	double[] visBias;
	double[] hidBias;
	Random r = new Random();
	
	double randomGaussian(double mu, double sigma){
		double temp = 0;
		double N = 12;
		for(int i=0; i<N; ++i){
			//temp += Math.random();
			temp += r.nextDouble();
		}
		temp = mu+sigma*(temp-N/2.0)/Math.sqrt(N/12);
		return temp;
	}
	
	double[] sample(double[] prob){
		double sample[] = new double[prob.length];
		for(int i=0; i<prob.length; ++i){
			if(r.nextDouble()<prob[i]){
				sample[i] = 1;
			}
		}
		return sample;
	}
	
	double[] sigmoidHgivenV(double[] V){
		double[] p = new double[hidNum];
		for(int j=0; j<hidNum; ++j){
			double temp = 0;	
			for(int i=0; i<visNum; ++i){
				temp += V[i]*W[i][j];
			}
			temp += hidBias[j];
			p[j] = 1/(1+Math.pow(Math.E, -temp));
		}
		return p;
	}
	
	double[] sigmoidVgivenH(double[] H){
		double[] p = new double[visNum];
		for(int i=0; i<visNum; ++i){
			double temp = 0;
			for(int j=0; j<hidNum; ++j){	
				temp += H[j]*W[i][j];
			}
			temp = temp+visBias[i];
			p[i] = 1/(1+Math.pow(Math.E, -temp));
		}
		return p;
	}
	
	public void getRBM(MyRBM rbm){
		this.num = rbm.num;
		this.visNum = rbm.visNum;
		this.hidNum = rbm.hidNum;
		
		this.W = new double[this.visNum][this.hidNum];
		for(int i=0; i<this.visNum; ++i){
			for(int j=0; j<this.hidNum; ++j){
				this.W[i][j] = rbm.W[i][j];
			}
		}
		this.visBias = new double[this.visNum];
		for(int i=0; i<this.visNum; ++i){
			this.visBias[i] = rbm.visBias[i];
		}
		this.hidBias = new double[this.hidNum];
		for(int j=0; j<this.hidNum; ++j){
			this.hidBias[j] = rbm.hidBias[j];
		}
	}
	
	void initialRBM(double[][] train, int hidNum){
		this.hidNum = hidNum;
		num = train.length;
		visNum = train[0].length;
		
		W = new double[visNum][hidNum];
		for(int i=0; i<visNum; ++i){
			for(int j=0; j<hidNum; ++j){
				W[i][j] = randomGaussian(0, 0.1);
			}
		}
		
		visBias = new double[visNum];
		/*for(int j=0; j<visNum; ++j){
			int count = 0;
			for(int i=0; i<num; ++i){
				if(train[i][j]==1){
					count++;
				}
			}
			temp = (double) count/num;
			temp = temp/(1-temp);
			if(temp>Math.E){
				visBias[j] = 1;
			} else if(temp<1/Math.E){
				visBias[j] = -1;
			} else{
				visBias[j] = Math.log(temp);
			}
		}*/
		
		hidBias = new double[hidNum];
	}
	
	void cD(double[] sV0, int k, double learningRate){
		double[] pH0 = sigmoidHgivenV(sV0);
		double[] pHn = new double[hidNum];
		double[] pVn = new double[visNum];
		double[] sHn = new double[hidNum];
		double[] sVn = new double[visNum];
		for(int i=0; i<visNum; ++i){
			sVn[i] = sV0[i];
		}
		for(int j=0; j<hidNum; ++j){
			pHn[j] = pH0[j];
		}
		for(int count=0; count<k; count++){
			sHn = sample(pHn);
			pVn = sigmoidVgivenH(sHn);
			sVn = sample(pVn);
			pHn = sigmoidHgivenV(sVn);
		}
		for(int i=0; i<visNum; ++i){
			for(int j=0; j<hidNum; ++j){
				W[i][j] += (pH0[j]*sV0[i]-pHn[j]*sVn[i])/num*learningRate;
			}
		}
		for(int i=0; i<visNum; ++i){
			visBias[i] += (double) (sV0[i]-sVn[i])/num*learningRate;
		}
		for(int j=0; j<hidNum; ++j){
			hidBias[j] += (pH0[j]-pHn[j])/num*learningRate;
		}
	}
	
	void reconstruct(double[][] test){
		int count = test.length;
		double[][] resembledTest = new double[count][visNum];
		if(test[0].length!=visNum){
			System.out.println("test data doesn't match");
		} else{
			for(int k=0; k<count; ++k){
				resembledTest[k] = sigmoidVgivenH(sigmoidHgivenV(test[k]));
			}
			//System.out.println(Arrays.deepToString(tt));
			System.out.println(Arrays.deepToString(resembledTest));
		}
	}
	
	public static void testrbm(){
		MyRBM rbm = new MyRBM();
		double[][] train = {
				{1, 1, 1, 0, 0, 0},
				{1, 0, 1, 0, 0, 0},
				{1, 1, 1, 0, 0, 0},
				{0, 0, 1, 1, 1, 0},
				{0, 0, 1, 0, 1, 0},
				{0, 0, 1, 1, 1, 0}
			};
		double[][] test = {
				{1, 1, 0, 0, 0, 0},
				{0, 0, 0, 1, 1, 0}
			};
		rbm.initialRBM(train, 6);
		for(int l=0; l<1000; ++l){
			for(int count=0; count<rbm.num; ++count){
				rbm.cD(train[count], 1, 0.1);
			}
		}
		rbm.reconstruct(test);
		//System.out.println(Arrays.deepToString(rbm.W));
		//System.out.println(Arrays.toString(rbm.visBias));
		//System.out.println(Arrays.toString(rbm.hidBias));
	
		
		//RBM.test_rbm();
		/*System.out.println(Arrays.deepToString(RBM.W));
		System.out.println(Arrays.toString(RBM.vbias));
		System.out.println(Arrays.toString(RBM.hbias));*/
	}
	
	/*public static void main(String Args[]){
		testrbm();
	}*/
}
