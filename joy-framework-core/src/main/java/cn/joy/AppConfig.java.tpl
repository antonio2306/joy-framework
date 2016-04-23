public class AppConfig{
	public void init() {
		//用户需要自定义安全管理器，检查请求的安全性
		JoyManager.setSecurityManager(new ShangtanSecurityManager());
		
		//用户需要自定义服务器路由存储
		ShangtanRouteStore routeStore = new ShangtanRouteStore();
		JoyManager.setRouteStore(routeStore);

		try{
			JoyManager.init();
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}
