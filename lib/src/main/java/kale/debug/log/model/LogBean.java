package kale.debug.log.model;

import java.io.Serializable;

import kale.debug.log.util.Level;


/**
 * @author Kale
 * @date 2016/3/25
 */
public class LogBean implements Serializable{

    public String tag, msg, time;

    public Level lev;

    /**
     * If tag, time, lev are equal, then we assume they are equal.
     *
     * This is because we don't want to see the messages that are
     * separated by a line break to be shown as two or more different logs.
     *
     * On the contrary, the Android System's logcat treat every log
     * in a different line as a different log.
     *
     * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogBean logBean = (LogBean) o;

        return tag.equals(logBean.tag) && time.equals(logBean.time) && lev.equals(logBean.lev);
    }

    @Override
    public int hashCode() {
        int result = tag.hashCode();
        result = 31 * result + msg.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + lev.hashCode();
        return result;
    }
}
