import codeanticode.tablet.*;
// hello 3

/* 
 Plan.
 
 Layers --> pointclouds
 Dynamic changing of type? Steady on.
 So, redraw when necessary.
 
 Could go screenspace hitboxing. Not necessary for the moment, but would help performance a lot if we start to chug.

 Hello small update
 */


ClusterLayer cClusterLayer;
PGraphics sk; // our sketch PGraphics
PImage reference;

Tablet tablet;

void setup() {
    size(600, 600, P2D);
    sk = createGraphics(width, height, P2D);
    sk.beginDraw();
    sk.strokeCap(ROUND);
    sk.strokeJoin(ROUND);
    sk.background(0, 0, 0, 0);
    sk.endDraw();
    reference = loadImage("./data/ref.jpg");
    tablet = new Tablet(this); 
    cClusterLayer = new ClusterLayer();
    // This is lovely, but a massive resource hog.
    pixelDensity(displayDensity());
    background(255);
}


void draw() {
    background(255);
    image(reference, 0, 0);
    image(sk, 0, 0, width, height);
}

boolean mouseDown = false;

float getPenPressure(){
    return tablet.getPressure()+0.1*5;
    //return 2;
}

void mousePressed() {
    mouseDown = true;
    cClusterLayer.createCluster(mouseX, mouseY, getPenPressure());
}

void mouseDragged() {
    println("Pressure: "+getPenPressure());
    if(!mouseDown){
        println("mouseDragged fired before mousePressed");
        return;
    }
    cClusterLayer.addPoint(mouseX, mouseY, getPenPressure());
    // background(255);
    cClusterLayer.drawRecent();
}

void mouseReleased() {
    mouseDown = false;
}

void keyPressed(){
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