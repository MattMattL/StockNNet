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

	private int netIn;
	private int netMid;
	private int netOut;
	private DeepNNetBase deepNNet;

	private int numTrainingBatch;
	private int numLoadingMonths;

	public CompanyBase(String ticker) throws IOException
	{
		this.ticker = ticker.toUpperCase();
		this.stock = YahooFinance.get(this.ticker);
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public void calibrateNNet()
	{

	}

	public void trainNNet(int loadingMonths, int batchDays) throws IOException
	{
		this.numLoadingMonths = loadingMonths;
		this.numTrainingBatch= batchDays;
		this.deepNNet = new DeepNNetBase(4 * this.numTrainingBatch, 4, 8);

		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.MONTH, -this.numLoadingMonths);

		DailyData dailyData = new DailyData(from, to, this.stock);

		for(int i=0; i<(dailyData.size() - this.numTrainingBatch - 1); i++)
		{
			// set input values
			List<OneDayData> sampleDays = new ArrayList<>();
			OneDayData nextDayData = dailyData.getDay(i + this.numTrainingBatch + 1);

			for(int j=0; j<this.numTrainingBatch; j++)
				sampleDays.add(dailyData.getDay(i + j));

			// get neural network output
			OneDayData prediction = trainLocalData(sampleDays);

			if(dailyData.size() - this.numTrainingBatch - 1 - i < 25)
				this.printResult(nextDayData, prediction);

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
	}

	private void printResult(OneDayData actual, OneDayData estimate)
	{
		System.out.printf("[%s]\n", actual.getDate());

		System.out.printf("Open  = %6.2f (%5.2f)", estimate.getOpen(), actual.getOpen());
		System.out.printf("  (%5.1f%% off)\n", errorRate(actual.getOpen(), estimate.getOpen()));

		System.out.printf("Close = %6.2f (%5.2f)", estimate.getClose(), actual.getClose());
		System.out.printf("  (%5.1f%% off)\n", errorRate(actual.getClose(), estimate.getClose()));

		System.out.printf("High  = %6.2f (%5.2f)", estimate.getHigh(), actual.getHigh());
		System.out.printf("  (%5.1f%% off)\n", errorRate(actual.getHigh(), estimate.getHigh()));

		System.out.printf("Low   = %6.2f (%5.2f)", estimate.getLow(), actual.getLow());
		System.out.printf("  (%5.1f%% off)\n", errorRate(actual.getLow(), estimate.getLow()));

		System.out.printf("\n");
	}

	private double errorRate(double actual, double estimate)
	{
		return 100 * (estimate - actual) / actual;
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
