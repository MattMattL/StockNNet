package stocktest.data;

import yahoofinance.histquotes.HistoricalQuote;

import java.text.SimpleDateFormat;

public class OneDayData
{
	private int year;
	private int month;
	private int day;
	private String date;

	private double open;
	private double close;
	private double high;
	private double low;

	public OneDayData()
	{

	}

	public static OneDayData setData(HistoricalQuote dataIn)
	{
		OneDayData newData = new OneDayData();

		SimpleDateFormat formatted = new SimpleDateFormat("yyyy.MM.dd");
		newData.setDate(formatted.format(dataIn.getDate().getTime()));

		newData.setOpen(dataIn.getOpen().doubleValue());
		newData.setClose(dataIn.getClose().doubleValue());
		newData.setHigh(dataIn.getHigh().doubleValue());
		newData.setLow(dataIn.getLow().doubleValue());

		return newData;
	}

	public OneDayData shallowCopy()
	{
		OneDayData newData = new OneDayData();

		newData.setOpen(this.getOpen());
		newData.setClose(this.getClose());
		newData.setHigh(this.getHigh());
		newData.setLow(this.getLow());

		return newData;
	}

	public void scaleDown(OneDayData unitData)
	{
		this.open /= unitData.getOpen();
		this.close /= unitData.getClose();
		this.high /= unitData.getHigh();
		this.low /= unitData.getLow();
	}

	public void scaleUp(OneDayData unitData)
	{
		this.open *= unitData.getOpen();
		this.close *= unitData.getClose();
		this.high *= unitData.getHigh();
		this.low *= unitData.getLow();
	}

	public void print()
	{
		System.out.printf("Open  = %7.2f\n", this.open);
		System.out.printf("Close = %7.2f\n", this.close);
		System.out.printf("High  = %7.2f\n", this.high);
		System.out.printf("Low   = %7.2f\n", this.low);
	}

	/* Setters */

	public void setDate(String date)
	{
		this.date = date;
	}

	public void setOpen(double value)
	{
		this.open = value;
	}

	public void setClose(double value)
	{
		this.close = value;
	}

	public void setHigh(double value)
	{
		this.high = value;
	}

	public void setLow(double value)
	{
		this.low = value;
	}

	/* Getters */

	public String getDate()
	{
		return date;
	}

	public double getOpen()
	{
		return this.open;
	}

	public double getClose()
	{
		return this.close;
	}

	public double getHigh()
	{
		return this.high;
	}

	public double getLow()
	{
		return this.low;
	}
}
