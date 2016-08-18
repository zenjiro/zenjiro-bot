package zenjiro;

/**
 * Google Places APIの応答
 */
class Response {
    /**
     * 結果の一覧
     */
    Result[] results;

    /**
     * 結果
     */
    static class Result {
	/**
	 * 位置
	 */
	Geometry geometry;

	/**
	 * 位置
	 */
	static class Geometry {
	    /**
	     * 位置
	     */
	    Location location;

	    /**
	     * 位置
	     */
	    static class Location {
		/**
		 * 経度
		 */
		double lat;
		/**
		 * 緯度
		 */
		double lng;
	    }
	}
    }
}
