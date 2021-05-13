package twitter4jads.impl;

import static twitter4jads.TwitterAdsConstants.PARAM_CURSOR;
import static twitter4jads.TwitterAdsConstants.PARAM_TWEET_TYPE;
import static twitter4jads.TwitterAdsConstants.PATH_TWEETS;
import static twitter4jads.TwitterAdsConstants.PREFIX_ACCOUNTS_URI;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import twitter4jads.BaseAdsListResponse;
import twitter4jads.BaseAdsListResponseIterable;
import twitter4jads.TwitterAdsClient;
import twitter4jads.api.TwitterTweetApi;
import twitter4jads.internal.http.HttpParameter;
import twitter4jads.internal.models4j.TwitterException;
import twitter4jads.models.ads.Tweet;
import twitter4jads.util.TwitterAdUtil;

/**
 * User: abhishekanand Date: 03/08/17 2:17 PM.
 */
public class TwitterTweetsApiImpl implements TwitterTweetApi {

	private final TwitterAdsClient twitterAdsClient;

	public TwitterTweetsApiImpl(TwitterAdsClient twitterAdsClient) {
		this.twitterAdsClient = twitterAdsClient;
	}

	@Override
	public BaseAdsListResponseIterable<Tweet> getAll(String accountId, String tweetType,
			Integer count, String cursor) throws TwitterException {

		TwitterAdUtil.ensureNotNull(accountId, "accountId");
		// TwitterAdUtil.ensureNotNull(userId, "userId");

		final List<HttpParameter> params = new ArrayList<>();

		params.add(new HttpParameter(PARAM_TWEET_TYPE, tweetType));

		if (TwitterAdUtil.isNotNullOrEmpty(cursor)) {
			params.add(new HttpParameter(PARAM_CURSOR, cursor));
		}

		final String baseUrl = twitterAdsClient.getBaseAdsAPIUrl() + PREFIX_ACCOUNTS_URI + accountId
				+ PATH_TWEETS;
		final Type type = new TypeToken<BaseAdsListResponse<Tweet>>() {
		}.getType();
		return twitterAdsClient.executeHttpListRequest(baseUrl, params, type);
	}

}
