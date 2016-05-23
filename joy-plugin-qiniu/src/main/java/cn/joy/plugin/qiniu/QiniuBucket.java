package cn.joy.plugin.qiniu;

import com.qiniu.util.StringMap;

public class QiniuBucket {
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_FILE = "file";
	private String name;
	private String domain;
	private long expires = 3600;
	private StringMap policyMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public StringMap getPolicyMap() {
		return policyMap;
	}

	public void setPolicyMap(StringMap policyMap) {
		this.policyMap = policyMap;
	}
}
