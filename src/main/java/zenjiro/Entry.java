package zenjiro;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 1エントリ
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Entry {
    /**
     * ID
     */
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    Long id;
    /**
     * 日時
     */
    @Persistent
    Date date;
    /**
     * 名前
     */
    @Persistent
    String name;
    /**
     * メッセージ
     */
    @Persistent
    String message;

    /**
     * コンストラクタ
     * @param date 日時
     * @param name 名前
     * @param message メッセージ
     */
    public Entry(Date date, String name, String message) {
	this.date = date;
	this.name = name;
	this.message = message;
    }

    @Override
    public String toString() {
	final DateFormat format = DateFormat.getDateTimeInstance();
	format.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
	return "[date = " + format.format(this.date) + ", name = " + this.name + ", message = "
	    + this.message + "]";
    }
}
