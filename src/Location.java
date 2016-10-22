/**
 * 封装了点信息
 * 
 * @author Myths
 *
 */
public class Location {
	/**
	 * 基本的坐标和时间戳，以及get、set方法
	 */

	private int longitude, latitude;
	private int timeStamp;

	/**
	 * 设置时间戳
	 * 
	 * @param time
	 *            时间戳
	 */
	public void setTimeStamp(int time) {
		timeStamp = time;
	}

	/**
	 * 返回纬度*10w的值
	 * 
	 * @return 纬度*10w的值
	 */
	public int getLatitude() {
		return latitude;
	}

	/**
	 * 返回时间戳
	 * 
	 * @return 时间戳
	 */
	public int getTimeSatmp() {
		return timeStamp;
	}

	/**
	 * 返回经度*10w
	 * 
	 * @return 经度*10w
	 */
	public int getLongitude() {
		return longitude;
	}

	/**
	 * 全数据构造方法
	 * 
	 * @param longitude
	 *            经度*10w
	 * @param latitude
	 *            纬度*10w
	 * @param timeStamp
	 *            时间戳(s)
	 */
	public Location(int longitude, int latitude, int timeStamp) {
		this(longitude,latitude);
		this.timeStamp = timeStamp;
	}
	
	
	public Location(int longitude, int latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.timeStamp=0;
	}

	@Override
	public String toString() {
		return longitude + ":" + latitude + ":" + timeStamp;
	}

	/**
	 * 与另一点间的距离，引入时间比重参数采用三维欧氏距离。
	 * 
	 * @param loc
	 *            另一个点数据
	 * @param timeRate
	 *            时间占的比重，比重为1时表示忽略位置信息，比重为0时表示忽略时间信息。
	 * @return 距离
	 */
	public int distance(Location loc, double timeRate) {
		if (timeRate > 1) {
			timeRate = 1;
		} else if (timeRate < 0) {
			timeRate = 0;
		}
		double dist = (1 - timeRate)
				* Math.sqrt((this.latitude - loc.latitude) * (this.latitude - loc.latitude)
						+ (this.longitude - loc.longitude) * (this.longitude - loc.longitude))
				+ timeRate * Math.abs(this.timeStamp - loc.timeStamp);
		return (int) dist;
	}

}
