package deepBeliefNetwork;

import java.util.Arrays;

public class LogisticRegression {
	static double[] traditionalLR(double[][] train, double[] prelabel, 
			int epoche, double learningRate, double[][] test, double[] pretest_label){
		int num = train.length;//number of training samples
		int length = train[0].length;//number of visible units
		double[][] label = new double[num][1];
		for(int n=0; n<num; ++n){
			label[n][0] = prelabel[n];
		}
		double[][] test_label = new double[pretest_label.length][1];
		for(int n=0; n<test_label.length; ++n){
			test_label[n][0] = pretest_label[n];
		}
		double[][] W = new double[train[0].length][label[0].length];
		double[] B = new double[label[0].length];
		double[] output = new double[test_label.length];
		
		
		for(int e=0; e<epoche; ++e){
			for(int n=0; n<num; ++n){
				double[] topError = new double[label[0].length];
				for(int j=0; j<label[0].length; ++j){
					double temp = 0;
					for(int i=0; i<length; ++i){
						temp += train[n][i]*W[i][j];
					}
					temp = 1.0/(1.0+Math.exp(-temp-B[j]));
					topError[j] = label[n][j]-temp;
				}
				//update top-layer theta then calculate previous layer error
				FineTune.updateTheta(W, B, topError, train[n], learningRate, num);
			}
		}
		for(int n=0; n<test.length; ++n){
			for(int j=0; j<test_label[0].length; ++j){
				for(int i=0; i<length; ++i){
					output[n] += test[n][i]*W[i][j];
				}
				output[n] = 1.0/(1.0+Math.exp(-output[n]-B[j]));
			}
		}
		
		/*Random r = new Random();
		double sample[] = new double[output.length];
		for(int i=0; i<output.length; ++i){
			if(r.nextDouble()<output[i]){
				sample[i] = 1;
			}
		}*/
		double sample[] = new double[output.length];
		for(int i=0; i<output.length; ++i){
			if(output[i]>=0.5){
				sample[i] = 1;
			}
		}
		System.out.println(Arrays.deepToString(test_label));
		System.out.println(Arrays.toString(output));
		return sample;
	}

}
