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
 * 川崎市消防局の災害情報
 */
public class KawasakiWarnBot implements Bot {
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
									  "http://www.jma.go.jp/jp/warn/1413000.html").openStream(), "UTF-8"));
	String title = "";
	String description = null;
	while (scanner.hasNextLine()) {
	    final String line = scanner.nextLine();
	    if (line.equals("神奈川県の注意警戒事項<br>")) {
		title = scanner.nextLine().replaceAll("<[^>]+>", "").replace("　", "");
	    } else if (line.equals("<br>＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br>")) {
		description = "";
		for (String line2 = scanner.nextLine(); line2.length() > 0
			 && !line2.equals("</td></tr></table>"); line2 = scanner.nextLine()) {
		    description = description
			+ line2.replaceAll("<[^>]+>", "").replaceFirst("^　+", "") + "\n";
		}
		description = description.replaceFirst("\n+$", "");
		description = description.substring(0,
						    Math.min(140 - 1 - title.length(), description.length()));
	    }
	}
	scanner.close();
	return title + "\n" + description;
    }

    @Override
    public GeoLocation getLocation() {
	return null;
    }

    @Override
    public InputStream getImageStream() throws IOException {
	return null;
    }
}
