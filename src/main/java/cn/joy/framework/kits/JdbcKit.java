package cn.joy.framework.kits;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class JdbcKit {
	// 私有变量，初始化连接参数
	private static String url = "jdbc:mysql://localhost:3306/jdbc";
	private static String user = "root";
	private static String password = "";

	// 私有构造器
	private JdbcKit() {

	}

	// 单例模式
	/*
	 * private static JdbcKit instance = new JdbcKit(); public static
	 * JdbcKit getInstance() { // 延迟加载 if(instance == null) { //
	 * 加锁解决并发问题，将类锁起再构造 synchronized(JdbcKit.class) { // 双重检查，不可少 if(instance
	 * == null) { instance = new JdbcKit(); } } } return instance; }
	 */

	// 静态代码块，注册驱动
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	// 静态方法，创建连接
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	public static Connection getConnection(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	// 静态方法，释放资源
	public static void free(ResultSet rs, Statement st, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void debug(String msg) {
		debug(msg, null);
	}

	private static void debug(String msg, Throwable e) {
		System.out.println(msg);
		if (e != null)
			e.printStackTrace();
	}

	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				debug("Could not close JDBC Connection", ex);
			} catch (Throwable ex) {
				debug("Unexpected exception on closing JDBC Connection", ex);
			}
		}
	}

	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				debug("Could not close JDBC Statement", ex);
			} catch (Throwable ex) {
				debug("Unexpected exception on closing JDBC Statement", ex);
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				debug("Could not close JDBC ResultSet", ex);
			} catch (Throwable ex) {
				debug("Unexpected exception on closing JDBC ResultSet", ex);
			}
		}
	}

	public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
		Object obj = rs.getObject(index);
		if (obj instanceof Blob) {
			obj = rs.getBytes(index);
		} else if (obj instanceof Clob) {
			obj = rs.getString(index);
		} else if (obj != null && obj.getClass().getName().startsWith("oracle.sql.TIMESTAMP")) {
			obj = rs.getTimestamp(index);
		} else if (obj != null && obj.getClass().getName().startsWith("oracle.sql.DATE")) {
			String metaDataClassName = rs.getMetaData().getColumnClassName(index);
			if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
				obj = rs.getTimestamp(index);
			} else {
				obj = rs.getDate(index);
			}
		} else if (obj != null && obj instanceof java.sql.Date) {
			if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
				obj = rs.getTimestamp(index);
			}
		}
		return obj;
	}

	public static boolean supportsBatchUpdates(Connection con) {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd != null) {
				if (dbmd.supportsBatchUpdates()) {
					debug("JDBC driver supports batch updates");
					return true;
				} else {
					debug("JDBC driver does not support batch updates");
				}
			}
		} catch (SQLException ex) {
			debug("JDBC driver 'supportsBatchUpdates' method threw exception", ex);
		} catch (AbstractMethodError err) {
			debug("JDBC driver does not support JDBC 2.0 'supportsBatchUpdates' method", err);
		}
		return false;
	}

	public static boolean isNumeric(int sqlType) {
		return Types.BIT == sqlType || Types.BIGINT == sqlType || Types.DECIMAL == sqlType || Types.DOUBLE == sqlType
				|| Types.FLOAT == sqlType || Types.INTEGER == sqlType || Types.NUMERIC == sqlType
				|| Types.REAL == sqlType || Types.SMALLINT == sqlType || Types.TINYINT == sqlType;
	}

}
