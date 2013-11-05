package ch.hearc.android.sucle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity
{
	private boolean					isResumed					= false;
	private static final int		SOCIAL_CONNECTION_FRAGMENT	= 0;
	private static final int		POSTS_FRAGMENT				= 1;
	private static final int		FRAGMENT_COUNT				= POSTS_FRAGMENT + 1;

	private Fragment[]				fragments					= new Fragment[FRAGMENT_COUNT];

	private UiLifecycleHelper		uiHelper;
	private Session.StatusCallback	callback					= new Session.StatusCallback() {
																	@Override
																	public void call(Session session, SessionState state, Exception exception)
																	{
																		onSessionStateChange(session, state, exception);
																	}
																};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		Log.d(LOG, "Before layout");
		setContentView(R.layout.main_activity);
		Log.d(LOG, "After layout");

		FragmentManager fm = getFragmentManager();
		fragments[SOCIAL_CONNECTION_FRAGMENT] = fm.findFragmentById(R.id.socialConnectionFragment);
		fragments[POSTS_FRAGMENT] = fm.findFragmentById(R.id.postListFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++)
		{
			transaction.hide(fragments[i]);
		}
		transaction.commit();
	}

	private void showFragment(int fragmentIndex, boolean addToBackStack)
	{
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++)
		{
			if (i == fragmentIndex)
			{
				transaction.show(fragments[i]);
			}
			else
			{
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack)
		{
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
		// Only make changes if the activity is visible
		if (isResumed)
		{
			FragmentManager manager = getFragmentManager();
			// Get the number of entries in the back stack
			int backStackSize = manager.getBackStackEntryCount();
			// Clear the back stack
			for (int i = 0; i < backStackSize; i++)
			{
				manager.popBackStack();
			}
			if (state.isOpened())
			{
				// If the session state is open:
				// Show the authenticated fragment
				showFragment(POSTS_FRAGMENT, false);
				Log.d(FACEBOOK_LOG, "Access Token" + session.getAccessToken());
				Request.newMeRequest(session, new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response)
					{
						if (user != null)
						{
							Log.d(FACEBOOK_LOG, "User ID " + user.getId());
							Log.d(FACEBOOK_LOG, "User Name " + user.getFirstName() + " " + user.getLastName());
							Toast.makeText(MainActivity.this, "Welcome : " + user.getFirstName() + " " + user.getLastName() + "\nYour User ID IS : " + user.getId(), Toast.LENGTH_SHORT).show();
						}
					}
				}).executeAsync();
			}
			else if (state.isClosed())
			{
				// If the session state is closed:
				// Show the login fragment
				showFragment(SOCIAL_CONNECTION_FRAGMENT, false);
			}
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		Session session = Session.getActiveSession();

		if (session != null && session.isOpened())
		{
			// if the session is already open,
			// try to show the selection fragment
			showFragment(POSTS_FRAGMENT, false);
		}
		else
		{
			// otherwise present the splash screen
			// and ask the person to login.
			showFragment(SOCIAL_CONNECTION_FRAGMENT, false);
		}

		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	public void generateKeyHash()
	{
		PackageInfo info;
		try
		{
			info = getPackageManager().getPackageInfo(PACKAGE_NAME, PackageManager.GET_SIGNATURES);

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

	private static final String	PACKAGE_NAME	= "ch.hearc.android.sucle";

	private static final String	FACEBOOK_LOG	= "Log : Facebook";
	private static final String	LOG				= "Log : " + MainActivity.class.getSimpleName();
}
