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
import esnerda.keboola.ex.twitterads.result.wrapper.AdStatsWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.AdsWrapperBuilder;
import esnerda.keboola.ex.twitterads.result.wrapper.CampaignWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.LineItemWrapper;
import esnerda.keboola.ex.twitterads.util.CsvUtil;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsApiClient;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsApiService;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsWsConfig;
import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequest;
import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequestBuilder;
import esnerda.keboola.ex.twitterads.ws.request.AsyncAdsRequestChunk;
import esnerda.keboola.ex.twitterads.ws.response.AdStatsResponseWrapper;
import twitter4j.internal.models4j.TwitterException;
import twitter4j.models.ads.AdAccount;
import twitter4j.models.ads.Campaign;
import twitter4j.models.ads.JobDetails;
import twitter4j.models.ads.LineItem;
import twitter4j.models.ads.TwitterEntity;
import twitter4j.models.ads.TwitterEntityType;
import twitter4j.models.ads.sort.CampaignSortByField;
import twitter4j.models.ads.sort.LineItemsSortByField;


/**
 * @author David Esner
 */
public class TwitterAdsExRunner extends ComponentRunner{
	
	private static final long TIMEOUT = 9900000L; //3 hrs

	private KBCConfigurationEnvHandler handler;
	private TwAdsConfigParams config;
	private TwitterAdsApiService apiService;
	private KBCLogger log;
	/* writers */
	private static IResultWriter<AdStatsWrapper> performanceDataWriter;
	private static IResultWriter<CampaignWrapper> campaignsWriter;
	private static IResultWriter<LineItemWrapper> lineItemWriter;

	public TwitterAdsExRunner (String[] args) {
		log = new DefaultLogger(TwitterAdsExRunner.class);
		handler = initHandler(args, log);
		config = (TwAdsConfigParams) handler.getParameters();		
		try {
			OAuthCredentials creds = handler.getOAuthCredentials();
			TwitterAuthTokens tokens = TwitterAuthResponseParser.parseOAuthData(creds.getData());
			 TwitterAdsApiClient twClient = new TwitterAdsApiClient(new TwitterAdsWsConfig(creds.getAppKey(),
					 creds.getAppSecret(), tokens.getoAuthToken(), tokens.getoAuthTokenSecret()));
			apiService = new TwitterAdsApiService(twClient);			
		
		} catch (Exception e) {
			handleException(new KBCException("Failed to init web service!", e.getMessage(), e, 2));
		}
	}

