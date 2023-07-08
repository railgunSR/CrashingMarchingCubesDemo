package electrosphere;

public class FluidSim {

    public static final int DIM = 18;

    public static final int IX(int x, int y, int z){
        return ((x)+(DIM)*(y) + (DIM)*(DIM)*(z));
    }

    static float[] data = new float[DIM * DIM * DIM];

    static {
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 4; y++){
                for(int z = 0; z < 4; z++){
                    data[IX(x+8,y+8,z+8)] = 1;
                }
            }
        }
    }

    public float[] getData(){
        return data;
    }
    
}
