package cn.joy.framework.kits;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.impl.Log4jLoggerAdapter;
import org.slf4j.spi.LocationAwareLogger;

import cn.joy.framework.core.JoyManager;

/**
 * 日志工具类
 */
public class LogKit {
	public static final String MDC_KEY = "K_MDC";
	private static final Map<String, Log> map = new HashMap<String, Log>();
	private static Set<String> debugLogKeys = new HashSet<>();

	private LogKit() {
	}

	/**
	 * 使用默认的Log对象
	 */
	public static Log use() {
		return use("joy");
	}

	/**
	 * 使用指定key的Log对象，无则创建
	 */
	public static Log use(String logKey) {
		Log result = map.get(logKey);
		if (result == null) {
			synchronized (map) {
				result = map.get(logKey);
				if (result == null) {
					result = new Log(logKey);
					map.put(logKey, result);
				}
			}
		}
		return result;
	}

	public static Log get() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return new Log(stackTrace[2].getClassName());
	}

	/**
	 * 创建指定类型的Log对象
	 */
	public static Log getLog(Class<?> clazz) {
		return new Log(clazz);
	}

	public static void useDebugLog(String logKey) {
		debugLogKeys.add(logKey);
	}

	public static void unuseDebugLog(String logKey) {
		debugLogKeys.remove(logKey);
	}

	public static Set<String> getDebugLogs() {
		return debugLogKeys;
	}

	public static boolean isDebugLogKey(String logKey) {
		return debugLogKeys.contains(logKey);
	}

	public static String getMDC(String key) {
		return MDC.get(key);
	}

	public static void setMDC(String key, String value) {
		MDC.put(key, value);
	}
	
	public static void removeMDC(String key) {
		MDC.remove(key);
	}

	public static void clearMDC() {
		MDC.clear();
	}

	public static class Log {
		private Logger logger;

		Log(String logKey) {
			this.logger = LoggerFactory.getLogger(logKey);
		}

		Log(Class<?> clazz) {
			this.logger = LoggerFactory.getLogger(clazz);
		}

		public Logger getLogger() {
			return logger;
		}

		private Logger getRealLogger() {
			if (!debugLogKeys.isEmpty()) {
				String logKey = getMDC(MDC_KEY);
				if (StringKit.isNotEmpty(logKey))
					return getFileLogger(logKey, JoyManager.getServer().getDebugLogDir());
			}
			return logger;
		}

		public void trace(String msg) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jTrace((Log4jLoggerAdapter) logger, msg, null);
			} else
				logger.trace(msg);
		}

		public void trace(String msg, Object... params) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jTrace((Log4jLoggerAdapter) logger, msg, params);
			} else
				logger.trace(msg);
		}

		public void debug(String msg) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jDebug((Log4jLoggerAdapter) logger, msg, null);
			} else
				logger.debug(msg);
		}

		public void debug(String msg, Object... params) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jDebug((Log4jLoggerAdapter) logger, msg, params);
			} else
				logger.debug(msg);
		}

		public void info(String msg) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jInfo((Log4jLoggerAdapter) logger, msg, null);
			} else
				logger.info(msg);
		}

		public void info(String msg, Object... params) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jInfo((Log4jLoggerAdapter) logger, msg, params);
			} else
				logger.info(msg);
		}

		public void warn(String msg) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jWarn((Log4jLoggerAdapter) logger, msg, null);
			} else
				logger.warn(msg);
		}

		public void warn(String msg, Object... params) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jWarn((Log4jLoggerAdapter) logger, msg, params);
			} else
				logger.warn(msg);
		}

		public void error(String msg) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jError((Log4jLoggerAdapter) logger, msg, null);
			} else
				logger.error(msg);
		}

		public void error(String msg, Object... params) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jError((Log4jLoggerAdapter) logger, msg, params);
			} else
				logger.error(msg);
		}

		public void error(String msg, Throwable t) {
			Logger logger = getRealLogger();
			if (logger instanceof Log4jLoggerAdapter) {
				log4jError((Log4jLoggerAdapter) logger, msg, new Object[]{t});
			} else
				logger.error(msg);
		}
		
		public void error(Throwable t) {
			error("", t);
		}
	}

	/**
	 * 使用默认Log输出Trace级别日志
	 */
	public static void trace(String msg) {
		use().trace(msg);
	}

	/**
	 * 使用默认Log输出Trace级别日志，支持参数格式化
	 */
	public static void trace(String msg, Object... params) {
		use().trace(msg, params);
	}

	/**
	 * 使用默认Log输出Debug级别日志
	 */
	public static void debug(String msg) {
		use().debug(msg);
	}

	/**
	 * 使用默认Log输出Debug级别日志，支持参数格式化
	 */
	public static void debug(String msg, Object... params) {
		use().debug(msg, params);
	}

	/**
	 * 使用默认Log输出Info级别日志
	 */
	public static void info(String msg) {
		use().info(msg);
	}

	/**
	 * 使用默认Log输出Info级别日志，支持参数格式化
	 */
	public static void info(String msg, Object... params) {
		use().info(msg, params);
	}

	/**
	 * 使用默认Log输出Warn级别日志
	 */
	public static void warn(String msg) {
		use().warn(msg);
	}

	/**
	 * 使用默认Log输出Warn级别日志，支持参数格式化
	 */
	public static void warn(String msg, Object... params) {
		use().warn(msg, params);
	}

	/**
	 * 使用默认Log输出Error级别日志
	 */
	public static void error(String msg) {
		use().error(msg);
	}

	/**
	 * 使用默认Log输出Error级别日志，支持参数格式化
	 */
	public static void error(String msg, Object... params) {
		use().error(msg, params);
	}

	/**
	 * 使用默认Log输出Error级别日志
	 */
	public static void error(String msg, Throwable t) {
		use().error(msg, t);
	}

	/**
	 * 使用默认Log输出Error级别日志
	 */
	public static void error(String msg, Throwable t, Object... params) {
		use().error(msg, t, params);
	}

	/**
	 * 使用默认Log输出Error级别日志
	 */
	public static void error(Throwable t) {
		use().error("", t);
	}

	/**
	 * 获取指定名称的基于log4j的Logger对象
	 * 
	 * 如果没有配置appender，则按默认策略生成一个DailyRollingFileAppender
	 */
	public static Logger getDailyRollingFileLogger(String logKey, String logDir) {
		Logger logger = LoggerFactory.getLogger(logKey);

		if (logger instanceof Log4jLoggerAdapter) {
			Log4jLoggerAdapter log4jAdapter = (Log4jLoggerAdapter) logger;
			org.apache.log4j.Logger log4j = null;
			try {
				Field loggerField = Log4jLoggerAdapter.class.getDeclaredField("logger");
				loggerField.setAccessible(true);
				log4j = (org.apache.log4j.Logger) loggerField.get(log4jAdapter);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!log4j.getAllAppenders().hasMoreElements()) {
				DailyRollingFileAppender appender = new DailyRollingFileAppender();
				appender.setDatePattern("'.'yyyy-MM-dd'.log'");

				addLog4jFileAppender(log4j, appender, logDir + File.separator + logKey + ".log");
			}
		}
		return logger;
	}

	/**
	 * 获取指定名称的基于log4j的Logger对象
	 * 
	 * 如果没有配置appender，则按默认策略生成一个FileAppender
	 */
	public static Logger getFileLogger(String logKey, String logDir) {
		Logger logger = LoggerFactory.getLogger(logKey);

		if (logger instanceof Log4jLoggerAdapter) {
			Log4jLoggerAdapter log4jAdapter = (Log4jLoggerAdapter) logger;
			org.apache.log4j.Logger log4j = null;
			try {
				Field loggerField = Log4jLoggerAdapter.class.getDeclaredField("logger");
				loggerField.setAccessible(true);
				log4j = (org.apache.log4j.Logger) loggerField.get(log4jAdapter);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!log4j.getAllAppenders().hasMoreElements()) {
				addLog4jFileAppender(log4j, new FileAppender(), logDir + File.separator + logKey + ".log");
			}
		}
		return logger;
	}

	private static void addLog4jFileAppender(org.apache.log4j.Logger log4j, FileAppender appender, String logFile) {
		log4j.setLevel(Level.DEBUG);
		// 是否继承父Logger
		log4j.setAdditivity(true);
		PatternLayout layout = new PatternLayout();
		// log的输出形式
		String conversionPattern = "%d %-5p %F %L - %m%n";
		layout.setConversionPattern(conversionPattern);
		appender.setLayout(layout);
		appender.setFile(logFile);
		appender.setEncoding("UTF-8");
		// true:在已存在log文件后面追加 false:新log覆盖以前的log
		appender.setAppend(true);
		// 适用当前配置
		appender.activateOptions();
		// 将新的Appender加到Logger中
		log4j.addAppender(appender);
	}

	private static void log4jTrace(Log4jLoggerAdapter logger, String msg, Object[] params) {
		logger.log(null, Log.class.getName(), LocationAwareLogger.TRACE_INT, formatMsg(msg, params), params, null);
	}

	private static void log4jDebug(Log4jLoggerAdapter logger, String msg, Object[] params) {
		logger.log(null, Log.class.getName(), LocationAwareLogger.DEBUG_INT, formatMsg(msg, params), params, null);
	}

	private static void log4jInfo(Log4jLoggerAdapter logger, String msg, Object[] params) {
		logger.log(null, Log.class.getName(), LocationAwareLogger.INFO_INT, formatMsg(msg, params), params, null);
	}

	private static void log4jWarn(Log4jLoggerAdapter logger, String msg, Object[] params) {
		logger.log(null, Log.class.getName(), LocationAwareLogger.WARN_INT, formatMsg(msg, params), params, null);
	}

	private static void log4jError(Log4jLoggerAdapter logger, String msg, Object[] params) {
		logger.log(null, Log.class.getName(), LocationAwareLogger.ERROR_INT, formatMsg(msg, params), params, getThrowableCandidate(params));
	}
	
	private static String formatMsg(String msg, Object[] params){
		if (params != null)
			return MessageFormatter.arrayFormat(msg, params).getMessage();
		return msg;
	}

	private static Throwable getThrowableCandidate(Object[] argArray) {
		if (argArray == null || argArray.length == 0) {
			return null;
		}

		final Object lastEntry = argArray[argArray.length - 1];
		if (lastEntry instanceof Throwable) {
			return (Throwable) lastEntry;
		}
		return null;
	}

}
