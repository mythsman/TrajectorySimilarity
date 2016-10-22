
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 封装了后台信息处理类
 * 
 * @author Myths
 */
@WebServlet("/Display")
@MultipartConfig
public class Display extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * 加载初始页面
	 * 
	 * @return String
	 * @throws IOException
	 */
	private String initPage() throws IOException {
		String templatePath = this.getServletConfig().getServletContext().getRealPath("/display.html");
		BufferedReader ins = new BufferedReader(new InputStreamReader(new FileInputStream(new File(templatePath))));
		String s = new String();
		StringBuffer sb = new StringBuffer();
		while ((s = ins.readLine()) != null) {
			sb.append(s + "\n");
		}
		ins.close();
		return sb.toString();
	}

	/**
	 * 根据轨迹信息、距离算法和时间比重，返回查询结果
	 * 
	 * @param traj
	 *            待查询的轨迹
	 * @param timeRate
	 *            时间的比重
	 * @param measure
	 *            使用的距离算法
	 * @return String 返回给前端的数据
	 */
	private String processTraj(Trajectory traj, double timeRate, int measure) {
		Index index = new Index(2, timeRate, measure);
		// 根据路径长度，综合考虑时间和精度，设置临近点的个数

		ResultList list = new ResultList();

		// 测试计算时间
		long time0 = System.currentTimeMillis();
		list = index.topK(traj, 10);

		long time1 = System.currentTimeMillis();
		int time = (int) (time1 - time0);
		Location mid = getCenter(traj);
		// 返回json数据
		JSONObject js = new JSONObject();
		try {
			js.append("length", traj.size());
			js.append("spandTime", time);
			js.append("data", "loadMap(" + mid.getLatitude() / 100000.0 + "," + mid.getLongitude() / 100000.0 + ");"
					+ drawTraj(traj, 1));
			JSONArray arr = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				JSONObject tmp = new JSONObject();
				tmp.append("trajId", list.get(i).getTrajId());
				tmp.append("length", list.get(i).getSize());
				tmp.append("value", list.get(i).getDist());
				arr.put(tmp);
			}
			js.append("trajs", arr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return js.toString();
	}

	/**
	 * 根据轨迹信息和模式、在地图中绘制轨迹
	 * 
	 * @param traj
	 *            待绘制的轨迹
	 * @param mode
	 *            轨迹的类型（原轨迹为1，匹配轨迹为2）
	 * @return String 返回前端的数据
	 */
	private String drawTraj(Trajectory traj, int mode) {
		StringBuilder ans = new StringBuilder();
		if (mode == 1) {
			ans.append("var trajectory =new Array(");
		} else {
			ans.append("var trajectory1 =new Array(");
		}

		for (int i = 0; i < traj.size(); i++) {
			ans.append("new BMap.Point(" + traj.get(i).getLongitude() / 100000.0 + ", "
					+ traj.get(i).getLatitude() / 100000.0 + "),");
		}
		ans.setCharAt(ans.length() - 1, ')');
		if (mode == 1) {
			ans.append(";drawPath(trajectory)");
		} else {
			ans.append(";drawSubPath(trajectory1)");
		}
		return ans.toString();
	}

	/**
	 * 获得轨迹的中心点
	 * 
	 * @param traj
	 *            轨迹数据
	 * @return 中心点的位置
	 */
	private Location getCenter(Trajectory traj) {
		long latitude = 0, longitude = 0;
		for (int i = 0; i < traj.size(); i++) {
			latitude += traj.get(i).getLatitude();
			longitude += traj.get(i).getLongitude();
		}
		latitude /= traj.size();
		longitude /= traj.size();
		return new Location((int) latitude, (int) longitude, -1);
	}

	/**
	 * 相应Get请求
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//初始化Jdbc
		try {
			Class.forName("Jdbc");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		if (request.getParameter("mode") == null) {// 初始化加载页面
			response.getWriter().write(initPage());
			response.getWriter().flush();
		} else if (request.getParameter("mode").equals("1")) {// 提交路径ID
			double timeRate = Double.parseDouble(request.getParameter("timeRate"));
			int pathId = Integer.parseInt(request.getParameter("pathId"));
			int measure = Integer.parseInt(request.getParameter("measure"));
			Trajectory traj = Jdbc.getData(pathId);
			response.getWriter().write(processTraj(traj, timeRate, measure));
			response.getWriter().flush();
		} else if (request.getParameter("mode").equals("2")) {// 提交绘图ID
			int pathId = 0;
			Enumeration<String> ee = request.getParameterNames();
			while (ee.hasMoreElements()) {
				String ans = ee.nextElement();
				if (!ans.equals("mode")) {
					pathId = Integer.parseInt(ans);
				}
			}
			Trajectory traj = Jdbc.getData(pathId);
			JSONObject js = new JSONObject();
			try {
				js.append("data", drawTraj(traj, 2));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.getWriter().write(js.toString());
			response.getWriter().flush();
		}

	}

	/**
	 * 相应Post请求
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("mode").equals("3")) {// 输入一个文件
			double timeRate = Double.parseDouble(request.getParameter("timeRate"));
			Trajectory traj = null;
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			Part part = request.getPart("file");
			part.write(this.getServletConfig().getServletContext().getRealPath("tmp.csv"));
			try {
				traj = Util.readTrajFromFile(this.getServletConfig().getServletContext().getRealPath("tmp.csv"));
			} catch (ParseException e) {
				System.out.println("write error.");
				e.printStackTrace();
			}
			response.getWriter().write(processTraj(traj, timeRate, Integer.parseInt(request.getParameter("measure"))));
			response.flushBuffer();

		} else {// 输入两个文件
			double timeRate = Double.parseDouble(request.getParameter("timeRate"));
			Trajectory traj1 = null, traj2 = null;
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			Part part1 = request.getPart("file1");
			Part part2 = request.getPart("file2");
			
			part1.write(this.getServletConfig().getServletContext().getRealPath("tmp1.csv"));
			part2.write(this.getServletConfig().getServletContext().getRealPath("tmp2.csv"));
			try {
				traj1 = Util.readTrajFromFile(this.getServletConfig().getServletContext().getRealPath("tmp1.csv"));
				traj2 = Util.readTrajFromFile(this.getServletConfig().getServletContext().getRealPath("tmp2.csv"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int length1 = traj1.size();
			int length2 = traj2.size();
			int measure = Integer.parseInt(request.getParameter("measure"));
			long time0 = System.currentTimeMillis();
			int score = 0;
			switch (measure) {
			case 1:
				score = traj1.dtw(traj2, timeRate);
				break;
			case 2:
				score = traj1.md(traj2, timeRate);
				break;
			case 3:
				score = traj1.owd(traj2, timeRate);
				break;
			}
			long time1 = System.currentTimeMillis();
			int time = (int) (time1 - time0);
			JSONObject js = new JSONObject();
			Location mid = getCenter(traj1);
			try {
				js.append("length1", length1);
				js.append("length2", length2);
				js.append("spandTime", time + "ms");
				js.append("score", score);
				js.append("data", "loadMap(" + mid.getLatitude() / 100000.0 + "," + mid.getLongitude() / 100000.0 + ");"
						+ drawTraj(traj1, 1) + ";" + drawTraj(traj2, 2));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.getWriter().write(js.toString());
			response.flushBuffer();
		}
	}
}
