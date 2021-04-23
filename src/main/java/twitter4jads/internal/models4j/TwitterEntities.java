package twitter4jads.internal.models4j;

import com.google.gson.annotations.SerializedName;

import twitter4jads.internal.json.HashtagEntityJSONImpl;
import twitter4jads.internal.json.URLEntityJSONImpl;
import twitter4jads.internal.json.UserMentionEntityJSONImpl;

/**
 * @author David Esner
 */
public class TwitterEntities implements EntitySupport {

	@SerializedName("user_mentions")
	private UserMentionEntityJSONImpl[] userMentionEntities;

	@SerializedName("hashtags")
	private HashtagEntityJSONImpl[] hashtagEntities;

	@SerializedName("urls")
	private URLEntityJSONImpl[] urlEntities;

	@Override
	public UserMentionEntity[] getUserMentionEntities() {
		return this.userMentionEntities;
	}

	@Override
	public URLEntity[] getURLEntities() {
		return this.urlEntities;
	}

	@Override
	public HashtagEntity[] getHashtagEntities() {
		return new HashtagEntity[0];
	}

	@Override
	public MediaEntity[] getMediaEntities() {
		return new MediaEntity[0];
	}

	@Override
	public MediaEntity[] getExtendedMediaEntities() {
		return new MediaEntity[0];
	}

}
