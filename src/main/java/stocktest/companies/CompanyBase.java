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

	public CompanyBase(String ticker) throws IOException
	{
		this.ticker = ticker.toUpperCase();
		this.stock = YahooFinance.get(this.ticker);
		this.deepNNet = new DeepNNetBase(100, 5, 8);
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public void trainNNet() throws IOException
	{
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.MONTH, -5);

		DailyData dailyData = new DailyData(from, to, stock);

		int numSampleDays = 25;

		for(int i=0; i<dailyData.size() - numSampleDays - 1; i++)
		{
			// set input values
			List<OneDayData> sampleDays = new ArrayList<>();
			OneDayData nextDayData = dailyData.getDay(i + numSampleDays + 1);

			for(int j=0; j<numSampleDays; j++)
				sampleDays.add(dailyData.getDay(i + j));

			// get neural network output
			OneDayData prediction = trainLocalData(sampleDays);

			if(dailyData.size() - numSampleDays - 1 - i < 50)
			{
				System.out.printf("Open  = %6.2f (%6.2f)", prediction.getOpen(), nextDayData.getOpen());
				System.out.printf("  (%4.0f%% off)\n", errorRate(nextDayData.getOpen(), prediction.getOpen()));

				System.out.printf("Close = %6.2f (%6.2f)", prediction.getClose(), nextDayData.getClose());
				System.out.printf("  (%4.0f%% off)\n", errorRate(nextDayData.getClose(), prediction.getClose()));

				System.out.printf("High  = %6.2f (%6.2f)", prediction.getHigh(), nextDayData.getHigh());
				System.out.printf("  (%4.0f%% off)\n", errorRate(nextDayData.getHigh(), prediction.getHigh()));

				System.out.printf("Low   = %6.2f (%6.2f)", prediction.getLow(), nextDayData.getLow());
				System.out.printf("  (%4.0f%% off)\n", errorRate(nextDayData.getLow(), prediction.getLow()));
				System.out.printf("\n");
			}

			// run backpropagation
			int iNNet = 0;

			this.deepNNet.vecDesired[iNNet++] = nextDayData.getOpen() / sampleDays.get(0).getOpen() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getOpen() / sampleDays.get(0).getOpen() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getClose() / sampleDays.get(0).getClose() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getClose() / sampleDays.get(0).getClose() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getHigh() / sampleDays.get(0).getHigh() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getHigh() / sampleDays.get(0).getHigh() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getLow() / sampleDays.get(0).getLow() / 2;
			this.deepNNet.vecDesired[iNNet++] = nextDayData.getLow() / sampleDays.get(0).getLow() / 2;

			this.deepNNet.nnRunBackprop();
		}

		from = Calendar.getInstance();
		to = Calendar.getInstance();
		from.add(Calendar.MONTH, -3);

		dailyData = new DailyData(from, to, stock);

		List<OneDayData> sampleDays = new ArrayList<>();

		for(int i=dailyData.size() - 25; i<dailyData.size(); i++)
			sampleDays.add(dailyData.getDay(i));

		OneDayData prediction = trainLocalData(sampleDays);
		System.out.printf("Open  = %f\n", prediction.getOpen());
		System.out.printf("Close = %f\n", prediction.getClose());
		System.out.printf("High  = %f\n", prediction.getHigh());
		System.out.printf("Low   = %f\n", prediction.getLow());
	}

	private double errorRate(double estimate, double actual)
	{
		return 100 * (actual - estimate) / actual;
	}

	private OneDayData trainLocalData(List<OneDayData> sampleData)
	{
		// scale raw data and copy to input vector
		int iNNet = 0;
		OneDayData unitData = sampleData.get(0).shallowCopy();

		double fOpen = sampleData.get(0).getOpen();
		double fClose = sampleData.get(0).getClose();
		double fHigh = sampleData.get(0).getHigh();
		double fLow = sampleData.get(0).getLow();

		for(int i=0; i<sampleData.size(); i++)
		{
//			sampleValues.get(i).scaleDown(fOpen, fClose, fHigh,fLow);

			this.deepNNet.vecIn[iNNet++] = sampleData.get(i).getOpen() / fOpen;
			this.deepNNet.vecIn[iNNet++] = sampleData.get(i).getClose() / fClose;
			this.deepNNet.vecIn[iNNet++] = sampleData.get(i).getHigh() / fHigh;
			this.deepNNet.vecIn[iNNet++] = sampleData.get(i).getLow() / fLow;
		}

		// calculate neural output
		this.deepNNet.nnRunFeedForward();

		// process the result and return
		OneDayData result = new OneDayData();

		result.setOpen(this.deepNNet.vecOut[0] + this.deepNNet.vecOut[1]);
		result.setClose(this.deepNNet.vecOut[2] + this.deepNNet.vecOut[3]);
		result.setHigh(this.deepNNet.vecOut[4] + this.deepNNet.vecOut[5]);
		result.setLow(this.deepNNet.vecOut[6] + this.deepNNet.vecOut[7]);

		result.scaleUp(fOpen, fClose, fHigh,fLow);

		return result;
	}

	public OneDayData getNextDayPrediction(List<OneDayData> sampleData)
	{
		return null;
	}
}
