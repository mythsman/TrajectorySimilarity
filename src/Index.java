import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * ��װ�����ղ�ѯ�ķ���
 * 
 * @author Myths
 *
 */
public class Index {

	public int nearby = 20;// ���ñ������ٽ���ĸ���
	public double timeRate = 0;// ����ʱ�����
	public int measure;

	public Index(int nearby, double timeRate, int measure) {
		this.nearby = nearby;
		this.timeRate = timeRate;
		this.measure = measure;
	}

	/**
	 * �����λ�ú��ٽ���ĸ���������õ��ٽ���num����
	 * 
	 * @param loc
	 *            ����ѯ�ĵ�
	 * @param num
	 *            �ٽ���ĸ���
	 * @return num�����к��ʵĵ㼯
	 */
	public ArrayList<pointItem> getNeighbourPoints(Location loc, int num) {
		GeoHash geohash = new GeoHash();
		String hash = geohash.encode(loc.getLongitude(), loc.getLatitude());
		String[] neighbours;
		int cnt = 0;
		hash += "--";

		do {
			hash = hash.substring(0, hash.length() - 2);
			neighbours = geohash.findNeighbours(hash);
			cnt = 0;
			for (String s : neighbours) {
				cnt += Jdbc.getCntByGeoHash(s);
			}

		} while (cnt <= num);

		ArrayList<pointItem> items = new ArrayList<pointItem>();
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
		items.subList(num, items.size()).clear();

		return items;
	}

	/**
	 * ����켣��Ϣ������ٽ��Ĺ켣����
	 * 
	 * @param traj
	 *            ����ѯ�Ĺ켣
	 * @return �ٽ��Ĺ켣����
	 */
	public ArrayList<Trajectory> getNeighbourTrajectories(Trajectory traj) {

		ArrayList<pointItem> items = new ArrayList<pointItem>();
		Set<Integer> set = new HashSet<Integer>();
		ArrayList<Trajectory> ans = new ArrayList<Trajectory>();
		
		// ���̲߳�ѯ
		MultiThread[] thread = new MultiThread[traj.size()];
		for (int i = 0; i < traj.size(); i++) {
			thread[i]=new MultiThread(traj.get(i),nearby);
			thread[i].start();
		}
		for (int i = 0; i < traj.size(); i++) {
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			items.addAll(thread[i].getItems());
		}


		for (pointItem it : items) {
			set.add(it.getPathId());
		}

		for (int it : set) {
			ans.add(Jdbc.getData(it));
		}

		return ans;
	}

	/**
	 * ����켣��Ϣ�Ͳ�ѯ����Ŀk����˳�����k�����ƵĹ켣
	 * 
	 * @param traj
	 *            ����ѯ�Ĺ켣
	 * @param k
	 *            ��Ҫ����Ŀ
	 * @return �����

	 */
	public ResultList topK(Trajectory traj, int k){
		ArrayList<Trajectory> candidates = this.getNeighbourTrajectories(traj);
		ResultList list = new ResultList();
		switch (measure) {
		case 1:
			for (Trajectory t : candidates) {
				list.add(t.getId(), t.size(), traj.dtw(t, timeRate));
			}
			break;
		case 2:
			for (Trajectory t : candidates) {
				list.add(t.getId(), t.size(), traj.md(t, timeRate));
			}
			break;
		case 3:
			for (Trajectory t : candidates) {
				list.add(t.getId(), t.size(), traj.owd(t, timeRate));
			}
			break;
		}
		list.sort();
		return list.getTopKAsc(k);
	}


}
