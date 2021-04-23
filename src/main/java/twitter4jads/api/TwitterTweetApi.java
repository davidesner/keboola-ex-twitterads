package twitter4jads.api;

import twitter4jads.BaseAdsListResponseIterable;
import twitter4jads.internal.models4j.TwitterException;
import twitter4jads.models.ads.Tweet;

/**
 * User: abhishekanand
 * Date: 02/08/17 8:26 PM.
 */
public interface TwitterTweetApi {

    /**
     * @param accountId The identifier for the leveraged account.
     * @param tweetType The Tweet type for the specified tweet_ids. Possible values: DRAFT, PUBLISHED, SCHEDULED.
     * @param count Specifies the number of Scheduled Promoted Tweets to try to retrieve, up to a maximum of 1000 per distinct request.
     * @param cursor Specifies a cursor to get the next page of Scheduled Promoted Tweets.
     */

    BaseAdsListResponseIterable<Tweet> getAll(String accountId, String tweetType, Integer count, String cursor)
            throws TwitterException;


}
