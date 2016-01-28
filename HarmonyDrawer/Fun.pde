
PVector sparkGravity = new PVector(0, 0.5);

class Spark extends Physical{

	String name;

	public Spark(PVector startPosition){
		name = getRandomPersonName();
		println("Setting spark position: "+startPosition.x+" "+startPosition.y);
		this.position.x = startPosition.x;
		this.position.y = startPosition.y;
		mass = random(1, 3);
		gravity = sparkGravity;
		addForce(Utils.randomSignedVector().mult(5));
	}

	protected void update(){
		super.update();
		life-=0.05/mass;
	}

}