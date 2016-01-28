

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

	void update(){
		int m = millis();
		deltaTime = (m-lastFrameMillis)*invDefinedFrameRate;
		deltaMillis = m-lastFrameMillis;
		lastFrameMillis = m;
	  	fCount++;
	    if (m - frameStampLast > frameStampTime) {
		    frate = fCount / (frameStampTime*0.001);
			fCount = 0;
		    frameStampLast = m;
		    // This is optional, but nice.
		}
	}

	float getFrameRate(){
		return frate;
	}

	// This is the main point of the Time class.
	// Used in time-based calculations (physics, mostly), to
	// ensure a consistent experience across fluxing frameRates 
	float deltaTime(){
		// This conditional is optional
		return useTimeCompensation? min(deltaTime, spikeCull) : 1;
	}

	float deltaMillis(){
		return deltaMillis;
	}

	// Converts a frame count to an estimated millis count
	float framesToMillis(float fValue){
		return fValue*frate/1000;

	}




}