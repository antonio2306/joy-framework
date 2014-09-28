package cn.joy.demo.app.module.order;

import cn.joy.framework.annotation.Module;

@Module(name="订单模块", desc="", init="initModule")
public class OrderModule {
	public void initModule(){
		System.out.println("init order module...");
	}

}
