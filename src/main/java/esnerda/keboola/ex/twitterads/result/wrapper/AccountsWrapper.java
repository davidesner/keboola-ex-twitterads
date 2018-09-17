package esnerda.keboola.ex.twitterads.result.wrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import twitter4jads.models.ads.AdAccount;



/**
 * @author David Esner
 */
public class AccountsWrapper {

	private String id;

	public String getId() {
		return id;
	}

	public AccountsWrapper() {
	}

	public AccountsWrapper(AdAccount acc) {
		this.name = acc.getName();
		this.createdAt = acc.getCreatedAt();
		this.updatedAt = acc.getUpdatedAt();
		this.deleted = acc.getDeleted();
		this.currency = acc.getCurrency();
		this.timezone = acc.getTimezone();
		this.timezoneSwitchAt = acc.getTimezoneSwitchAt();
		this.salt = acc.getSalt();
		this.approvalStatus = acc.getApprovalStatus();
		this.id = acc.getId();
	}

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private Date createdAt;

	@SerializedName("updated_at")
	private Date updatedAt;

	@SerializedName("deleted")
	private Boolean deleted;

	@SerializedName("currency")
	private String currency;

	@SerializedName("timezone")
	private String timezone;

	@SerializedName("timezone_switch_at")
	private Date timezoneSwitchAt;

	@SerializedName("salt")
	private String salt;

	@SerializedName("approval_status")
	private String approvalStatus;

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Date getTimezoneSwitchAt() {
		return timezoneSwitchAt;
	}

	public void setTimezoneSwitchAt(Date timezoneSwitchAt) {
		this.timezoneSwitchAt = timezoneSwitchAt;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public static class Builder {
		public static List<AccountsWrapper> build(List<AdAccount> accounts) {
			List<AccountsWrapper> result = new ArrayList<>();
			if (accounts == null) {
				return result;
			}
			accounts.forEach(t -> result.add(new AccountsWrapper(t)));
			return result;
		}
	}
}
