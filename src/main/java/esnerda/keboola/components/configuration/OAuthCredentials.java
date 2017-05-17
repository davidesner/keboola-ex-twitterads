package esnerda.keboola.components.configuration;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class OAuthCredentials {

	private static final String[] REQUIRED_FIELDS = {"data", "appKey", "appSecret"};
    private final Map<String, Object> parametersMap;

    @JsonProperty("#data")
    private String data;
    @JsonProperty("appKey")
    private String appKey;
    @JsonProperty("#appSecret")
    private String appSecret;

    public OAuthCredentials() {
        parametersMap = new HashMap<>();

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    @JsonCreator
    public OAuthCredentials(@JsonProperty("#data") String data, @JsonProperty("appKey") String appKey,
            @JsonProperty("#appSecret") String appSecret) throws ParseException {
        parametersMap = new HashMap<>();

        this.data = data;
        this.appKey = appKey;
        this.appSecret = appSecret;        
       
        parametersMap.put("data", data);
        parametersMap.put("appKey", appKey);
        parametersMap.put("appSecret", appSecret);

    }

    public String[] getREQUIRED_FIELDS() {
        return REQUIRED_FIELDS;
    }

    public Map<String, Object> getParametersMap() {
        return parametersMap;
    }

}