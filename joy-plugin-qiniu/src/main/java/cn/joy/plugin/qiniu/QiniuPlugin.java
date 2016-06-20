package cn.joy.plugin.qiniu;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.plugin.ResourcePlugin;

@Plugin(key = "qiniu")
public class QiniuPlugin extends ResourcePlugin<QiniuResourceBuilder, QiniuResource>{

	public static QiniuResourceBuilder builder() {
		return new QiniuResourceBuilder();
	}

	public static QiniuPlugin plugin() {
		return (QiniuPlugin) JoyManager.plugin(QiniuPlugin.class);
	}
	
	@Override
	public boolean start() {
		Prop prop = getConfig();
		String[] resources = prop.get("resources").split(",");

		for (String resource : resources) {
			Prop resProp = prop.getSubPropTrimPrefix(resource + ".");
			QiniuResource qiniuResource = builder().prop(resProp).name(resource).buildTo(this);
			if(this.mainResource==null)
				this.mainResource = qiniuResource;
		}

		logger.info("qiniu plugin start success");
		return true;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public static QiniuResource use() {
		return plugin().useResource();
	}

	public static QiniuResource use(String name) {
		return plugin().useResource(name);
	}
	
	public static QiniuResource use(String name, JoyCallback missCallback) {
		return plugin().useResource(name, missCallback);
	}

	public static void unuse(String name) {
		plugin().unuseResource(name);
	}
	
	public static String getUploadToken(String fileType){
		return use().getUploadToken(fileType);
	}
	
	public static String getDownloadURL(String fileKey){
		return use().getDownloadURL(fileKey);
	}
	
	public static String getDownloadURL(String fileKey, long expires){
		return use().getDownloadURL(fileKey, expires);
	}
}
