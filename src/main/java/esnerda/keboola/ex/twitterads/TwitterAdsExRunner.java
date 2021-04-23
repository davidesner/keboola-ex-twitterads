package esnerda.keboola.ex.twitterads;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import esnerda.keboola.components.KBCException;
import esnerda.keboola.components.configuration.OAuthCredentials;
import esnerda.keboola.components.configuration.handler.ConfigHandlerBuilder;
import esnerda.keboola.components.configuration.handler.KBCConfigurationEnvHandler;
import esnerda.keboola.components.configuration.tableconfig.ManifestFile;
import esnerda.keboola.components.logging.DefaultLogger;
import esnerda.keboola.components.logging.KBCLogger;
import esnerda.keboola.components.result.IResultWriter;
import esnerda.keboola.components.result.ResultFileMetadata;
import esnerda.keboola.components.result.impl.DefaultBeanResultWriter;
import esnerda.keboola.ex.twitterads.config.TwAdsConfigParams;
import esnerda.keboola.ex.twitterads.config.TwAdsConfigParams.EntityDatasets;
import esnerda.keboola.ex.twitterads.config.TwAdsState;
import esnerda.keboola.ex.twitterads.config.TwitterAuthResponseParser;
import esnerda.keboola.ex.twitterads.config.TwitterAuthTokens;
import esnerda.keboola.ex.twitterads.result.wrapper.AccountsWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.AdStatsWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.AdsWrapperBuilder;
import esnerda.keboola.ex.twitterads.result.wrapper.AppDownloadCardWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.CampaignWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.LineItemWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.MediaCreativeWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.TweetWrapper;
import esnerda.keboola.ex.twitterads.util.CsvUtil;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsApiClient;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsApiService;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsWsConfig;
import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequest;
import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequestBuilder;
import esnerda.keboola.ex.twitterads.ws.request.AsyncAdsRequestChunk;
import esnerda.keboola.ex.twitterads.ws.response.AdStatsResponseWrapper;
import twitter4jads.internal.models4j.TwitterException;
import twitter4jads.models.ScheduledTweet;
import twitter4jads.models.ads.AdAccount;
import twitter4jads.models.ads.Campaign;
import twitter4jads.models.ads.JobDetails;
import twitter4jads.models.ads.LineItem;
import twitter4jads.models.ads.PromotedTweets;
import twitter4jads.models.ads.TwitterAsyncQueryStatus;
import twitter4jads.models.ads.TwitterEntity;
import twitter4jads.models.ads.TwitterEntityType;
import twitter4jads.models.ads.cards.TwitterImageAppDownloadCard;
import twitter4jads.models.ads.cards.TwitterVideoAppDownloadCard;
import twitter4jads.models.ads.sort.CampaignSortByField;
import twitter4jads.models.ads.sort.LineItemsSortByField;
import twitter4jads.models.ads.sort.PromotedTweetsSortByField;
import twitter4jads.models.media.TwitterAccountMediaCreative;

/**
 * @author David Esner
 */
public class TwitterAdsExRunner extends ComponentRunner {

	private static final long TIMEOUT = 9900000L; // 3 hrs

	private static final int SINCE_DAYS_OFFSET = 1;

	private KBCConfigurationEnvHandler handler;
	private TwAdsConfigParams config;
	private TwitterAdsApiService apiService;
	private KBCLogger log;
	/* writers */
	private static IResultWriter<AdStatsWrapper> performanceDataWriter;
	private static IResultWriter<CampaignWrapper> campaignsWriter;
	private static IResultWriter<LineItemWrapper> lineItemWriter;
	private static IResultWriter<MediaCreativeWrapper> mediaCreativeWriter;
	private static IResultWriter<AppDownloadCardWrapper> appCardWriter;
	/* Entity writers */
	private static IResultWriter<PromotedTweets> promotedTweetsWriter;
	private static IResultWriter<ScheduledTweet> scheduledTweetsWriter;
	private static IResultWriter<TweetWrapper> tweetsWriter;
	private static IResultWriter<AccountsWrapper> accountWriter;

