package cn.joy.framework.exception;

import java.util.EnumMap;
/**
 * 子错误类型编码
 * @author liyy
 * @date 2014-05-20
 */
public enum SubErrorType {
    ISP_SERVICE_UNAVAILABLE,
    ISP_SERVICE_TIMEOUT,

    ISV_NOT_EXIST,
    ISV_INVALID_PERMISSION,
    ISV_MISSING_PARAMETER,
    ISV_INVALID_PARAMETER,
    ISV_PARAMETERS_MISMATCH;

    private static EnumMap<SubErrorType, String> errorCodeMap = new EnumMap<SubErrorType, String>(SubErrorType.class);

    static {
        errorCodeMap.put(SubErrorType.ISP_SERVICE_UNAVAILABLE, "isp.xxx-service-unavailable");
        errorCodeMap.put(SubErrorType.ISP_SERVICE_TIMEOUT, "isp.xxx-service-timeout");

        errorCodeMap.put(SubErrorType.ISV_NOT_EXIST, "isv.xxx-not-exist:invalid-yyy");
        errorCodeMap.put(SubErrorType.ISV_MISSING_PARAMETER, "isv.missing-parameter:xxx");
        errorCodeMap.put(SubErrorType.ISV_INVALID_PARAMETER, "isv.invalid-parameter:xxx");
        errorCodeMap.put(SubErrorType.ISV_INVALID_PERMISSION, "isv.invalid-permission");
        errorCodeMap.put(SubErrorType.ISV_PARAMETERS_MISMATCH, "isv.parameters-mismatch:xxx-and-yyy");
    }

    public String value() {
        return errorCodeMap.get(this);
    }
}

