package deepBeliefNetwork;

import java.util.Arrays;

public class Main {
	
	public static void main(String Args[]){

		int epoche = 1000;//number of iterations in training one RBM
		int k = 1;//parameter in CD when train a RBM
		double learningRate = 0.1;//learning rate in CD
		//double finetuneRate = 1.5;
		double finetuneRate = 1.5;
		//int[] hidLayer = {100,60,40,20};
		int[] hidLayer = {20,12};
		
		int fold = 10;
		
		Input input = new Input("input1615.txt", "labelR1615.txt", 1615);
		//System.out.println(Arrays.toString(input.labelC));
		
		Cross cross = new Cross(input.data, input.labelC, fold);

		cross.validation(hidLayer, epoche, k, learningRate, finetuneRate);
	}
}
