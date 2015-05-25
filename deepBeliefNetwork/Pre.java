package deepBeliefNetwork;

import java.util.ArrayList;
import java.util.List;


public class Pre {
	
	public static List<MyRBM> train(double[][] train, int[] hidLayer, int epoche, int k, double learningRate){
		int num = train.length;//number of training samples
		int length = train[0].length;//number of visible units
		int DBNlayer = hidLayer.length;//number of RBM layers
		double[][] currentLayer = new double[num][length];
		double[][] nextLayer;
		List<MyRBM> rbmList = new  ArrayList<MyRBM>();
		//double[][][] visLayers = new double[DBNlayer+1][num][length];
		for(int n=0; n<num; ++n){
			for(int i=0; i<length; ++i){
				currentLayer[n][i] = train[n][i];
			}
		}
		
		for(int l=0; l<DBNlayer; ++l){
			MyRBM rbm = new MyRBM();
			nextLayer = new double[num][hidLayer[l]];
			rbm.initialRBM(currentLayer, hidLayer[l]);
			for(int e=0; e<epoche; ++e){
				for(int n=0; n<num; ++n){
					rbm.cD(currentLayer[n], k, learningRate);
				}
			}
			for(int n=0; n<num; ++n){
				nextLayer[n] = rbm.sample(rbm.sigmoidHgivenV(currentLayer[n],1,0));
			}
			rbmList.add(rbm);
			currentLayer = nextLayer;
		}
		return rbmList;
	}
	
	/*private static void test(double[][] test, List<MyRBM> rbmList){
		int DBNlayer = rbmList.size();
		int length = test[0].length;
		double[][] reconstructLayer = new double[test.length][length];
		for(int n=0; n<test.length; ++n){
			double[] nextLayer;
			double[] currentLayer = new double[length];
			for(int i=0; i<length; ++i){
				currentLayer[i] = test[n][i];
			}
			for(int l=0; l<DBNlayer; ++l){
				MyRBM rbm = new MyRBM();
				rbm.getRBM(rbmList.get(l));
				nextLayer = new double[rbm.hidNum];
				nextLayer = rbm.sigmoidHgivenV(currentLayer,1,0);
				currentLayer = nextLayer;
				//System.out.println(Arrays.toString(currentLayer));
			}
			for(int l=DBNlayer-1; l>-1; --l){
				MyRBM rbm = new MyRBM();
				rbm.getRBM(rbmList.get(l));
				nextLayer = new double[rbm.visNum];
				nextLayer = rbm.sigmoidVgivenH(currentLayer);
				currentLayer = nextLayer;
				//System.out.println(Arrays.toString(currentLayer));
			}
			for(int i=0; i<length; ++i){
				reconstructLayer[n][i] = currentLayer[i];
			} 
		}
		System.out.println(Arrays.deepToString(reconstructLayer));
	}*/
	
	
}