	public TwitterAdsExRunner(String[] args) {
		log = new DefaultLogger(TwitterAdsExRunner.class);
		handler = initHandler(args, log);
		config = (TwAdsConfigParams) handler.getParameters();
		try {
			OAuthCredentials creds = handler.getOAuthCredentials();
			TwitterAuthTokens tokens = TwitterAuthResponseParser.parseOAuthData(creds.getData());
			TwitterAdsApiClient twClient = new TwitterAdsApiClient(
					new TwitterAdsWsConfig(creds.getAppKey(), creds.getAppSecret(),
							tokens.getoAuthToken(), tokens.getoAuthTokenSecret()));
			apiService = new TwitterAdsApiService(twClient, log);

		} catch (Exception e) {
			handleException(new KBCException("Failed to init web service!", e.getMessage(), e, 2));
		}
	}

	@Override
	protected void run() {
		startTimer();
		List<Campaign> campaigns = Collections.emptyList();
		List<LineItem> lineItems = Collections.emptyList();
		List<TwitterAccountMediaCreative> mediaCreatives = Collections.emptyList();
		AdsStatsAsyncRequestBuilder builder = new AdsStatsAsyncRequestBuilder();

		try {
			List<AdAccount> accounts = getAccounts(config);

			if (!config.getAccountNames().isEmpty()
					&& accounts.size() < config.getAccountNames().size()) {
				System.err.println("Some accounts were not found! " + getMissingAccounts(accounts));
			}

			Instant now = Instant.now();
			Date since = getSinceDate();

			List<JobDetails> finished = Collections.emptyList();
			List<AdsStatsAsyncRequest> unfinishedReqs = new ArrayList<>();

			if (accounts.isEmpty()) {
				System.err.println("No such accounts found!");
				System.exit(1);
			}

			// init writers
			initWriters();

			// write accounts
			if (config.getEntityDatasets().contains(EntityDatasets.ACCOUNT.name())) {
				accountWriter.writeAllResults(AccountsWrapper.Builder
						.build(apiService.getAllAccounts(config.getIncludeDeleted())));
			}

			// get entities
			for (AdAccount acc : accounts) {
				String accountId = acc.getId();
				log.info("Retrieving Entities for account '" + acc.getName() + "'...");
				if (config.getEntityDatasets().contains(EntityDatasets.CAMPAIGN.name())
						|| config.getEntityTypeEnum().equals(TwitterEntityType.CAMPAIGN)) {
					campaigns = apiService.getCampaigns(accountId, config.getIncludeDeleted(),
							CampaignSortByField.UPDATED_AT_DESC);
					campaignsWriter
							.writeAllResults(CampaignWrapper.Builder.build(campaigns, accountId));
				}
				if (config.getEntityDatasets().contains(EntityDatasets.LINE_ITEM.name())
						|| config.getEntityTypeEnum().equals(TwitterEntityType.LINE_ITEM)) {
					lineItems = apiService.getLineItems(accountId, config.getIncludeDeleted(),
							LineItemsSortByField.UPDATED_AT);
					lineItemWriter
							.writeAllResults(LineItemWrapper.Builder.build(lineItems, accountId));
				}

				if (config.getEntityDatasets().contains(EntityDatasets.MEDIA_CREATIVE.name())
						|| config.getEntityTypeEnum().equals(TwitterEntityType.MEDIA_CREATIVE)) {
					mediaCreatives = apiService.getMediaCreatives(accountId,
							config.getIncludeDeleted());
					mediaCreativeWriter.writeAllResults(
							MediaCreativeWrapper.Builder.build(mediaCreatives, accountId));
				}

				if (config.getEntityDatasets().contains(EntityDatasets.APP_CARDS.name())) {
					List<TwitterVideoAppDownloadCard> videoCards = apiService
							.getVideoAppDownloadCards(accountId, config.getIncludeDeleted());
					appCardWriter.writeAllResults(
							AppDownloadCardWrapper.Builder.build(videoCards, accountId));

					List<TwitterImageAppDownloadCard> imageCards = apiService
							.getImageoAppDownloadCards(accountId, config.getIncludeDeleted());
					appCardWriter.writeAllResults(
							AppDownloadCardWrapper.Builder.build(imageCards, accountId));
				}

				if (config.getEntityDatasets().contains(EntityDatasets.SCHEDULED_TWEETS.name())) {
					scheduledTweetsWriter.writeAllResults(
							apiService.getScheduleddTweets(accountId, config.getIncludeDeleted()));
				}

				if (config.getEntityDatasets().contains(EntityDatasets.PUBLISHED_TWEETS.name())) {
					tweetsWriter.writeAllResults(TweetWrapper.Builder
							.build(apiService.getPublishedTweets(accountId), accountId));
				}

				/* Get implicit entities */
				promotedTweetsWriter.writeAllResults(apiService.getPromotedTweets(accountId,
						config.getIncludeDeleted(), PromotedTweetsSortByField.UPDATED_AT_DESC));


				// retrieve data only for recently updated
				log.info("Geting data since: " + since.toString());
				List<String> reqEntityIds = Collections.emptyList();
				switch (config.getEntityTypeEnum()) {
				case CAMPAIGN:
					reqEntityIds = getEntIds(
							apiService.filterRecentlyUpdatedCampaigns(campaigns, since));
					break;
				case LINE_ITEM:
					reqEntityIds = getEntIds(
							apiService.filterRecentlyUpdatedLineItems(lineItems, since));
					break;

				case MEDIA_CREATIVE:
					reqEntityIds = getEntIds(
							apiService.filterRecentlyUpdatedLineItems(lineItems, since));
					break;

				}
				List<AsyncAdsRequestChunk> chunks = builder.buildAdRequestsChunks(
						config.getEntityTypeEnum(), acc, reqEntityIds, since.toInstant(), now,
						config.getGranularityEnum());

				int cnt = 0;
				for (AsyncAdsRequestChunk chunk : chunks) {
					cnt++;
					log.info("Submitting " + chunk.size() + " async data retrieval jobs..");
					Map<String, AdsStatsAsyncRequest> jdIds = apiService
							.submitAdStatsAsyncRequests(chunk.getRequestList());
					log.info("Waiting to proccess " + cnt + ". chunk of jobs..");
					// collect finished
					finished = apiService.waitForAllJobsToFinish(chunk.getChunkAccountId(),
							new ArrayList(jdIds.keySet()));
					checkForFailures(finished);
					// get unfinished
					unfinishedReqs.addAll(getUnfinishedRequests(finished, jdIds));

					if (!unfinishedReqs.isEmpty()) {
						log.warning("Some jobs (" + unfinishedReqs.size()
								+ ")  didn't finish in time. They might not be processed properly. Please use shorter interval!",
								null);
					}

					if (isTimedOut()) {
						System.err.println("Job processing timed out!");
						break;
					}
				}

				log.info("Parsing results for account '" + acc.getName() + "'...");
				for (JobDetails jd : finished) {
					AdStatsResponseWrapper resp = new AdStatsResponseWrapper(
							apiService.fetchJobDataAsync(jd));
					performanceDataWriter
							.writeAllResults((AdsWrapperBuilder.buildFromResponse(resp)));
				}
			}

			finalize(closeWritersAndRetrieveResults(),
					new TwAdsState(Date.from(now), unfinishedReqs));
			deleteEmptyFiles();
			log.info("Extraction finished successfuly!");
		} catch (KBCException e) {
			handleException(e);
		} catch (TwitterException e) {
			handleException(new KBCException(e.getActualDetailMessage(), 2, e));
		} catch (Exception e) {
			handleException(new KBCException(e.getMessage(), 2, e));
		}

	}

