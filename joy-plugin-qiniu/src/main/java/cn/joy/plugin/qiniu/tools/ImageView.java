package cn.joy.plugin.qiniu.tools;

import cn.joy.framework.core.JoyList;
import cn.joy.framework.kits.StringKit;

/**
 * 图片基本处理
 * 
 * imageView2/<mode>/w/<LongEdge>
 *        /h/<ShortEdge>
 *        /format/<Format>
 *        /interlace/<Interlace>
 *        /q/<Quality>
 *        /ignore-error/<ignoreError>
 */
public class ImageView {
	private int mode;
	private int width;
	private int height;
	private String format;
	private String interlace = "0";
	private int quality;
	private String ignoreError;
	
	private ImageView(){
		
	}
	
	public static ImageView create(){
		return new ImageView();
	}
	
	/**
	 * 模式	说明
	 *		/0/w/<LongEdge>/h/<ShortEdge>	限定缩略图的长边最多为<LongEdge>，短边最多为<ShortEdge>，进行等比缩放，不裁剪。如果只指定 w 参数则表示限定长边（短边自适应），只指定 h 参数则表示限定短边（长边自适应）。
	 *		/1/w/<Width>/h/<Height>			限定缩略图的宽最少为<Width>，高最少为<Height>，进行等比缩放，居中裁剪。转后的缩略图通常恰好是 <Width>x<Height> 的大小（有一个边缩放的时候会因为超出矩形框而被裁剪掉多余部分）。如果只指定 w 参数或只指定 h 参数，代表限定为长宽相等的正方图。
	 *		/2/w/<Width>/h/<Height>			限定缩略图的宽最多为<Width>，高最多为<Height>，进行等比缩放，不裁剪。如果只指定 w 参数则表示限定宽（长自适应），只指定 h 参数则表示限定长（宽自适应）。它和模式0类似，区别只是限定宽和高，不是限定长边和短边。从应用场景来说，模式0适合移动设备上做缩略图，模式2适合PC上做缩略图。
	 *		/3/w/<Width>/h/<Height>			限定缩略图的宽最少为<Width>，高最少为<Height>，进行等比缩放，不裁剪。如果只指定 w 参数或只指定 h 参数，代表长宽限定为同样的值。你可以理解为模式1是模式3的结果再做居中裁剪得到的。
	 *		/4/w/<LongEdge>/h/<ShortEdge>	限定缩略图的长边最少为<LongEdge>，短边最少为<ShortEdge>，进行等比缩放，不裁剪。如果只指定 w 参数或只指定 h 参数，表示长边短边限定为同样的值。这个模式很适合在手持设备做图片的全屏查看（把这里的长边短边分别设为手机屏幕的分辨率即可），生成的图片尺寸刚好充满整个屏幕（某一个边可能会超出屏幕）。
	 *		/5/w/<LongEdge>/h/<ShortEdge>	限定缩略图的长边最少为<LongEdge>，短边最少为<ShortEdge>，进行等比缩放，居中裁剪。如果只指定 w 参数或只指定 h 参数，表示长边短边限定为同样的值。同上模式4，但超出限定的矩形部分会被裁剪。
	 */
	public ImageView mode(int mode){
		this.mode = mode;
		return this;
	}
	
	public ImageView width(int width){
		this.width = width;
		return this;
	}
	
	public ImageView height(int height){
		this.height = height;
		return this;
	}
	
	/**
	 * 新图的图片质量
	 * 取值范围是[1, 100]，默认75。
	 * 七牛会根据原图质量算出一个修正值，取修正值和指定值中的小值。
	 * 注意：
	 * ● 如果图片的质量值本身大于90，会根据指定值进行处理，此时修正值会失效。
	 * ● 指定值后面可以增加 !，表示强制使用指定值，如100!。
	 * ● 支持图片类型：jpg。
	 */
	public ImageView quality(int quality){
		this.quality = quality;
		return this;
	}
	
	/**
	 * 新图的输出格式
	 * 取值范围：jpg，gif，png，webp等，默认为原图格式。
	 */
	public ImageView format(String format){
		this.format = format;
		return this;
	}
	
	/**
	 * 支持渐进显示
	 * 适用目标格式：jpg
	 * 效果：网速慢时，图片显示由模糊到清晰。
	 */
	public ImageView interlace(){
		this.interlace = "1";
		return this;
	}
	
	/**
	 *  若图像处理的结果失败，则返回原图。
	 *  若图像处理的结果成功，则正常返回处理结果。
	 */
	public ImageView ignoreError(){
		this.ignoreError = "1";
		return this;
	}
	
	public String toUrl(){
		JoyList<Object> options = new JoyList<Object>().addNotEmpty(mode).addWhen(width>0, "w", width).addWhen(height>0, "h", height)
				.addNotEmpty("format", format).addWhen("1".equals(interlace), "interlace", interlace).addWhen(quality>0, "q", quality)
				.addWhen("1".equals(ignoreError), "ignore-error", ignoreError);
		
		return "imageView2/"+StringKit.joinCollection(options.list(), "/");
	}
	
}
