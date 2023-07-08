package electrosphere.render;

public class Triangle {
    
    int[] indices = new int[3]; //array of size 3

    public Triangle(int index0, int index1, int index2){
        indices[0] = index0;
        indices[1] = index1;
        indices[2] = index2;
    }

}