	private void checkForFailures(List<JobDetails> finished) throws KBCException {
		boolean someNotFinished = false;
		List<String> failedJobs = new ArrayList<>();
		for (JobDetails jd : finished) {
			if ((jd != null) && (jd.getStatus() == TwitterAsyncQueryStatus.FAILED)) {
				failedJobs.add(jd.getUrl());
			}
		}

		if (!failedJobs.isEmpty()) {
			throw new KBCException("Some jobs failed to submit! : " + String.join(" ", failedJobs),
					2, null);
		}

	}

	private List<AdAccount> getAccounts(TwAdsConfigParams config2) throws TwitterException {
		if (config.getAccountNames().isEmpty()) {
			return apiService.getAllAccounts(true);
		}
		return apiService.getAccountsByNames(config.getAccountNames(), config.getIncludeDeleted());
	}

	private List<ResultFileMetadata> closeWritersAndRetrieveResults() throws Exception {
		List<ResultFileMetadata> allResults = new ArrayList<>();

		if (campaignsWriter != null) {
			allResults.addAll(campaignsWriter.closeAndRetrieveMetadata());
		}
		if (lineItemWriter != null) {
			allResults.addAll(lineItemWriter.closeAndRetrieveMetadata());
		}
		if (performanceDataWriter != null) {
			allResults.addAll(performanceDataWriter.closeAndRetrieveMetadata());
		}
		if (promotedTweetsWriter != null) {
			allResults.addAll(promotedTweetsWriter.closeAndRetrieveMetadata());
		}
		if (scheduledTweetsWriter != null) {
			allResults.addAll(scheduledTweetsWriter.closeAndRetrieveMetadata());
		}
		if (tweetsWriter != null) {
			allResults.addAll(tweetsWriter.closeAndRetrieveMetadata());
		}
		if (accountWriter != null) {
			allResults.addAll(accountWriter.closeAndRetrieveMetadata());
		}
		if (appCardWriter != null) {
			allResults.addAll(appCardWriter.closeAndRetrieveMetadata());
		}
		if (mediaCreativeWriter != null) {
			allResults.addAll(mediaCreativeWriter.closeAndRetrieveMetadata());
		}

		return allResults;
	}

