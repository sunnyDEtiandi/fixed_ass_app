package fixedass.barcode.com.fixed_ass_app.bean;

/**
 * Created by Administrator on 2018/3/21.
 */

public class CountState {
    private String state;
    private int count;

    public CountState(String state, int count) {
        this.state = state;
        this.count = count;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
