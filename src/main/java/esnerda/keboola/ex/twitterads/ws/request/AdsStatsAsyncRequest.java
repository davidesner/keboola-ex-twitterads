	package esnerda.keboola.ex.twitterads.ws.request;

import java.time.Instant;
import java.util.List;

import twitter4jads.models.Granularity;
import twitter4jads.models.TwitterSegmentationType;
import twitter4jads.models.ads.Placement;
import twitter4jads.models.ads.TwitterAdObjective;
import twitter4jads.models.ads.TwitterEntityType;



/**
 * @author David Esner
 */
public class AdsStatsAsyncRequest {

	private final TwitterEntityType type;
	private final String accountId;
	private final List<String> entityIds;
	private final Instant startTime;
	private final Instant endTime;
	private boolean withDeleted;
	private Granularity granularity;
	private TwitterAdObjective objective;
	private Placement placement;
	// not supported yet
	private com.google.common.base.Optional<TwitterSegmentationType> segment;

	public AdsStatsAsyncRequest(String accountId, List<String> entityIds, Instant startTime, Instant endTime, TwitterEntityType type, Granularity granularity) {
		super();
		this.accountId = accountId;
		this.entityIds = entityIds;
		this.startTime = startTime;
		this.endTime = endTime;
		this.granularity = granularity;
		this.objective = null;
		this.placement = Placement.ALL_ON_TWITTER;
		this.type = type;
	}

	public AdsStatsAsyncRequest(String accountId, List<String> entityIds, Instant startTime, Instant endTime,
			boolean withDeleted, Granularity granularity, TwitterAdObjective objective, Placement placement,
			TwitterSegmentationType segment, TwitterEntityType type) {
		super();
		this.accountId = accountId;
		this.entityIds = entityIds;
		this.startTime = startTime;
		this.endTime = endTime;
		this.withDeleted = withDeleted;
		this.granularity = granularity;
		this.objective = objective;
		this.placement = placement;
		this.segment = com.google.common.base.Optional.absent();
		this.type = type;
	}

	public String getAccountId() {
		return accountId;
	}

	public List<String> getEntityIds() {
		return entityIds;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public long getStartTimeEpoch() {
		return startTime.toEpochMilli();
	}

	public Instant getEndTime() {
		return endTime;
	}

	public long getEndTimeEpoch() {
		return endTime.toEpochMilli();
	}

	public boolean isWithDeleted() {
		return withDeleted;
	}

	public Granularity getGranularity() {
		return granularity;
	}

	public TwitterAdObjective getObjective() {
		return objective;
	}

	public Placement getPlacement() {
		return placement;
	}

	public com.google.common.base.Optional<TwitterSegmentationType> getSegment() {
		return segment;
	}

	public TwitterEntityType getType() {
		return type;
	}

	

}
