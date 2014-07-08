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

import org.apache.log4j.Logger;

public class EventManager {
	private static Logger logger = Logger.getLogger(EventManager.class);

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
                	Type t = joyEventListener.getClass().getGenericSuperclass();
                    if (t instanceof ParameterizedType) {
                        Type[] p = ((ParameterizedType) t).getActualTypeArguments();
                        System.out.print(p[0]);
                        if (p[0].equals(eventType)) {
                            allListeners.add(joyEventListener);
                        }
                    }
                }
                sortJoyEventListener(allListeners);
            }
            ListenerRegistry listenerRegistry = new ListenerRegistry(allListeners);
            cachedEventListeners.put(eventType, listenerRegistry);
        }
        return cachedEventListeners.get(eventType).getJoyEventListeners();
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
			logger.error("", e);
		}
	}

	public static Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
}
