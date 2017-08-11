package cn.yph.library;

/**
 * @Author penghao
 * @Date 2017/8/9
 */

public class PointData {

    private int time;
    private int value;

    public PointData(int time, int value) {
        this.time = time;
        this.value = value;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
