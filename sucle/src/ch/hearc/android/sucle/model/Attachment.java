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
	
	public interface ImageViewInfo
	{
		public void onImageLoaded();
	}
	

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

	public void loadImage(ImageView imageView, ImageViewInfo callback)
	{
		int width, height;
		if(imageView.getWidth() != 0 || imageView.getHeight() != 0)
		{
			width = imageView.getWidth();
			height = imageView.getHeight();
		}
		else
			width = height = 350;
		
		if (content == null || ((Bitmap) content).getWidth() < width || ((Bitmap) content).getHeight() < height)
		{
			if(content != null)
				imageView.setImageBitmap((Bitmap) content);
			
			new DownloadImageTask(imageView, this, width, height, callback).execute(filePath);
		}
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
