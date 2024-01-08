package yg.Master;

public class SlaveBLoad {
    private int slaveBLoad;

    public SlaveBLoad(int slaveBLoad) {
        this.slaveBLoad = slaveBLoad;
    }

    public int getSlaveBLoad() {
        return slaveBLoad;
    }

    public void setSlaveBLoad(int slaveBLoad) {
        this.slaveBLoad = slaveBLoad;
    }

    public void add(int add) {
        this.slaveBLoad = slaveBLoad + add;
    }
}
