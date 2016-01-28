
class Physics{

	String name;
	ArrayList physicals = new ArrayList<Physical>();

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

	protected PVector position = new PVector();
	protected PVector velocity = new PVector();
	protected PVector gravity =new PVector(0, 0.15);
	protected float mass = 1;
	protected float invMass = 1;
	protected float life = 1;
	Physics parentSystem;

	private boolean kinetic = true;

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

	protected void setKinetic(boolean v){
		kinetic  = v;
	}

	protected void update(){
		if(kinetic){
			velocity.add(gravity);
			position.add(velocity);
		}
		if(life<=0){
			kill();
		}
	}

	protected void kill(){
		parentSystem.removePhysical(this);
	}

	protected void display(PGraphics c){
		c.strokeWeight(mass);
		c.stroke(0, 255*life*life);
		c.point(position.x, position.y);
	}

}

class CachingPhysical{

	PVector[] positionSamples;

	public CachingPhysical(int samplingDepth){
		positionSamples = new PVector[samplingDepth];
		Utils_AS.initialiseArraySample(positionSamples);
	}

}