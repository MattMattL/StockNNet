package stocktest.graphics;

import stocktest.companies.CompanyBase;
import stocktest.data.DataType;

import javax.swing.*;
import java.awt.*;

public class StockGraphics
{
	private final CompanyBase company;

	private final int FRAME_HOR = 1200;
	private final int FRAME_VER = 700;

	public StockGraphics(CompanyBase company)
	{
		this.company = company;
	}

	public void createFrame()
	{
		// Frame
		JFrame frame = new JFrame();

		frame.setTitle("[Test] Graphics#drawGUI");

		frame.setSize(this.FRAME_HOR, this.FRAME_VER);
		frame.setResizable(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.BLACK);

		// add components
		this.addGraphPanels(frame);

		// final settings
		frame.setLayout(null);
		frame.setVisible(true);
	}

	public JFrame addGraphPanels(JFrame frame)
	{
		int width = 600;
		int height = 300;
		int margin = 5;

		int panelPos[][][] = new int[2][2][2];

		for(int i=0; i<panelPos.length; i++)
		{
			for(int j=0; j<panelPos[0].length; j++)
			{
				panelPos[i][j][0] = (i + 1) * margin + i * width;
				panelPos[i][j][1] =	(j + 1) * margin + j * height;
			}
		}

		GraphPanel graphOpen = new GraphPanel(this.company, panelPos[0][0][0], panelPos[0][0][1], width, height);
		GraphPanel graphClose = new GraphPanel(this.company, panelPos[0][1][0], panelPos[0][1][1], width, height);
		GraphPanel graphHigh = new GraphPanel(this.company, panelPos[1][0][0], panelPos[1][0][1], width, height);
		GraphPanel graphLow = new GraphPanel(this.company, panelPos[1][1][0], panelPos[1][1][1], width, height);

//		graphOpen.include(DataType.HISTORIC_OPEN, DataType.PREDICTED_OPEN, DataType.HISTORIC_CLOSE, DataType.PREDICTED_CLOSE);
//		graphClose.include(DataType.HISTORIC_CLOSE, DataType.PREDICTED_CLOSE);
//		graphHigh.include(DataType.HISTORIC_HIGH, DataType.PREDICTED_HIGH);
//		graphLow.include(DataType.HISTORIC_LOW, DataType.PREDICTED_LOW);

		graphOpen.include(DataType.HISTORIC_OPEN, DataType.PREDICTED_OPEN, DataType.HISTORIC_CLOSE, DataType.PREDICTED_CLOSE);
		graphClose.include(DataType.HISTORIC_CLOSE, DataType.PREDICTED_CLOSE);
		graphHigh.include(DataType.HISTORIC_HIGH, DataType.PREDICTED_HIGH, DataType.HISTORIC_LOW, DataType.PREDICTED_LOW);
		graphLow.include(DataType.HISTORIC_LOW, DataType.PREDICTED_LOW);

		frame.getContentPane().add(graphOpen);
		frame.getContentPane().add(graphClose);
		frame.getContentPane().add(graphHigh);
		frame.getContentPane().add(graphLow);

		return frame;
	}
}
