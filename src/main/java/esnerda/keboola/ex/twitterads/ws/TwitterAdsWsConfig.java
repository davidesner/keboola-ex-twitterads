package esnerda.keboola.ex.twitterads.ws;
/**
 * @author David Esner
 */
public class TwitterAdsWsConfig {
	private final String consumerKey;
	private final String consumerSecret;
	private final String accessToken;
	private final String accessTokenSecret;

	public TwitterAdsWsConfig(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		super();
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;		
	}

	public String getConsumerKey() {
		return consumerKey;
	}
	public String getConsumerSecret() {
		return consumerSecret;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	
	

}
