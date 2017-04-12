package esnerda.keboola.ex.twitterads;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import esnerda.keboola.ex.twitterads.result.wrapper.AdStatsWrapper;
import esnerda.keboola.ex.twitterads.result.wrapper.AdsWrapperBuilder;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsApiClient;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsApiService;
import esnerda.keboola.ex.twitterads.ws.TwitterAdsWsConfig;
import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequest;
import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequestBuilder;
import esnerda.keboola.ex.twitterads.ws.request.AsyncAdsRequestChunk;
import esnerda.keboola.ex.twitterads.ws.response.AdStatsResponseWrapper;
import twitter4j.BaseAdsListResponse;
import twitter4j.BaseAdsListResponseIterable;
import twitter4j.TwitterAds;
import twitter4j.api.TwitterAdsCampaignApi;
import twitter4j.api.TwitterAdsLineItemApi;
import twitter4j.api.TwitterAdsStatApi;
import twitter4j.internal.models4j.TwitterException;
import twitter4j.models.ads.AdAccount;
import twitter4j.models.ads.Campaign;
import twitter4j.models.ads.JobDetails;
import twitter4j.models.ads.LineItem;
import twitter4j.models.ads.TwitterAsyncQueryStatus;
import twitter4j.models.ads.TwitterEntityStatistics;
import twitter4j.models.ads.TwitterEntityType;
/**
 * @author David Esner
 */
public class TestAccounts {

	public static void main(String[] args) throws Exception {		
	
	        Instant since = Instant.now().truncatedTo(ChronoUnit.HOURS).minus(190, ChronoUnit.DAYS);
	        Instant until = Instant.now().truncatedTo(ChronoUnit.HOURS);
	        
	        TwitterAdsApiClient twClient = new TwitterAdsApiClient(new TwitterAdsWsConfig("ELWv5tfYrCbEz2izMF1x1tVRH", "hOxCTwiWXZyRc9Tec8KzgGdLsXr5WjP02xZOASdDJvUhJaP27p", "143820595-HUjJ2mzEtSbLPO38n0qGpQUwxnHF4Vqu2KG3zQwL", "Qrq6epi7KIi2oy1zDHAyue8KrOkmyJIJonNKlCmi5RsTI"));
	        List<AdAccount> acc = twClient.getAccounts(true);
	        TwitterAdsApiService tService = new TwitterAdsApiService(twClient);
	        acc.get(0).getName();
	        
	       List<Campaign> cpgns = tService.getRecentlyUpdatedCampaigns("2dmkoj", false, Date.from(since));
	       
	       AdsStatsAsyncRequestBuilder builder = new AdsStatsAsyncRequestBuilder();
	       List<AsyncAdsRequestChunk> chunks = builder.buildAdRequestsChunks(TwitterEntityType.CAMPAIGN, "2dmkoj", getEntIds(cpgns), since, until);
	       List<JobDetails> finished = Collections.EMPTY_LIST;
	       for (AsyncAdsRequestChunk chunk : chunks) {
	    	  Map<String, AdsStatsAsyncRequest> jdIds =  tService.submitAdStatsAsyncRequests(chunk.getRequestList());
	    	   finished =  tService.waitForAllJobsToFinish(chunk.getChunkAccountId(), new ArrayList(jdIds.keySet()));	    	  
	       }
	
	       List<BaseAdsListResponse<TwitterEntityStatistics>> resultStats = Lists.newArrayList();
	     for (JobDetails jd : finished) {
	    	 resultStats.add(tService.fetchJobDataAsync(jd));
	     }
           
	     List<AdStatsWrapper> wr = new ArrayList<>();
	     for (BaseAdsListResponse<TwitterEntityStatistics> stats : resultStats) {
	    	 AdStatsResponseWrapper resp = new AdStatsResponseWrapper(stats);
	    	 wr.addAll(AdsWrapperBuilder.buildFromResponse(resp));           
       }
	     
   wr.size();
		

	}

	private static List<String> getEntIds(List<Campaign> cpgns) {
		return cpgns.stream().map(c -> c.getId()).collect(Collectors.toList());
	}
	
