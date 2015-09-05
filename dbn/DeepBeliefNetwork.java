package dbn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeepBeliefNetwork {
	public List<RestrictedBoltzmannMachine> pretrain(
			double[][] train, int[] hidLayer, int epoche, int k, double learningRate, Map<String,Double> map) {
		int num = train.length;//number of training samples
		int length = train[0].length;//number of visible units
		int DBNlayer = hidLayer.length;//number of RBM layers
		double[][] currentLayer = new double[num][length];
		double[][] nextLayer;
		List<RestrictedBoltzmannMachine> rbmList = new ArrayList<RestrictedBoltzmannMachine>();
		for(int n=0; n<num; ++n)
			for(int i=0; i<length; ++i)
				currentLayer[n][i] = train[n][i];
		for(int l=0; l<DBNlayer; ++l){
			RestrictedBoltzmannMachine rbm = new RestrictedBoltzmannMachine(currentLayer, hidLayer[l]);
			nextLayer = new double[num][hidLayer[l]];
			for(int e=0; e<epoche; ++e)
				for(int n=0; n<num; ++n)
					rbm.contrasiveDivergence(currentLayer[n],k,learningRate,map);
			for(int n=0; n<num; ++n)
				nextLayer[n] = rbm.generateNextLayer(currentLayer[n]);
			rbmList.add(rbm);
			currentLayer = nextLayer;
		}
		
		return rbmList;
	}
}
