package ch.hearc.android.sucle;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class Sucle extends Application
{
	private static Context context;

	public void onCreate()
	{
		super.onCreate();
		Log.i("TEST", "TEST");
		Sucle.context = getApplicationContext();
	}

	public static Context getAppContext()
	{
		return Sucle.context;
	}
}
