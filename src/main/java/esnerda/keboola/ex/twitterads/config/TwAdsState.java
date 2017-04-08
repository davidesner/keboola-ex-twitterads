package esnerda.keboola.ex.twitterads.config;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import esnerda.keboola.components.appstate.LastState;
import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequest;

/**
 * @author David Esner
 */
public class TwAdsState implements LastState {

	   @JsonProperty("lastRun")
	   private Date lastRun;
	   
	   @JsonProperty("unfinishedJobs")
	   private List<AdsStatsAsyncRequest> unfinishedJobs;

	public TwAdsState(Date lastRun,List<AdsStatsAsyncRequest> unfinishedJobs) {
		super();
		this.lastRun = lastRun;
		this.unfinishedJobs = unfinishedJobs;
	}

	public TwAdsState() {	
	}

	public Date getLastRun() {
		return lastRun;
	}

	public List<AdsStatsAsyncRequest> getUnfinishedJobs() {
		return unfinishedJobs;
	}

	public void setUnfinishedJobs(List<AdsStatsAsyncRequest> unfinishedJobs) {
		this.unfinishedJobs = unfinishedJobs;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}
}
