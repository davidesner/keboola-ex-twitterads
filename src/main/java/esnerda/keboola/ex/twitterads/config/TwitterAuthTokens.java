package esnerda.keboola.ex.twitterads.config;

/**
 * @author David Esner
 */
public class TwitterAuthTokens {

	private final String oAuthToken;
	private final String oAuthTokenSecret;

	public TwitterAuthTokens(String oAuthToken, String oAuthTokenSecret) {
		super();
		this.oAuthToken = oAuthToken;
		this.oAuthTokenSecret = oAuthTokenSecret;
	}

	public String getoAuthToken() {
		return oAuthToken;
	}

	public String getoAuthTokenSecret() {
		return oAuthTokenSecret;
	}

}
