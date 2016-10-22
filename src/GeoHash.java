
/**
 * 封装了GeoHash相关操作
 * 
 * @author Myths
 *
 */
public class GeoHash {
	private int left, right, top, buttom;
	private int length;// 默认的精度为最高精度

	/**
	 * 构造方法，用所在区域的西、东、北、南的经纬度*100000来初始化正方形区域
	 * 
	 * @param left
	 *            西边界的经度*10w
	 * @param right
	 *            东边界的经度*10w
	 * @param top
	 *            北边界的纬度*10w
	 * @param buttom
	 *            南边界的纬度*10w
	 */
	public GeoHash(int left, int right, int top, int buttom) {
		int halfScale = (int) (Math.max(right - left, top - buttom) * 0.55);
		int midEW = (right + left) / 2;
		int midNS = (top + buttom) / 2;
		this.left = midEW - halfScale;
		this.right = midEW + halfScale;
		this.top = midNS + halfScale;
		this.buttom = midNS - halfScale;
		length = (int) (Math.log(halfScale * 2) / Math.log(2));

	}

	/**
	 * 无参构造用中国大陆的位置来初始化 73E-136E 20N-54N
	 */
	public GeoHash() {
		this(7300000, 13600000, 5400000, 200000);
	}

	/**
	 * 利用经纬度对点进行GeoHash编码
	 * 
	 * @param longitude
	 *            位置点的经度*10w
	 * @param latitude
	 *            位置点的纬度*10w
	 * @return GeoHash值
	 */
	public String encode(int longitude, int latitude) {

		String ans = "";
		int curLeft = left, curRight = right, curTop = top, curButtom = buttom;
		for (int i = 0; i < length; i++) {
			int midEW = (curRight + curLeft) / 2;
			int midNS = (curTop + curButtom) / 2;
			if (longitude >= midEW) {
				curLeft = midEW;
				ans += "1";
			} else {
				curRight = midEW;
				ans += "0";
			}
			if (latitude >= midNS) {
				curButtom = midNS;
				ans += "1";
			} else {
				curTop = midNS;
				ans += "0";
			}
		}

		return ans;
	}

	
	/**
	 * 对坐标点求GeoHash编码
	 * @param loc	坐标点信息
	 * @return GeoHash码
	 */
	public String encode(Location loc){
		return encode(loc.getLongitude(),loc.getLatitude());
	}

	/**
	 * 输入GeoHash码，输出区域对应的范围（西、东、北、南）
	 * 
	 * @param s
	 *            GeoHash码
	 * @return 区域的边界信息
	 */
	private int[] getArea(String s) {
		int curLeft = left, curRight = right, curTop = top, curButtom = buttom;
		int len = s.length() / 2;
		for (int i = 0; i < len; i++) {
			int midEW = (curRight + curLeft) / 2;
			int midNS = (curTop + curButtom) / 2;
			if (s.charAt(i * 2) == '1') {
				curLeft = midEW;
			} else {
				curRight = midEW;
			}
			if (s.charAt(i * 2 + 1) == '1') {
				curButtom = midNS;
			} else {
				curTop = midNS;
			}
		}
		return new int[] { curLeft, curRight, curTop, curButtom };
	}

	/**
	 * 输入GeoHash码，输出等精度下临近的九个格子内的GeoHash码
	 * 
	 * @param s
	 *            GeoHash码
	 * @return 临近九个等精度格子的GeoHash码
	 */
	public String[] findNeighbours(String s) {
		int[] area = getArea(s);
		String[] ans = new String[9];
		int curLeft = area[0], curRight = area[1], curTop = area[2], curButtom = area[3];
		ans[0] = s.substring(0, s.length());
		ans[1] = encode(curLeft - 1, curButtom).substring(0, s.length());
		ans[2] = encode(curLeft - 1, curButtom - 1).substring(0, s.length());
		ans[3] = encode(curLeft - 1, curTop).substring(0, s.length());
		ans[4] = encode(curLeft, curButtom - 1).substring(0, s.length());
		ans[5] = encode(curLeft, curTop).substring(0, s.length());
		ans[6] = encode(curRight, curButtom).substring(0, s.length());
		ans[7] = encode(curRight, curButtom - 1).substring(0, s.length());
		ans[8] = encode(curRight, curTop).substring(0, s.length());
		return ans;
	}

}
