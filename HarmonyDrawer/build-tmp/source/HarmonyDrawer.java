import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class HarmonyDrawer extends PApplet {

//import codeanticode.tablet.*;


/* 
 Plan.
 
 Layers --> pointclouds
 Dynamic changing of type? Steady on.
 So, redraw when necessary.
 
 Could go screenspace hitboxing. Not necessary for the moment, but would help performance a lot if we start to chug.
 */


ClusterLayer cClusterLayer;
PGraphics sk; // our sketch PGraphics
PImage reference;

//Tablet tablet;

public void setup() {
    
    sk = createGraphics(width, height, P2D);
    sk.beginDraw();
    sk.strokeCap(ROUND);
    sk.strokeJoin(ROUND);
    sk.background(0, 0, 0, 0);
    sk.endDraw();
    reference = loadImage("./data/ref.jpg");
    //tablet = new Tablet(this); 
    cClusterLayer = new ClusterLayer();
    // This is lovely, but a massive resource hog.
    
    background(255);
}


public void draw() {
    background(255);
    image(reference, 0, 0);
    image(sk, 0, 0, width, height);
}

boolean mouseDown = false;

public float getPenPressure(){
    //return tablet.getPressure()+0.1*5;
    return 2;
}

public void mousePressed() {
    mouseDown = true;
    cClusterLayer.createCluster(mouseX, mouseY, getPenPressure());
}

public void mouseDragged() {
    if(!mouseDown){
        println("mouseDragged fired before mousePressed");
        return;
    }
    cClusterLayer.addPoint(mouseX, mouseY, getPenPressure());
    // background(255);
    cClusterLayer.drawRecent();
}

public void mouseReleased() {
    mouseDown = false;
}

public void keyPressed(){
    switch(key){
        case 'r':
            sk.beginDraw();
            sk.clear();
            cClusterLayer.redraw();
            sk.endDraw();
        break;
        default:
        println("Key pressed, not handled: "+key);
    }
}





//


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
                float cull = 0.75f;
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


class ClusterLayer{

	String name = "unnamed ClusterLayer";
	ArrayList clusters = new ArrayList<Cluster>();
	Cluster cCluster;
	float maxClusterDistance = 5;

	public ClusterLayer(){
	}

	public void createCluster(float x, float y, float w){
		cCluster = new Cluster(x, y, w, this);
		clusters.add(cCluster);
	}

	public void addPoint(float x, float y, float w){
		cCluster.addPoint(x, y, w);
	}

	public void drawRecent(){
		cCluster.drawRecent();
	}

	public void redraw(){
		Cluster c;
		for(int k = 0; k<clusters.size();k++){
			c = (Cluster) clusters.get(k);
			c.drawPoints();
		}
	}

	public ArrayList getAllClusters(){
		return this.clusters;
	}

}


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
    public void settings() {  size(600, 600, P2D);  pixelDensity(displayDensity()); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "HarmonyDrawer" };
        if (passedArgs != null) {
          PApplet.main(concat(appletArgs, passedArgs));
        } else {
          PApplet.main(appletArgs);
        }
    }
}
