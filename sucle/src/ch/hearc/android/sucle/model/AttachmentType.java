package ch.hearc.android.sucle.model;

public enum AttachmentType
{
	Picture(0, "image/jpeg"), Video(1, "video/mpeg"), Sound(2, "audio/mpeg");

	private String	mimeType;
	private int		value;

	private AttachmentType(int value, String mimeType)
	{
		this.value = value;
		this.mimeType = mimeType;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public int getValue()
	{
		return value;
	}
}
