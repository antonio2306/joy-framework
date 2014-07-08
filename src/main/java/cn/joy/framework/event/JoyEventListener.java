package cn.joy.framework.event;

import java.util.EventListener;

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