package deepBeliefNetwork;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;



public class Cross {
	int num;
	int testN;
	int trainN;
	int length;
	int fold;
	int[] index;
	double[][] data;
	double[] label;
	Random r = new Random(32553532);
	
	Cross(double[][] data, double[] label, int fold){

		num = data.length;
		length = data[0].length;
		this.fold = fold;
		this.data = new double[num][length];
		this.label = new double[num];
		testN = num/fold;
		trainN = num-testN;
		for(int n=0; n<num; ++n){
			System.arraycopy(data[n], 0, this.data[n], 0, length);
		}
		System.arraycopy(label, 0, this.label, 0, num);

		int temp = 0;
		
		index = new int[num];
		boolean[] bool = new boolean[num];
		for(int n=0; n<num; ++n){
			do{
				temp = r.nextInt(num);
			} while(bool[temp]);
			bool[temp] = true;
			index[n] = temp;
		}
		for(int f=0; f<fold; ++f){
			for(int n=f*testN; n<(f+1)*testN; ++n){
				//System.out.print(index[n]+" ");
			}
			//System.out.println("");
		}
	}
	
	void getTrain(int fold, double[][] train, double[] trainLabel){
		int[] trainIndex = new int[trainN];
		System.arraycopy(index, 0, trainIndex, 0, fold*testN);
		System.arraycopy(index, (fold+1)*testN, trainIndex, fold*testN, num-(fold+1)*testN);
		//System.out.println("train index is: "+Arrays.toString(trainIndex));
		for(int n=0; n<trainN; ++n){
			System.arraycopy(data[trainIndex[n]], 0, train[n], 0, length);
			trainLabel[n] = label[trainIndex[n]];
		}
	}
	
	void getTest(int fold, double[][] test, double[] testLabel){
		int[] testIndex = new int[testN];
		System.arraycopy(index, fold*testN, testIndex, 0, testN);
		//System.out.println("test index is: "+Arrays.toString(testIndex));
		for(int n=0; n<testN; ++n){
			System.arraycopy(data[testIndex[n]], 0, test[n], 0, length);
			testLabel[n] = label[testIndex[n]];
		}
	}
	
	
	public void validation(int[] hidLayer, int epoche, int k, double learningRate, double finetuneRate){
		double[][] train;
		double[][] test;
		double[] trainLabel;
		double[] testLabel;
		int TP = 0;
		int TN = 0;
		int FP = 0;
		int FN = 0;
		int sTP = 0;
		int sTN = 0;
		int sFP = 0;
		int sFN = 0;
		
		//fold = 2;
		
		for(int f=0; f<fold; ++f){
			test = new double[testN][length];
			testLabel = new double[testN];
			getTest(f, test, testLabel);
			
			train = new double[trainN][length];
			trainLabel = new double[trainN];
			getTrain(f, train, trainLabel);
			
			double[] lglabel = LogisticRegression.traditionalLR(train, trainLabel, epoche, finetuneRate, test, testLabel);
			
			
			for(int n=0; n<lglabel.length; ++n){
				if(lglabel[n]==1){
					if(lglabel[n]==testLabel[n]){
						sTP++;
					} else{
						sFP++;
					}
				} else{
					if(testLabel[n]==0){
						sTN++;
					} else{
						sFN++;
					}
				}
			}
			

			FineTune f_t = new FineTune();
			List<MyRBM> rbm = Pre.train(train, hidLayer, epoche, k, learningRate);
			//SVM svm = new SVM();
			//svm.train(train, label, rbm, epoche);
			//double[] svmlabel = svm.test(test, svm.model, rbm);
			f_t.fineTune(train, trainLabel, rbm, epoche, finetuneRate);
			double[] mylabel = f_t.test(test, testLabel, rbm);
			
			for(int n=0; n<mylabel.length; ++n){
				if(mylabel[n]==1){
					if(mylabel[n]==testLabel[n]){
						TP++;
					} else{
						FP++;
					}
				} else{
					if(mylabel[n]==testLabel[n]){
						TN++;
					} else{
						FN++;
					}
				}
			}
		}
		System.out.println("True Positive is: "+sTP);
		System.out.println("False Positive is: "+sFP);
		System.out.println("False Negative is: "+sFN);
		System.out.println("True Negative is: "+sTN);
		System.out.println("Accuracy is: "+(double)(sTP+sTN)/(sFP+sFN+sTP+sTN));
		System.out.println("");
		
		System.out.println("True Positive is: "+TP);
		System.out.println("False Positive is: "+FP);
		System.out.println("False Negative is: "+FN);
		System.out.println("True Negative is: "+TN);
		System.out.println("Accuracy is: "+(double)(TP+TN)/(FP+FN+TP+TN));
		
		
		
		FineTune f_t = new FineTune();
		List<MyRBM> rbm = Pre.train(data, hidLayer, epoche, k, learningRate);
		f_t.fineTune(data, label, rbm, epoche, finetuneRate);
		

		InputArizona.read();
    	InputArizona.compare();
    	InputArizona.encode();
		double[] l = new double[InputArizona.encode.length];
		
		double[] mylabel = f_t.test(InputArizona.encode, l, rbm);
		System.out.println(Arrays.toString(mylabel));
				/*TP = 0;
				TN = 0;
				FP = 0;
				FN = 0;
				for(int n=0; n<mylabel.length; ++n){
					if(mylabel[n]==1){
						if(mylabel[n]==label[n]){
							TP++;
						} else{
							FP++;
						}
					} else{
						if(mylabel[n]==label[n]){
							TN++;
						} else{
							FN++;
						}
					}
				}
				System.out.println("True Positive is: "+TP);
				System.out.println("False Positive is: "+FP);
				System.out.println("False Negative is: "+FN);
				System.out.println("True Negative is: "+TN);
				System.out.println("Accuracy is: "+(double)(TP+TN)/(FP+FN+TP+TN));*/
		try{
			String s = new String();
			s = "modelW.txt";
			FileWriter file = new FileWriter(s);
			FileWriter fileb = new FileWriter("modelB.txt");
			BufferedWriter out = new BufferedWriter(file);
			BufferedWriter outb = new BufferedWriter(fileb);
			for(int j=0; j<rbm.size(); ++j){
				for(int x=0; x<rbm.get(j).hidNum; ++x){
					for(int y=0; y<rbm.get(j).visNum; ++y){
						out.write(Double.toString(rbm.get(j).W[y][x]));
						out.write((int)' ');
					}
					outb.write(Double.toString(rbm.get(j).hidBias[x]));
					outb.write((int)' ');
					out.write((int)'\r');
					out.write((int)'\n');
				}
				outb.write((int)'\r');
				outb.write((int)'\n');
				out.write((int)'/');
				out.write((int)'\r');
				out.write((int)'\n');
			}
			for(int j=0; j<f_t.topW.length; ++j) {
				out.write(Double.toString(f_t.topW[j][0]));
				out.write((int)' ');
			}
			out.write((int)'\r');
			out.write((int)'\n');
			out.write((int)'/');
			outb.write(Double.toString(f_t.topB[0]));
			outb.write((int)' ');
			outb.write((int)'\r');
			outb.write((int)'\n');
			out.close();
			outb.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		//InputArizona.reload();
		//InputArizona.test();
	}
}