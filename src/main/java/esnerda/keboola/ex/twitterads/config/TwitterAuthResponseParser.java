package esnerda.keboola.ex.twitterads.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author David Esner
 */
public class TwitterAuthResponseParser {
	private static final String KEY_OAUTH_TOKEN = "oauth_token";
	private static final String KEY_OAUTH_TOKEN_SECRET = "oauth_token_secret";
	
	public static TwitterAuthTokens parseOAuthData(String data) throws Exception {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		
		return mapper.readValue(data, TwitterAuthTokens.class);		
	}

}
