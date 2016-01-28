
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
	PVector gravity =new PVector(0, -0.1);
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