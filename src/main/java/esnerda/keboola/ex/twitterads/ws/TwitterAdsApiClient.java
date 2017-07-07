package esnerda.keboola.ex.twitterads.ws;

import java.util.ArrayList;
import java.util.List;

import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequest;
import twitter4j.BaseAdsListResponse;
import twitter4j.BaseAdsListResponseIterable;
import twitter4j.BaseAdsResponse;
import twitter4j.TwitterAds;
import twitter4j.TwitterAdsFactory;
import twitter4j.api.TwitterAdsAccountApi;
import twitter4j.api.TwitterAdsCampaignApi;
import twitter4j.api.TwitterAdsLineItemApi;
import twitter4j.api.TwitterAdsPromotedTweetApi;
import twitter4j.api.TwitterAdsStatApi;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.models4j.TwitterException;
import twitter4j.models.ads.AdAccount;
import twitter4j.models.ads.Campaign;
import twitter4j.models.ads.JobDetails;
import twitter4j.models.ads.LineItem;
import twitter4j.models.ads.PromotedTweets;
import twitter4j.models.ads.TwitterEntityStatistics;
import twitter4j.models.ads.sort.CampaignSortByField;
import twitter4j.models.ads.sort.LineItemsSortByField;
import twitter4j.models.ads.sort.PromotedTweetsSortByField;

/**
 * @author David Esner
 */
public class TwitterAdsApiClient {

	private static final int REQ_RETRY_COUNT = 5;
	private static final int CONNECTION_TIMEOUT = 5000;
	private final TwitterAds clientInstance;

	public TwitterAdsApiClient(TwitterAdsWsConfig config) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerSecret(config.getConsumerSecret())
				.setOAuthConsumerKey(config.getConsumerKey()).setOAuthAccessToken(config.getAccessToken())
				.setOAuthAccessTokenSecret(config.getAccessTokenSecret()).setHttpRetryCount(REQ_RETRY_COUNT)
				.setHttpConnectionTimeout(CONNECTION_TIMEOUT);
		this.clientInstance = new TwitterAdsFactory(configurationBuilder.build()).getAdsInstance();
	}

	public List<AdAccount> getAccounts(boolean withDeleted)
			throws TwitterException {
		TwitterAdsAccountApi accountApi = clientInstance.getAccountApi();
		
		BaseAdsListResponseIterable<AdAccount> allAccounts = accountApi.getAllAccounts(withDeleted, null);
		
		return transformListResponseToList(allAccounts);
	}
	
	public List<Campaign> getCampaignsForAccount(String accountId, boolean withDeleted, CampaignSortByField sortBy)
			throws TwitterException {
		TwitterAdsCampaignApi campaignApi = clientInstance.getCampaignApi();

		BaseAdsListResponseIterable<Campaign> allCampaigns = campaignApi.getAllCampaigns(accountId, null, null,
				withDeleted, null, null, com.google.common.base.Optional.fromNullable(sortBy));

		return transformListResponseToList(allCampaigns);
	}

	public List<LineItem> getLineItems(String accountId, boolean withDeleted, LineItemsSortByField sortBy)
			throws TwitterException {
		TwitterAdsLineItemApi lineItemApi = clientInstance.getLineItemApi();
		BaseAdsListResponseIterable<LineItem> allLineItems = lineItemApi.getAllLineItems(accountId, null, null, null, null, withDeleted, null,
				com.google.common.base.Optional.fromNullable(sortBy));

		return transformListResponseToList(allLineItems);
	}
	
	public List<PromotedTweets> getPromotedTweets(String accountId, boolean withDeleted, PromotedTweetsSortByField sortBy)
			throws TwitterException {
		TwitterAdsPromotedTweetApi promotedTwApi = clientInstance.getPromotedTweetApi();
		BaseAdsListResponseIterable<PromotedTweets> promotedTweets = promotedTwApi.getAllPromotedTweets(accountId, null, withDeleted, null, null, com.google.common.base.Optional.fromNullable(sortBy));

		return transformListResponseToList(promotedTweets);
	}

	public JobDetails submitAsyncStatsRequest(AdsStatsAsyncRequest req) throws TwitterException {
		TwitterAdsStatApi statApi = clientInstance.getStatApi();

		BaseAdsResponse<JobDetails> twitterAsyncJob = statApi.createAsyncJob(req.getAccountId(), req.getType(),
				req.getEntityIds(), req.getStartTimeEpoch(), req.getEndTimeEpoch(), req.isWithDeleted(),
				req.getGranularity(), req.getObjective(), req.getPlacement(), req.getSegment());
		return twitterAsyncJob.getData();

	}

	public  BaseAdsListResponse<TwitterEntityStatistics> fetchJobDataAsync(String jobUrl) throws TwitterException {
		 return getStatsApi().fetchJobDataAsync(jobUrl);
	}

	public List<JobDetails> getJobsStatus(String accountId, List<String> jobIds) throws TwitterException {

		BaseAdsListResponseIterable<JobDetails> jobsDetails = getStatsApi().getJobExecutionDetails(accountId, jobIds);
		return transformListResponseToList(jobsDetails);

	}

	private <T> List<T> transformListResponseToList(BaseAdsListResponseIterable<T> listResp) {		
		List<T> resultList = new ArrayList<>();
		for (BaseAdsListResponse<T> allCampaign : listResp) {
			resultList.addAll(allCampaign.getData());
		}
		return resultList;
	}

	private TwitterAdsStatApi getStatsApi() {
		return clientInstance.getStatApi();
	}
}
