
static class Utils{

	public static PApplet processing;
	// __________________ MATHS __________________ //

	// Used to avoid square rooting with distance checks.
	static boolean checkDistSq(PVector pos1, PVector pos2, float thres){
		return ( (pos2.x-pos1.x)*(pos2.x-pos1.x) + (pos2.y-pos1.y)*(pos2.y-pos1.y) + (pos2.z-pos1.z)*(pos2.z-pos1.z) < thres*thres);
	}
	static boolean checkDistSq(float x1, float y1, float x2, float y2, float thres){
		return(	(x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) < thres*thres );
	}

	// These weeds out negative modulos
	static int safeMod(int num, int mod){
		while(num<0){
			num+=mod;
		}
		return num%mod;
	}
	static float safeMod(float num, int mod){
		while(num<0){
			num+=mod;
		}
		return num%mod;
	}

	static int randomIndex(int upper){
		return randomIndex(0, upper);
	}

	static int randomIndex(int lower, int upper){
		return processing.round( processing.random(lower, upper)-0.5 );
	}

	// __________________ VECTORS __________________ //
	// A convenience function for drawing vector-to-vector.
	// Which happens a lot.
	static void vectorLine(PVector v1, PVector v2, PGraphics c){
		c.line(v1.x, v1.y, v2.x, v2.y);
	}

	static PVector randomVector(){
		return new PVector(processing.random(1), processing.random(1), processing.random(1));
	}

	static PVector randomSignedVector(){
		return new PVector(processing.random(-1, 1), processing.random(-1, 1), processing.random(-1, 1));
	}


} // END UTILS