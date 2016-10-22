
/**
 * ��װ��GeoHash��ز���
 * 
 * @author Myths
 *
 */
public class GeoHash {
	private int left, right, top, buttom;
	private int length;// Ĭ�ϵľ���Ϊ��߾���

	/**
	 * ���췽����������������������������ϵľ�γ��*100000����ʼ������������
	 * 
	 * @param left
	 *            ���߽�ľ���*10w
	 * @param right
	 *            ���߽�ľ���*10w
	 * @param top
	 *            ���߽��γ��*10w
	 * @param buttom
	 *            �ϱ߽��γ��*10w
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
	 * �޲ι������й���½��λ������ʼ�� 73E-136E 20N-54N
	 */
	public GeoHash() {
		this(7300000, 13600000, 5400000, 200000);
	}

	/**
	 * ���þ�γ�ȶԵ����GeoHash����
	 * 
	 * @param longitude
	 *            λ�õ�ľ���*10w
	 * @param latitude
	 *            λ�õ��γ��*10w
	 * @return GeoHashֵ
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
	 * ���������GeoHash����
	 * @param loc	�������Ϣ
	 * @return GeoHash��
	 */
	public String encode(Location loc){
		return encode(loc.getLongitude(),loc.getLatitude());
	}

	/**
	 * ����GeoHash�룬��������Ӧ�ķ�Χ���������������ϣ�
	 * 
	 * @param s
	 *            GeoHash��
	 * @return ����ı߽���Ϣ
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
	 * ����GeoHash�룬����Ⱦ������ٽ��ľŸ������ڵ�GeoHash��
	 * 
	 * @param s
	 *            GeoHash��
	 * @return �ٽ��Ÿ��Ⱦ��ȸ��ӵ�GeoHash��
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