	private void deleteEmptyFiles() {
		File[] files = new File(handler.getOutputTablesPath()).listFiles(f -> {
			String name = f.getName();
			return name.substring(name.length() - 4, name.length()).equals(".csv");
		});
		CsvUtil.deleteEmptyFiles(Arrays.asList(files));
	}

	private String getMissingAccounts(List<AdAccount> accs) {
		List<String> accNames = accs.parallelStream().map(a -> a.getName())
				.collect(Collectors.toList());
		return String.join(";", config.getAccountNames().stream().filter(a -> !accNames.contains(a))
				.map(a -> a).collect(Collectors.toList()));
	}

	private List<String> getEntIds(List cpgns) {
		return (List<String>) cpgns.stream().map(c -> ((TwitterEntity) c).getId())
				.collect(Collectors.toList());
	}

	private Date getSinceDate() throws KBCException {
		TwAdsState lastState = (TwAdsState) handler.getStateFile();
		if (!config.getSinceLast() || lastState == null || lastState.getLastRun() == null) {
			return config.getSince();
		} else {
			return LocalDate.fromDateFields(lastState.getLastRun()).minusDays(SINCE_DAYS_OFFSET)
					.toDate();
		}
	}

	private List<AdsStatsAsyncRequest> getUnfinishedRequests(List<JobDetails> finishedJobs,
			Map<String, AdsStatsAsyncRequest> submittedReqs) {

		for (JobDetails jd : finishedJobs) {
			submittedReqs.remove(jd.getJobId());
		}
		return new ArrayList(submittedReqs.values());
	}

	protected ManifestFile generateManifestFile(ResultFileMetadata result) throws KBCException {
		return ManifestFile.Builder.buildDefaultFromResult(result, null, config.getIncremental())
				.build();
	}

