package esnerda.keboola.ex.twitterads.ws;

import java.util.ArrayList;
import java.util.List;

import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequest;
import twitter4jads.BaseAdsListResponse;
import twitter4jads.BaseAdsListResponseIterable;
import twitter4jads.BaseAdsResponse;
import twitter4jads.TwitterAds;
import twitter4jads.TwitterAdsFactory;
import twitter4jads.api.TwitterAdsAccountApi;
import twitter4jads.api.TwitterAdsCampaignApi;
import twitter4jads.api.TwitterAdsCardsApi;
import twitter4jads.api.TwitterAdsLineItemApi;
import twitter4jads.api.TwitterAdsMediaApi;
import twitter4jads.api.TwitterAdsPromotedTweetApi;
import twitter4jads.api.TwitterAdsStatApi;
import twitter4jads.conf.ConfigurationBuilder;
import twitter4jads.internal.models4j.TwitterException;
import twitter4jads.models.ads.AdAccount;
import twitter4jads.models.ads.Campaign;
import twitter4jads.models.ads.JobDetails;
import twitter4jads.models.ads.LineItem;
import twitter4jads.models.ads.PromotedTweets;
import twitter4jads.models.ads.TwitterEntityStatistics;
import twitter4jads.models.ads.cards.TwitterImageAppDownloadCard;
import twitter4jads.models.ads.cards.TwitterVideoAppDownloadCard;
import twitter4jads.models.ads.sort.CampaignSortByField;
import twitter4jads.models.ads.sort.LineItemsSortByField;
import twitter4jads.models.ads.sort.PromotedTweetsSortByField;
import twitter4jads.models.media.TwitterAccountMediaCreative;

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
				.setOAuthConsumerKey(config.getConsumerKey())
				.setOAuthAccessToken(config.getAccessToken())
				.setOAuthAccessTokenSecret(config.getAccessTokenSecret())
				.setHttpRetryCount(REQ_RETRY_COUNT).setHttpConnectionTimeout(CONNECTION_TIMEOUT);
		this.clientInstance = new TwitterAdsFactory(configurationBuilder.build()).getAdsInstance();
	}

	public List<AdAccount> getAccounts(boolean withDeleted) throws TwitterException {
		TwitterAdsAccountApi accountApi = clientInstance.getAccountApi();

		BaseAdsListResponseIterable<AdAccount> allAccounts = accountApi.getAllAccounts(withDeleted,
				null);

		return transformListResponseToList(allAccounts);
	}

	public List<Campaign> getCampaignsForAccount(String accountId, boolean withDeleted,
			CampaignSortByField sortBy) throws TwitterException {
		TwitterAdsCampaignApi campaignApi = clientInstance.getCampaignApi();

		BaseAdsListResponseIterable<Campaign> allCampaigns = campaignApi.getAllCampaigns(accountId,
				null, null, withDeleted, null, null,
				com.google.common.base.Optional.fromNullable(sortBy));

		return transformListResponseToList(allCampaigns);
	}

	public List<TwitterAccountMediaCreative> getMediaCreatives(String accountId,
			boolean withDeleted) throws TwitterException {
		TwitterAdsMediaApi mediaApi = clientInstance.getPromotedApi();

		BaseAdsListResponseIterable<TwitterAccountMediaCreative> allCreatives = mediaApi
				.getMediaCreativesForAccount(accountId, withDeleted);

		return transformListResponseToList(allCreatives);
	}

	public List<LineItem> getLineItems(String accountId, boolean withDeleted,
			LineItemsSortByField sortBy) throws TwitterException {
		TwitterAdsLineItemApi lineItemApi = clientInstance.getLineItemApi();
		BaseAdsListResponseIterable<LineItem> allLineItems = lineItemApi.getAllLineItems(accountId,
				null, null, null, null, withDeleted, null,
				com.google.common.base.Optional.fromNullable(sortBy));

		return transformListResponseToList(allLineItems);
	}

	public List<TwitterVideoAppDownloadCard> getVideoAppDownloadCards(String accountId,
			boolean withDeleted) throws TwitterException {
		TwitterAdsCardsApi cardsApi = clientInstance.getCardsApi();

		BaseAdsListResponseIterable<TwitterVideoAppDownloadCard> allCards = cardsApi
				.getAllVideoAppDownloadCards(accountId, null, withDeleted, null);

		return transformListResponseToList(allCards);
	}

	public List<TwitterImageAppDownloadCard> getImageAppDownloadCards(String accountId,
			boolean withDeleted) throws TwitterException {
		TwitterAdsCardsApi cardsApi = clientInstance.getCardsApi();

		BaseAdsListResponseIterable<TwitterImageAppDownloadCard> allCards = cardsApi
				.getAllImageAppDownloadCards(accountId, null, withDeleted, null);

		return transformListResponseToList(allCards);
	}

	public List<PromotedTweets> getPromotedTweets(String accountId, boolean withDeleted,
			PromotedTweetsSortByField sortBy) throws TwitterException {
		TwitterAdsPromotedTweetApi promotedTwApi = clientInstance.getPromotedTweetApi();
		BaseAdsListResponseIterable<PromotedTweets> promotedTweets = promotedTwApi
				.getAllPromotedTweets(accountId, withDeleted, null, null, null,
						com.google.common.base.Optional.fromNullable(sortBy));

		return transformListResponseToList(promotedTweets);
	}

	public JobDetails submitAsyncStatsRequest(AdsStatsAsyncRequest req) throws TwitterException {
		TwitterAdsStatApi statApi = clientInstance.getStatApi();

		BaseAdsResponse<JobDetails> twitterAsyncJob = statApi.createAsyncJob(req.getAccountId(),
				req.getType(), req.getEntityIds(), req.getStartTimeEpoch(), req.getEndTimeEpoch(),
				req.isWithDeleted(), req.getGranularity(), req.getPlacement(), req.getSegment());
		return twitterAsyncJob.getData();

	}

	public BaseAdsListResponse<TwitterEntityStatistics> fetchJobDataAsync(String jobUrl)
			throws TwitterException {
		return getStatsApi().fetchJobDataAsync(jobUrl);
	}

	public List<JobDetails> getJobsStatus(String accountId, List<String> jobIds)
			throws TwitterException {

		BaseAdsListResponseIterable<JobDetails> jobsDetails = getStatsApi()
				.getJobExecutionDetails(accountId, jobIds);
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
