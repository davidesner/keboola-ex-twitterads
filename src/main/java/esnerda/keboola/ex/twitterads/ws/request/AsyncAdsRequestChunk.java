package esnerda.keboola.ex.twitterads.ws.request;

import java.util.List;

/**
 * @author David Esner
 * @param <E>
 */
public class AsyncAdsRequestChunk {

	private static final int MAX_CHUNK_SIZE = 1000;
	private final List<AdsStatsAsyncRequest> chunkList;
	private final String chunkAccountId;

	public AsyncAdsRequestChunk(List<AdsStatsAsyncRequest> chunkList, String accountId) {
		if (chunkList.size() > MAX_CHUNK_SIZE) {
			throw new IllegalArgumentException("The asyncAdRequest chunk size can be max " + MAX_CHUNK_SIZE);
		}
		this.chunkList = chunkList;
		this.chunkAccountId = accountId;
	}

	public List<AdsStatsAsyncRequest> getRequestList() {
		return chunkList;
	}

	public String getChunkAccountId() {
		return chunkAccountId;
	}

	public int size() {
		return chunkList.size();
	}

	

}
