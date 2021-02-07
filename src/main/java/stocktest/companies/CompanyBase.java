package stocktest.companies;

import stocktest.data.DailyData;
import stocktest.data.OneDayData;
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
		this.deepNNet = new DeepNNetBase(200, 15, 8);
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public void trainNNet() throws IOException
	{
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.YEAR, -3);

		DailyData dailyData = new DailyData(from, to, stock);

		int numSampleDays = 50;

		for(int i=0; i<dailyData.size() - numSampleDays - 1; i++)
		{
			// set input values
			List<OneDayData> sampleDays = new ArrayList<>();
			OneDayData nextDayData = dailyData.getDay(i + numSampleDays + 1);

			for(int j=0; j<numSampleDays; j++)
				sampleDays.add(dailyData.getDay(i + j));

			// get neural network output
			OneDayData prediction = getNextDayPrediction(sampleDays);

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
//			nextDayData.scaleDown(sampleDays.get(0));

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
		from.add(Calendar.MONTH, -12);

		dailyData = new DailyData(from, to, stock);

		List<OneDayData> sampleDays = new ArrayList<>();

		for(int i=dailyData.size() - 50; i<dailyData.size(); i++)
			sampleDays.add(dailyData.getDay(i));

		OneDayData prediction = getNextDayPrediction(sampleDays);
		System.out.printf("Open  = %f\n", prediction.getOpen());
		System.out.printf("Close = %f\n", prediction.getClose());
		System.out.printf("High  = %f\n", prediction.getHigh());
		System.out.printf("Low   = %f\n", prediction.getLow());

		//	Open  = 131.458954
		//	Close = 133.440203
		//	High  = 133.577650
		//	Low   = 129.691139

		//	Open  = 131.384490
		//	Close = 133.495722
		//	High  = 133.492453
		//	Low   = 129.908556

		//	Open  = 131.317421
		//	Close = 133.319760
		//	High  = 133.575140
		//	Low   = 129.329789
	}

	private double errorRate(double estimate, double actual)
	{
		return 100 * (actual - estimate) / actual;
	}

	private OneDayData getNextDayPrediction(List<OneDayData> sampleValues)
	{
		// scale raw data and copy to input vector
		int iNNet = 0;
		OneDayData unitData = sampleValues.get(0).shallowCopy();

		double fOpen = sampleValues.get(0).getOpen();
		double fClose = sampleValues.get(0).getClose();
		double fHigh = sampleValues.get(0).getHigh();
		double fLow = sampleValues.get(0).getLow();

		for(int i=0; i<sampleValues.size(); i++)
		{
//			sampleValues.get(i).scaleDown(fOpen, fClose, fHigh,fLow);

			this.deepNNet.vecIn[iNNet++] = sampleValues.get(i).getOpen() / fOpen;
			this.deepNNet.vecIn[iNNet++] = sampleValues.get(i).getClose() / fClose;
			this.deepNNet.vecIn[iNNet++] = sampleValues.get(i).getHigh() / fHigh;
			this.deepNNet.vecIn[iNNet++] = sampleValues.get(i).getLow() / fLow;
		}

		// calculate neural output
		this.deepNNet.nnRunFeedForward();

		// process the result and return
		OneDayData result = new OneDayData();

		result.setOpen(this.deepNNet.vecOut[0] + this.deepNNet.vecOut[1]);
		result.setClose(this.deepNNet.vecOut[2] + this.deepNNet.vecOut[3]);
		result.setHigh(this.deepNNet.vecOut[4] + this.deepNNet.vecOut[5]);
		result.setLow(this.deepNNet.vecOut[6] + this.deepNNet.vecOut[7]);
//
//		result.setOpen((this.deepNNet.vecOut[0] + this.deepNNet.vecOut[1]) / 2);
//		result.setClose((this.deepNNet.vecOut[2] + this.deepNNet.vecOut[3]) / 2);
//		result.setHigh((this.deepNNet.vecOut[4] + this.deepNNet.vecOut[5]) / 2);
//		result.setLow((this.deepNNet.vecOut[6] + this.deepNNet.vecOut[7]) / 2);

		result.scaleUp(fOpen, fClose, fHigh,fLow);

		return result;
	}

/*
	Open  = 145.06 (139.52)  (   4% off)
	Close = 142.46 (137.09)  (   4% off)
	High  = 145.23 (141.99)  (   2% off)
	Low   = 141.31 (136.70)  (   3% off)

	Open  = 140.04 (135.83)  (   3% off)
	Close = 140.81 (131.96)  (   6% off)
	High  = 142.53 (136.74)  (   4% off)
	Low   = 137.23 (130.21)  (   5% off)

	Open  = 142.66 (133.75)  (   6% off)
	Close = 142.36 (134.14)  (   6% off)
	High  = 145.77 (135.38)  (   7% off)
	Low   = 135.35 (130.93)  (   3% off)

	Open  = 141.98 (135.73)  (   4% off)
	Close = 132.25 (134.99)  (  -2% off)
	High  = 135.99 (136.31)  (  -0% off)
	Low   = 130.31 (134.61)  (  -3% off)

	Open  = 132.88 (135.76)  (  -2% off)
	Close = 134.94 (133.94)  (   1% off)
	High  = 134.24 (135.77)  (  -1% off)
	Low   = 133.84 (133.61)  (   0% off)

	Open  = 132.72 (136.30)  (  -3% off)
	Close = 133.44 (137.39)  (  -3% off)
	High  = 133.50 (137.40)  (  -3% off)
	Low   = 132.18 (134.59)  (  -2% off)

	Open  = 138.04 (137.35)  (   0% off)
	Close = 134.88 (136.76)  (  -1% off)
	High  = 137.75 (137.42)  (   0% off)
	Low   = 135.66 (135.86)  (  -0% off)
*/
}
