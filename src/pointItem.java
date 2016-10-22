
/**
 * ��������ݿ���ȡ�õĵ���Ϣ��Ŀ������ID����γ�ȡ�ʱ�����GeoHashλ�ú�������·��ID��
 * 
 * @author Administrator
 *
 */
public class pointItem {
	private int id, longitude, latitude, timeStamp, pathId;
	private String geoHash;

	/**
	 * ȫ���ݹ���
	 * 
	 * @param id
	 *            ���Id
	 * @param longitude
	 *            ����
	 * @param latitude
	 *            γ��
	 * @param timeStamp
	 *            ʱ���
	 * @param geoHash
	 *            GeoHash��
	 * @param pathId
	 *            ���ڵ�·��Id
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
	 * ���ص��Location��Ϣ
	 * 
	 * @return Location���͵ĵ���Ϣ
	 */
	public Location getLocation() {
		return new Location(longitude, latitude, timeStamp);
	}

	/**
	 * ���ص��ID
	 * 
	 * @return ���ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * ���ص����ڵ�·��ID
	 * 
	 * @return �����ڵ�·��ID
	 */
	public int getPathId() {
		return pathId;
	}

	/**
	 * ���ص�����Ӧ��GeoHashֵ
	 * 
	 * @return ������Ӧ��GeoHashֵ
	 */
	public String getGeoHash() {
		return geoHash;
	}
}