	private static List<Campaign> getCampaigns(TwitterAds inst) {
		TwitterAdsCampaignApi campaignApi = inst.getCampaignApi();
		List<Campaign> campaignList = Lists.newArrayList();

		BaseAdsListResponseIterable<Campaign> allCampaigns;
		try {
			allCampaigns = campaignApi.getAllCampaigns("2dmkoj", null, null, false, null, null, null);
			transformListResponseToList(allCampaigns,campaignList);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return campaignList;
	}
	
	private static void async(TwitterAds inst,List<String> cIds) {
		TwitterAdsStatApi statApi = inst.getStatApi();
		Date d = new Date();		
        long since = Instant.now().truncatedTo(ChronoUnit.HOURS).minus(90, ChronoUnit.DAYS).toEpochMilli();
        long until = Instant.now().truncatedTo(ChronoUnit.HOURS).toEpochMilli();
        try {
        //    BaseAdsResponse<JobDetails> twitterAsyncJob = statApi.createAsyncJob("2dmkoj", TwitterEntityType.CAMPAIGN,cIds.subList(0, 2), since, until, Boolean.FALSE, Granularity.HOUR, null, Placement.ALL_ON_TWITTER, null);
            BaseAdsListResponseIterable<JobDetails> jobExecutionDetails;
            boolean flag;
            long timeOut = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2);
            do {
                flag = false; //continue iterating as long as status of job of job is either queued, uploading or processing
                //TwitterAdUtil.reallySleep(10000L);
                jobExecutionDetails = statApi.getJobExecutionDetails("2dmkoj", Lists.newArrayList("850044209805524994"));

                for (BaseAdsListResponse<JobDetails> base : jobExecutionDetails) {
                    List<JobDetails> baselist = base.getData();
                    for (JobDetails jd : baselist) {
                        if ((jd != null) && (jd.getStatus() != TwitterAsyncQueryStatus.SUCCESS)) {
                            flag = true;
                        }
                    }
                }
            } while (flag && System.currentTimeMillis() <= timeOut);

            List<BaseAdsListResponse<TwitterEntityStatistics>> twitterEntityStatsList = Lists.newArrayList();

            for (BaseAdsListResponse<JobDetails> base : jobExecutionDetails) {
                List<JobDetails> baselist = base.getData();
                for (JobDetails jd : baselist) {
                    BaseAdsListResponse<TwitterEntityStatistics> allTwitterEntityStat = statApi.fetchJobDataAsync(jd.getUrl());
                    
                    if(allTwitterEntityStat == null || allTwitterEntityStat.getData() == null){
                        continue;
                    }
                    
                    
                    twitterEntityStatsList.add(allTwitterEntityStat);
                }
              
            }
            AdStatsResponseWrapper resp = new AdStatsResponseWrapper(twitterEntityStatsList.get(0));
            
            List<AdStatsWrapper> wr = AdsWrapperBuilder.buildFromResponse(resp);
            wr.size();
        } catch (Exception e) {
        	e.printStackTrace();
            System.err.println(e.getMessage());
        }
	}

	private static List<LineItem> getLineItems(TwitterAds inst) {
	      TwitterAds twitterAdsInstance =inst;
	        TwitterAdsLineItemApi lineItemApi = twitterAdsInstance.getLineItemApi();
	        List<LineItem> lineItemList = Lists.newArrayList();
	        try {
	            BaseAdsListResponseIterable<LineItem> allLineItems = lineItemApi.getAllLineItems("2dmkoj", null, null, null, null, false, null, null);
	            transformListResponseToList(allLineItems, lineItemList);
	        } catch (TwitterException e) {
	            System.err.println(e.getErrorMessage());
	        }
	        return lineItemList;
	}
	
	private static <T> List<T> transformListResponseToList(BaseAdsListResponseIterable<T> listResp, List<T> resultList) {		
		for (BaseAdsListResponse<T> allCampaign : listResp) {
			resultList.addAll(allCampaign.getData());
		}
		return resultList;
	}

}
