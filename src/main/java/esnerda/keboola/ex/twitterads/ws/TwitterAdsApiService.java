package esnerda.keboola.ex.twitterads.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequest;
import twitter4j.BaseAdsListResponse;
import twitter4j.internal.models4j.TwitterException;
import twitter4j.models.ads.AdAccount;
import twitter4j.models.ads.Campaign;
import twitter4j.models.ads.JobDetails;
import twitter4j.models.ads.LineItem;
import twitter4j.models.ads.PromotedTweets;
import twitter4j.models.ads.TwitterAsyncQueryStatus;
import twitter4j.models.ads.TwitterEntityStatistics;
import twitter4j.models.ads.sort.CampaignSortByField;
import twitter4j.models.ads.sort.LineItemsSortByField;
import twitter4j.models.ads.sort.PromotedTweetsSortByField;
import twitter4j.util.TwitterAdUtil;

/**
 * @author David Esner
 */
public class TwitterAdsApiService {

	//wait between status check reqs
	private static final long JOB_CHECK_WAIT_INTERVAL = 10000L;
	//wait hour for jobs chunk to finish
	private static final long WAIT_TIMEOUT = 3600000L;

	private final TwitterAdsApiClient client;

	public TwitterAdsApiService(TwitterAdsApiClient client) {
		this.client = client;
	}

	public List<Campaign> getCampaigns(String accountId, boolean includeDeleted, CampaignSortByField sortBy) throws TwitterException {
		return client.getCampaignsForAccount(accountId, includeDeleted, sortBy);
	}

	public List<LineItem> getLineItems(String accountId, boolean includeDeleted, LineItemsSortByField sortBy) throws TwitterException {
		return client.getLineItems(accountId, includeDeleted, sortBy);
	}

	public List<PromotedTweets> getPromotedTweets(String accountId, boolean includeDeleted, PromotedTweetsSortByField sortBy) throws TwitterException {
		return client.getPromotedTweets(accountId, includeDeleted, sortBy);
	}

	public List<AdAccount> getAccountsByNames(List<String> accountNames, boolean includeDeleted) throws TwitterException {
		return client.getAccounts(includeDeleted).stream().filter(a -> accountNames.contains(a.getName())).collect(Collectors.toList());
		
	}

	public List<AdAccount> getAllAccounts(boolean includeDeleted) throws TwitterException {
		return client.getAccounts(includeDeleted);
		
	}
	
	public List<Campaign> getRecentlyUpdatedCampaigns(String accountId, boolean includeDeleted, Date since) throws TwitterException {
		return filterRecentlyUpdatedCampaigns(
				getCampaigns(accountId, includeDeleted, CampaignSortByField.UPDATED_AT_DESC), since);
	}

	public List<Campaign> filterRecentlyUpdatedCampaigns(List<Campaign> campaigns, Date since) {
		return campaigns.stream().filter(c -> c.getUpdateTime().after(since)).collect(Collectors.toList());
	}

	public List<LineItem> filterRecentlyUpdatedLineItems(List<LineItem> lineItems, Date since) {
		return lineItems.stream().filter(c -> c.getUpdatedAt().after(since)).collect(Collectors.toList());
	}

	public List<LineItem> getRecentlyUpdatedLineItems(String accountId, boolean includeDeleted, Date since) throws TwitterException {
		return 	filterRecentlyUpdatedLineItems(getLineItems(accountId, includeDeleted, LineItemsSortByField.UPDATED_AT),since);
	}

	/**
	 * Submits bulk job requests.
	 * @param requests
	 * @return Map where Key is submittedJobId and value the original request
	 * @throws TwitterException
	 */
	public Map<String, AdsStatsAsyncRequest> submitAdStatsAsyncRequests(List<AdsStatsAsyncRequest> requests) throws TwitterException {
		Map<String, AdsStatsAsyncRequest> result = new HashMap<>();
		for (AdsStatsAsyncRequest req : requests) {
			JobDetails jd = client.submitAsyncStatsRequest(req);
			result.put(jd.getJobId(), req);
		}
		return result;
	}

	public BaseAdsListResponse<TwitterEntityStatistics> fetchJobDataAsync(JobDetails jd) throws TwitterException {
		return client.fetchJobDataAsync(jd.getUrl());
	}

	/**
	 * Wait for all jobs for given account to finish and return the JobDetails objects
	 * 
	 * @param accountId
	 * @param jobIds 
	 * @return
	 */
	public List<JobDetails> waitForAllJobsToFinish(String accountId, List<String> jobIds) {
		List<JobDetails> currentDetails = new ArrayList<>();
		long startTime = System.currentTimeMillis();
		boolean flag;
		do {
			TwitterAdUtil.reallySleep(10000L);
			flag = false; // continue iterating as long as status of job of job is either queued, uploading or processing
			try {
				currentDetails = client.getJobsStatus(accountId, jobIds);
				if (!allJobsFinished(currentDetails)) {
					flag = true;
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (flag && (System.currentTimeMillis() - startTime) <= WAIT_TIMEOUT);

		return currentDetails;
	}

	private boolean allJobsFinished(List<JobDetails> jobs) {
		boolean someNotFinished = false;
		for (JobDetails jd : jobs) {
			if ((jd != null) && (jd.getStatus() != TwitterAsyncQueryStatus.SUCCESS)) {
				someNotFinished = true;
			}
		}
		return !someNotFinished;
	}

	private List<String> getFinishedJobIds(List<JobDetails> jobs) {
		List<String> ids = new ArrayList<>();
		for (JobDetails jd : jobs) {
			if ((jd != null) && (jd.getStatus() == TwitterAsyncQueryStatus.SUCCESS)) {
				ids.add(jd.getJobId());
			}
		}
		return ids;
	}

	/**
	 * Returns all finished jobs from the supplied one.
	 * @param jobDetails
	 * @return
	 * @throws TwitterException
	 */
	public List<JobDetails> getFinishedJobs(String accountId, List<JobDetails> jobDetails) throws TwitterException {
		return client.getJobsStatus(accountId, retrieveJobIds(jobDetails));
	}

	private List<String> retrieveJobIds(List<JobDetails> jobDetails) {
		return jobDetails.stream().map(j -> j.getJobId()).collect(Collectors.toList());
	}




}
