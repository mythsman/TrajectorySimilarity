import java.util.ArrayList;

/**
 * 多线程查询临近点
 * 
 * @author Myths
 *
 */
public class MultiThread extends Thread {
	private Location loc;
	private int nearby;
	private ArrayList<pointItem> items;

	/**
	 * 
	 * @param loc
	 *            目标点
	 * @param nearby
	 *            查找临近点的个数
	 */
	public MultiThread(Location loc, int nearby) {
		this.loc = loc;
		this.nearby = nearby;
	}

	@Override
	public void run() {
		GeoHash geohash = new GeoHash();
		String hash = geohash.encode(loc.getLongitude(), loc.getLatitude());
		String[] neighbours;

		int cnt = Jdbc.getCntByGeoHash(hash);
		neighbours = new String[1];
		neighbours[0] = hash;

		while (cnt <= nearby) {
			neighbours = geohash.findNeighbours(hash);
			cnt = 0;
			for (String s : neighbours) {
				cnt += Jdbc.getCntByGeoHash(s);
			}
			hash = hash.substring(0, hash.length() - 2);
		}

		items = new ArrayList<pointItem>();
		for (String s : neighbours) {
			items.addAll(Jdbc.getItemsByGeoHash(s));
		}
		for (int i = 0; i < items.size(); i++) {
			for (int j = i + 1; j < items.size(); j++) {
				if (items.get(j).getLocation().distance(loc, 0) < items.get(i).getLocation().distance(loc, 0)) {
					pointItem tmp = items.get(i);
					items.set(i, items.get(j));
					items.set(j, tmp);
				}
			}
		}
		items.subList(nearby, items.size()).clear();
	}

	public ArrayList<pointItem> getItems() {
		return items;
	}
}
