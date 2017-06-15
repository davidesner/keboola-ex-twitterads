package esnerda.keboola.ex.twitterads.ws.request;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import twitter4j.models.ads.TwitterEntityType;

/**
 * @author David Esner
 */
public class AdsStatsAsyncRequestBuilder {

	private static final int MAX_DAY_INTERVAL = 90;
	private static final int MAX_ENTITY_COUNT = 20;
	private static final int MAX_CHUNK_REQ_COUNT = 100;

	public List<AsyncAdsRequestChunk> buildAdRequestsChunks(TwitterEntityType type, String accountId, List<String> entityIds, Instant startTime, Instant endTime) {
		List<AdsStatsAsyncRequest> requests = new ArrayList<>();
		List<List<String>> entityChunks = Lists.partition(entityIds, MAX_ENTITY_COUNT);
		// split by allowed entity chunks	
		
		for (List<String> entityChunk : entityChunks) {
			TimeWindowIterator tIt = new TimeWindowIterator(startTime, endTime);
			//iterate over time windows
			while (tIt.hasNext()) {
				TimeWindow tw = tIt.next();
				requests.add(buildRequest(accountId, entityChunk, tw, type));
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
	private AdsStatsAsyncRequest buildRequest(String accountId, List<String> eIds, TimeWindow window, TwitterEntityType type) {
		return new AdsStatsAsyncRequest(accountId, eIds, window.getStartTime(), window.getEndTime(), type);
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

		public TimeWindowIterator(Instant startTime, Instant endTime) {
			super();
			this.startTime = startTime.truncatedTo(ChronoUnit.HOURS);
			this.endTime = endTime.truncatedTo(ChronoUnit.HOURS);
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
