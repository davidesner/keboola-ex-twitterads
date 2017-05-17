package esnerda.keboola.ex.twitterads.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * @author David Esner
 */
public class TwitterAuthResponseParser {
	private static final String KEY_OAUTH_TOKEN = "oauth_token";
	private static final String KEY_OAUTH_TOKEN_SECRET = "oauth_token_secret";
	
	public static TwitterAuthTokens parseOAuthData(String data) throws URISyntaxException {
		Map<String, String> params = getParMap(URLEncodedUtils.parse(new URI("http://example.org/dummy?" + data), "UTF-8"));
		return new TwitterAuthTokens(params.get(KEY_OAUTH_TOKEN), params.get(KEY_OAUTH_TOKEN_SECRET));		
	}

	private static Map<String, String> getParMap(List<NameValuePair> pars) {
		Map<String, String> res = new HashMap<>();
		for(NameValuePair par : pars) {
			res.put(par.getName(), par.getValue());
		}
		return res;
	}

}
