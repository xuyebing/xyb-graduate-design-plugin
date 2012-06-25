package buaa.sei.xyb.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import buaa.sei.xyb.resource.UtilityTools;

/**
 *  创建同义词词库
 */
public class CreateSynonymDict {

	public static String dataBaseName = "SynonymDB";
	public static String synonymTableName = "synonymDict";
	private static String keyField = "cn_word";
	private static String lineNo = "lineNo";
	
	public static void createSynonymTable() {
		DataBaseOperation.createDataBase(dataBaseName);
		StringBuilder tableFields = new StringBuilder(keyField + " VARCHAR(200) NOT NULL,");
		tableFields.append("lineNo TEXT, PRIMARY KEY (" + keyField + ")");
		DataBaseOperation.createTable(dataBaseName, synonymTableName, tableFields.toString());
	}
	public static String getLineNo(String cnWd) {
		Connection conn = DataBaseOperation.getConn(dataBaseName);
		String retStr = null;
		Statement st = null;
		try {
			st = conn.createStatement();
			String findSql = "Select lineNo from " + synonymTableName + " where " + keyField + "=\"" + cnWd + "\";";
			ResultSet rs = st.executeQuery(findSql);
			if (rs.next()) {
				retStr = rs.getString(1);
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retStr;
	}
	
	private static String getCnWdLineNo(Connection conn, String cnWd) {
		String retStr = null;
		Statement st = null;
		try {
			st = conn.createStatement();
			String findSql = "Select lineNo from " + synonymTableName + " where " + keyField + "=\"" + cnWd + "\";";
			ResultSet rs = st.executeQuery(findSql);
			if (rs.next()) {
				retStr = rs.getString(1);
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retStr;
	}
	private static void insertIntoSynonymTable(Connection conn, String tableName, String columnStr, String insertValue) {
		Statement st = null;
		try {
			st = conn.createStatement();
			String insertSql = "replace into " + tableName + "(" + columnStr + ") " + " values (" + insertValue + ");";
			st.execute(insertSql);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void insertSynonymTable(String filePath) {
		Connection conn = DataBaseOperation.getConn(dataBaseName);
		if (conn == null) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			UtilityTools.warning(shell, "数据库连接失败!");
			return ;
		}
		File file = new File(filePath);
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = "";
				int lineId = 1;
				while ((line = br.readLine()) != null) {
					char[] lineCh = line.toCharArray();
					// 根据同义词表的特点，判断第8个字符是否为等号“=”，只对是等号的行进行分析
					assert(lineCh.length >= 8);
					if (lineCh[7] == '=') {
						String subLine = line.substring(8).trim();
						String[] cnWords = subLine.split("\\s+");
						for (String cnWd : cnWords) {
							if (!cnWd.equals("")) {
								String cnLineNo = getCnWdLineNo(conn, cnWd);
								String columnStr = "cn_word, lineNo";
								if (cnLineNo == null) { // 数据库中不包含cnWd
									String value = "'" + cnWd + "', '" + lineId + "'";
									insertIntoSynonymTable(conn, synonymTableName, columnStr, value);
								} else { // 数据库中已包含cnWd，则将lineId加到其lineNo后，已逗号分隔每个行号
									cnLineNo += "," + lineId;
									String value = "'" + cnWd + "', '" + cnLineNo + "'";
									insertIntoSynonymTable(conn, synonymTableName, columnStr, value);
								}
							}
						}
						lineId++;
					}
				}
				conn.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args) {
		String fileName = "D:\\Synonym.txt";
		CreateSynonymDict.createSynonymTable();
		CreateSynonymDict.insertSynonymTable(fileName);
		System.out.println(">>> 同义词库建立完毕!");
	}
}
