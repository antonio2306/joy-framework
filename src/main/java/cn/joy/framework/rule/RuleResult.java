package cn.joy.framework.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.joy.framework.exception.IErrorType;
import cn.joy.framework.exception.MainError;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.StringKit;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 业务规则执行结果
 * @author liyy
 * @date 2014-05-20
 */
public class RuleResult {
	private static final RuleResult NULL_RULE_RESULT;
	public static final String FLAG_EMPTY_RESULT = "EMPTY";
	public static final String KEY_DATA_MAP = "_key_data_map";
	private boolean result;
	private String rMessage;
	private Object rContent;
	private RuleExtraData rExtra;
	private RuleException rException;
	private MainError rError;
	
	static{
		NULL_RULE_RESULT = RuleResult.create().fail(FLAG_EMPTY_RESULT);
		NULL_RULE_RESULT.rExtra = RuleExtraData.NULL_RULE_EXTRA;
	}

	private RuleResult(){
		rExtra = RuleExtraData.create();
	}
	
	public static RuleResult create(){
		return new RuleResult();
	}
	
	public static RuleResult empty(){
		return NULL_RULE_RESULT.fail(FLAG_EMPTY_RESULT).clearExtra();
	}
	
	public static RuleResult create(boolean result, String message){
		RuleResult sResult = create();
		sResult.result = result;
		sResult.rMessage = message;
		return sResult;
	}
	
	public RuleResult success(){
		return success("");
	}
	
	public RuleResult success(String message){
		return success(message, null);
	}
	
	public RuleResult success(String message, Object content){
		this.result = true;
		this.rMessage = message;
		this.rContent = content;
		return this;
	}
	
	public RuleResult fail(){
		return fail("");
	}
	
	public RuleResult fail(IErrorType errorType){
		return fail(errorType.getErrorCode());
	}
	
	public RuleResult fail(String message){
		this.result = false;
		this.rMessage = message;
		return this;
	}
	
	public RuleResult fail(RuleException exception){
		this.result = false;
		this.rException = exception;
		RuleResult failResult = exception.getFailResult();
		if(failResult!=null){
			this.rMessage = failResult.getMsg();
			this.rContent = failResult.getContent();
			this.rError = failResult.getError();
		}else{
			this.rMessage = exception.getMessage();
		}
		return this;
	}
	
	public RuleResult fail(MainError error){
		this.result = false;
		this.rError = error;
		this.rMessage = error.toJSON();
		return this;
	}
	
	@JsonIgnore
	public boolean isSuccess() {
		return result;
	}
	
	public RuleResult putContent(Object content){
		rContent = content;
		return this;
	}
	
	@JsonIgnore
	public Object getContentThrowExceptionWhenFail(){
		if(!this.isSuccess())
			throw new RuntimeException("规则出错， result="+this.toJSON());
		return rContent;
	}

	@JsonIgnore
	public MainError getError() {
		return rError;
	}
	
	public RuleResult clearExtra(){
		rExtra.clear();
		return this;
	}
	
	public RuleResult putExtraData(String key, Object value){
		rExtra.put(key, value);
		return this;
	}

	@JsonIgnore
	public Object getExtraData(String key){
		return this.getExtra().get(key);
	}

	@JsonIgnore
	public Object getDataThrowExceptionWhenFail(String key){
		if(!this.isSuccess())
			throw new RuntimeException("规则出错， result="+this.toJSON());
		return this.getExtra().get(key);
	}
	
	@Override
	public String toString() {
		return "结果："+(result?"成功":"失败")+"\n"
				+"消息："+rMessage;
	}
	
	public boolean getResult() {
		return result;
	}
	
	public String getMsg() {
		return rMessage;
	}
	
	public Object getContent(){
		return rContent;
	}
	
	public Map<String, Object> getExtra(){
		return rExtra.getDatas();
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
	
	public void setMsg(String msg) {
		this.rMessage = msg;
	}
	
	public void setContent(Object content) {
		this.putContent(content);
	}
	
	public String toJSON(){
		return JsonKit.object2Json(this);
	}

	public static RuleResult fromJSON(String json){
		return (RuleResult)JsonKit.json2Object(json, RuleResult.class);
	}
	
	@JsonIgnore
	public String getStringFromContent(){
		return StringKit.getString(rContent);
	}
	
	@JsonIgnore
	public Map<String, Object> getMapFromContent(){
		return (Map<String, Object>)rContent;
	}
	
	@JsonIgnore
	public List<Map<String, Object>> getListMapFromContent(){
		return (List<Map<String, Object>>)rContent;
	}
	
}
