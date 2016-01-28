import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import codeanticode.tablet.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class HarmonyDrawer extends PApplet {


// hello 4

/* 
 Plan.
 
 Layers --> pointclouds
 Dynamic changing of type? Steady on.
 So, redraw when necessary.
 
 Could go screenspace hitboxing. Not necessary for the moment, but would help performance a lot if we start to chug.

 Hello small update
 */


ClusterLayer cClusterLayer;
PGraphics sk, ui, fx;
PImage reference;

Tablet tablet;
Pen cPen;
Time time;
Physics physics;

public void setup() {
    
    noCursor();
    Utils.processing = this;
    Utils_AS.processing = this;
    // Utlis_AS.processing = this;
    ui = createGraphics(width, height, P2D);
    sk = createGraphics(width, height, P2D);
    fx = createGraphics(width, height, P2D);
    sk.beginDraw();
    sk.strokeCap(ROUND);
    sk.strokeJoin(ROUND);
    sk.background(0, 0, 0, 0);
    sk.endDraw();
    reference = loadImage("./data/ref.jpg");
    tablet = new Tablet(this); 
    cClusterLayer = new ClusterLayer();
    // This is lovely, but a massive resource hog.
    
    background(255);
    time = new Time();
    cPen = new Pen("Steve the Pen");
    physics = new Physics("Phill the Phriendly Physics System");
}


public void draw() {
    background(255);
    refreshCanvases();
    cPen.update();
    image(reference, 0, 0);
    image(sk, 0, 0, width, height);
    ui.beginDraw();
    cPen.display(ui);
    ui.endDraw();
    image(ui, 0, 0, width, height);
}

public void refreshCanvases(){
    ui.beginDraw();
    ui.clear();
    ui.endDraw();
    fx.beginDraw();
    fx.clear();
    fx.endDraw();
}

boolean mouseDown = false;

public float getPenPressure(){
    return tablet.getPressure();
    //return 2;
}

public void mousePressed() {
    mouseDown = true;
    cClusterLayer.createCluster(mouseX, mouseY, getPenPressure());
}

public void mouseDragged() {
    float pressure = getPenPressure();
    if(!mouseDown){
        println("mouseDragged fired before mousePressed");
        return;
    }
    cClusterLayer.addPoint(mouseX, mouseY, pressure);
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
                    sk.strokeWeight(iPoint.w);
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
    public float w = 1;

    public ClusterPoint(float x, float y) {
        this.x = round(x);
        this.y = round(y);
    }
    
    public void setWeight(float w){
        this.w = w;
    }
}


class Spark extends Physical{

	String name;

	public Spark(PVector position, PVector heading){
		name = getRandomPersonName();
	}

}
public String getRandomPersonName(){
	return peopleNames[Utils.randomIndex(peopleNames.length)];
}

String[] peopleNames = {
"Isaiah",
"Jamee",
"Aracelis",
"Jodee",
"Trudy",
"Keva",
"Brenna",
"Ginette",
"Bettye",
"Rubye",
"Myesha",
"Onita",
"Ashly",
"Reagan",
"Sid",
"Delorse",
"Peter",
"Colby",
"Corey",
"Ila",
"Nanette",
"Delphia",
"Emelina",
"Sunny",
"Hank",
"Brooks",
"Adrien",
"Timmy",
"Apryl",
"Beatrice",
"Elvira",
"Robbyn",
"Tiffani",
"Fidel",
"Tawanda",
"Xiomara",
"Crystal",
"Marhta",
"Sina",
"Jannie",
"Codi",
"Shirlene",
"Justina",
"Annabelle",
"Karolyn",
"Brandi",
"Loida",
"Gino",
"Andrea",
"Bradley",
"Halina",
"Jeanna",
"Tonia",
"Elenora",
"Heidi",
"Leila",
"Janey",
"Marcela",
"Rafaela",
"Shauna",
"Dortha",
"Eric",
"Barbra",
"Mattie",
"Cindy",
"Lajuana",
"Maragret",
"Beulah",
"Bonnie",
"Jacquline",
"Harold",
"Chana",
"Karla",
"Therese",
"Tomiko",
"Parthenia",
"Earline",
"Toccara",
"Emil",
"Particia",
"Stefania",
"Tobias",
"Davida",
"Marleen",
"Barney",
"Adena",
"Fumiko",
"Terrence",
"Randolph",
"Thu",
"Niesha",
"Dwight",
"Karen",
"Ronna",
"Mellisa",
"Raul",
"Stacey",
"Marguerite",
"Tawny",
"Jetta",
"Sacha",
"Bruno",
"Arlean",
"Joel",
"Lucile",
"Cherelle",
"Anabel",
"Karisa",
"Jewel",
"Rosalia",
"Palmer",
"Celena",
"Von",
"Mikel",
"Markita",
"Jan",
"Nelle",
"Gena",
"Dolores",
"Devon",
"Kylee",
"Tiffiny",
"Shonna",
"Rosette",
"Sanford",
"Millie",
"Fawn",
"Cesar",
"Jessia",
"Ellen",
"Synthia",
"Lucius",
"Nolan",
"Dona",
"Jasper",
"Cordia",
"Lauran",
"Orval",
"Garth",
"Susana",
"Dia",
"Irwin",
"Glynda",
"Ilana",
"Annett",
"Sharlene",
"Rickie",
"Magdalena",
"Verda",
"Virgen"
};


class Pen{

	String name;
	PVector[] positionSamples = new PVector[5];
	int positionSampleIndex = 0;
	PVector velocity = new PVector();
	PVector position = new PVector();
	float pressure;

	public Pen(String name){
		this.name = name;
	}

	public PVector getPosition(){
		return position;
	}

	public void display(PGraphics c){
		displayDefault(c);
	}

	private void displayDefault(PGraphics c){
	    c.strokeWeight(2);
	    c.stroke(190, 20, 20);
	    c.point(mouseX, mouseY);
	}

	public void update(){
		position.x = mouseX;
		position.y = mouseY;
		addPositionSample(position);
	}

	private void addPositionSample(PVector v){
		positionSamples[positionSampleIndex] = v;
		positionSampleIndex = (positionSampleIndex+1) % positionSamples.length;
	}

	public void mouseDragged(){
	    pressure = getPenPressure();

	    if(pressure>0.98f){

	    	int sparkCount = (int) random(0, 4);
	    	for(int k = 0; k<sparkCount; k++){
	    		// physics.add()
	    	}

	    }
	}

	public void mouseReleased(){
		pressure = 0;
	}

}

class Physics{

	String name;
	ArrayList physicals;

	public Physics(String name){
		this.name = name;
	}

	public void addPhysical(Physical p){
		physicals.add(p);
		p.setParentSystem(this);
	}

	public void removePhysical(Physical p){
		physicals.remove(p);
	}

	public void update(){
		Physical p;
		for(int k = 0; k<physicals.size();k++){
			p = (Physical) physicals.get(k);
			p.update();
		}
	}

	public void display(PGraphics c){
		Physical p;
		for(int k = 0; k<physicals.size();k++){
			p = (Physical) physicals.get(k);
			p.display(c);
		}
	}

}

class Physical{

	PVector position = new PVector();
	PVector velocity = new PVector();
	PVector gravity =new PVector(0, -0.1f);
	protected float mass = 1;
	protected float invMass = 1;
	protected float life = 1;
	Physics parentSystem;

	public Physical(){}

	public void setParentSystem(Physics s){
		parentSystem = s;
	}

	protected void setMass(float m){
		mass = m;
		invMass = 1/m;
	}

	protected void addForce(PVector f){
		f.mult(invMass);
		velocity.add(f);
	}

	protected void update(){
		velocity.add(gravity);
		position.add(velocity);
	}

	protected void kill(){
		parentSystem.removePhysical(this);
	}

	protected void display(PGraphics c){
		c.strokeWeight(mass*2);
		c.stroke(0);
		c.point(position.x, position.y);
	}

}

class CachingPhysical{

	PVector[] positionCache;

	public CachingPhysical(int cacheDepth){
		positionCache = new PVector[cacheDepth];
	}


}


// This class is used to maintain speeds at varying framerates.
class Time{

	// This is used in time.deltaTime
	int lastFrameMillis = 0;
	float deltaTime;
	float deltaMillis;
	float spikeCull = 3;
	// This is the frameRate we've set our time-based computations at
	float definedFrameRate = 1000/60;
	float invDefinedFrameRate = 1/definedFrameRate;

	// This is a way of getting frame rate based on millis() over a time
	// Often more useful that rather than proc's 1-frame frameRate call.
	int frameStampTime = 50;
	int frameStampLast = 0;
	float fCount = 0;
	float frameStampRate = 0;
	float frate = 0;
	boolean useTimeCompensation = true;

	Time(){
	}

	public void update(){
		int m = millis();
		deltaTime = (m-lastFrameMillis)*invDefinedFrameRate;
		deltaMillis = m-lastFrameMillis;
		lastFrameMillis = m;
	  	fCount++;
	    if (m - frameStampLast > frameStampTime) {
		    frate = fCount / (frameStampTime*0.001f);
			fCount = 0;
		    frameStampLast = m;
		    // This is optional, but nice.
		}
	}

	public float getFrameRate(){
		return frate;
	}

	// This is the main point of the Time class.
	// Used in time-based calculations (physics, mostly), to
	// ensure a consistent experience across fluxing frameRates 
	public float deltaTime(){
		// This conditional is optional
		return useTimeCompensation? min(deltaTime, spikeCull) : 1;
	}

	public float deltaMillis(){
		return deltaMillis;
	}

	// Converts a frame count to an estimated millis count
	public float framesToMillis(float fValue){
		return fValue*frate/1000;

	}




}

static class Utils{

	public static PApplet processing;
	// __________________ MATHS __________________ //

	// Used to avoid square rooting with distance checks.
	public static boolean checkDistSq(PVector pos1, PVector pos2, float thres){
		return ( (pos2.x-pos1.x)*(pos2.x-pos1.x) + (pos2.y-pos1.y)*(pos2.y-pos1.y) + (pos2.z-pos1.z)*(pos2.z-pos1.z) < thres*thres);
	}
	public static boolean checkDistSq(float x1, float y1, float x2, float y2, float thres){
		return(	(x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) < thres*thres );
	}

	// These weeds out negative modulos
	public static int safeMod(int num, int mod){
		while(num<0){
			num+=mod;
		}
		return num%mod;
	}
	public static float safeMod(float num, int mod){
		while(num<0){
			num+=mod;
		}
		return num%mod;
	}

	public static int randomIndex(int upper){
		return randomIndex(0, upper);
	}

	public static int randomIndex(int lower, int upper){
		return processing.round( processing.random(lower, upper)-0.5f );
	}

	// __________________ VECTORS __________________ //
	// A convenience function for drawing vector-to-vector.
	// Which happens a lot.
	public static void vectorLine(PVector v1, PVector v2, PGraphics c){
		c.line(v1.x, v1.y, v2.x, v2.y);
	}

	public static PVector randomVector(){
		return new PVector(processing.random(1), processing.random(1), processing.random(1));
	}

	public static PVector randomSignedVector(){
		return new PVector(processing.random(-1, 1), processing.random(-1, 1), processing.random(-1, 1));
	}


} // END UTILS

/* __________________________________ ARRAY_AVERAGING __________________________________ */

// ** This section has been combined into the SampleArray classes. **

// These assume values are being fed in by a modulo against the processing.frameCount
// Oh look! addArraySample() is a great way to do this ;)

static class Utils_AS{

    public static PApplet processing;

    public static void initialiseArraySample(PVector arr[]){
    	for(int k = 0; k<arr.length;k++){
    		arr[k] = new PVector();
    	}
    }

    public static void initialiseArraySample(PVector arr[], PVector startingValue){
    	for(int k = 0; k<arr.length;k++){
    		arr[k] = new PVector();
    		arr[k].set(startingValue);
    	}
    }

    public static void addArraySample(PVector[] arr, PVector value){
    	arr[processing.frameCount%arr.length].set(value);
    }

    public static PVector getArrayAverage(PVector[] arr){
    	return getArrayAverage(arr, 0);
    }

    public static PVector getArrayAverage(PVector[] arr, int offset){

        PVector accum = new PVector();
        // The offset position
        int o = Utils.safeMod((processing.frameCount+offset), arr.length);
        // The current addition frame
        int c = processing.frameCount%arr.length;
        int t = 0; // We'll use this to count what we need to average by

        for(int k = 0; k<arr.length; k++){

            if(arr[k]==null) // we ignore nulls
                continue;
            if(offset!=0){
                if(c>o){ // If standard order
                    if(k>o && k<c) // cull inside
                        continue;
                }else{ // Else if wrapping
                    if(k<c || k>o) // cull outside
                        continue;
                }
            }

            accum.add(arr[k]);
            t++;
        }
        return PVector.div(accum, t);
    }

    public static PVector getArrayAverageVelocity(PVector[] arr){
    	return getArrayAverage(arr, 0);
    }

    public static PVector getArrayAverageVelocity(PVector[] arr, int offset){
        PVector thisC = new PVector();
        PVector prev = new PVector();
        PVector accum = new PVector();
        // The offset frame
        int o = Utils.safeMod((processing.frameCount+offset), arr.length);
        // The current addition frame
        int c = processing.frameCount%arr.length;
        int p = Utils.safeMod(c-1, arr.length);
        int end = (processing.frameCount+1)%arr.length;
        int t = 0;

        for(int k = 0; k<arr.length; k++){
        	if(k==end)
        		continue; // Ignore the last value (it has no previous)
            if(arr[k]==null || arr[p]==null) // Ignore nulls
                continue;
            if(offset!=0){
                if(c>o){ // If standard order
                    if(k>o && k<c) // cull inside
                        continue;
                }else{ // Else if wrapping
                    if(k<c || k>o) // cull outside
                        continue;
                }
            }
            prev = arr[p];
            thisC = arr[k];
            accum.add(PVector.sub(thisC,prev));
            t++;
        }
        return PVector.div(accum, t);
    }


    public static float getArrayAverage(float[] arr, int offset){

        float accum = 0;
        // The offset position
        int o = Utils.safeMod((processing.frameCount+offset), arr.length);
        // The current addition frame
        int c = processing.frameCount%arr.length;
        int t = 0; // We'll use this to count what we need to average by

        for(int k = 0; k<arr.length; k++){

            if(offset!=0){
                if(c>o){ // If standard order
                    if(k>o && k<c) // cull inside
                        continue;
                }else{ // Else if wrapping
                    if(k<c || k>o) // cull outside
                        continue;
                }
            }

            accum+=arr[k];
            t++;
        }
        return accum/t;
    }
}


    public void settings() {  size(1200, 800, P2D);  pixelDensity(displayDensity()); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "HarmonyDrawer" };
        if (passedArgs != null) {
          PApplet.main(concat(appletArgs, passedArgs));
        } else {
          PApplet.main(appletArgs);
        }
    }
}
