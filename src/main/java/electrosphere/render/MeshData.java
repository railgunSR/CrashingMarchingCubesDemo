package electrosphere.render;

import java.util.List;

public class MeshData {

    List<Float> vertices;
    List<Float> normals;
    List<Integer> elements;

    MeshData(List<Float> vertices, List<Float> normals, List<Integer> elements){
        this.vertices = vertices;
        this.normals = normals;
        this.elements = elements;
    }
    
}
