package esnerda.keboola.ex.twitterads.result.wrapper;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import esnerda.keboola.ex.twitterads.ws.response.AdStatsResponseWrapper;
import twitter4jads.models.ads.TwitterAdStatistics;
import twitter4jads.models.ads.TwitterEntityStatistics;


/**
 * @author David Esner
 */
public class AdsWrapperBuilder {

	private static Instant startTime;
	private static ChronoUnit granularityUnit;
	private static int seriesLength;
	private static final String[] CUSTOM_SETTERS = new String[] {"setTimeStamp", "setEntityId"};

	public static List<AdStatsWrapper> buildFromResponse(AdStatsResponseWrapper resp) throws Exception {	
		List<AdStatsWrapper> result = new ArrayList<>();
		init(resp);		
		for (TwitterEntityStatistics entityStats : resp.getEntities()) {
			String currEntityId = entityStats.getId();
			//just first record, do not support segmentation yet
			buildRecordsForEntity(currEntityId, entityStats.getIdData().get(0).getMetrics(), result);
		}
		
		return result;		
	}

	/**
	 * Build ads wrappers for entity
	 * @param entityId
	 * @param stats
	 * @param result
	 * @throws Exception
	 */
	private static void buildRecordsForEntity(String entityId, TwitterAdStatistics stats, List<AdStatsWrapper> result) throws Exception {
		for (int i = 0; i < seriesLength; i++) {
			AdStatsWrapper wr = new AdStatsWrapper();
			wr.setEntityId(entityId);
			wr.setTimeStamp(DateTimeFormatter.ISO_INSTANT.format(startTime.plus(i, granularityUnit)));			
			setAllFieldsUsingReflection(stats, wr, i);
			result.add(wr);
		}		
	}

	/**
	 * Invoke all setters in adsWrapper according to setters;
	 * @param stats
	 * @param wr
	 * @param currIndex
	 * @throws Exception 
	 */
	private static void setAllFieldsUsingReflection(TwitterAdStatistics stats, AdStatsWrapper wr, int currIndex) throws Exception {
		Method[] allMethods = wr.getClass().getMethods();

		for (Method method : allMethods) {
			String setterName = method.getName();
			if (setterName.startsWith("set") &&  !isCustomMethod(setterName)) {				
					String currValue = getAccordingValue(stats, setterName, currIndex);					
					method.invoke(wr, currValue);
			}
		}
	}

	/**
	 * Get string value according to its opposite setter;
	 * @param stats
	 * @param setterName
	 * @param currIndex
	 * @return
	 * @throws Exception
	 */
	private static String getAccordingValue(TwitterAdStatistics stats, String setterName, int currIndex) throws Exception {
		String getterName = setterName.replaceFirst("set", "get");
		//fix so reflection works
		switch (getterName) {
			case "getVideo3s100pctViews":
				getterName = "getVideo3s100PercentViews";
				break;
		}
		Object invoke = stats.getClass().getMethod(getterName, null).invoke(stats, null);
		return invoke!=null ? ((String []) invoke)[currIndex] : "";
	}
	
	private static void init(AdStatsResponseWrapper stats) {
		startTime = stats.getStartTime();
		granularityUnit = getTimeWindowForGranularity(stats.getGranularity());
		seriesLength = calculateTimeSeriesLength(stats);
	}

	private static ChronoUnit getTimeWindowForGranularity(String gran) {
		switch (gran) { 
		case "HOUR":
			return  ChronoUnit.HOURS;
		case "DAY":
			return ChronoUnit.DAYS;
		default:
			throw new IllegalArgumentException();
		}
	}

	private static int calculateTimeSeriesLength(AdStatsResponseWrapper stats) {
		long window = granularityUnit.between(stats.getStartTime(), stats.getEndTime());		
		return Math.toIntExact(window);
	}

	private static boolean isCustomMethod(String method) {
		for (String setter : CUSTOM_SETTERS) {
			if (setter.equals(method)) {
				return true;
			}
		}

		return false;
	}
	
}
