package ch.hearc.android.sucle.model;

public enum SocialType
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
