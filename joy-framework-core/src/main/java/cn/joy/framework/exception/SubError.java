package cn.joy.framework.exception;

import java.util.EnumMap;
import java.util.Locale;

import cn.joy.framework.core.JoyManager;
/**
 * 子错误定义，一个主错误中可包含若干个子错误
 * @author liyy
 * @date 2014-05-20
 */
public class SubError {
	private static final Locale defaultLocale = JoyManager.getServer().getLocale();
	private static final EnumMap<SubErrorType, MainErrorType> SUBERROR_MAINERROR_MAPPINGS = new EnumMap<SubErrorType, MainErrorType>(
			SubErrorType.class);

	static {
		SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_SERVICE_UNAVAILABLE, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
		SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_SERVICE_TIMEOUT, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);

		SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_MISSING_PARAMETER, MainErrorType.MISSING_REQUIRED_ARGUMENTS);
		SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_PARAMETERS_MISMATCH, MainErrorType.INVALID_ARGUMENTS);
		SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_INVALID_PARAMETER, MainErrorType.INVALID_ARGUMENTS);

		SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_NOT_EXIST, MainErrorType.INVALID_ARGUMENTS);
	}

	private String code;

	private String message;

	public SubError() {
	}

	public SubError(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public static MainError getMainError(SubErrorType subErrorType) {
		return getMainError(subErrorType, defaultLocale);
	}
	
	public static MainError getMainError(SubErrorType subErrorType, Locale locale) {
		return MainError.create(SUBERROR_MAINERROR_MAPPINGS.get(subErrorType), locale);
	}
	
	public static SubError create(SubErrorType subErrorType, Object... params) {
		return create(subErrorType, defaultLocale, params);
	}

	public static SubError create(SubErrorType subErrorType, Locale locale, Object... params) {
		return new SubError(getSubErrorCode(subErrorType, params), String.format(subErrorType.value(), params));//I18NKit.getText(subErrorType.value(), locale, params));
	}
	
	public static MainError createMain(SubErrorType subErrorType, Object... params) {
		return createMain(subErrorType, defaultLocale, params);
	}

	public static MainError createMain(SubErrorType subErrorType, Locale locale, Object... params) {
		MainError mainError = getMainError(subErrorType, locale);
		SubError subError = new SubError(getSubErrorCode(subErrorType, params), String.format(subErrorType.value(), params));//I18NKit.getText(subErrorType.value(), locale, params));
		mainError.addSubError(subError);
		return mainError;
	}
	
	private static final String PARAM_1 = "xxx";
	private static final String PARAM_2 = "yyyy";
	
	private static String getSubErrorCode(SubErrorType subErrorType, Object... params) {
        String subErrorCode = subErrorType.value();
        if (params.length > 0) {
            if (params.length == 1) {
                subErrorCode = subErrorCode.replace(PARAM_1, (String) params[0]);
            } else {
                subErrorCode = subErrorCode.replace(PARAM_1, (String) params[0]);
                if (params[1] != null) {
                    subErrorCode = subErrorCode.replace(PARAM_2, (String) params[1]);
                }
            }
        }
        return subErrorCode;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
