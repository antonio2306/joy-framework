package cn.joy.framework.exception;

import cn.joy.framework.rule.RuleResult;

/**
 * 规则执行时异常
 * @author liyy
 * @date 2014-05-20
 */
public class RuleException extends RuntimeException {
	private RuleResult failResult;

	public RuleException(String message){
		super(message);
		this.failResult = RuleResult.create().fail(message);
	}
	
	public RuleException(RuleResult failResult){
		this.failResult = failResult;
	}
	
	public RuleException(Throwable thr){
		super(thr);
		this.failResult = RuleResult.create().fail(thr.getMessage());
	}
	
	public RuleException(MainErrorType mainErrorType){
		this.failResult = RuleResult.create().fail(MainError.create(mainErrorType));
	}
	
	public RuleException(SubErrorType subErrorType, Object... params){
		MainError mainError = SubError.getMainError(subErrorType);
		SubError subError = SubError.create(subErrorType, params);
		mainError.addSubError(subError);
		this.failResult = RuleResult.create().fail(mainError);
	}

	@Override
	public String getMessage() {
		if(this.failResult!=null){
			return this.failResult.getMsg();
		}
		return super.getMessage();
	}

	public RuleResult getFailResult() {
		return failResult;
	}
	
}
