package zenjiro;

import java.io.IOException;
import java.io.InputStream;

import twitter4j.GeoLocation;
import twitter4j.Twitter;

/**
 * 1つのbot
 */
public interface Bot {
    /**
     * @return Twitterインスタンス
     */
    public Twitter getTwitter();

    /**
     * @return 表示するメッセージ
     * @throws IOException 入出力例外
     */
    public String getMessage() throws IOException;

    /**
     * @return 位置
     * @throws IOException 入出力例外
     */
    public GeoLocation getLocation() throws IOException;

    /**
     * @return 画像
     * @throws IOException 入出力例外
     */
    public abstract InputStream getImageStream() throws IOException;
}
