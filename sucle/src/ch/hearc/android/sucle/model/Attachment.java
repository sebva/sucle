package ch.hearc.android.sucle.model;

public class Attachment
{
	private Object			content;
	private AttachmentType	attachementType;
	private String			filePath;

	public Attachment(Object content, AttachmentType attachementType, String filePath)
	{
		this.content = content;
		this.attachementType = attachementType;
		this.filePath = filePath;
	}

	public Object getContent()
	{
		return content;
	}

	public AttachmentType getAttachementType()
	{
		return attachementType;
	}

}
