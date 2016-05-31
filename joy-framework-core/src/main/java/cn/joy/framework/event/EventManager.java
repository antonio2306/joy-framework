package cn.joy.framework.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
/**
 * 事件管理器，负责事件监听器的注册及事件的发布
 * @author liyy
 * @date 2014-07-06
 */
public class EventManager {
	private static Log logger = LogKit.get();

	private static Executor executor;

    private static Set<JoyEventListener> eventListeners = new HashSet<JoyEventListener>();

    private static final Map<Class<? extends JoyEvent>, ListenerRegistry> cachedEventListeners =
            new HashMap<Class<? extends JoyEvent>, ListenerRegistry>();

    public static void addListener(JoyEventListener listener) {
    	eventListeners.add(listener);
    }

    public static void removeListener(JoyEventListener listener) {
    	eventListeners.remove(listener);
    }
    
    public static void removeAllListeners() {
    	eventListeners.clear();
    }

    private static List<JoyEventListener> getJoyEventListeners(JoyEvent event) {
        Class<? extends JoyEvent> eventType = event.getClass();
        if (!cachedEventListeners.containsKey(eventType)) {
            LinkedList<JoyEventListener> allListeners = new LinkedList<JoyEventListener>();
            if (eventListeners != null && eventListeners.size() > 0) {
                for (JoyEventListener joyEventListener : eventListeners) {
                	Type[] ts = joyEventListener.getClass().getGenericInterfaces();
                	for(Type t:ts){
                		if (t instanceof ParameterizedType) {
                            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
                            if (p[0].equals(eventType)) {
                                allListeners.add(joyEventListener);
                            }
                        }
                	}
                }
                sortJoyEventListener(allListeners);
            }
            ListenerRegistry listenerRegistry = new ListenerRegistry(allListeners);
            cachedEventListeners.put(eventType, listenerRegistry);
        }
        List listeners = cachedEventListeners.get(eventType).getJoyEventListeners();
    	logger.debug("publishEvent, event type={}, listener size={}", event.getClass().getSimpleName(), listeners.size());
        return listeners;
    }

    private static void sortJoyEventListener(List<JoyEventListener> JoyEventListeners) {
        Collections.sort(JoyEventListeners, new Comparator<JoyEventListener>() {
            public int compare(JoyEventListener o1, JoyEventListener o2) {
                if (o1.getOrder() > o2.getOrder()) {
                    return 1;
                } else if (o1.getOrder() < o2.getOrder()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    private static class ListenerRegistry {

        public List<JoyEventListener> eventListenerList;

        private ListenerRegistry(List<JoyEventListener> eventListenerList) {
            this.eventListenerList = eventListenerList;
        }

        public List<JoyEventListener> getJoyEventListeners() {
            return eventListenerList;
        }
    }
    
    public static void publishEvent(final JoyEvent event) {
    	if(event==null)
    		return;
    	logger.debug("publishEvent, event type={}", event.getClass().getSimpleName());
		try {
			for (final JoyEventListener listener : getJoyEventListeners(event)) {
				Executor executor = getExecutor();
				if (executor != null) {
					executor.execute(new Runnable() {
						public void run() {
							listener.onJoyEvent(event);
						}
					});
				} else {
					listener.onJoyEvent(event);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
}
