package cn.joy.plugin.qiniu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.plugin.PluginResource;

public class QiniuResource extends PluginResource {
	// 要上传的空间
	private QiniuBucket defaultBucket;
	private Map<String/*file type*/, QiniuBucket/*bucket*/> bucketMap;
	// 密钥配置
	private Auth auth;
	// 创建上传对象
	private UploadManager uploadManager;
	
	QiniuResource(String name, Prop prop) {
		this.auth = Auth.create(prop.get("access_key"), prop.get("secret_key"));
		this.uploadManager = new UploadManager();
		
		this.bucketMap = new HashMap<>();
		String[] buckets = prop.get("buckets").split(",");
		for(String bucketKey:buckets){
			Prop bucketProp = prop.getSubPropTrimPrefix("bucket."+bucketKey+".");
			QiniuBucket bucket = new QiniuBucket();
			bucket.setName(bucketProp.get("name"));
			bucket.setDomain(bucketProp.get("domain"));
			bucket.setExpires(bucketProp.getLong("expires", bucket.getExpires()));
			
			Prop policyProp = bucketProp.getSubPropTrimPrefix("policy.");
			if(!policyProp.isEmpty())
				bucket.setPolicyMap(new StringMap().putAll(policyProp.toMap()));
			bucketMap.put(bucketKey, bucket);
			
			if(defaultBucket==null)
				defaultBucket = bucket;
		}
	}
	
	private QiniuBucket getBucketByFileType(String fileType){
		QiniuBucket bucket = bucketMap.get(fileType);
		return bucket==null?defaultBucket:bucket;
	}
	
	public String getUploadToken(String fileType){
		QiniuBucket bucket = getBucketByFileType(fileType);
		return auth.uploadToken(bucket.getName(), null, bucket.getExpires(), bucket.getPolicyMap());
	}
	
	public String getDownloadURL(String fileKey){
		return auth.privateDownloadUrl(fileKey, 600);
	}
	
	public String getDownloadURL(String fileKey, long expires){
		return auth.privateDownloadUrl(fileKey, expires);
	}
	
	public void upload(String filePath, String fileType, Map<String, Object> datas){
		try {
			Response res = uploadManager.put(filePath, (String)datas.get("key"), getUploadToken(fileType), new StringMap().putAll(datas), null, false);
			// 打印返回的信息
			if(logger.isDebugEnabled())
				logger.debug(res.bodyString());
		} catch (QiniuException e) {
			logger.error("", e);
			
			Response r = e.response;
			// 请求失败时打印的异常的信息
			if(logger.isDebugEnabled())
				logger.debug(r.toString());
			try {
				// 响应的文本信息
				if(logger.isDebugEnabled())
					logger.debug(r.bodyString());
			} catch (QiniuException e1) {
				// ignore
			}
		}
	}

	@Override
	public void release() {
		auth = null;
		uploadManager = null;
		if(bucketMap!=null)
			bucketMap.clear();
	}

}
