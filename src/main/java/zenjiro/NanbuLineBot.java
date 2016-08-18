package zenjiro;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Formatter;
import java.util.Scanner;

import twitter4j.GeoLocation;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * 南武線の運行状況を自動的につぶやくbot
 */
public class NanbuLineBot implements Bot {
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
	final Scanner scanner = new Scanner(new InputStreamReader(new URL(
									  "http://traininfo.jreast.co.jp/train_info/kanto.aspx").openStream(), "UTF-8"));
	while (scanner.hasNextLine()) {
	    if (scanner.nextLine().matches(
					   ".+<font class=\"px12\" size=\"3\">(<a href=[^>]+>)?南武線(</a>)?</font>.+")) {
		scanner.nextLine();
		final String date = scanner.nextLine().replaceFirst("^\\s+<[^>]+><[^>]+>2015年", "")
		    .replaceFirst("<[^>]+><[^>]+>$", "");
		scanner.nextLine();
		final String message = scanner.nextLine().replaceFirst("^\\s+<[^>]+><[^>]+>", "")
		    .replaceAll("<[^>]+>", "");
		scanner.close();
		final Formatter formatter = new Formatter();
		final String ret = formatter.format("%s（%s）", new Object[] { message, date })
		    .toString();
		formatter.close();
		return ret;
	    }
	}
	scanner.close();
	return "南武線に遅れの情報はありません。";
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
