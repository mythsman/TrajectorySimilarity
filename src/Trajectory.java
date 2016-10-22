import java.util.ArrayList;

/**
 * ��װ�켣��Ϣ
 * 
 * @author Myths
 *
 */
public class Trajectory {

	private ArrayList<Location> locations;// �켣�����еĵ���Ϣ
	private int length;// �켣����
	private double timeRate;
	private int id;// �켣ID

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < locations.size(); i++) {
			sb.append(locations.get(i).toString() + ",");
		}
		return sb.toString();
	}

	/**
	 * ���ù켣��ID
	 * 
	 * @param n Idֵ
	 */
	public void setId(int n) {
		id = n;
	}

	/**
	 * ��ù켣��ID
	 * 
	 * @return ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * �����ݹ���켣�����ݸ�ʽΪ: (longitude1):(latitude1):(timestamp1),
	 * (longitude2):(latitude2):(timestamp2), ......
	 * (longitudeN):(latitudeN):(timestampN),
	 * 
	 * @param loc �켣���ݵ�
	 */

	public Trajectory(ArrayList<Location> loc) {
		id = 0;
		locations = loc;
		length = -1;
	}

	/**
	 * ��ID�����ݹ���
	 * 
	 * @param id
	 *            �켣��ID
	 * @param s
	 *            �켣����
	 */
	public Trajectory(int id, String s) {
		this.id = id;
	}

	/**
	 * ����ʱ����Ϣ�ı��أ����仯�����������ع켣���ܳ���
	 * 
	 * @param timeRate
	 *            ʱ�����
	 * @return �켣����
	 */
	public int getLength(double timeRate) {
		if (length == -1 || timeRate != this.timeRate) {
			this.timeRate = timeRate;
			length = 0;
			for (int i = 0; i < this.size() - 1; i++) {
				length += locations.get(i).distance(locations.get(i + 1), timeRate);
			}
		}
		return length;
	}

	/**
	 * ���ع켣�е�ĸ���
	 * 
	 * @return �����Ŀ
	 */
	public int size() {
		return locations.size();
	}

	/**
	 * ���λ����index�ĵ���Ϣ
	 * 
	 * @param index
	 *            ����±�
	 * @return	Location��Ϣ
	 */
	public Location get(int index) {
		return locations.get(index);
	}

	/**
	 * ���ع켣���Dtw����
	 * 
	 * @param traj
	 *            �ڶ����켣
	 * @param timeRate
	 *            ʱ�����
	 * @return ����
	 */
	public int dtw(Trajectory traj, double timeRate) {
		int[][] dp = new int[this.size()][traj.size()];
		dp[0][0] = this.get(0).distance(traj.get(0), timeRate);
		for (int i = 0; i < this.size(); i++) {
			for (int j = 0; j < traj.size(); j++) {
				int dis = this.get(i).distance(traj.get(j), timeRate);
				if (i == 0 && j == 0) {
					dp[i][j] = dis;
				} else if (i == 0) {
					dp[i][j] = dp[i][j - 1] + dis;
				} else if (j == 0) {
					dp[i][j] = dp[i - 1][j] + dis;
				} else {
					dp[i][j] = dis;
					if (dp[i][j - 1] <= Math.min(dp[i - 1][j - 1], dp[i - 1][j])) {
						dp[i][j] += dp[i][j - 1];
					} else if (dp[i - 1][j - 1] <= Math.min(dp[i][j - 1], dp[i - 1][j])) {
						dp[i][j] += dp[i - 1][j - 1];
					} else {
						dp[i][j] += dp[i - 1][j];
					}
				}
			}
		}
		return dp[this.size() - 1][traj.size() - 1] / Math.max(this.size(), traj.size());
	}

	/**
	 * ���ع켣���Md����
	 * 
	 * @param traj
	 *            �ڶ����켣
	 * @param timeRate
	 *            ʱ�����
	 * @return ����
	 */
	public int md(Trajectory traj, double timeRate) {
		int[][] mda = new int[this.size()][traj.size()];
		int[][] mdb = new int[this.size()][traj.size()];
		int[] sum, trajSum;
		sum = new int[this.size()];
		sum[0] = 0;
		for (int i = 1; i < this.size(); i++) {
			sum[i] = sum[i - 1] + locations.get(i).distance(locations.get(i - 1), timeRate);
		}

		trajSum = new int[traj.size()];
		trajSum[0] = 0;
		for (int i = 1; i < traj.size(); i++) {
			trajSum[i] = trajSum[i - 1] + traj.locations.get(i).distance(traj.locations.get(i - 1), timeRate);
		}

		for (int i = 0; i < traj.size(); i++) {
			mda[0][i] = trajSum[i] + this.get(0).distance(traj.get(i), timeRate);
		}
		for (int i = 0; i < this.size(); i++) {
			mdb[i][0] = sum[i] + this.get(i).distance(traj.get(0), timeRate);
		}
		for (int i = 0; i < this.size(); i++) {
			for (int j = 0; j < traj.size(); j++) {
				if (i != 0) {
					mda[i][j] = Math.min(mda[i - 1][j] + sum[i] - sum[i - 1],
							mdb[i - 1][j] + this.get(i).distance(traj.get(j), timeRate));
				}
				if (j != 0) {
					mdb[i][j] = Math.min(mda[i][j - 1] + this.get(i).distance(traj.get(j), timeRate),
							mdb[i][j - 1] + trajSum[j] - trajSum[j - 1]);
				}
			}
		}
		return (int) (1000
				* (2 * Math.min(mda[this.size() - 1][traj.size() - 1], mdb[this.size() - 1][traj.size() - 1])) * 1.0
				/ (traj.getLength(timeRate) + this.getLength(timeRate)) - 1);

	}

	/**
	 * ���ع켣���Owd����
	 * 
	 * @param traj
	 *            �ڶ����켣
	 * @param timeRate
	 *            ʱ�����
	 * @return ����
	 */
	public int owd(Trajectory traj, double timeRate) {
		int ans1 = 0, ans2 = 0;
		for (int i = 0; i < this.size(); i++) {
			int mini = Integer.MAX_VALUE;
			for (int j = 0; j < traj.size(); j++) {
				mini = Math.min(mini, get(i).distance(traj.get(j), timeRate));
			}
			ans1 += mini;
		}
		ans1 /= this.size();
		for (int i = 0; i < traj.size(); i++) {
			int mini = Integer.MAX_VALUE;
			for (int j = 0; j < this.size(); j++) {
				mini = Math.min(mini, traj.get(i).distance(this.get(j), timeRate));
			}
			ans2 += mini;
		}
		ans2 /= traj.size();
		return (ans1 + ans2) / 2;
	}

	/**
	 * ��ù켣������ԱȵĹ켣���ƶ���ߵĹ켣���±�
	 * 
	 * @param traj
	 *            ���ԱȵĹ켣
	 * @param timeRate
	 *            ʱ�����
	 * @return �±�
	 */
	public int bestPointId(Trajectory traj, double timeRate) {
		int leastDist = Integer.MAX_VALUE;
		int leastId = -1;
		for (int i = 0; i < this.size(); i++) {
			int dist = Integer.MAX_VALUE;
			for (int j = 0; j < traj.size(); j++) {
				dist = Math.min(dist, this.get(i).distance(traj.get(j), timeRate));
			}
			if (leastDist > dist) {
				leastDist = dist;
				leastId = i;
			}
		}
		return leastId;
	}

}
