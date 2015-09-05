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
	Random r = new Random(325325);
	
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
	
	double[] sigmoidHgivenV(double[] V, double alpha, double beta){
		double[] p = new double[hidNum];
		for(int j=0; j<hidNum; ++j){
			double temp = 0;	
			for(int i=0; i<visNum; ++i){
				//temp += V[i]*W[i][j];
				temp+=V[i]*(W[i][j]*alpha+beta);
			}
			temp = temp+hidBias[j];
			//temp = alpha*temp+beta+hidBias[j];
			p[j] = 1/(1+Math.exp(-temp));
		}
		return p;
	}
	
	void dropOut(double[] prob, double[] dropRate, boolean flag){
		if(flag){
			double[] sample = sample(dropRate);
			for(int i=0; i<prob.length; ++i){
				if(sample[i]==0) prob[i] = 0;
			}
		} else{
			for(int i=0; i<prob.length; ++i){
				prob[i] *= dropRate[i];
			}
		}
	}
	
	double[] simpleRate(double rate){
		double[] dropRate = new double[hidNum];
		for(int i=0; i<dropRate.length; ++i){
			dropRate[i] = rate;
		}
		return dropRate;
	}
	
	double[] sigmoidVgivenH(double[] H){
		double[] p = new double[visNum];
		for(int i=0; i<visNum; ++i){
			double temp = 0;
			for(int j=0; j<hidNum; ++j){	
				temp += H[j]*W[i][j];
			}
			temp = temp+visBias[i];
			p[i] = 1/(1+Math.exp(-temp));
		}
		return p;
	}
	
	public void getRBM(MyRBM rbm){
		this.num = rbm.num;
		this.visNum = rbm.visNum;
		this.hidNum = rbm.hidNum;
		this.r = rbm.r;
		
		this.W = new double[this.visNum][this.hidNum];
		for(int i=0; i<this.visNum; ++i){
			System.arraycopy(rbm.W[i], 0, this.W[i], 0, hidNum);
		}
		
		this.visBias = new double[this.visNum];
		System.arraycopy(rbm.visBias, 0, this.visBias, 0, visNum);
		
		this.hidBias = new double[this.hidNum];
		System.arraycopy(rbm.hidBias, 0, this.hidBias, 0, hidNum);
		
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
		hidBias = new double[hidNum];
	}
	
	void cD(double[] sV0, int k, double learningRate){
		double[] pH0 = sigmoidHgivenV(sV0,1,0);
		double[] dropRate = simpleRate(0.6);
	    dropOut(pH0, dropRate, true);
		//dropOut(pH0, sigmoidHgivenV(sV0,2,3), true);
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
			pHn = sigmoidHgivenV(sVn,1,0);
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
				resembledTest[k] = sigmoidVgivenH(sigmoidHgivenV(test[k],1,0));
			}
			System.out.println(Arrays.deepToString(resembledTest));
		}
	}
	
	protected static void testrbm(){
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
	
		
		//RBM.test_rbm();
	}
	
	/*public static void main(String Args[]){
		testrbm();
	}*/
}