	@Override
	protected KBCConfigurationEnvHandler initHandler(String[] args, KBCLogger log) {
		KBCConfigurationEnvHandler handler = null;
		try {
			handler = ConfigHandlerBuilder.create(TwAdsConfigParams.class)
					.setStateFileType(TwAdsState.class).build();
			// process the configuration
			handler.processConfigFile(args);
		} catch (KBCException ex) {
			log.log(ex);
			System.exit(1);
		}
		setHandler(handler);
		return handler;
	}

	@Override
	protected void initWriters() throws Exception {
		this.performanceDataWriter = new DefaultBeanResultWriter<>(
				config.getEntityType().toLowerCase() + "PerformanceData.csv",
				new String[] { "entityId", "timeStamp" });
		performanceDataWriter.initWriter(handler.getOutputTablesPath(), AdStatsWrapper.class);
		if (config.getEntityDatasets().contains(EntityDatasets.CAMPAIGN.name())
				|| config.getEntityTypeEnum().equals(TwitterEntityType.CAMPAIGN)) {
			this.campaignsWriter = new DefaultBeanResultWriter<>("campaigns.csv",
					new String[] { "id" });
			this.campaignsWriter.initWriter(handler.getOutputTablesPath(), CampaignWrapper.class);
		}
		if (config.getEntityDatasets().contains(EntityDatasets.LINE_ITEM.name())
				|| config.getEntityTypeEnum().equals(TwitterEntityType.LINE_ITEM)) {
			this.lineItemWriter = new DefaultBeanResultWriter<>("lineItem.csv",
					new String[] { "id" });
			lineItemWriter.initWriter(handler.getOutputTablesPath(), LineItemWrapper.class);
		}

		if (config.getEntityDatasets().contains(EntityDatasets.MEDIA_CREATIVE.name())
				|| config.getEntityTypeEnum().equals(TwitterEntityType.MEDIA_CREATIVE)) {
			this.mediaCreativeWriter = new DefaultBeanResultWriter<>("mediaCreative.csv",
					new String[] { "id" });
			mediaCreativeWriter.initWriter(handler.getOutputTablesPath(),
					MediaCreativeWrapper.class);
		}
		if (config.getEntityDatasets().contains(EntityDatasets.ACCOUNT.name())) {
			this.accountWriter = new DefaultBeanResultWriter<>("accounts.csv", null);
			accountWriter.initWriter(handler.getOutputTablesPath(), AccountsWrapper.class);
		}
		if (config.getEntityDatasets().contains(EntityDatasets.APP_CARDS.name())) {
			this.appCardWriter = new DefaultBeanResultWriter<>("app_download_cards.csv", null);
			appCardWriter.initWriter(handler.getOutputTablesPath(), AppDownloadCardWrapper.class);
		}
		this.promotedTweetsWriter = new DefaultBeanResultWriter<>("promotedTweets.csv",
				new String[] { "tweetId" });
		this.promotedTweetsWriter.initWriter(handler.getOutputTablesPath(), PromotedTweets.class);
		
		if (config.getEntityDatasets().contains(EntityDatasets.SCHEDULED_TWEETS.name())) {
			this.scheduledTweetsWriter = new DefaultBeanResultWriter<>("scheduledTweets.csv",
					new String[] { "tweetId" });

			this.scheduledTweetsWriter.initWriter(handler.getOutputTablesPath(),
					ScheduledTweet.class);
		}
		if (config.getEntityDatasets().contains(EntityDatasets.PUBLISHED_TWEETS.name())) {
			this.tweetsWriter = new DefaultBeanResultWriter<>("published_tweets.csv",
					new String[] { "tweetId" });
			this.tweetsWriter.initWriter(handler.getOutputTablesPath(), TweetWrapper.class);
		}

	}

	@Override
	public KBCLogger getLogger() {
		return log;
	}

	@Override
	protected long getTimeout() {
		return TIMEOUT;
	}

}
