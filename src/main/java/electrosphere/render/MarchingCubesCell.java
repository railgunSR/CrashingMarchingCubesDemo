package electrosphere.render;

import org.joml.Vector3f;

public class MarchingCubesCell {

    Vector3f[] points = new Vector3f[8]; //array of size 8
    double[] val = new double[8]; //array of size 8
    public void setValues(
        Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
        Vector3f p5, Vector3f p6, Vector3f p7, Vector3f p8,
        double val1, double val2, double val3, double val4,
        double val5, double val6, double val7, double val8
    ){
        points[0] = p1; points[1] = p2; points[2] = p3; points[3] = p4;
        points[4] = p5; points[5] = p6; points[6] = p7; points[7] = p8;
        val[0] = val1; val[1] = val2; val[2] = val3; val[3] = val4;
        val[4] = val5; val[5] = val6; val[6] = val7; val[7] = val8;
    }
    
}
