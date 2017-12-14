package esnerda.keboola.ex.twitterads.result.wrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

import twitter4j.models.ads.LineItem;
import twitter4j.models.ads.Placement;

/**
 * @author David Esner
 */
public class LineItemWrapper {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LineItemWrapper(LineItem li) {
		this.id = li.getId();
		this.name = li.getName();
		this.accountId = li.getAccountId();
		this.currency = li.getCurrency();
		this.deleted = li.getDeleted();
		this.paused = li.getPaused();
		this.advertiserDomain = li.getAdvertiserDomain();
		this.advertiserUserId = li.getAdvertiserUserId();
		this.automaticallySelectBid = li.isAutomaticallySelectBid();
		this.bidAmtInMicro = li.getBidAmtInMicro();
		this.bidType = li.getBidType().name();
		this.bidUnit = li.getBidUnit();
		this.campaignId = li.getCampaignId();
		if (li.getCategories() != null) {
			this.categories = String.join(";", li.getCategories());
		}
		this.chargeBy = li.getChargeBy();
		this.createdAt = li.getCreatedAt();
		this.creativeSource = li.getCreativeSource();
		this.goalSettings = li.getGoalSettings();
		this.matchRelevantPopularQueries = li.getMatchRelevantPopularQueries();
		this.objective = li.getObjective();
		this.optimization = li.getOptimization();
		if (this.placements != null) {
			this.placements = joinPlacements(li.getPlacements());
		}
		if (li.getProductType() != null) {
			this.productType = li.getProductType().name();
		}
		if (li.getSentiment() != null) {
			this.sentiment = li.getSentiment().name();
		}
		this.webEventTag = li.getWebEventTag();
		this.updatedAt = li.getUpdatedAt();

	}

	private String joinPlacements(List<Placement> pl) {
		return String.join(";", pl.stream().map(p -> p.name()).collect(Collectors.toList()));
	}

	@SerializedName("account_id")
	private String accountId;

	@SerializedName("name")
	private String name;

	@SerializedName("bid_amount_local_micro")
	private Long bidAmtInMicro;

	@SerializedName("campaign_id")
	private String campaignId;

	@SerializedName("created_at")
	private Date createdAt;

	@SerializedName("currency")
	private String currency;

	@SerializedName("goal_settings")
	private String goalSettings;

	@SerializedName("match_relevant_popular_queries")
	private Boolean matchRelevantPopularQueries;

	@SerializedName("objective")
	private String objective;

	@SerializedName("deleted")
	private Boolean deleted;

	@SerializedName("placements")
	private String placements;

	@SerializedName("product_type")
	private String productType;

	@SerializedName("include_sentiment")
	private String sentiment;

	@SerializedName("paused")
	private Boolean paused;

	@SerializedName("primary_web_event_tag")
	private String webEventTag;

	@SerializedName("suggested_high_cpe_bid_local_micro")
	private Long suggestedHighCpeBidInMicro;

	@SerializedName("suggested_low_cpe_bid_local_micro")
	private Long suggestedLowCpeBidInMicro;

	@SerializedName("updated_at")
	private Date updatedAt;

	@SerializedName("automatically_select_bid")
	private boolean automaticallySelectBid;

	@SerializedName("bid_type")
	private String bidType;

	@SerializedName("charge_by")
	private String chargeBy;

	@SerializedName("bid_unit")
	private String bidUnit;

	@SerializedName("advertiser_domain")
	private String advertiserDomain;

	@SerializedName("advertiser_user_id ")
	private String advertiserUserId;

	@SerializedName("categories")
	private String categories;

	@SerializedName("optimization")
	private String optimization;

	@SerializedName("creative_source")
	private String creativeSource;

	public String getAccountId() {
		return accountId;
	}

	public String getName() {
		return name;
	}

	public Long getBidAmtInMicro() {
		return bidAmtInMicro;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getCurrency() {
		return currency;
	}

	public String getGoalSettings() {
		return goalSettings;
	}

	public Boolean getMatchRelevantPopularQueries() {
		return matchRelevantPopularQueries;
	}

	public String getObjective() {
		return objective;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public String getPlacements() {
		return placements;
	}

	public String getProductType() {
		return productType;
	}

	public String getSentiment() {
		return sentiment;
	}

	public Boolean getPaused() {
		return paused;
	}

	public String getWebEventTag() {
		return webEventTag;
	}

	public Long getSuggestedHighCpeBidInMicro() {
		return suggestedHighCpeBidInMicro;
	}

	public Long getSuggestedLowCpeBidInMicro() {
		return suggestedLowCpeBidInMicro;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public boolean isAutomaticallySelectBid() {
		return automaticallySelectBid;
	}

	public String getBidType() {
		return bidType;
	}

	public String getChargeBy() {
		return chargeBy;
	}

	public String getBidUnit() {
		return bidUnit;
	}

	public String getAdvertiserDomain() {
		return advertiserDomain;
	}

	public String getAdvertiserUserId() {
		return advertiserUserId;
	}

	public String getCategories() {
		return categories;
	}

	public String getOptimization() {
		return optimization;
	}

	public String getCreativeSource() {
		return creativeSource;
	}

	public LineItemWrapper() {
	}

	public static class Builder {
		public static List<LineItemWrapper> build(List<LineItem> LineItems) {
			List<LineItemWrapper> result = new ArrayList<>();
			if (LineItems == null) {
				return result;
			}
			LineItems.forEach(t -> result.add(new LineItemWrapper(t)));
			return result;
		}
	}
}
