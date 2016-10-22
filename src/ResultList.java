import java.util.ArrayList;

/**
 * ��װ�˲�ѯ�õ��������Ľ����
 * 
 * @author Myths
 *
 */
public class ResultList {

	/**
	 * ��װ�˽���м򵥵Ĺ켣��Ϣ��trajId���켣��ĸ����Լ�����Ķ���ֵDist
	 * 
	 * @author Myths
	 *
	 */
	class TinyItem {
		private int trajId, dist, size;

		public TinyItem(int trajId, int length, int dist) {
			this.trajId = trajId;
			this.size = length;
			this.dist = dist;
		}

		public int getTrajId() {
			return trajId;
		}

		public int getSize() {
			return size;
		}

		public int getDist() {
			return dist;
		}
	}

	private ArrayList<TinyItem> items;

	/**
	 * ��õ�k�����ŵĽ��
	 * 
	 * @param k ����ѯ���±�
	 * @return ����ĵ���Ϣ
	 */
	public TinyItem get(int k) {
		return items.get(k);
	}

	public ResultList() {
		items = new ArrayList<TinyItem>();
	}

	/**
	 * ���ر���ĵ����ݵ���Ŀ
	 * 
	 * @return �����ݵ���Ŀ
	 */
	public int size() {
		return items.size();
	}

	/**
	 * ���������������
	 * 
	 * @param trajId
	 *            ����ӵĹ켣Id
	 * @param length
	 *            �켣��ĸ���
	 * @param dist
	 *            ����ֵ
	 */
	public void add(int trajId, int length, int dist) {
		items.add(new TinyItem(trajId, length, dist));
	}

	/**
	 * �Խ������dist������������
	 */
	public void sort() {
		for (int i = 0; i < items.size(); i++) {
			for (int j = i + 1; j < items.size(); j++) {
				if (items.get(i).getDist() > items.get(j).getDist()) {
					TinyItem it = new TinyItem(items.get(i).getTrajId(), items.get(i).getSize(),
							items.get(i).getDist());
					items.set(i, items.get(j));
					items.set(j, it);
				}
			}
		}
	}

	/**
	 * ��ȡǰk�����
	 * 
	 * @param k
	 *            �������Ŀ
	 * @return
	 *            �����
	 */
	public ResultList getTopKAsc(int k) {
		sort();
		ResultList ans = new ResultList();
		for (int i = 0; i < Math.min(items.size(),k); i++) {
			ans.add(items.get(i).getTrajId(), items.get(i).getSize(), items.get(i).getDist());
		}
		return ans;
	}
}
