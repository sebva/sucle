package ch.hearc.android.sucle.view;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.R.id;
import ch.hearc.android.sucle.R.layout;

import com.google.android.gms.common.SignInButton;

public class SocialConnectionFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.social_connection_fragment, container, false);
		SignInButton googleButton = (SignInButton) view.findViewById(R.id.google_sign_in_button);
		googleButton.setSize(SignInButton.SIZE_WIDE);
		
		googleButton.setOnClickListener((OnClickListener)getActivity());
		
		return view;
	}

}
