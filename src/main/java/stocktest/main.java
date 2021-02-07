package stocktest;

import stocktest.companies.Apple;
import stocktest.companies.CompanyBase;
import stocktest.neuralnet.NNetTest;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class Main
{
	public static void main(String[] args) throws IOException
	{
//		NNetTest nnet = new NNetTest();
//		nnet.test();

		CompanyBase apple = new CompanyBase("AAPL");
		CompanyBase google = new CompanyBase("GOOG");

//		apple.trainNNet();
		google.trainNNet();
	}

	/*
	for(int i=0; i<history.size(); i++)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		String formatted = format.format(singleDay.getDate().getTime());
		System.out.printf("[%s]  ", formatted);

		System.out.printf("%6.2f -> ", singleDay.getOpen().doubleValue());
		System.out.printf("%6.2f", singleDay.getClose().doubleValue());

		System.out.printf("  (%6.2f : ", singleDay.getLow().doubleValue());
		System.out.printf("%6.2f)\n", singleDay.getHigh().doubleValue());
	}
	*/
}
