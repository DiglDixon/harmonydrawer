
/* __________________________________ ARRAY_AVERAGING __________________________________ */

// ** This section has been combined into the SampleArray classes. **

// These assume values are being fed in by a modulo against the processing.frameCount
// Oh look! addArraySample() is a great way to do this ;)

static class Utils_AS{

    public static PApplet processing;

    static void initialiseArraySample(PVector arr[]){
    	for(int k = 0; k<arr.length;k++){
    		arr[k] = new PVector();
    	}
    }

    static void initialiseArraySample(PVector arr[], PVector startingValue){
    	for(int k = 0; k<arr.length;k++){
    		arr[k] = new PVector();
    		arr[k].set(startingValue);
    	}
    }

    static void addArraySample(PVector[] arr, PVector value){
    	arr[processing.frameCount%arr.length].set(value);
    }

    static PVector getArrayAverage(PVector[] arr){
    	return getArrayAverage(arr, 0);
    }

    static PVector getArrayAverage(PVector[] arr, int offset){

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

    static PVector getArrayAverageVelocity(PVector[] arr){
    	return getArrayAverage(arr, 0);
    }

    static PVector getArrayAverageVelocity(PVector[] arr, int offset){
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


    static float getArrayAverage(float[] arr, int offset){

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


