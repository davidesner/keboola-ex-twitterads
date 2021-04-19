package esnerda.keboola.ex.twitterads.result.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import twitter4jads.models.media.TwitterAccountMediaCreative;

/**
 * @author David Esner
 */
public class MediaCreativeWrapper {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SerializedName("line_item_id")
	private String lineItemId;

	@SerializedName("landing_url")
	private String landingUrl;

	@SerializedName("serving_status")
	private String servingStatus;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("account_media_id")
	private String accountMediaId;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("approval_status")
	private String approvalStatus;

	@SerializedName("deleted")
	private boolean deleted;

	public MediaCreativeWrapper() {
	}

	public MediaCreativeWrapper(TwitterAccountMediaCreative ent, String accountId) {
		this.id = ent.getId();
		// from some point, the API does not return account id
		this.lineItemId = ent.getLineItemId();
		this.landingUrl = ent.getLandingUrl();
		this.servingStatus = ent.getServingStatus();
		this.createdAt = ent.getCreatedAt();
		this.accountMediaId = ent.getAccountMediaId();
		this.updatedAt = ent.getUpdatedAt();
		this.approvalStatus = ent.getApprovalStatus();
		this.deleted = ent.isDeleted();
	}

	public String getLineItemId() {
		return lineItemId;
	}

	public String getLandingUrl() {
		return landingUrl;
	}

	public String getServingStatus() {
		return servingStatus;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getAccountMediaId() {
		return accountMediaId;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public static class Builder {
		public static List<MediaCreativeWrapper> build(List<TwitterAccountMediaCreative> campaigns,
				String accountId) {
			List<MediaCreativeWrapper> result = new ArrayList<>();
			if (campaigns == null) {
				return result;
			}
			campaigns.forEach(t -> result.add(new MediaCreativeWrapper(t, accountId)));
			return result;
		}
	}
}
