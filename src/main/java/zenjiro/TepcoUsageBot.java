package zenjiro;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Formatter;

import twitter4j.GeoLocation;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.gson.Gson;

/**
 * 東京電力の電力使用状況を自動的につぶやくbot
 */
public class TepcoUsageBot implements Bot {
    /**
     * メッセージ
     */
    String message;
    /**
     * 使用率
     */
    long percent = -1L;
    /**
     * 計画停電中かどうか
     */
    boolean isBlackout = false;

    @Override
    public Twitter getTwitter() {
	final Twitter twitter = new TwitterFactory().getInstance();
	twitter.setOAuthConsumer("CONSUMER_KEY",
				 "CONSUMER_SECRET");
	twitter.setOAuthAccessToken(new AccessToken(
						    "ACCESS_TOKEN",
						    "ACCESS_TOKEN_SECRET"));
	return twitter;
    }

    @Override
    public String getMessage() throws IOException {
	final Response response = new Gson().fromJson(new InputStreamReader(new URL(
										    "http://tepco-usage-api.appspot.com/latest.json").openStream()), Response.class);
	this.percent = Math.round((double) response.usage / response.capacity * 100);
	this.isBlackout = response.saving;
	final Formatter formatter = new Formatter();
	final String ret = formatter.format(
					    "%s%d時台の消費電力：%,d万kW/%,d万kW（%d%%）",
					    new Object[] { response.saving ? "[計画停電中]" : "", Integer.valueOf(response.hour),
							   Integer.valueOf(response.usage), Integer.valueOf(response.capacity),
							   Long.valueOf(this.percent) }).toString();
	formatter.close();
	return ret;
    }

    @Override
    public GeoLocation getLocation() throws IOException {
	return null;
    }

    @Override
    public InputStream getImageStream() throws IOException {
	if (this.percent < 0L) {
	    return null;
	}
	if (this.percent < 80L) {
	    return getClass().getResourceAsStream(
						  this.isBlackout ? "tepco-green-black.png" : "tepco-green.png");
	}
	if (this.percent < 90L) {
	    return getClass().getResourceAsStream(
						  this.isBlackout ? "tepco-orange-black.png" : "tepco-orange.png");
	}
	return getClass().getResourceAsStream(
					      this.isBlackout ? "tepco-red-black.png" : "tepco-red.png");
    }

    /**
     * 応答
     */
    static class Response {
	/**
	 * 供給可能量
	 */
	int capacity;
	/**
	 * 計画停電中かどうか
	 */
	boolean saving;
	/**
	 * 時
	 */
	int hour;
	/**
	 * 月
	 */
	int month;
	/**
	 * 使用量
	 */
	int usage;
	/**
	 * 更新日時
	 */
	String capacity_updated;
	/**
	 * 日
	 */
	int day;
    }
}
