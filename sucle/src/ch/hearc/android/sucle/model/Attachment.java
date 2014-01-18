package ch.hearc.android.sucle.model;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.widget.ImageView;
import ch.hearc.android.sucle.DownloadImageTask;

public class Attachment implements Serializable
{
	private transient Object	content;
	private AttachmentType		attachementType;
	private String				filePath;

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

	public void loadImage(ImageView imageView)
	{
		if (content == null)
			new DownloadImageTask(imageView, this, imageView.getWidth(), imageView.getHeight()).execute(filePath);
		else
			imageView.setImageBitmap((Bitmap) content);
	}

	public String getFilePath()
	{
		return filePath;
	}

	public AttachmentType getAttachementType()
	{
		return attachementType;
	}

	@Override
	public String toString()
	{
		return attachementType + " " + filePath;
	}

	public void setContent(Bitmap result)
	{
		this.content = result;
	}
}
