package zenjiro;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import twitter4j.GeoLocation;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.gson.Gson;

/**
 * 川崎市消防局の災害情報
 */
public class KawasakiFireBot implements Bot {
    /**
     * メッセージ
     */
    String message;

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
	// 2012年2月29日（水）12:41時点ではEUC-JPで、15:32にShift_JISに変わった。
	// 2012年10月15日（月）0:01以前はhttp://www.city.kawasaki.jp/84/84sirei/saigai/index.htmで、
	// 0:02にhttp://sc.city.kawasaki.jp/saigai/index.htmに変わった模様。
	final Scanner scanner = new Scanner(new InputStreamReader(new URL(
									  "http://sc.city.kawasaki.jp/saigai/index.htm").openStream(), "JISAutoDetect"));
	final List<String> messages = new ArrayList<String>();
	while (scanner.hasNextLine()) {
	    if (scanner.nextLine().matches("      <B><FONT size=\"?\\+1\"?>")) {
		final String message = scanner.nextLine().replaceAll("<[^>]+>", "").trim()
		    .replaceFirst("^　+", "");
		Logger.getAnonymousLogger().info("message = " + message);
		if (message.length() > 0) {
		    messages.add(message);
		}
	    }
	}
	scanner.close();
	Logger.getAnonymousLogger().info("messages = " + messages);
	if (messages.size() > 0) {
	    final String message = StringUtils.join(messages, "\n");
	    this.message = message.substring(0, Math.min(140 - 1, message.length()));
	}
	return this.message;
    }

    @Override
    public GeoLocation getLocation() throws IOException {
	if (this.message == null) {
	    return null;
	} else {
	    final Matcher locationMatcher = Pattern.compile("分頃　([^\n]+)付近").matcher(this.message);
	    if (locationMatcher.find()) {
		final URL url = new URL(
					"https://maps.googleapis.com/maps/api/place/textsearch/json?sensor=false&key=AIzaSyBdIBQGXNuQpBwnWoB1GwS6gsFSsKI-f7M&query="
					+ URLEncoder.encode(locationMatcher.group(1), "UTF-8"));
		{
		    final Scanner scanner = new Scanner(new InputStreamReader(url.openStream(),
									      "UTF-8"));
		    while (scanner.hasNextLine()) {
			System.out.println(scanner.nextLine());
		    }
		    scanner.close();
		}
		final Response response = new Gson().fromJson(
							      new InputStreamReader(url.openStream(), "UTF-8"), Response.class);
		Logger.getAnonymousLogger().info("url = " + url + ", response = " + response);
		for (final Response.Result result : response.results) {
		    return new GeoLocation(result.geometry.location.lat,
					   result.geometry.location.lng);
		}
		return null;
	    } else {
		return null;
	    }
	}
    }

    @Override
    public InputStream getImageStream() throws IOException {
	return null;
    }
}
