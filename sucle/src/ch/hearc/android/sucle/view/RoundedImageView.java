package ch.hearc.android.sucle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView
{
	private int	borderColor	= Color.TRANSPARENT;
	private int	borderWidth	= 0;

	/* http://stackoverflow.com/a/16208548/2648956 */

	public RoundedImageView(Context context)
	{
		super(context);
	}

	public RoundedImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		Drawable drawable = getDrawable();

		if (drawable == null) return;
		if (getWidth() == 0 || getHeight() == 0) return;

		if (!(drawable instanceof BitmapDrawable)) return;
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		if (b == null) return;
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
		Bitmap roundBitmap = getCroppedBitmap(bitmap, (int) (getWidth() - 2 * borderWidth), borderColor, borderWidth);
		canvas.drawBitmap(roundBitmap, 0, 0, null);
	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius, int borderColor, int borderWidht)
	{
		Bitmap sbmp;
		int largeRadius = radius + (int) Math.ceil(borderWidht * 2);

		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
		{
			float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
			float factor = smallest / largeRadius;
			sbmp = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() / factor), (int) (bmp.getHeight() / factor), false);
		}
		else
		{
			sbmp = bmp;
		}
		Bitmap output = Bitmap.createBitmap(largeRadius, largeRadius, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(borderWidht, borderWidht, largeRadius, largeRadius);

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.BLACK);
		canvas.drawCircle(largeRadius / 2, largeRadius / 2, radius / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
		paint.setXfermode(null);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(borderWidht);
		paint.setColor(borderColor);
		canvas.drawCircle(largeRadius / 2, largeRadius / 2, radius / 2, paint);

		return output;
	}

	public int getBorderColor()
	{
		return borderColor;
	}

	public void setBorderColor(int borderColor)
	{
		this.borderColor = borderColor;
		invalidate();
	}

	public int getBorderWidth()
	{
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth)
	{
		this.borderWidth = borderWidth;
		invalidate();
	}
}
