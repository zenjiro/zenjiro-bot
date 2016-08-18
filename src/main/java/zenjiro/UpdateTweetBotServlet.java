package zenjiro;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

/**
 * サーブレット
 */
public class UpdateTweetBotServlet extends HttpServlet {
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
	throws IOException, ServletException {
	final boolean dryRun = false;
	response.setCharacterEncoding("UTF-8");
	response.setContentType("text/plain");
	for (final Bot bot : new Bot[] { new KawasakiFireBot(), new KawasakiWarnBot(),
					 new NanbuLineBot(), new TepcoUsageBot(), new TokyuLineBot(), new WimaxOutageBot() }) {
	    try {
		final Entry entry = new Entry(new Date(), bot.getClass().getName(),
					      bot.getMessage());
		Logger.getAnonymousLogger().info(entry.toString());
		Cache cache = null;
		try {
		    final CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
		    cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (final CacheException exception) {
		    exception.printStackTrace(response.getWriter());
		}
		if (cache == null || !((String) cache.get(bot.getClass().getCanonicalName())).equals(entry.message)) {
		    response.getWriter().println("情報が更新されました。" + entry);
		    Logger.getAnonymousLogger().info("情報が更新されました。");
		    try {
			final InputStream image = bot.getImageStream();
			if (image != null && !dryRun) {
			    bot.getTwitter().updateProfileImage(image);
			}
			final GeoLocation location = bot.getLocation();
			final StatusUpdate status = new StatusUpdate(entry.message);
			if (location != null) {
			    status.setLocation(location);
			}
			if (!dryRun) {
			    if (cache != null) {
				cache.put(bot.getClass().getCanonicalName(), entry.message);
			    }
			    bot.getTwitter().updateStatus(status);
			    Logger.getAnonymousLogger().info("発言に成功しました。" + status);
			}
		    } catch (final TwitterException exception) {
			exception.printStackTrace(response.getWriter());
		    }
		} else {
		    response.getWriter().println("情報は変化していません。" + entry.message);
		}
	    } catch (final SocketTimeoutException exception) {
		Logger.getAnonymousLogger().warning("データ取得がタイムアウトしました。" + exception.getMessage());
		exception.printStackTrace();
	    } catch (final IOException exception) {
		Logger.getAnonymousLogger().warning("データ取得に失敗しました。" + exception.getMessage());
		exception.printStackTrace();
	    }
	}
    }
}
