

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