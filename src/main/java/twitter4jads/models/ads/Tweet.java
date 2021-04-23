package twitter4jads.models.ads;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import twitter4jads.internal.json.PlaceJSONImpl;
import twitter4jads.internal.json.UserJSONImpl;
import twitter4jads.internal.models4j.GeoLocation;
import twitter4jads.internal.models4j.TwitterEntities;

/**
 * User: prashant Date: 19/05/16. Time: 9:54 PM
 */
public class Tweet extends TwitterEntity  implements Serializable{

	@SerializedName("tweet_id")
	private String tweetId;

	@SerializedName("user")
	private UserJSONImpl user;

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
	private Date createdAt;

	@SerializedName("scheduled_at")
	private Date scheduledAt;

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

	@SerializedName("scopes")
	private TweetScopeImpl scopes;

	@SerializedName("geo")
	private GeoLocation geo;

	@SerializedName("entities")
	private TwitterEntities entities;

	@SerializedName("in_reply_to_status_id_str")
	private String inReplyToStatusIdStr;

	@SerializedName("display_text_range")
	private String[] displayTextRange;

	@SerializedName("retweeted")
	private Boolean retweeted;

	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	public UserJSONImpl getUser() {
		return user;
	}

	public void setUser(UserJSONImpl user) {
		this.user = user;
	}

	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}

	public String getInReplyToUserIdStr() {
		return inReplyToUserIdStr;
	}

	public void setInReplyToUserIdStr(String inReplyToUserIdStr) {
		this.inReplyToUserIdStr = inReplyToUserIdStr;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public List<Long> getContributors() {
		return contributors;
	}

	public void setContributors(List<Long> contributors) {
		this.contributors = contributors;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public String getTweetType() {
		return tweetType;
	}

	public void setTweetType(String tweetType) {
		this.tweetType = tweetType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getConversationSettings() {
		return conversationSettings;
	}

	public void setConversationSettings(String conversationSettings) {
		this.conversationSettings = conversationSettings;
	}

	public boolean isFavourited() {
		return favourited;
	}

	public void setFavourited(boolean favourited) {
		this.favourited = favourited;
	}

	public Boolean getNullcast() {
		return nullcast;
	}

	public void setNullcast(Boolean nullcast) {
		this.nullcast = nullcast;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getScheduledAt() {
		return scheduledAt;
	}

	public void setScheduledAt(Date scheduledAt) {
		this.scheduledAt = scheduledAt;
	}

	public PlaceJSONImpl getPlace() {
		return place;
	}

	public void setPlace(PlaceJSONImpl place) {
		this.place = place;
	}

	public Long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(Long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public Integer getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(Integer retweetCount) {
		this.retweetCount = retweetCount;
	}

	public Integer getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(Integer favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public Long getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(Long inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	public TweetScopeImpl getScopes() {
		return scopes;
	}

	public void setScopes(TweetScopeImpl scopes) {
		this.scopes = scopes;
	}

	public GeoLocation getGeo() {
		return geo;
	}

	public void setGeo(GeoLocation geo) {
		this.geo = geo;
	}

	public TwitterEntities getEntities() {
		return entities;
	}

	public void setEntities(TwitterEntities entities) {
		this.entities = entities;
	}

	public String getInReplyToStatusIdStr() {
		return inReplyToStatusIdStr;
	}

	public void setInReplyToStatusIdStr(String inReplyToStatusIdStr) {
		this.inReplyToStatusIdStr = inReplyToStatusIdStr;
	}

	public String[] getDisplayTextRange() {
		return displayTextRange;
	}

	public void setDisplayTextRange(String[] displayTextRange) {
		this.displayTextRange = displayTextRange;
	}

	public Boolean getRetweeted() {
		return retweeted;
	}

	public void setRetweeted(Boolean retweeted) {
		this.retweeted = retweeted;
	}

}
