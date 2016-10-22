import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 封装了静态的数据库操作
 * 
 * @author Myths
 *
 */
public class Jdbc {
	private static Connection conn = null;
	private static PreparedStatement stmtGetTraj = null;
	private static PreparedStatement stmtGetCnt = null;
	private static PreparedStatement stmtGetItems = null;
	private static PreparedStatement stmtGetTrajCnt = null;
	private static PreparedStatement stmtGetPointCnt = null;

	private static String url = null;
	private static String user = null;
	private static String password = null;

	/**
	 * 初始化配置数据库
	 * @param file 数据库配置文件
	 */
	static {
		try {
			url = "jdbc:mysql://localhost/trajectory?useSSL=true";
			user = "root";
			password = "123456";

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);						
					
			stmtGetTraj = conn.prepareStatement("select longitude,latitude,timeStamp from point where pathId = ?");
			stmtGetCnt = conn.prepareStatement("select count(*) as value from point where geoHash like ?");
			stmtGetItems = conn.prepareStatement("select *  from point where geoHash like ?");
			stmtGetTrajCnt = conn.prepareStatement("select pathId from point where id=(select max(id) from point )");
			stmtGetPointCnt = conn.prepareStatement("select max(id) from point");
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("数据库链接错误");
			e.printStackTrace();
		}
	}

	/**
	 * 查找轨迹表，根据Id获得路径信息，并返回为Trajectory对象
	 * 
	 * @param id
	 *            待查轨迹的Id
	 * @return 轨迹信息
	 */

	public synchronized static Trajectory getData(int id) {
		ResultSet rs = null;
		Trajectory traj = null;
		ArrayList<Location> loc=new ArrayList<Location>();
		try {		
			stmtGetTraj.setInt(1, id);
			rs = stmtGetTraj.executeQuery();
			while (rs.next()) {
				loc.add(new Location(rs.getInt("longitude"), rs.getInt("latitude"), rs.getInt("timeStamp")));
			}
		} catch (SQLException e) {
			System.out.println("数据操作错误");
			e.printStackTrace();
		}
		traj=new Trajectory(loc);
		traj.setId(id);
		return traj;
	}

	/**
	 * 查找点信息表，返回某个GeoHash下的点数目
	 * 
	 * @param geoHash
	 *            待查的GeoHash码
	 * @return 区域中包含的点的数目
	 */
	public synchronized static int getCntByGeoHash(String geoHash) {
		int ans = 0;
		ResultSet rs = null;
		try {

			stmtGetCnt.setString(1, geoHash + "%");
			rs = stmtGetCnt.executeQuery();
			rs.next();
			ans = rs.getInt("value");
		} catch (SQLException e) {
			System.out.println("数据操作错误");
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * 获得当前traj表中轨迹的个数
	 * 
	 * @return traj表中轨迹的个数
	 */

	public synchronized static int getTrajCnt() {
		ResultSet rs = null;
		int cnt = 0;
		try {
			rs = stmtGetTrajCnt.executeQuery();
			rs.next();
			cnt = rs.getInt("max(id)");
		} catch (SQLException e) {
			System.out.println("数据操作错误");
			e.printStackTrace();
		}
		return cnt;
	}

	/**
	 * 获得当前point表中点的个数
	 * 
	 * @return point表中点的个数
	 */
	public synchronized static int getPointCnt() {
		ResultSet rs = null;
		int cnt = 0;
		try {
			rs = stmtGetPointCnt.executeQuery();
			rs.next();
			cnt = rs.getInt("max(id)");
		} catch (SQLException e) {
			System.out.println("数据操作错误");
			e.printStackTrace();
		}
		return cnt;
	}

	/**
	 * 查找点信息表，根据GeoHash值查找其包含的所有点
	 * 
	 * @param geoHash
	 *            待查询的GeoHash码
	 * @return 点信息数组
	 */
	public synchronized static ArrayList<pointItem> getItemsByGeoHash(String geoHash) {
		ArrayList<pointItem> items = new ArrayList<pointItem>();
		ResultSet rs = null;
		try {
			stmtGetItems.setString(1, geoHash + "%");
			rs = stmtGetItems.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				int longitude = rs.getInt("longitude");
				int latitude = rs.getInt("latitude");
				int timeStamp = rs.getInt("timeStamp");
				int pathId = rs.getInt("pathId");
				String hash = rs.getString("geoHash");
				items.add(new pointItem(id, longitude, latitude, timeStamp, hash, pathId));
			}
		} catch (SQLException e) {
			System.out.println("数据操作错误");
			e.printStackTrace();
		}
		return items;
	}

	public static void main(String[] args) {
		
	}

}
