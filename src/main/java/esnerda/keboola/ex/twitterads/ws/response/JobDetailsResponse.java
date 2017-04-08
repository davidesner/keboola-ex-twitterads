package esnerda.keboola.ex.twitterads.ws.response;

import esnerda.keboola.ex.twitterads.ws.request.AdsStatsAsyncRequest;
import twitter4j.models.ads.JobDetails;

/**
 * @author David Esner
 */
public class JobDetailsResponse {

	private final JobDetails jobDetails;
	private final AdsStatsAsyncRequest origRequest;

	public JobDetailsResponse(JobDetails jobDetails, AdsStatsAsyncRequest origRequest) {
		super();
		this.jobDetails = jobDetails;
		this.origRequest = origRequest;
	}

	public JobDetails getJobDetails() {
		return jobDetails;
	}

	public AdsStatsAsyncRequest getOrigRequest() {
		return origRequest;
	}

	public String getJobId() {
		if (jobDetails == null) {
			return null;
		}
		return jobDetails.getJobId();
	}

	@Override
	public boolean equals(Object obj) {
		if (getJobId() == null) {
			return false;
		}
		return getJobId().equals(((JobDetailsResponse)obj).getJobDetails().getJobId());
	}

	
}
