package cn.joy.framework.plugin.spring.db;
/**
 * 数据库操作异常
 * @author liyy
 * @date 2014-07-18
 */
public class DbException extends RuntimeException {

	public DbException(String message) {
		super(message);
	}

	public DbException(Throwable cause) {
		super(cause);
	}

	public DbException(String message, Throwable cause) {
		super(message, cause);
	}
}
