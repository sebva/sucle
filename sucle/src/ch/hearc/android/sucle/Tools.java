package ch.hearc.android.sucle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class Tools
{
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth)
		{

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth)
			{
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static String sha512(String base)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++)
			{
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static void generateKeyHash(final String PACKAGE_NAME)
	{
		PackageInfo info;
		try
		{
			info = Sucle.getAppContext().getApplicationContext().getPackageManager().getPackageInfo(PACKAGE_NAME, PackageManager.GET_SIGNATURES);

			for (Signature signature : info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}
}
