package cn.joy.framework.exception;

import cn.joy.framework.rule.RuleResult;

/**
 * 规则执行异常
 * @author liyy
 * @date 2014-05-20
 */
public class RuleException extends RuntimeException {
	private MainError error;
	private RuleResult failResult;

	public RuleException(String message){
		super(message);
	}
	
	public RuleException(RuleResult failResult){
		this.failResult = failResult;
	}
	
	public RuleException(Throwable thr){
		super(thr);
	}
	
	public RuleException(MainErrorType mainErrorType){
		this.error = MainError.create(mainErrorType);
	}
	
	public RuleException(SubErrorType subErrorType, Object... params){
		this.error = SubError.getMainError(subErrorType);
		SubError subError = SubError.create(subErrorType, params);
		this.error.addSubError(subError);
	}

	public MainError getError() {
		return error;
	}
	
	@Override
	public String getMessage() {
		if(this.error!=null){
			return this.error.toJSON();
		}else if(this.failResult!=null){
			return this.failResult.getMsg();
		}
		return super.getMessage();
	}

	public RuleResult getFailResult() {
		return failResult;
	}
	
}
