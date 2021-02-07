package stocktest.data;

import yahoofinance.histquotes.HistoricalQuote;

public class OneDayData
{
	private int year;
	private int month;
	private int day;

	private double open;
	private double close;
	private double high;
	private double low;

	public OneDayData()
	{

	}

	public static OneDayData setData(HistoricalQuote data)
	{
		OneDayData newData = new OneDayData();

		newData.setOpen(data.getOpen().doubleValue());
		newData.setClose(data.getClose().doubleValue());
		newData.setHigh(data.getHigh().doubleValue());
		newData.setLow(data.getLow().doubleValue());

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

	public void scaleDown(double fOpen, double fClose, double fHigh, double fLow)
	{
		this.open /= fOpen;
		this.close /= fClose;
		this.high /= fHigh;
		this.low /= fLow;
	}

	public void scaleUp(double fOpen, double fClose, double fHigh, double fLow)
	{
		this.open *= fOpen;
		this.close *= fClose;
		this.high *= fHigh;
		this.low *= fLow;
	}

	/* Setters */

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
