package twitter4jads.internal.models4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	
	public String getUserMentionsAsString() {
		return this.convertArrayToString(this.userMentionEntities);
	}
	
	public String getHashtagAsString() {
		return this.convertArrayToString(this.hashtagEntities);
	}
	
	public String getUrlsAsString() {
		return this.convertArrayToString(this.urlEntities);
	}
	
	private String convertArrayToString(Object[] array) {
		String result = "[";
		List<String> names = Arrays.asList(array).stream().map(o -> o.toString()).collect(Collectors.toList());
		result += names.stream().collect(Collectors.joining(","));
		result += "]";
		return result;
		
	}

}
