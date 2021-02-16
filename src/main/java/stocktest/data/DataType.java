package stocktest.data;

public enum DataType
{
	HISTORIC_OPEN(true, false),
	HISTORIC_CLOSE(true, false),
	HISTORIC_HIGH(true, false),
	HISTORIC_LOW(true, false),
	PREDICTED_OPEN(false, true),
	PREDICTED_CLOSE(false, true),
	PREDICTED_HIGH(false, true),
	PREDICTED_LOW(false, true);

	private boolean isHistoric;
	private boolean isPrediction;

	DataType(boolean isHistoric, boolean isPrediction)
	{
		this.isHistoric = isHistoric;
		this.isPrediction = isPrediction;
	}

	public boolean isHistoric()
	{
		return this.isHistoric;
	}

	public boolean isPrediction()
	{
		return isPrediction;
	}
}
