package buaa.sei.xyb.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import buaa.sei.xyb.resource.UtilityTools;

/**
 * DataBaseOperation �����������ݿ���ص�ȫ������
 * @author Xu Yebing
 */
public class DataBaseOperation {
	
	private static String driverClassName = "com.mysql.jdbc.Driver";
	private static String url = "";
	public static String db_name = "traceableformulaDB";
	
	//
	private static String serverHost = "127.0.0.1";
	private static String port = "3306";
	private static String userName = "root";
	private static String password = "123456";

	public static String translate_table_name = "translateTable";
	// Ӣ�ķ�������������ĵ��г��ֵ�Ӣ�Ĵʽ��з���
	public static final String[][] translate_table_fields_map = {
									{"Ӣ��", "en_word"},
									{"���Ĵʴ�", "cn_words"},
									{"������", "in_parenthesis"},
									{"ǰһ�����Ĵ�", "previous_cn_word"}
								};
	public static String keyForTable = "en_word";
	
	// �����������ݿ�����ӣ���ֹ�������������������ӳصĴ�С
	private static Connection dbConn = null;
	private static Connection syDbConn = null; // ͬ��ʴʿ������
	
	/**
	 * ������ݿ�����
	 */
	public static Connection getConn(String db_name) {
		if (db_name.equals("traceableformulaDB")) {
			if (dbConn == null) {
				try {
					Class.forName(driverClassName);
					url = "jdbc:mysql://" + serverHost + ":" + port + "/";
					dbConn = DriverManager.getConnection(url + db_name, userName, password);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return dbConn;
		}
		else if (db_name.equals("SynonymDB")) {
			if (syDbConn == null) {
				try {
					Class.forName(driverClassName);
					url = "jdbc:mysql://" + serverHost + ":" + port + "/";
					syDbConn = DriverManager.getConnection(url + db_name, userName, password);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return syDbConn;
		} else {
			Connection conn = null;
			try {
				Class.forName(driverClassName);
				url = "jdbc:mysql://" + serverHost + ":" + port + "/";
				conn = DriverManager.getConnection(url + db_name, userName, password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return conn;
		}
	}
	
	/**
	 * �������ݿ⣬db_nameΪҪ���������ݿ�����
	 */
	public static boolean createDataBase(String databaseName) {
		Connection conn = DataBaseOperation.getConn("");
		if (!checkDBConnection(conn)) {
			return false;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			String createDBSql = "CREATE DATABASE IF NOT EXISTS " + databaseName +
					" CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';";
			st.execute(createDBSql);
		} catch (SQLException e) {
			e.printStackTrace();
			warningWhenCatchException(e);
			return false;
		}
		releaseConnAndStat(conn, st);
		return true;
	}
	/**
	 * ��ʼʱ�������ݱ�
	 * @param tableName Ҫ�����ı���
	 * @param tableFields ���и����еĶ������
	 */
	public static boolean createTable(String dbName, String tableName, String tableFields) {
		Connection conn = DataBaseOperation.getConn(dbName);
		if (!checkDBConnection(conn)) {
			return false;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			// ���ȼ�����ݱ��Ƿ��Ѿ����ڣ���������ɾ��ԭ�б�
			String dropTableIfTableIsExist = "drop table if exists " + tableName + ";";
			st.execute(dropTableIfTableIsExist);
			
			String createTableSql = "create table if not exists " + tableName + " ( ";
			createTableSql += tableFields + " ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			System.out.println("====>>createTableSql:");
			System.out.println("\t\t" + createTableSql);
			st.execute(createTableSql);
		} catch (SQLException e) {
			e.printStackTrace();
			warningWhenCatchException(e);
			return false;
		}
		releaseStat(st);
		return true;
	}
	/**
	 * insertTable ��ָ���ı�tableName�в�����Ӧ��ֵ��insertValue��
	 */
	public static boolean insertTable(String tableName, String columnStr, String insertValue) {
		Connection conn = DataBaseOperation.getConn(db_name);
		if (!checkDBConnection(conn)) {
			return false;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			String insertSql = "insert ignore into " + tableName + "(" + columnStr + ") " + " values (" + insertValue + ");";
			st.execute(insertSql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		releaseStat(st);
		return true;
	}
	/**
	 * ��ѯ������Ӣ�Ĵ�enWd�Ƿ������ĳ������֮�У����ҵ����򽫸�������Ӧ�����Ĵ����أ�û�ҵ����򽫷���NULL
	 */
	public static String getEnWdContext(String tableName, String enWd) {
		String retCnStrs = null;
		Connection conn = DataBaseOperation.getConn(db_name);
		if (!checkDBConnection(conn)) {
			return null;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			String findSql = "Select cn_words from " + tableName + " where " + keyForTable + " like \"%" + enWd + "%\"";
			ResultSet rs = st.executeQuery(findSql); 
			StringBuilder retCnStrBd = new StringBuilder("");
			while (rs.next()) {
				retCnStrBd.append(rs.getString(1)).append(" ");
			}
			st.close();
			retCnStrs = retCnStrBd.toString();
			return retCnStrs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * warningWhenCatchException �����ڲ������ݿ�����쳣ʱ������ʾ�ľ����
	 * @param e �쳣����
	 */
	private static void warningWhenCatchException(Exception e) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		UtilityTools.warning(shell, e.getLocalizedMessage());
	}
	/**
	 * �ͷŻ���
	 * @param conn ���ͷŵ�Connectionʵ��
	 * @param stat ���ͷŵ�Statementʵ��
	 */
	private static void releaseConnAndStat(Connection conn, Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
				warningWhenCatchException(e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				warningWhenCatchException(e);
			}
		}
	}
	private static void releaseStat(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
				warningWhenCatchException(e);
			}
		}
	}
	/**
	 * checkDBConnection ������ݿ��Ƿ����ӳɹ�
	 */
	private static boolean checkDBConnection(Connection conn) {
		if (conn == null) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			UtilityTools.warning(shell, "���ݿ�����ʧ��!");
			return false;
		}
		return true;
	}
	
}
