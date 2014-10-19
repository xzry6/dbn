package deepBeliefNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pretrain {
	
	static List<MyRBM> rbmList = new  ArrayList<MyRBM>();
	
	public static void pretrain(double[][] train, double[][] test, 
			int DBNlayer, int epoche, int k, double learningRate){
		int num = train.length;//number of training samples
		int length = train[0].length;//number of visible units
		double[][][] visLayers = new double[DBNlayer+1][num][length];
		for(int n=0; n<num; ++n){
			for(int i=0; i<length; ++i){
				visLayers[0][n][i] = train[n][i];
			}
		}
		
		for(int l=0; l<DBNlayer; ++l){
			MyRBM rbm = new MyRBM();
			rbm.initialRBM(visLayers[l], 6);
			for(int e=0; e<epoche; ++e){
				for(int n=0; n<num; ++n){
					rbm.cD(visLayers[l][n], k, learningRate);
				}
			}
			for(int n=0; n<num; ++n){
				visLayers[l+1][n] = rbm.sample(rbm.sigmoidHgivenV(visLayers[l][n]));
			}
			rbmList.add(rbm);
		}
		//System.out.println(Arrays.toString(rbmList.get(4).visBias));
		//System.out.println(Arrays.toString(rbmList.get(1).visBias));
	}
	
	static void test(double[][] test, List<MyRBM> rbmList, int DBNlayer){
		int length = test[0].length;
		double[][] tt = new double[test.length][length];
		for(int n=0; n<test.length; ++n){
			double[] temp = new double[length];
			for(int i=0; i<length; ++i){
				temp[i] = test[n][i];
			}
			//System.out.println(Arrays.toString(temp));
			for(int l=0; l<DBNlayer; ++l){
				MyRBM rbm = new MyRBM();
				rbm.getRBM(rbmList.get(l));
				//System.out.println("visbias is: \n"+Arrays.toString(rbm.visBias));
				temp = rbm.sigmoidHgivenV(temp);
				//System.out.println(Arrays.toString(temp));
			}
			for(int l=DBNlayer-1; l>-1; --l){
				MyRBM rbm = new MyRBM();
				rbm.getRBM(rbmList.get(l));
				//System.out.println("visbias is: \n"+Arrays.toString(rbm.visBias));
				temp = rbm.sigmoidVgivenH(temp);
				//System.out.println(Arrays.toString(temp));
			}
			for(int i=0; i<length; ++i){
				tt[n][i] = temp[i];
			} 
		}
		System.out.println(Arrays.deepToString(tt));
	}
	
	public static void main(String Args[]){
		int DBNlayer = 5;//number of layers in DBN
		int epoche = 1000;//number of iterations in training one RBM
		int k = 1;//parameter in CD when train a RBM
		double learningRate = 0.2;//learning rate in CD
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
		pretrain(train, test, DBNlayer, epoche, k, learningRate);
		test(test, rbmList, DBNlayer);
		//test(test, rbmList, 4);
		//test(test, rbmList, 3);
		//test(test, rbmList, 2);
		//test(test, rbmList, 1);
		//MyRBM.testrbm();
	}
}
