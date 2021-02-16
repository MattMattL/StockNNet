package stocktest.graphics;

import stocktest.companies.CompanyBase;
import stocktest.data.DailyData;
import stocktest.data.DataType;
import stocktest.data.OneDayData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel
{
	private int panelX;
	private int panelY;
	private int panelWidth;
	private int panelHeight;

	private final CompanyBase company;
	private List<Double> data = new ArrayList<>();
	private List<DataType> graphRequests = new ArrayList<>();

	private OneDayData maxSample;
	private OneDayData minSample;
	private OneDayData maxResult;
	private OneDayData minResult;

	private int xOffset;
	private int deltaX;
	private double yScalar;

	public GraphPanel(CompanyBase company, int x, int y, int width, int height)
	{
		this.company = company;
		this.panelX = x;
		this.panelY = y;
		this.panelWidth = width;
		this.panelHeight = height;

		this.setBounds(x, y, width, height);
		this.setBackground(Color.DARK_GRAY);
	}

	public void include(DataType... options)
	{
		for(DataType type : options)
			this.graphRequests.add(type);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		this.preInit(g);
		this.init(g);
		this.parseData(g);
	}

	private void preInit(Graphics g)
	{
		DailyData samples = this.company.getTrainingSamples();
		DailyData results = this.company.getTrainingResult();

		this.maxSample = samples.getMaxData();
		this.minSample = samples.getMinData();
		this.maxResult = samples.getMaxData();
		this.minResult = samples.getMinData();

		this.xOffset = this.panelHeight - 10;
		this.deltaX = this.panelWidth / samples.size();
		this.yScalar = 0.8 * this.panelHeight / (this.maxSample.getMax() - this.minSample.getMin());
	}

	private void init(Graphics g)
	{
		// draw vertical lines
		g.setColor(Color.gray);

		for(int i=0; i<this.data.size(); i++)
		{
			g.drawLine(i * deltaX, 0, i * deltaX, 450);
		}
	}

	private void parseData(Graphics g)
	{
		for(DataType type : this.graphRequests)
		{
			this.data.clear();

			if(type.isHistoric())
			{
				for(OneDayData oneDayData : this.company.getTrainingSamples().toList())
				{
					switch(type)
					{
						case HISTORIC_OPEN:
							this.data.add(oneDayData.getOpen()); break;
						case HISTORIC_CLOSE:
							this.data.add(oneDayData.getClose()); break;
						case HISTORIC_HIGH:
							this.data.add(oneDayData.getHigh()); break;
						case HISTORIC_LOW:
							this.data.add(oneDayData.getLow()); break;
						default:
							break;
					}
				}

				drawGraph(g, Color.green);
			}
			else
			{
				for(OneDayData oneDayData : this.company.getTrainingResult().toList())
				{
					switch(type)
					{
						case PREDICTED_OPEN:
							this.data.add(oneDayData.getOpen()); break;
						case PREDICTED_CLOSE:
							this.data.add(oneDayData.getClose()); break;
						case PREDICTED_HIGH:
							this.data.add(oneDayData.getHigh()); break;
						case PREDICTED_LOW:
							this.data.add(oneDayData.getLow()); break;
						default:
							break;
					}
				}

				drawGraph(g, Color.lightGray);
			}
		}
	}

	private void drawGraph(Graphics g, Color lineColor)
	{


		// draw historic data
		g.setColor(lineColor);

		for(int i=0; i<this.data.size() - 1; i++)
		{
			int x0 = i * this.deltaX;
			int x1 = (i + 1) * this.deltaX;
			int y0 = this.xOffset - (int)(this.yScalar * (this.data.get(i) - this.minResult.getMin()));
			int y1 = this.xOffset - (int)(this.yScalar * (this.data.get(i + 1) - this.minResult.getMin()));

			g.drawLine(x0, y0, x1, y1);
		}
	}
}
