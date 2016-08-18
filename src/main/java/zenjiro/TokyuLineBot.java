package zenjiro;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import twitter4j.GeoLocation;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * 東急線の運行状況を自動的につぶやくbot
 */
public class TokyuLineBot implements Bot {
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
	final Scanner scanner = new Scanner(new InputStreamReader(new URL(
									  "http://www.tokyu.co.jp/unten2.cgi").openStream(), "SJIS"));
	while (scanner.hasNextLine()) {
	    final String line = scanner.nextLine();
	    if (line.contains("document.write")) {
		String description = line.replace("<br>", "\n").replaceFirst("^[^']+'", "")
		    .replaceFirst("'[^']+$", "").replaceFirst(".+分　現在", "")
		    .replaceFirst("<BR>$", "").trim();
		description = description.substring(0, Math.min(140, description.length()));
		scanner.close();
		return description;
	    }
	}
	scanner.close();
	return null;
    }

    @Override
    public GeoLocation getLocation() throws IOException {
	return null;
    }

    @Override
    public InputStream getImageStream() throws IOException {
	return null;
    }
}
