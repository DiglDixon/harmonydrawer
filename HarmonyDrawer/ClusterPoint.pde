

class ClusterPoint {

    // We'll suck up floats for now, but they may be squashed into integers
    public int x, y;
    public float w= 1;

    public ClusterPoint(float x, float y) {
        this.x = round(x);
        this.y = round(y);
    }
    
    public void setWeight(float w){
        this.w = w;
    }
}