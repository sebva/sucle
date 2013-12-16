package ch.hearc.android.sucle.model;

import java.io.Serializable;

public enum SocialType implements Serializable
{
	Facebook("FB"), GooglePlus("GP"), Undefined("UD");

	private SocialType(final String text)
	{
		this.text = text;
	}

	private final String text;

	@Override
	public String toString()
	{
		return text;
	}
}
