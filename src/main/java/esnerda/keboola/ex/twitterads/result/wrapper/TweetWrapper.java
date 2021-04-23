package esnerda.keboola.ex.twitterads.result.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

import twitter4jads.internal.json.PlaceJSONImpl;
import twitter4jads.internal.models4j.GeoLocation;
import twitter4jads.models.ads.Tweet;

/**
 * @author David Esner
 */
public class TweetWrapper {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SerializedName("account_id")
	private String accountId;
	@SerializedName("tweet_id")
	private String tweetId;

	@SerializedName("user_id")
	private Long user_id;

	@SerializedName("user_name")
	private String user_name;

	@SerializedName("id_str")
	private String idStr;

	@SerializedName("in_reply_to_user_id_str")
	private String inReplyToUserIdStr;

	@SerializedName("in_reply_to_screen_name")
	private String inReplyToScreenName;

	@SerializedName("contributors")
	private List<Long> contributors;

	@SerializedName("text")
	private String text;

	@SerializedName("full_text")
	private String fullText;

	@SerializedName("tweet_type")
	private String tweetType;

	@SerializedName("source")
	private String source;

	@SerializedName("card_uri")
	private String cardUri;

	@SerializedName("truncated")
	private boolean truncated;

	@SerializedName("lang")
	private String language;

	@SerializedName("conversation_settings")
	private String conversationSettings;

	@SerializedName("favourited")
	private boolean favourited;

	@SerializedName("nullcast")
	private Boolean nullcast;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("scheduled_at")
	private String scheduledAt;

	@SerializedName("place")
	private PlaceJSONImpl place;

	@SerializedName("in_reply_to_status_id")
	private Long inReplyToStatusId;

	@SerializedName("retweet_count")
	private Integer retweetCount;

	@SerializedName("favorite_count")
	private Integer favoriteCount;

	@SerializedName("in_reply_to_user_id")
	private Long inReplyToUserId;

	@SerializedName("scope_followers")
	private Boolean scopeFollowers;

	@SerializedName("geo")
	private GeoLocation geo;

	@SerializedName("in_reply_to_status_id_str")
	private String inReplyToStatusIdStr;

	@SerializedName("display_text_range")
	private String displayTextRange;

	@SerializedName("retweeted")
	private Boolean retweeted;

	@SerializedName("user_mentions")
	private String userMentions;

	@SerializedName("hash_tags")
	private String hashTags;

	private String urls;

	public TweetWrapper() {
	}

	public TweetWrapper(Tweet obj, String accountId) {
		super();
		this.id = obj.getId();
		this.tweetId = obj.getTweetId();
		this.user_id = obj.getUser().getId();
		this.user_name = obj.getUser().getName();
		this.idStr = obj.getIdStr();
		this.inReplyToUserIdStr = obj.getInReplyToUserIdStr();
		this.inReplyToScreenName = obj.getInReplyToScreenName();
		this.contributors = obj.getContributors();
		this.text = obj.getText();
		this.fullText = obj.getFullText();
		this.tweetType = obj.getTweetType();
		this.source = obj.getSource();
		this.cardUri = obj.getCardUri();
		this.truncated = obj.isTruncated();
		this.language = obj.getLanguage();
		this.conversationSettings = obj.getConversationSettings();
		this.favourited = obj.isFavourited();
		this.nullcast = obj.getNullcast();
		this.createdAt = obj.getCreatedAt();
		this.scheduledAt = obj.getScheduledAt();
		this.place = obj.getPlace();
		this.inReplyToStatusId = obj.getInReplyToStatusId();
		this.retweetCount = obj.getRetweetCount();
		this.favoriteCount = obj.getFavoriteCount();
		this.inReplyToUserId = obj.getInReplyToUserId();
		this.scopeFollowers = obj.getScopes().followers();
		this.geo = obj.getGeo();
		this.userMentions = obj.getEntities().getUserMentionsAsString();
		this.hashTags = obj.getEntities().getHashtagAsString();
		this.urls = obj.getEntities().getUrlsAsString();
		this.inReplyToStatusIdStr = obj.getInReplyToStatusIdStr();
		this.displayTextRange = Arrays.asList(obj.getDisplayTextRange()).stream()
				.collect(Collectors.joining(","));
		this.retweeted = obj.getRetweeted();
	}

	public static class Builder {
		public static List<TweetWrapper> build(List<Tweet> campaigns, String accountId) {
			List<TweetWrapper> result = new ArrayList<>();
			if (campaigns == null) {
				return result;
			}
			campaigns.forEach(t -> result.add(new TweetWrapper(t, accountId)));
			return result;
		}
	}
}
