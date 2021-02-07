package stocktest.data;

import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyData
{
	List<OneDayData> dailyData = new ArrayList<>();

	private double scaleFactorOpen;
	private double scaleFactorClose;
	private double scaleFactorHigh;
	private double scaleFactorLow;

	public DailyData(Calendar from, Calendar to, Stock stock) throws IOException
	{
		List<HistoricalQuote> history = stock.getHistory(from, to, Interval.DAILY);

		for(int i=0; i<history.size(); i++)
			dailyData.add(OneDayData.setData(history.get(i)));
	}

	public OneDayData getDay(int index)
	{
		return dailyData.get(index);
	}

	public int size()
	{
		return this.dailyData.size();
	}

//	public void setScaleFactor(double fOpen, double fClose, double fHigh, double fLow)
//	{
//		this.scaleFactorOpen = fOpen;
//		this.scaleFactorClose = fClose;
//		this.scaleFactorHigh = fHigh;
//		this.scaleFactorLow = fLow;
//	}

	public void scaleDown(OneDayData unitData)
	{
		for(OneDayData day : dailyData)
			day.scaleDown(unitData);
	}

	public void scaleUp(OneDayData unitData)
	{
		for(OneDayData day : dailyData)
			day.scaleUp(unitData);
	}
}
