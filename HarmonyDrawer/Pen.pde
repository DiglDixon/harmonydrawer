

class Pen{

	String name;
	PVector[] positionSamples = new PVector[5];
	int positionSampleIndex = 0;
	PVector velocity = new PVector();
	PVector position = new PVector();
	float pressure;

	public Pen(String name){
		this.name = name;
		Utils_AS.initialiseArraySample(positionSamples);
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

	void mouseDragged(){
	    pressure = getPenPressure();

	    if(pressure>0.6){
	    	int maxSparks = (int) map(pressure, 0.6, 1, 0, 5);
	    	int sparkCount = (int) random(0, maxSparks);
	    	for(int k = 0; k<sparkCount; k++){
	    		physics.addPhysical(new Spark(position));
	    	}

	    }
	}

	void mouseReleased(){
		pressure = 0;
	}

}