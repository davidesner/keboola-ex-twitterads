package esnerda.keboola.ex.twitterads.ws.request;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import twitter4jads.models.Granularity;
import twitter4jads.models.ads.AdAccount;
import twitter4jads.models.ads.TwitterEntityType;



/**
 * @author David Esner
 */
public class AdsStatsAsyncRequestBuilder {

	private static final int MAX_DAY_INTERVAL = 90;
	private static final int MAX_ENTITY_COUNT = 20;
	private static final int MAX_CHUNK_REQ_COUNT = 100;

	public List<AsyncAdsRequestChunk> buildAdRequestsChunks(TwitterEntityType type, AdAccount account, List<String> entityIds, Instant startTime, Instant endTime, Granularity gran) {
		List<AdsStatsAsyncRequest> requests = new ArrayList<>();
		List<List<String>> entityChunks = Lists.partition(entityIds, MAX_ENTITY_COUNT);
		// split by allowed entity chunks	
		TimeZone tz = TimeZone.getTimeZone(ZoneId.of(account.getTimezone()));
		for (List<String> entityChunk : entityChunks) {
			TimeWindowIterator tIt = new TimeWindowIterator(startTime, endTime, tz);
			//iterate over time windows
			while (tIt.hasNext()) {
				TimeWindow tw = tIt.next();
				requests.add(buildRequest(account.getId(), entityChunk, tw, type, gran));
			}
		}

		return buildChunkList(requests);
	}

	/**
	 * Build request entity
	 * @param accountId
	 * @param eIds
	 * @param window
	 * @return
	 */
	private AdsStatsAsyncRequest buildRequest(String accountId, List<String> eIds, TimeWindow window, TwitterEntityType type, Granularity gran) {
		return new AdsStatsAsyncRequest(accountId, eIds, window.getStartTime(), window.getEndTime(), type, gran);
	}

	/**
	 * Split requests into allowed chunks
	 * @param requests
	 * @return
	 */
	private List<AsyncAdsRequestChunk> buildChunkList(List<AdsStatsAsyncRequest> requests) {
		List<List<AdsStatsAsyncRequest>> chunks = Lists.partition(requests, MAX_CHUNK_REQ_COUNT);
		return chunks.stream().map(c -> new AsyncAdsRequestChunk(c, requests.get(0).getAccountId())).collect(Collectors.toList());
	}

	/* Helper classes */
	class  TimeWindowIterator implements Iterator<TimeWindow>{
		private final Instant startTime;
		private final Instant endTime;
		private long cursor = 0;
		private final long totalChunks;

		public TimeWindowIterator(Instant startTime, Instant endTime, TimeZone tz) {
			super();
			this.startTime = convertToTimezoneMidnight(startTime, tz);
			this.endTime = convertToTimezoneMidnight(endTime, tz);
			this.totalChunks = getNumberOfChunks();
		}

		public boolean hasNext() {
			return cursor < totalChunks;
		}		

		private long getNumberOfChunks() {
			return (int) Math.ceil((double) ChronoUnit.DAYS.between(startTime, endTime) / (double) MAX_DAY_INTERVAL);
		}

		@Override
		public TimeWindow next() {
			Instant startWindow = getNextStart();
			Instant endWindow = getNextEnd();
			cursor ++;
			return new TimeWindow(startWindow, endWindow);
		}

		private Instant convertToTimezoneMidnight(Instant time, TimeZone tz) {
			return Instant.ofEpochMilli(time.truncatedTo(ChronoUnit.DAYS).toEpochMilli() + tz.getOffset(time.toEpochMilli()));
		}

		private Instant getNextStart() {
			Instant candid = startTime.plus(cursor * MAX_DAY_INTERVAL, ChronoUnit.DAYS);
			if (candid.isAfter(endTime)) {
				endTime.toEpochMilli();
			}
			return candid.isAfter(endTime) ? startTime.plus((cursor-1) * MAX_DAY_INTERVAL, ChronoUnit.DAYS) : candid;
		}

		private Instant getNextEnd() {
			Instant candid = startTime.plus((cursor+1) * MAX_DAY_INTERVAL, ChronoUnit.DAYS);
			return candid.isAfter(endTime) ? endTime : candid;
		}
	}
	
	class TimeWindow {
		private final Instant startTime;
		private final Instant endTime;

		public TimeWindow(Instant startTime, Instant endTime) {
			super();
			this.startTime = startTime;
			this.endTime = endTime;
		}

		public Instant getStartTime() {
			return startTime;
		}

		public Instant getEndTime() {
			return endTime;
		}

	}

}
