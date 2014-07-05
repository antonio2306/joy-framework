package cn.joy.framework.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.I18NKit;
import cn.joy.framework.kits.JsonKit;
/**
 * 主错误
 * @author liyy
 * @date 2014-05-20
 */
public class MainError {
	private static final Locale defaultLocale = JoyManager.getServer().getLocale();
	private static final String ERROR_CODE_PREFIX = "ERROR_";
	private static final String ERROR_SOLUTION_SUBFIX = "_SOLUTION";

    private String code;

    private String message;

    private String solution;

    private List<SubError> subErrors = new ArrayList<SubError>();

    public MainError(String code, String message, String solution) {
        this.code = code;
        this.message = message;
        this.solution = solution;
    }
    
	public static MainError create(MainErrorType mainErrorType) {
		return create(mainErrorType, defaultLocale);
	}

	public static MainError create(MainErrorType mainErrorType, Locale locale) {
		String errorMessage = I18NKit.getText(ERROR_CODE_PREFIX + mainErrorType.value(), locale);
		String errorSolution = I18NKit.getText(ERROR_CODE_PREFIX
				+ mainErrorType.value() + ERROR_SOLUTION_SUBFIX, locale);
		return new MainError(mainErrorType.value(), errorMessage, errorSolution);
	}
	
	public String toJSON() {
		/*
		<error code="9"> 
			<message>无效的参数</message>
			<solution>请查看方法参数说明</solution>
			<subErrors>
				<subError code="isv.invalid-paramete:userName">
				<message>参数userName无效，格式不对、非法值等</message>
				</subError>
			</subErrors>
		</error>
		 */
		return JsonKit.object2Json(this);
	}

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getSolution() {
        return solution;
    }

    public List<SubError> getSubErrors() {
        return this.subErrors;
    }

    public void setSubErrors(List<SubError> subErrors) {
        this.subErrors = subErrors;
    }

    public MainError addSubError(SubError subError) {
        this.subErrors.add(subError);
        return this;
    }
}

