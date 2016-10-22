import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * ��Ҫ�ǵ�������ݽ��д���ľ�̬����
 * 
 * @author Myths
 *
 */
public class Util {

	/**
	 * ��ȡ��׼��csv�ļ�������·������
	 * 
	 * @param path
	 *            �ļ�·��
	 * @return ���ļ����ɵĹ켣����
	 * @throws IOException
	 *             �����쳣
	 * @throws ParseException
	 *             ����ת���쳣
	 */
	public static Trajectory readTrajFromFile(String path) throws IOException, ParseException {

		BufferedReader reader = new BufferedReader(new FileReader(path));
		String s = reader.readLine();
		boolean withTime = s.equals("����,γ��,����ʱ��");
		ArrayList<Location> loc = new ArrayList<Location>();
		s = "";
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		while ((s = reader.readLine()) != null) {

			if (withTime) {
				String[] points = s.split(",");
				int longitude = (int) (Double.parseDouble(points[0]) * 100000);
				int latitude = (int) (Double.parseDouble(points[1]) * 100000);
				int timeStamp = (int) (dateFormater.parse(points[2]).getTime() / 1000);
				loc.add(new Location(longitude, latitude, timeStamp));
			} else {
				String[] points = s.split(",");
				int longitude = (int) (Double.parseDouble(points[0]) * 100000);
				int latitude = (int) (Double.parseDouble(points[1]) * 100000);
				loc.add(new Location(longitude, latitude));
			}
		}
		reader.close();
		return new Trajectory(loc);
	}

	/**
	 * ����׼�Ĺ켣���ݱ���Ϊcsv�ļ�
	 * 
	 * @param traj
	 *            �켣��Ϣ
	 * @param path
	 *            ����ļ���·��
	 * @param withTime
	 *            �Ƿ��ʱ����Ϣ
	 * @throws IOException
	 *             д�ļ��쳣
	 */
	public static void writeTrajToFile(Trajectory traj, String path, boolean withTime) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		if (withTime) {
			writer.write("����,γ��,����ʱ��\r\n");
			for (int i = 0; i < traj.size(); i++) {
				SimpleDateFormat dateFormater = new SimpleDateFormat("YYYYMMdd HH:mm:ss");
				String time = dateFormater.format(new Date(((long) traj.get(i).getTimeSatmp()) * 1000));
				writer.write(traj.get(i).getLongitude() / 100000.0 + "," + traj.get(i).getLatitude() / 100000.0 + ","
						+ time + "\r\n");
			}

		} else {
			writer.write("����,γ��\r\n");
			for (int i = 0; i < traj.size(); i++) {
				writer.write(
						traj.get(i).getLongitude() / 100000.0 + "," + traj.get(i).getLatitude() / 100000.0 + "\r\n");
			}
		}
		writer.flush();
		writer.close();
	}

	public static void writeDataToFile(String path) throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader(path));
		BufferedWriter writer=new BufferedWriter(new FileWriter("D:/out.txt"));
		GeoHash geoHash=new GeoHash();
		String s="";
		int pathId=1;
		while((s=reader.readLine())!=null){
			String ss=s.split("\t")[1];
			String[] sss=ss.split(",");
			for(String t:sss){
				String[] ssss=t.split(":");
				int longitude,latitude,timeStamp;
				longitude=Integer.parseInt(ssss[0]);
				latitude=Integer.parseInt(ssss[1]);
				timeStamp=Integer.parseInt(ssss[2]);
				writer.write("null\t"+longitude+"\t"+latitude+"\t"+timeStamp+"\t"+geoHash.encode(longitude, latitude)+"\t"+pathId+"\n");
			}
			pathId++;
		}
		reader.close();
		writer.close();
	}
	
	
	/**
	 * ���켣�ļ���Ϊpoint���еĸ�ʽ
	 * @param path	�켣�ļ���·��
	 * @param pathId	�켣��Id
	 * @return	�ַ�����ʾ�ĵ㼯
	 * @throws IOException	IO�쳣
	 * @throws ParseException	ʱ���ʽת���쳣
	 */
	public static String genPointFromFile(String path,int pathId) throws IOException, ParseException{
		GeoHash geoHash=new GeoHash();
		String ans="";
		Trajectory traj=Util.readTrajFromFile(path);
		for(int i=0;i<traj.size();i++){
			Location loc=traj.get(i);
			ans+="null\t"+loc.getLongitude()+"\t"+loc.getLatitude()+"\t"+loc.getTimeSatmp()+"\t"+geoHash.encode(loc)+"\t"+pathId+"\n";
		}
		return ans;
	}
	
	/**
	 * ���켣�ļ��ж�Ϊpoint���еĸ�ʽ
	 * @param dir	�켣�ļ��е�·��
	 * @param outputFile ����Ĺ켣���ļ�
	 * @throws IOException	IO�쳣
	 * @throws ParseException	ʱ���ʽת���쳣
	 */
	public static void genPointFromDir(String dir,String outputFile) throws IOException, ParseException{
		BufferedWriter writer=new BufferedWriter(new FileWriter(outputFile));
		File file=new File(dir);
		File[] files=file.listFiles();
		int pathId=1;
		for(int i=0;i<files.length;i++){
			if(files[i].getAbsolutePath().endsWith(".csv")){
				writer.write(genPointFromFile(files[i].getAbsolutePath(), pathId));
				pathId++;
			}
		}
		writer.close();
	}
	
	
	public static void main(String[] args) throws IOException, ParseException {
		//��sample�ļ����µ����й켣������data.txt��
		//genPointFromDir("D:/sample/", "D:/data.txt");
	}
}
