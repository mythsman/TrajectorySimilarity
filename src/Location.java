/**
 * ��װ�˵���Ϣ
 * 
 * @author Myths
 *
 */
public class Location {
	/**
	 * �����������ʱ������Լ�get��set����
	 */

	private int longitude, latitude;
	private int timeStamp;

	/**
	 * ����ʱ���
	 * 
	 * @param time
	 *            ʱ���
	 */
	public void setTimeStamp(int time) {
		timeStamp = time;
	}

	/**
	 * ����γ��*10w��ֵ
	 * 
	 * @return γ��*10w��ֵ
	 */
	public int getLatitude() {
		return latitude;
	}

	/**
	 * ����ʱ���
	 * 
	 * @return ʱ���
	 */
	public int getTimeSatmp() {
		return timeStamp;
	}

	/**
	 * ���ؾ���*10w
	 * 
	 * @return ����*10w
	 */
	public int getLongitude() {
		return longitude;
	}

	/**
	 * ȫ���ݹ��췽��
	 * 
	 * @param longitude
	 *            ����*10w
	 * @param latitude
	 *            γ��*10w
	 * @param timeStamp
	 *            ʱ���(s)
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
	 * ����һ���ľ��룬����ʱ����ز���������άŷ�Ͼ��롣
	 * 
	 * @param loc
	 *            ��һ��������
	 * @param timeRate
	 *            ʱ��ռ�ı��أ�����Ϊ1ʱ��ʾ����λ����Ϣ������Ϊ0ʱ��ʾ����ʱ����Ϣ��
	 * @return ����
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
