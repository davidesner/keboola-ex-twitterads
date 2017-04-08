package esnerda.keboola.ex.twitterads.ws.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import twitter4j.BaseAdsListResponse;
import twitter4j.models.ads.TwitterEntityStatistics;

/**
 * @author David Esner
 */
public class AdStatsResponseWrapper {
	
	private static final String KEY_START_TIME = "start_time";
	private static final String KEY_END_TIME = "end_time";
	private static final String KEY_GRANULARITY = "granularity";

	private final BaseAdsListResponse<TwitterEntityStatistics> currResponse;
	private final Instant startTime;
	private final Instant endTime;
	private final String granularity;

	public AdStatsResponseWrapper (BaseAdsListResponse<TwitterEntityStatistics> response) throws Exception {
		if (response == null) {
			throw new Exception("Entity statistics si null!!");
		}
		this.currResponse = response;
		if (response.getRequest() == null) {
			throw new Exception("Twitter stats response request is null. " +response.toString());
		}
		Map<String, Object> params =  response.getRequest().getParams();
		this.startTime = Instant.parse((String) params.get(KEY_START_TIME));
		this.endTime = Instant.parse((String) params.get(KEY_END_TIME));
		this.granularity = (String) params.get(KEY_GRANULARITY);
	}
	public BaseAdsListResponse<TwitterEntityStatistics> getCurrResponse() {
		return currResponse;
	}
	public Instant getStartTime() {
		return startTime;
	}
	public Instant getEndTime() {
		return endTime;
	}
	
	public String getGranularity() {
		return granularity;
	}
	public List<TwitterEntityStatistics> getEntities() {
		return currResponse.getData();
	}
}
