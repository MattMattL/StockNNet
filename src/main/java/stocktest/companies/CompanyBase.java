package stocktest.companies;

import stocktest.data.OneDayData;
import stocktest.data.DailyData;
import stocktest.neuralnet.DeepNNetBase;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CompanyBase
{
	private final String ticker;
	private Stock stock;
	private DailyData dailyData;

	private int netIn;
	private int netMid;
	private int netOut;
	private DeepNNetBase deepNNet;

	private boolean isNNetInitialised;
	private int numTrainingBatch;
	private int numLoadingMonths;

	public CompanyBase(String ticker) throws IOException
	{
		this.ticker = ticker.toUpperCase();
		this.stock = YahooFinance.get(this.ticker);
		this.isNNetInitialised = false;
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public void calibrateNNet() throws IOException
	{
		this.isNNetInitialised = true;

		double localError[];

		int presetDepth[] = {3, 4, 5, 6, 10};
		int presetMonths[] = {6, 12, 24, 36};
		int presetDays[] = {10, 20, 30, 50, 100};

		int bestDepth = 0, bestMonths = 0, bestDays = 0; // dataset with least error rate
		double minError = Double.MAX_VALUE;

		for(int d=0; d<presetDepth.length; d++)
		{
			for(int m=0; m<presetMonths.length; m++)
			{
				for(int b=0; b<presetDays.length; b++)
				{
					setNNetProperties(presetDepth[d], presetMonths[m], presetDays[b]);
					localError = trainNNet();

					double localErrorSum = 0;

					for(int l=0; l<localError.length; l++)
						localErrorSum += localError[l];

					if(localErrorSum < minError)
					{
						minError = localErrorSum;

						bestDepth = presetDepth[d];
						bestMonths = presetMonths[m];
						bestDays = presetDays[b];
					}

					System.out.printf("(%d, %d, %d)\n", d, m, b);
					System.out.printf("open  = %8.4f\n", localError[0]);
					System.out.printf("close = %8.4f\n", localError[1]);
					System.out.printf("high  = %8.4f\n", localError[2]);
					System.out.printf("low   = %8.4f\n", localError[3]);
					System.out.printf("error = %8.4f\n\n", localErrorSum);
				}
			}
		}

		System.out.printf("final:\n");
		System.out.printf("depth = %d\n", bestDepth);
		System.out.printf("month = %d\n", bestMonths);
		System.out.printf("batch = %d\n", bestDays);
		System.out.printf("error = %f\n\n", minError);
	}

	public void setNNetProperties(int netDepth, int prevMonths, int batchDays) throws IOException
	{
		this.isNNetInitialised = true;

		this.numLoadingMonths = prevMonths;
		this.numTrainingBatch = batchDays;
		this.deepNNet = new DeepNNetBase(4 * batchDays, netDepth, 8);

		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.MONTH, -this.numLoadingMonths);
		this.dailyData = new DailyData(from, to, this.stock);
	}

	public double[] trainNNet() throws IOException
	{
		double sumErrorSquared[] = {0, 0, 0, 0};

		if(!this.isNNetInitialised)
		{
			System.out.printf("[Error] <CompanyBase#trainNNet> network not initialised\n");
			return sumErrorSquared;
		}

		for(int i=0; i<(this.dailyData.size() - this.numTrainingBatch - 1); i++)
		{
			// set input values
			List<OneDayData> sampleDays = new ArrayList<>();
			OneDayData nextDayData = this.dailyData.getDay(i + this.numTrainingBatch + 1);

			for(int j=0; j<this.numTrainingBatch; j++)
				sampleDays.add(this.dailyData.getDay(i + j));

			// get neural network output
			OneDayData prediction = trainLocalData(sampleDays);

			if(this.dailyData.size() - this.numTrainingBatch - 1 - i < 25)
				this.printResult(nextDayData, prediction);

			// calculate error
			sumErrorSquared[0] += Math.pow(errorRate(nextDayData.getOpen(), prediction.getOpen()), 2);
			sumErrorSquared[1] += Math.pow(errorRate(nextDayData.getClose(), prediction.getClose()), 2);
			sumErrorSquared[2] += Math.pow(errorRate(nextDayData.getHigh(), prediction.getHigh()), 2);
			sumErrorSquared[3] += Math.pow(errorRate(nextDayData.getLow(), prediction.getLow()), 2);

			// run backpropagation
			int iNNet = 0;

			nextDayData.scaleDown(sampleDays.get(0));

			this.deepNNet.vecDesired[iNNet++] = nextDayData.getOpen() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getOpen() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getClose() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getClose() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getHigh() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getHigh() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getLow() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getLow() / 2;

			nextDayData.scaleUp(sampleDays.get(0));

			this.deepNNet.nnRunBackprop();
		}

		for(int i=0; i<sumErrorSquared.length; i++)
			sumErrorSquared[i] /= (this.dailyData.size() - this.numTrainingBatch);

		return sumErrorSquared;
	}

	private void printResult(OneDayData actual, OneDayData estimate)
	{
		System.out.printf("[%s]\n", actual.getDate());

		System.out.printf("Open  = %6.2f (%5.2f)", estimate.getOpen(), actual.getOpen());
		System.out.printf("  (%5.1f%% off)\n", 100 * errorRate(actual.getOpen(), estimate.getOpen()));

		System.out.printf("Close = %6.2f (%5.2f)", estimate.getClose(), actual.getClose());
		System.out.printf("  (%5.1f%% off)\n", 100 * errorRate(actual.getClose(), estimate.getClose()));

		System.out.printf("High  = %6.2f (%5.2f)", estimate.getHigh(), actual.getHigh());
		System.out.printf("  (%5.1f%% off)\n", 100 * errorRate(actual.getHigh(), estimate.getHigh()));

		System.out.printf("Low   = %6.2f (%5.2f)", estimate.getLow(), actual.getLow());
		System.out.printf("  (%5.1f%% off)\n", 100 * errorRate(actual.getLow(), estimate.getLow()));

		System.out.printf("\n");
	}

	private double errorRate(double actual, double estimate)
	{
		return (estimate - actual) / actual;
	}

	/*
	*	Returns predicted result of one day stock data based on provided sample days.
	* 	Same as getNextDayPrediction, otherwise.
	*/
	private OneDayData trainLocalData(List<OneDayData> sampleData)
	{
		// scale raw data and copy to input vector
		int iNNet = 0;
		OneDayData unitData = sampleData.get(0).shallowCopy();

		for(int i=0; i<sampleData.size(); i++)
		{
			sampleData.get(i).scaleDown(unitData);

			this.deepNNet.vecIn[iNNet++] = sampleData.get(i).getOpen();
			this.deepNNet.vecIn[iNNet++] = sampleData.get(i).getClose();
			this.deepNNet.vecIn[iNNet++] = sampleData.get(i).getHigh();
			this.deepNNet.vecIn[iNNet++] = sampleData.get(i).getLow();

			sampleData.get(i).scaleUp(unitData);
		}

		// process the result and return
		this.deepNNet.nnRunFeedForward();

		OneDayData result = new OneDayData();

		result.setOpen(this.deepNNet.vecOut[0] + this.deepNNet.vecOut[1]);
		result.setClose(this.deepNNet.vecOut[2] + this.deepNNet.vecOut[3]);
		result.setHigh(this.deepNNet.vecOut[4] + this.deepNNet.vecOut[5]);
		result.setLow(this.deepNNet.vecOut[6] + this.deepNNet.vecOut[7]);

		result.scaleUp(unitData);

		return result;
	}

	public OneDayData getNextDayPrediction() throws IOException
	{
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.DATE, -2 * this.numTrainingBatch);

		DailyData dailyData = new DailyData(from, to, this.stock);

		List<OneDayData> sampleDays = new ArrayList<>();

		for(int i=dailyData.size() - this.numTrainingBatch; i<dailyData.size(); i++)
			sampleDays.add(dailyData.getDay(i));

		return trainLocalData(sampleDays);
	}
}
