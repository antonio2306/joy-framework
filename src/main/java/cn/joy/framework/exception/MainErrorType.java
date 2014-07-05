package cn.joy.framework.exception;
/**
 * 主错误编码
 * @author liyy
 * @date 2014-05-20
 */
public enum MainErrorType {
    SERVICE_CURRENTLY_UNAVAILABLE,
    FORBIDDEN_REQUEST,
    BUSINESS_LOGIC_ERROR,
    INVALID_SESSION,
    INVALID_TOKEN,
    INVALID_METHOD,
    OBSOLETED_METHOD,
    MISSING_REQUIRED_ARGUMENTS,
    MISSING_RESULT,
    INVALID_ARGUMENTS,
    PROGRAM_ERROR;

    public String value() {
        return (this.ordinal()+1)+"";
    }
    
}

