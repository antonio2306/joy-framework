package cn.joy.framework.event;

import java.util.EventObject;

import cn.joy.framework.rule.RuleContext;
/**
 * 事件类
 * @author liyy
 * @date 2014-07-06
 */
public abstract class JoyEvent extends EventObject {

    private RuleContext rContext;

    public JoyEvent(Object source, RuleContext rContext) {
        super(source);
        this.rContext = rContext;
    }

    public RuleContext getRuleContext() {
        return rContext;
    }
}
