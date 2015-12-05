

class Cluster {

    // This could get heavy, may have to switch to IntList or parallel int arrays
    ArrayList points = new ArrayList<ClusterPoint>();
    int recentLength = 4;
    ClusterLayer parentLayer;

    public Cluster(float startX, float startY, float startW, ClusterLayer l) {
        setParentLayer(l);
        this.addPoint(startX, startY, startW);
    }

    public void setParentLayer(ClusterLayer cl){
        parentLayer = cl;
    }

    public void addPoint(float x, float y, float w) {

        // ClusterPoint pCluster = cCluster.getPreviousPoint();
        // float pX = pCluster.x, pY = pCluster.y;
        // int d =  ceil(dist(x, y, pX, pY)/maxClusterDistance);
        // // println("d :: "+d+" dist values: "+x+" "+pX+" "+y+" "+pY+" :: "+dist(x, y, pX, pY));
        // float dX = x-pX, dY = y-pY;
        // println("d: "+d);
        // for(int k = 0; k<d; k++){
        //  cCluster.addPoint(pX+dX/d*k, pY+dY/d*k, w); 
        // }
        sk.beginDraw();
        ClusterPoint cp = new ClusterPoint(x, y);
        cp.setWeight(w);
        points.add(cp);
        // this.setRecentLength( min(1, points.size()-1) );
        ClusterPoint iPoint;
        float d, dx, dy, dx2, dy2;
        ArrayList cls = this.parentLayer.getAllClusters();
        Cluster c;
        for(int k = 0; k<cls.size();k++){
            c = (Cluster) cls.get(k);
            for(int j = 0; j<c.points.size();j++){
                iPoint = (ClusterPoint) c.points.get(j);
                dx = x-iPoint.x;
                dy = y-iPoint.y;
                dx2 = iPoint.x-x;
                dy2 =iPoint.y-y;
                d = dx*dx + dy*dy;
                float cull = 0.75;
                if(d<800){
                    sk.stroke(0, 5);
                    sk.strokeWeight(1);
                    sk.line(x+dx2*cull, y+dy2*cull, iPoint.x+dx*cull, iPoint.y+dy*cull);
                }
            }
        }
        sk.endDraw();
        
    }

    public void setRecentLength(int l){
        this.recentLength = l;
    }

    private int getRecentLength(){
        return min(this.recentLength, points.size()-1);
    }

    public void drawRecent(){
        sk.beginDraw();
        sk.noFill();
        int firstIndex = points.size()-this.getRecentLength()-1;
        int lastIndex = points.size()-1;
        // println(firstIndex+" :: "+lastIndex);
        ClusterPoint firstPoint = (ClusterPoint) points.get(firstIndex);
        ClusterPoint lastPoint = (ClusterPoint) points.get(lastIndex);
        ClusterPoint iPoint;
        sk.beginShape();
        sk.stroke(0, 10);
        sk.curveVertex(firstPoint.x, firstPoint.y);
        for (int k = firstIndex; k<=lastIndex; k++) {
            iPoint = (ClusterPoint) points.get(k);
            sk.strokeWeight(iPoint.w);
            sk.curveVertex(iPoint.x, iPoint.y);
        }
        sk.curveVertex(lastPoint.x, lastPoint.y);
        sk.endShape();
        sk.endDraw();
    }

    public void drawPoints() {
        sk.noFill();
        ClusterPoint firstPoint = (ClusterPoint) points.get(0);
        ClusterPoint lastPoint = (ClusterPoint) points.get(points.size()-1);
        ClusterPoint iPoint;
        sk.stroke(0, 3);
        sk.beginShape();
        sk.curveVertex(firstPoint.x, firstPoint.y);
        for (int k = 0; k<points.size(); k++) {
            iPoint = (ClusterPoint) points.get(k);
            sk.strokeWeight(iPoint.w);
            sk.curveVertex(iPoint.x, iPoint.y);
            // point(iPoint.x, iPoint.y);
        }
        sk.curveVertex(lastPoint.x, lastPoint.y);
        sk.endShape();
    }

    public ClusterPoint getPreviousPoint(){
        return (ClusterPoint) points.get(points.size()-1);
    }

}