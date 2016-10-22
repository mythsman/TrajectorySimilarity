
/**
 * 保存从数据库中取得的点信息条目，包括ID、经纬度、时间戳、GeoHash位置和所属的路径ID。
 * 
 * @author Administrator
 *
 */
public class pointItem {
	private int id, longitude, latitude, timeStamp, pathId;
	private String geoHash;

	/**
	 * 全数据构造
	 * 
	 * @param id
	 *            点的Id
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param timeStamp
	 *            时间戳
	 * @param geoHash
	 *            GeoHash码
	 * @param pathId
	 *            所在的路径Id
	 */
	public pointItem(int id, int longitude, int latitude, int timeStamp, String geoHash, int pathId) {
		this.id = id;
		this.longitude = longitude;
		this.latitude = latitude;
		this.timeStamp = timeStamp;
		this.pathId = pathId;
		this.geoHash = geoHash;
	}

	/**
	 * 返回点的Location信息
	 * 
	 * @return Location类型的点信息
	 */
	public Location getLocation() {
		return new Location(longitude, latitude, timeStamp);
	}

	/**
	 * 返回点的ID
	 * 
	 * @return 点的ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * 返回点所在的路径ID
	 * 
	 * @return 点所在的路径ID
	 */
	public int getPathId() {
		return pathId;
	}

	/**
	 * 返回点所对应的GeoHash值
	 * 
	 * @return 点所对应的GeoHash值
	 */
	public String getGeoHash() {
		return geoHash;
	}
}
