package esnerda.keboola.ex.twitterads.result.wrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import twitter4jads.models.ads.Campaign;



/**
 * @author David Esner
 */
public class CampaignWrapper {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private Date createTimeInUTC;

	@SerializedName("end_time")
	private Date endTimeInUTC;

	@SerializedName("updated_at")
	private Date updateTimeInUTC;

	@SerializedName("account_id")
	private String accountId;

	@SerializedName("deleted")
	private Boolean deleted;

	@Deprecated //deprecated in v3, kept for backward compatibility	
	@SerializedName("paused")
	private Boolean paused = false;

	@SerializedName("servable")
	private Boolean servable;
	
	@SerializedName("entity_status")
	private String entityStatus;

	@SerializedName("reasons_not_servable")
	private String reasonsNotServable;

	@SerializedName("total_budget_amount_local_micro")
	private Long totalBudgetInMicro;

	@SerializedName("currency")
	private String currency;

	@SerializedName("daily_budget_amount_local_micro")
	private Long dailyBudgetInMicro;

	@SerializedName("funding_instrument_id")
	private String fundingInstrumentId;

	@SerializedName("start_time")
	private Date startTimeInUTC;

	@SerializedName("standard_delivery")
	private Boolean standardDelivery;

	@SerializedName("frequency_cap")
	private Integer frequencyCap;

	@SerializedName("duration_in_days")
	private Integer durationInDays;

	public CampaignWrapper() {
	}

	public CampaignWrapper(Campaign camp) {
		this.id = camp.getId();
		this.name = camp.getName();
		this.accountId = camp.getAccountId();
		this.createTimeInUTC = camp.getCreateTime();
		this.currency = camp.getCurrency();
		this.dailyBudgetInMicro = camp.getDailyBudgetInMicro();
		this.deleted = camp.getDeleted();
		this.durationInDays = camp.getDurationInDays();
		this.endTimeInUTC = camp.getEndTime();
		this.frequencyCap = camp.getFrequencyCap();
		this.fundingInstrumentId = camp.getFundingInstrumentId();		
		this.paused = false;
		this.entityStatus = camp.getEntityStatus();
		if (camp.getReasonsNotServable() !=null) {
		this.reasonsNotServable = String.join(";", camp.getReasonsNotServable());
		}
		this.servable = camp.getServable();
		this.standardDelivery = camp.getStandardDelivery();
		this.startTimeInUTC = camp.getStartTime();
		this.totalBudgetInMicro = camp.getTotalBudgetInMicro();
		this.updateTimeInUTC = camp.getUpdateTime();
	}

	public String getName() {
		return name;
	}

	public Date getCreateTimeInUTC() {
		return createTimeInUTC;
	}

	public Date getEndTimeInUTC() {
		return endTimeInUTC;
	}

	public Date getUpdateTimeInUTC() {
		return updateTimeInUTC;
	}

	public String getAccountId() {
		return accountId;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public Boolean getPaused() {
		return paused;
	}

	public Boolean getServable() {
		return servable;
	}

	public String getReasonsNotServable() {
		return reasonsNotServable;
	}

	public Long getTotalBudgetInMicro() {
		return totalBudgetInMicro;
	}

	public String getCurrency() {
		return currency;
	}

	public Long getDailyBudgetInMicro() {
		return dailyBudgetInMicro;
	}

	public String getFundingInstrumentId() {
		return fundingInstrumentId;
	}

	public Date getStartTimeInUTC() {
		return startTimeInUTC;
	}

	public Boolean getStandardDelivery() {
		return standardDelivery;
	}

	public Integer getFrequencyCap() {
		return frequencyCap;
	}

	public Integer getDurationInDays() {
		return durationInDays;
	}

	public String getEntityStatus() {
		return entityStatus;
	}

	public static class Builder {
		public static List<CampaignWrapper> build(List<Campaign> campaigns) {
			List<CampaignWrapper> result = new ArrayList<>();
			if (campaigns == null) {
				return result;
			}
			campaigns.forEach(t -> result.add(new CampaignWrapper(t)));
			return result;
		}
	}
}
