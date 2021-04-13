import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import esnerda.keboola.components.result.IResultWriter;
import esnerda.keboola.components.result.impl.DefaultBeanResultWriter;
import esnerda.keboola.ex.twitterads.result.wrapper.AppDownloadCardWrapper;
import twitter4jads.BaseAdsListResponse;
import twitter4jads.BaseAdsListResponseIterable;
import twitter4jads.models.ads.cards.TwitterVideoAppDownloadCard;

/**
 * @author David Esner
 */
public class Test {
	private static <T> List<T> transformListResponseToList(BaseAdsListResponseIterable<T> listResp) {
		List<T> resultList = new ArrayList<>();
		for (BaseAdsListResponse<T> allCampaign : listResp) {
			resultList.addAll(allCampaign.getData());
		}
		return resultList;
	}
	
	public static void main(String[] argv) throws Exception {
		String sample = "{\n" + 
				"  \"request\": {\n" + 
				"    \"params\": {\n" + 
				"      \"card_type\": \"video_app_download\",\n" + 
				"      \"card_ids\": [\n" + 
				"        \"5a4z3\"\n" + 
				"      ],\n" + 
				"      \"account_id\": \"18ce54d4x5t\"\n" + 
				"    }\n" + 
				"  },\n" + 
				"  \"next_cursor\": null,\n" + 
				"  \"data\": [\n" + 
				"    {\n" + 
				"      \"name\": \"video app download\",\n" + 
				"      \"googleplay_app_id\": \"com.twitter.android\",\n" + 
				"      \"media_url\": \"https://video.twimg.com/amplify_video/vmap/958231855240589313.vmap\",\n" + 
				"      \"video_owner_id\": \"756201191646691328\",\n" + 
				"      \"media_key\": \"13_958231855240589313\",\n" + 
				"      \"id\": \"5a4z3\",\n" + 
				"      \"country_code\": \"US\",\n" + 
				"      \"created_at\": \"2018-01-30T07:00:24Z\",\n" + 
				"      \"card_uri\": \"card://958233417929261056\",\n" + 
				"      \"updated_at\": \"2018-01-30T07:00:24Z\",\n" + 
				"      \"poster_media_url\": \"https://pbs.twimg.com/amplify_video_thumb/958231855240589313/img/rjhswYG084qYYgF6.jpg\",\n" + 
				"      \"app_cta\": \"INSTALL\",\n" + 
				"      \"deleted\": false,\n" + 
				"      \"card_type\": \"VIDEO_APP_DOWNLOAD\"\n" + 
				"    }\n" + 
				"  ]\n" + 
				"}";
		TwitterVideoAppDownloadCard card = new TwitterVideoAppDownloadCard();
		Type type = new TypeToken<BaseAdsListResponse<TwitterVideoAppDownloadCard>>() {
		}.getType();
		Gson gson = new Gson();
		BaseAdsListResponse<Type> data = gson.fromJson(sample, type);
		//List<TwitterVideoAppDownloadCard> res = Test.transformListResponseToList(data);
		TwitterVideoAppDownloadCard res = (TwitterVideoAppDownloadCard) data.getData().get(0);
		
		IResultWriter<AppDownloadCardWrapper> appCardWriter = new DefaultBeanResultWriter("app_download_cards.csv", null);
		appCardWriter.initWriter("C:\\Users\\esner\\Documents\\Prace\\KBC\\TwitterAds\\data", AppDownloadCardWrapper.class);
		
		List<TwitterVideoAppDownloadCard> lst = new ArrayList<TwitterVideoAppDownloadCard>();
		lst.add(res);
		appCardWriter.writeAllResults(
				AppDownloadCardWrapper.Builder.build(lst, "a123"));
		appCardWriter.closeAndRetrieveMetadata();
	}
}
