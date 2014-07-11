package cn.joy.framework.event;

import java.util.EventListener;
/**
 * 事件监听器接口
 * @author liyy
 * @date 2014-07-06
 */
public interface JoyEventListener<E extends JoyEvent> extends EventListener {

    /**
     * 响应事件
     */
    void onJoyEvent(E ropEvent);

    /**
     * 执行的顺序号
     */
    int getOrder();
}