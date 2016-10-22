import java.util.ArrayList;

/**
 * 封装了查询得到的排序后的结果集
 * 
 * @author Myths
 *
 */
public class ResultList {

	/**
	 * 封装了结果中简单的轨迹信息，trajId、轨迹点的个数以及距离的度量值Dist
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
	 * 获得第k个最优的结果
	 * 
	 * @param k 待查询的下标
	 * @return 结果的点信息
	 */
	public TinyItem get(int k) {
		return items.get(k);
	}

	public ResultList() {
		items = new ArrayList<TinyItem>();
	}

	/**
	 * 返回保存的点数据的数目
	 * 
	 * @return 点数据的数目
	 */
	public int size() {
		return items.size();
	}

	/**
	 * 向结果集中添加数据
	 * 
	 * @param trajId
	 *            待添加的轨迹Id
	 * @param length
	 *            轨迹点的个数
	 * @param dist
	 *            距离值
	 */
	public void add(int trajId, int length, int dist) {
		items.add(new TinyItem(trajId, length, dist));
	}

	/**
	 * 对结果集按dist进行升序排序
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
	 * 获取前k个结果
	 * 
	 * @param k
	 *            需求的数目
	 * @return
	 *            结果集
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
