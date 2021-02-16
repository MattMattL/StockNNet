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

	public DailyData()
	{

	}

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

	public List<OneDayData> toList()
	{
		return this.dailyData;
	}

	public OneDayData getMaxData()
	{
		double maxOpen = Double.MIN_VALUE;
		double maxClose = Double.MIN_VALUE;
		double maxHigh = Double.MIN_VALUE;
		double maxLow = Double.MIN_VALUE;

		for(OneDayData data : this.dailyData)
		{
			if(data.getOpen() > maxOpen)
				maxOpen = data.getOpen();

			if(data.getClose() > maxClose)
				maxClose = data.getClose();

			if(data.getHigh() > maxHigh)
				maxHigh = data.getHigh();

			if(data.getLow() > maxLow)
				maxLow = data.getLow();
		}

		return new OneDayData(maxOpen, maxClose, maxHigh, maxLow);
	}

	public OneDayData getMinData()
	{
		double minOpen = Double.MAX_VALUE;
		double minClose = Double.MAX_VALUE;
		double minHigh = Double.MAX_VALUE;
		double minLow = Double.MAX_VALUE;

		for(OneDayData data : this.dailyData)
		{
			if(data.getOpen() < minOpen)
				minOpen = data.getOpen();

			if(data.getClose() < minClose)
				minClose = data.getClose();

			if(data.getHigh() < minHigh)
				minHigh = data.getHigh();

			if(data.getLow() < minLow)
				minLow = data.getLow();
		}

		return new OneDayData(minOpen, minClose, minHigh, minLow);
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

	public void clear()
	{
		this.dailyData.clear();
	}
}
