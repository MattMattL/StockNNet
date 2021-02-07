package stocktest.neuralnet;

import java.util.Random;

public class NNetTest
{
	private DeepNNetBase nnet;
	private Random random;

	public NNetTest()
	{
		this.nnet = new DeepNNetBase(10, 5, 10);
		this.random = new Random(System.currentTimeMillis());
	}

	public void test()
	{
		// set input and desired output
		for(int j=0; j<this.nnet.NET_IN; j++)
				this.nnet.vecIn[j] = abs(random.nextInt()) % 2;

		for(int j=0; j<this.nnet.NET_OUT; j++)
			this.nnet.vecDesired[j] = abs(random.nextInt()) % 2;

		// output before training
		for(int i=0; i<this.nnet.NET_OUT; i++)
			System.out.printf("%5.2f (%5.2f)\n", this.nnet.vecOut[i] , this.nnet.vecDesired[i]);
		System.out.printf("\n");

		// train NNet
		for(int i=0; i<100; i++)
		{
			this.nnet.nnRunFeedForward();
			this.nnet.nnRunBackprop();
		}

		// output after training
		for(int i=0; i<this.nnet.NET_OUT; i++)
			System.out.printf("%5.2f (%5.2f)\n", this.nnet.vecOut[i] , this.nnet.vecDesired[i]);
	}

	private int abs(int num)
	{
		return (num >= 0)? num : -num;
	}
}
