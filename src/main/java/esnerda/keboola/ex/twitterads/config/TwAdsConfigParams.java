package esnerda.keboola.ex.twitterads.config;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import esnerda.keboola.components.configuration.IKBCParameters;
import esnerda.keboola.components.configuration.ValidationException;
import twitter4j.models.Granularity;
import twitter4j.models.ads.TwitterEntityType;

/**
 * @author David Esner
 */
public class TwAdsConfigParams extends IKBCParameters {
	private final static String[] REQUIRED_FIELDS = { "consumerSecret", "accessToken", "accessTokenSecret", "consumerKey", "accountId", "since"};
	private final Map<String, Object> parametersMap;

	/* auth */
	@JsonProperty("#consumerSecret")
	private String consumerSecret;
	
	@JsonProperty("#accessToken")
	private String accessToken;
	
	@JsonProperty("#accessTokenSecret")
	private String accessTokenSecret;
	
	@JsonProperty("consumerKey")
	private String consumerKey;
	
	@JsonProperty("accountId")
	private String accountId;

	@JsonProperty("since")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date since; 

	@JsonProperty("entityType")
	private String entityType;

	@JsonProperty("granularity")
	private String granularity;

	@JsonProperty("includeDeleted")
	private Boolean includeDeleted;
	
	@JsonProperty("entityDatasets")
	private List<String> entityDatasets;	

	@JsonProperty("incremental")
	private Boolean incremental;
	
	@JsonCreator	
	public TwAdsConfigParams(@JsonProperty("#consumerSecret") String consumerSecret,
			@JsonProperty("#accessToken") String accessToken,
			@JsonProperty("#accessTokenSecret") String accessTokenSecret,
			@JsonProperty("consumerKey") String consumerKey, 
			@JsonProperty("accountId") String accountId,
			@JsonProperty("since") Date since, 
			@JsonProperty("entityType") String entityType,
			@JsonProperty("granularity") String granularity, 
			@JsonProperty("incremental") Boolean incremental,
			@JsonProperty("includeDeleted") Boolean includeDeleted,
			@JsonProperty("entityDatasets") List<String> entityDatasets) {
		super();
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
		this.consumerKey = consumerKey;
		this.accountId = accountId;
		this.since = since;
		this.entityType = Optional.ofNullable(entityType).orElse(TwitterEntityType.CAMPAIGN.name());
		this.granularity = Optional.ofNullable(granularity).orElse(Granularity.HOUR.name());
		this.incremental = Optional.ofNullable(incremental).orElse(true);
		this.includeDeleted = Optional.ofNullable(includeDeleted).orElse(false);
		this.entityDatasets = Optional.ofNullable(entityDatasets).orElse(Collections.EMPTY_LIST);

		// set param map
		parametersMap = new HashMap<>();
		parametersMap.put("consumerSecret", consumerSecret);
		parametersMap.put("accessToken", accessToken);
		parametersMap.put("accessTokenSecret", accessTokenSecret);
		parametersMap.put("consumerKey", consumerKey);
		parametersMap.put("accountId", accountId);
		parametersMap.put("since", since);
		parametersMap.put("entityType", entityType);
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getAccountId() {
		return accountId;
	}

	public Date getSince() {
		return since;
	}

	public String getEntityType() {
		return entityType;
	}
	
	public TwitterEntityType getEntityTypeEnum() {
		return TwitterEntityType.valueOf(entityType);
	}

	public String getGranularity() {
		return granularity;
	}

	public Granularity getGranularityEnum() {
		return Granularity.valueOf(granularity);
	}

	public static enum EntityDatasets {
		CAMPAIGN,
		LINE_ITEM;
		
		public static boolean isValid(String test) {
			for (EntityDatasets c : EntityDatasets.values()) {
				if (c.name().equals(test)) {
					return true;
				}
			}
			return false;
		}
	}

	

	public Boolean getIncludeDeleted() {
		return includeDeleted;
	}

	public List<String> getEntityDatasets() {
		return entityDatasets;
	}

	public Boolean getIncremental() {
		return incremental;
	}

	@Override
	protected String[] getRequiredFields() {
		return REQUIRED_FIELDS;
	}
	@Override
	protected boolean validateParametres() throws ValidationException {
		// validate date format
		String error = "";

		error += this.missingFieldsMessage(parametersMap);
		if (!this.isValidEntType(entityType)) {
			error += "Invalid Entity Type";
		}
		if (!this.isValidGranularity(granularity)) {
			error += "Invalid granularity parameter";
		}
		if (!this.isDatasetTypes()) {
			error += "Invalid dataset type parameter";
		}

		if (error.equals("")) {
			return true;
		} else {
			throw new ValidationException("Invalid configuration parameters!", "Config validation error: " + error,
					null);
		}
	}

	private boolean isDatasetTypes() {
		for (String ds : entityDatasets) {
			if(!EntityDatasets.isValid(ds)) {
				return false;
			}
		}
		return true;
	}
	private boolean isValidEntType(String type) {
			for (TwitterEntityType c : TwitterEntityType.values()) {
				if (c.name().equals(type)) {
					return true;
				}
			}
			return false;
		}
	
	private boolean isValidGranularity(String type) {
		for (Granularity c : Granularity.values()) {
			if (c.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
	

}
