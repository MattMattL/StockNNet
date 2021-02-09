package stocktest;

import stocktest.debug.TestClass;

import java.io.IOException;

public class StockTest
{
	public static void main(String[] args) throws IOException
	{
		runTest();
	}

	public static void runTest() throws IOException
	{
		TestClass.testCode();
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
