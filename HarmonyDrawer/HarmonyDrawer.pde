import codeanticode.tablet.*;
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

void setup() {
    size(1200, 800, P2D);
    noCursor();
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
    pixelDensity(displayDensity());
    background(255);
    time = new Time();
    cPen = new Pen("Steve the Pen");
    physics = new Physics("Phill the Phriendly Physics System");
}


void draw() {
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

void refreshCanvases(){
    ui.clear();
    fx.clear();
}

boolean mouseDown = false;

float getPenPressure(){
    return tablet.getPressure();
    //return 2;
}

void mousePressed() {
    mouseDown = true;
    cClusterLayer.createCluster(mouseX, mouseY, getPenPressure());
}

void mouseDragged() {
    float pressure = getPenPressure();
    if(!mouseDown){
        println("mouseDragged fired before mousePressed");
        return;
    }
    cClusterLayer.addPoint(mouseX, mouseY, pressure);
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