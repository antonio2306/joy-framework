package cn.joy.plugin.hibernate3.db;

/**
 * 数据库操作通用回调接口
 * @author liyy
 * @date 2014-07-06
 */
public interface DbCallback {
	public void run(DbSession dbSession, Object... params) throws Exception;
}
