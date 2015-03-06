package deepBeliefNetwork;

public class Main {
	public static void main(String Args[]){
		int epoche = 1000;//number of iterations in training one RBM
		int k = 1;//parameter in CD when train a RBM
		double learningRate = 0.1;//learning rate in CD
		//double finetuneRate = 1.5;
		double finetuneRate = 5;
		//int[] hidLayer = {100,60,40,20};
		int[] hidLayer = {60,20,12};
		
		int fold = 20;
		//FineTune f_t = new FineTune();
		
		Input input = new Input("input388.txt", "label388.txt", 388);
		/*int num = 10;
		Random r = new Random();
		boolean[] bool = new boolean[1529];
		int[] index = new int[num];
		int temp = 0;
		for(int n=0; n<num; ++n){
			do{
				temp = r.nextInt(1529);
			} while(bool[temp]);
			bool[temp] = true;
			index[n] = temp;
		}
		double[][] data = new double[num][input.data[0].length];
		double[] label = new double[num];
		for(int n=0; n<num; ++n){
			//System.out.println(index[n]);
			//System.out.println(Arrays.toString(input.data[index[n]]));
			//System.out.println(input.labelC[index[n]]);
			System.arraycopy(input.data[index[n]], 0, data[n], 0, data[0].length);
			label[n] = input.labelC[index[n]];
		}*/
		                       Cross cross = new Cross(input.data, input.labelC, fold);
		                       cross.validation(hidLayer, epoche, k, learningRate, finetuneRate);
		/*int testN = 1529/10;
		System.out.println(testN);
		int trainN = 1529-testN;*/
		/*int trainN = 1000;
		System.out.println(trainN);
		double[][] train = new double[trainN][input.data[0].length];
		double[][] label = new double[trainN][1];
		int nn = 0;
		for(int n=0; n<trainN; ++n){
			System.arraycopy(input.data[n], 0, train[n], 0, train[n].length);
			label[n][0] = input.labelC[n];
			if(label[n][0]>0){
				nn++;
			}
		}
		System.out.println("positive number is: "+nn+"\nnegative number is: "+(trainN-nn));
		System.out.println(Arrays.toString(train[trainN-1]));
		System.out.println(Arrays.toString(label[trainN-1]));*/
		/*double[][] test = new double[testN][input.data[0].length];
		double[][] test_label = new double[testN][1];
		for(int n=trainN; n<1529; ++n){
			System.arraycopy(input.data[n], 0, test[n-trainN], 0, test[n-trainN].length);
			test_label[n-trainN][0] = input.labelC[n];
		}

		System.out.println(Arrays.toString(test[0]));
		System.out.println(Arrays.toString(test_label[0]));
		System.out.println(Arrays.toString(test[testN-1]));
		System.out.println(Arrays.toString(test_label[testN-1]));*/
		/*double[][] test = new double[1][input.data[0].length];
		double[][] test_label = new double[1][1];
		System.arraycopy(input.data[1022], 0, test[0], 0, test[0].length);
		test_label[0][0] = input.labelC[1022];
		
		System.out.println(Arrays.toString(test[0]));
		System.out.println(Arrays.toString(test_label[0]));*/
		   //List<MyRBM> rbm = Pre.train(input.data, hidLayer, epoche, k, learningRate);
		   //f_t.fineTune(input.data, input.labelC, rbm, epoche, 0.8);
		   //SVM svm = new SVM();
		   //svm.train(input.data, input.labelC, rbm, epoche);
		//svm.test(test, svm.model, rbm);
		//test(test, rbmList);
		  // LogisticRegression.traditionalLR(train, label, epoche, 1.2, test, test_label);
		  //f_t.fineTune(train, label, rbm, epoche, 1.2);
		  // f_t.test(test, test_label, rbm);*/
		//MyRBM.testrbm();
		//DBN.test_dbn();
	}
}
