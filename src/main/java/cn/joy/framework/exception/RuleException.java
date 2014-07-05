package cn.joy.framework.exception;

/**
 * 规则执行异常
 * @author liyy
 * @date 2014-05-20
 */
public class RuleException extends RuntimeException {
	private MainError error;

	public RuleException(String message){
		super(message);
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
		}
		return super.getMessage();
	}
	
}
