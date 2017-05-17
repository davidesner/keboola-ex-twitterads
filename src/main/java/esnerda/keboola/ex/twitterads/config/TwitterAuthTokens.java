package esnerda.keboola.ex.twitterads.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author David Esner
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterAuthTokens {
	
	@JsonProperty("oauth_token")
	private String oAuthToken;
	@JsonProperty("oauth_token_secret")
	private String oAuthTokenSecret;

	
	public TwitterAuthTokens() {
	}

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