	@Override
	protected void run() {
		startTimer();
		List<Campaign> campaigns = Collections.emptyList();
		List<LineItem> lineItems = Collections.emptyList();
		AdsStatsAsyncRequestBuilder builder = new AdsStatsAsyncRequestBuilder();

		try {
		List<AdAccount> accounts = apiService.getAccountsByNames(config.getAccountNames(), config.getIncludeDeleted());
		if (accounts.size()< config.getAccountNames().size()) {
			log.warning("Some accounts were not found! " + getMissingAccounts(accounts), null);
		}

		Instant now = Instant.now();
		Date since = getSinceDate();

		List<JobDetails> finished = Collections.emptyList();
		List<AdsStatsAsyncRequest> unfinishedReqs = new ArrayList<>();
		
		if(accounts.isEmpty()) {
			log.warning("No such accounts found!", null);
			System.exit(0);
		}

		// init writers
		initWriters();
		//get entities
		for (AdAccount acc : accounts) {
			String accountId = acc.getId();
		log.info("Retrieving Entities for account '" + acc.getName() +"'...");
		if (config.getEntityDatasets().contains(EntityDatasets.CAMPAIGN.name()) || config.getEntityTypeEnum().equals(TwitterEntityType.CAMPAIGN)) {
			campaigns = apiService.getCampaigns(accountId, config.getIncludeDeleted(), CampaignSortByField.UPDATED_AT_DESC);
			campaignsWriter.writeAllResults(CampaignWrapper.Builder.build(campaigns));
		}
		if (config.getEntityDatasets().contains(EntityDatasets.LINE_ITEM.name()) || config.getEntityTypeEnum().equals(TwitterEntityType.LINE_ITEM)) {
			lineItems = apiService.getLineItems(accountId, config.getIncludeDeleted(), LineItemsSortByField.UPDATED_AT);
			lineItemWriter.writeAllResults(LineItemWrapper.Builder.build(lineItems));
		}

		//retrieve data only for recently updated
		List<String> reqEntityIds = Collections.emptyList();
		switch (config.getEntityTypeEnum()) {
		case CAMPAIGN:
			reqEntityIds = getEntIds(apiService.filterRecentlyUpdatedCampaigns(campaigns, since));	
			break;
		case LINE_ITEM:
			reqEntityIds = getEntIds(apiService.filterRecentlyUpdatedLineItems(lineItems,since));
			break;
		}
			List<AsyncAdsRequestChunk> chunks = builder.buildAdRequestsChunks(config.getEntityTypeEnum(),
					accountId, reqEntityIds, since.toInstant(), now);

			int cnt=0;
			for (AsyncAdsRequestChunk chunk : chunks) {
				cnt++;
				log.info("Submitting " + chunk.size() + " async data retrieval jobs..");
				Map<String, AdsStatsAsyncRequest> jdIds = apiService.submitAdStatsAsyncRequests(chunk.getRequestList());
				log.info("Waiting to proccess " + cnt +". chunk of jobs..");
				// collect finished
				finished = apiService.waitForAllJobsToFinish(chunk.getChunkAccountId(), new ArrayList(jdIds.keySet()));
				// get unfinished
				unfinishedReqs.addAll(getUnfinishedRequests(finished, jdIds));
				if (isTimedOut()) {
					log.warning("Job processing timed out!", null);
					break;
				}
			}

			log.info("Parsing results for account '" + acc.getName() +"'...");
			for (JobDetails jd : finished) {
				AdStatsResponseWrapper resp = new AdStatsResponseWrapper(apiService.fetchJobDataAsync(jd));
				performanceDataWriter.writeAllResults((AdsWrapperBuilder.buildFromResponse(resp)));
			}			
		}		

			finalize(closeWritersAndRetrieveResults(), new TwAdsState(Date.from(now), unfinishedReqs));
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
		return String.join(";", accs.stream().filter(a -> config.getAccountNames().contains(a.getName())).map(a -> a.getName())
				.collect(Collectors.toList()));
	}
	private List<String> getEntIds(List cpgns) {
		return (List<String>) cpgns.stream().map(c -> ((TwitterEntity)c).getId()).collect(Collectors.toList());
	}

	private Date getSinceDate() throws KBCException {
		TwAdsState lastState = (TwAdsState) handler.getStateFile();		
		if (!config.getSinceLast() || lastState == null || lastState.getLastRun() == null) {
			return config.getSince();
		} else {
			return lastState.getLastRun();
		}
	}

	private List<AdsStatsAsyncRequest> getUnfinishedRequests(List<JobDetails> finishedJobs,  Map<String, AdsStatsAsyncRequest> submittedReqs) {
		for (JobDetails jd : finishedJobs) {
			submittedReqs.remove(jd.getJobId());
		}
		return new ArrayList(submittedReqs.values());		
	}


	protected ManifestFile generateManifestFile(ResultFileMetadata result) throws KBCException {
		return ManifestFile.Builder.buildDefaultFromResult(result, null, config.getIncremental()).build();
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
				config.getEntityType().toLowerCase() + "PerformanceData.csv", new String[] { "entityId", "timeStamp" });
		performanceDataWriter.initWriter(handler.getOutputTablesPath(), AdStatsWrapper.class);
		if (config.getEntityDatasets().contains(EntityDatasets.CAMPAIGN.name()) || config.getEntityTypeEnum().equals(TwitterEntityType.CAMPAIGN)) {
			this.campaignsWriter = new DefaultBeanResultWriter<>("campaigns.csv", new String[] { "id" });
			this.campaignsWriter.initWriter(handler.getOutputTablesPath(), CampaignWrapper.class);
		}
		if (config.getEntityDatasets().contains(EntityDatasets.LINE_ITEM.name()) || config.getEntityTypeEnum().equals(TwitterEntityType.LINE_ITEM)) {
			this.lineItemWriter = new DefaultBeanResultWriter<>("lineItem.csv", new String[] { "id" });
			lineItemWriter.initWriter(handler.getOutputTablesPath(), LineItemWrapper.class);
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
