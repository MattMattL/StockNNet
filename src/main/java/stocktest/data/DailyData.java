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

		for(HistoricalQuote quote : history)
			dailyData.add(OneDayData.setData(quote));
	}

	public OneDayData getDay(int index)
	{
		return dailyData.get(index);
	}

	public void addDay(OneDayData data)
	{
		this.dailyData.add(data);
	}

	public int size()
	{
		return this.dailyData.size();
	}

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
