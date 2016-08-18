package zenjiro;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * 永続化マネージャファクトリ
 */
public final class PMF {
    /**
     * インスタンス
     */
    private static final PersistenceManagerFactory pmfInstance = JDOHelper
	.getPersistenceManagerFactory("transactions-optional");

    /**
     * 非公開のコンストラクタ
     */
    private PMF() {
    }

    /**
     * @return 永続化マネージャファクトリ
     */
    public static PersistenceManagerFactory get() {
	return pmfInstance;
    }
}
