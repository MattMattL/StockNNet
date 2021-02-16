package stocktest.graphics;

import stocktest.companies.CompanyBase;
import stocktest.data.DataType;

import javax.swing.*;
import java.awt.*;

public class StockGraphics
{
	private final CompanyBase company;

	private final int FRAME_HOR = 900;
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
		int width = 500;
		int height = 300;
		int margin = 5;

		int panelX[] = {margin, width + 2*margin, margin, width + 2*margin};
		int panelY[] = {margin, margin, height + 2*margin, height + 2*margin};

		GraphPanel graphOpen = new GraphPanel(this.company, panelX[0], panelY[0], width, height);
		GraphPanel graphClose = new GraphPanel(this.company, panelX[1], panelY[1], width, height);
		GraphPanel graphHigh = new GraphPanel(this.company, panelX[2], panelY[2], width, height);
		GraphPanel graphLow = new GraphPanel(this.company, panelX[3], panelY[3], width, height);

		graphOpen.include(DataType.HISTORIC_OPEN, DataType.PREDICTED_OPEN);
		graphClose.include(DataType.HISTORIC_CLOSE, DataType.PREDICTED_CLOSE);
		graphHigh.include(DataType.HISTORIC_HIGH, DataType.PREDICTED_HIGH);
		graphLow.include(DataType.HISTORIC_LOW, DataType.PREDICTED_LOW);

		frame.getContentPane().add(graphOpen);
		frame.getContentPane().add(graphClose);
		frame.getContentPane().add(graphHigh);
		frame.getContentPane().add(graphLow);

		return frame;
	}
}
