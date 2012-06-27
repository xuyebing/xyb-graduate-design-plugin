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
 * DataBaseOperation 包含了与数据库相关的全部操作
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
	// 英文翻译表，用来根据文档中出现的英文词进行翻译
	public static final String[][] translate_table_fields_map = {
									{"英文", "en_word"},
									{"中文词串", "cn_words"},
									{"含括号", "in_parenthesis"},
									{"前一个中文词", "previous_cn_word"}
								};
	public static String keyForTable = "en_word";
	
	// 保存两个数据库的连接，防止建立的连接数超过连接池的大小
	private static Connection dbConn = null;
	private static Connection syDbConn = null; // 同义词词库的连接
	
	/**
	 * 获得数据库连接
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
	 * 创建数据库，db_name为要创建的数据库名称
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
	 * 初始时创建数据表
	 * @param tableName 要创建的表名
	 * @param tableFields 表中各个列的定义语句
	 */
	public static boolean createTable(String dbName, String tableName, String tableFields) {
		Connection conn = DataBaseOperation.getConn(dbName);
		if (!checkDBConnection(conn)) {
			return false;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			// 首先检查数据表是否已经存在，若存在则删除原有表
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
	 * insertTable 向指定的表tableName中插入相应的值（insertValue）
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
	 * 查询给定的英文串enWd是否包含在某个主键之中，若找到，则将该主键对应的中文串返回；没找到，则将返回NULL
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
	 * warningWhenCatchException 用于在操作数据库出现异常时给出显示的警告框
	 * @param e 异常对象
	 */
	private static void warningWhenCatchException(Exception e) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		UtilityTools.warning(shell, e.getLocalizedMessage());
	}
	/**
	 * 释放环境
	 * @param conn 待释放的Connection实例
	 * @param stat 待释放的Statement实体
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
	 * checkDBConnection 检查数据库是否连接成功
	 */
	private static boolean checkDBConnection(Connection conn) {
		if (conn == null) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			UtilityTools.warning(shell, "数据库连接失败!");
			return false;
		}
		return true;
	}
	
}
