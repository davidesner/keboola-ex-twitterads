package twitter4jads.models.ads;

import com.google.gson.annotations.SerializedName;

import twitter4jads.internal.models4j.TweetScope;

/**
 * @author devashish.yadav
 * @date 06/04/18
 */
public class TweetScopeImpl implements TweetScope {

	@SerializedName("tracking_partner")
	private Boolean followers;

	public Boolean getFollowers() {
		return followers;
	}

	public void setFollowers(Boolean followers) {
		this.followers = followers;
	}

	@Override
	public Boolean followers() {
		return followers;
	}

}
