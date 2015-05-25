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
		
		Input input = new Input("input388.txt", "label388.txt", 388);

		Cross cross = new Cross(input.data, input.labelC, fold);
		cross.validation(hidLayer, epoche, k, learningRate, finetuneRate);

	}
}
