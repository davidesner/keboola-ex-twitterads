package esnerda.keboola.ex.twitterads.result.wrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import twitter4jads.models.ads.cards.AbstractAppCard;
import twitter4jads.models.ads.cards.TwitterCardType;
import twitter4jads.models.ads.cards.TwitterImageAppDownloadCard;
import twitter4jads.models.ads.cards.TwitterVideoAppDownloadCard;

/**
 * @author David Esner
 */
public class AppDownloadCardWrapper {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SerializedName("video_content_id")
	private String channelVideoId;

	@SerializedName("video_url")
	private String channelVideoUrl;

	@SerializedName("content_duration_seconds")
	private String channelVideoLength;

	@SerializedName("image_media_id")
	private String channelImageId;

	@SerializedName("video_poster_url")
	private String posterVideoUrl;

	@SerializedName("wide_app_image")
	private String wideAppImage;

	@SerializedName("wide_app_image_data")
	private String wideAppImageData;

	private String cardType;

	private String name;

	private String accountId;

	private TwitterCardType twitterCardType;

	private String previewUrl;

	private String cardUri;

	private Date updatedAt;

	private Date createdAt;

	private boolean deleted;

	private String iphoneAppId;

	private String googleplayAppId;

	private String ipadDeepLink;

	private String googleplayDeepLink;

	private String countryCode;

	private String appCta;

	private String iphoneDeepLink;

	private String ipadAppId;

	public AppDownloadCardWrapper() {
	}

	public AppDownloadCardWrapper(String iphoneAppId, String ipadAppId, String googleplayAppId,
			String iphoneDeepLink, String ipadDeepLink, String googleplayDeepLink,
			String countryCode, String appCta, String id, String channelVideoId,
			String channelVideoUrl, String channelVideoLength, String channelImageId,
			String posterVideoUrl, String wideAppImage, String wideAppImageData) {
		super();
		this.iphoneAppId = iphoneAppId;
		this.ipadAppId = ipadAppId;
		this.googleplayAppId = googleplayAppId;
		this.iphoneDeepLink = iphoneDeepLink;
		this.ipadDeepLink = ipadDeepLink;
		this.googleplayDeepLink = googleplayDeepLink;
		this.countryCode = countryCode;
		this.appCta = appCta;
		this.id = id;
		this.channelVideoId = channelVideoId;
		this.channelVideoUrl = channelVideoUrl;
		this.channelVideoLength = channelVideoLength;
		this.channelImageId = channelImageId;
		this.posterVideoUrl = posterVideoUrl;

	}

	public AppDownloadCardWrapper(AbstractAppCard camp, String accountId) {
		this.id = camp.getId();
		this.name = camp.getName();
		this.accountId = camp.getAccountId();
		this.twitterCardType = camp.getTwitterCardType();
		this.previewUrl = camp.getPreviewUrl();
		this.cardUri = camp.getCardUri();
		this.updatedAt = camp.getUpdatedAt();
		this.createdAt = camp.getCreatedAt();
		this.deleted = camp.getDeleted();
		this.iphoneAppId = camp.getIphoneAppId();
		this.ipadAppId = camp.getIpadAppId();
		this.googleplayAppId = camp.getGoogleplayAppId();
		this.iphoneDeepLink = camp.getIphoneDeepLink();
		this.ipadDeepLink = camp.getIpadDeepLink();
		this.googleplayDeepLink = camp.getGoogleplayDeepLink();
		this.countryCode = camp.getCountryCode();
		this.appCta = camp.getAppCta();

		if (camp instanceof TwitterVideoAppDownloadCard) {
			this.channelVideoId = ((TwitterVideoAppDownloadCard) camp).getChannelVideoId();
			this.channelVideoUrl = ((TwitterVideoAppDownloadCard) camp).getChannelVideoUrl();
			this.channelVideoLength = ((TwitterVideoAppDownloadCard) camp).getChannelVideoLength();
			this.channelImageId = ((TwitterVideoAppDownloadCard) camp).getChannelImageId();
			this.posterVideoUrl = ((TwitterVideoAppDownloadCard) camp).getPosterVideoUrl();
			this.cardType = "VideoAppDownloadCard";
		}

		if (camp instanceof TwitterImageAppDownloadCard) {
			this.wideAppImage = ((TwitterImageAppDownloadCard) camp).getWideAppImage();
			this.wideAppImageData = ((TwitterImageAppDownloadCard) camp).getWideAppImageData();
			this.cardType = "ImageAppDownloadCard";
		}
	}

	public String getChannelVideoId() {
		return channelVideoId;
	}

	public String getChannelVideoUrl() {
		return channelVideoUrl;
	}

	public String getChannelVideoLength() {
		return channelVideoLength;
	}

	public String getChannelImageId() {
		return channelImageId;
	}

	public String getPosterVideoUrl() {
		return posterVideoUrl;
	}

	public String getWideAppImage() {
		return wideAppImage;
	}

	public String getWideAppImageData() {
		return wideAppImageData;
	}

	public String getCardType() {
		return cardType;
	}

	public String getName() {
		return name;
	}

	public String getAccountId() {
		return accountId;
	}

	public TwitterCardType getTwitterCardType() {
		return twitterCardType;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public String getCardUri() {
		return cardUri;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public String getIphoneAppId() {
		return iphoneAppId;
	}

	public String getGoogleplayAppId() {
		return googleplayAppId;
	}

	public String getIpadDeepLink() {
		return ipadDeepLink;
	}

	public String getGoogleplayDeepLink() {
		return googleplayDeepLink;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getAppCta() {
		return appCta;
	}

	public String getIphoneDeepLink() {
		return iphoneDeepLink;
	}

	public String getIpadAppId() {
		return ipadAppId;
	}

	public static class Builder<T extends AbstractAppCard> {
		public static <T extends AbstractAppCard> List<AppDownloadCardWrapper> build(List<T> cards,
				String accountId) {
			List<AppDownloadCardWrapper> result = new ArrayList<>();
			if (cards == null) {
				return result;
			}
			cards.forEach(t -> result.add(new AppDownloadCardWrapper(t, accountId)));
			return result;
		}
	}
}
