package zenjiro;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.GeoLocation;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.gson.Gson;

/**
 * WiMAXの障害情報を自動的につぶやくbot
 */
public class WimaxOutageBot implements Bot {
    /**
     * 場所
     */
    String location;

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
	final Scanner scanner = new Scanner(
					    new InputStreamReader(new URL(
									  "http://www.uqwimax.jp/service/information/maintenance/service-info.xml")
								  .openStream(), "UTF-8"));
	final Pattern urlPattern = Pattern
	    .compile(".+(http://www.uqwimax.jp/information/maintenance/post-[0-9]+.html).+");
	while (scanner.hasNextLine()) {
	    final Matcher urlMatcher = urlPattern.matcher(scanner.nextLine());
	    if (urlMatcher.find()) {
		final String url = urlMatcher.group(1);
		final Scanner scanner2 = new Scanner(new InputStreamReader(
									   new URL(url).openStream(), "UTF-8"));
		final Pattern locationPattern = Pattern
		    .compile("^<title>(【復旧】)?(ＷｉＭＡＸ.*|ＵＱ Ｗｉ－Ｆｉ)通信障害（(.+)）");
		boolean isWiFi = false;
		String location = null;
		String started = null;
		String startedDate = null;
		String finished = null;
		while (scanner2.hasNextLine()) {
		    final String line = scanner2.nextLine();
		    final Matcher locationMatcher = locationPattern.matcher(line);
		    if (locationMatcher.find()) {
			isWiFi = locationMatcher.group(2).equals("ＵＱ Ｗｉ－Ｆｉ");
			location = locationMatcher.group(3);
			this.location = location;
		    } else if (line.equals("<dt>発生日時</dt>")) {
			started = scanner2.nextLine().replaceFirst("^<dd>[0-9]+年0?", "")
			    .replaceFirst("</dd>$", "").replace("月0", "月");
			startedDate = started.replaceFirst(" .+", "");
		    } else if (line.equals("<dt>復旧日時</dt>")) {
			finished = scanner2.nextLine().replaceFirst("^<dd>[0-9]+年0?", "")
			    .replaceFirst("</dd>$", "").replace("月0", "月")
			    .replace(startedDate, "").trim();
			break;
		    }
		}
		scanner2.close();
		Logger.getAnonymousLogger().log(Level.INFO,
						"location = {0}, started = {1}, finished = {2}",
						new String[] { location, started, finished });
		// test start
		if (location == null) {
		    final Scanner scanner3 = new Scanner(new InputStreamReader(
									       new URL(url).openStream(), "UTF-8"));
		    while (scanner3.hasNextLine()) {
			Logger.getAnonymousLogger().info(scanner3.nextLine());
		    }
		    scanner3.close();
		}
		// test finished
		scanner.close();
		if (finished == null) {
		    if (isWiFi) {
			return started + "～ " + location + "でＵＱ Ｗｉ－Ｆｉ通信サービスが接続できない状態が発生しております。"
			    + url;
		    } else {
			return started + "～ " + location + "でＷｉＭＡＸ通信サービスが接続しづらい状態が発生しております。" + url;
		    }
		} else {
		    if (isWiFi) {
			return started + "～" + finished + " " + location
			    + "で通信障害により、ＵＱ Ｗｉ－Ｆｉ通信サービスが接続できない状態となっておりました。現在は復旧しております。" + url;
		    } else {
			return started + "～" + finished + " " + location
			    + "で通信障害により、ＷｉＭＡＸ通信サービスが接続しづらい状態となっておりました。現在は復旧しております。" + url;
		    }
		}
	    }
	}
	scanner.close();
	return null;
    }

    @Override
    public GeoLocation getLocation() throws IOException {
	if (this.location == null) {
	    return null;
	} else {
	    final URL url = new URL(
				    "https://maps.googleapis.com/maps/api/place/textsearch/json?sensor=false&key=AIzaSyBdIBQGXNuQpBwnWoB1GwS6gsFSsKI-f7M&query="
				    + URLEncoder.encode(this.location.replaceFirst("の周辺$", ""), "UTF-8"));
	    System.out.println("url = " + url);
	    final Response response = new Gson().fromJson(new InputStreamReader(url.openStream(),
										"UTF-8"), Response.class);
	    System.out.println("response = " + new Gson().toJson(response));
	    for (final Response.Result result : response.results) {
		return new GeoLocation(result.geometry.location.lat, result.geometry.location.lng);
	    }
	    return null;
	}
    }

    @Override
    public InputStream getImageStream() throws IOException {
	return null;
    }
}
