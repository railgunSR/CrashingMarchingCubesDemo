package electrosphere.render;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import electrosphere.FluidSim;

public class Mesh {
    
    public static int vaoPtr;
    static int vertBufferPtr;
    static int normBufferPtr;
    static int faceBufferPtr;

    static int shaderProgram;

    public static int elementCount = 0;

    static Matrix4f viewMatrix = new Matrix4f();
    static Matrix4f projectionMatrix = new Matrix4f();
    static Matrix4f model = new Matrix4f().identity();

    static MemoryStack stack = MemoryStack.create(32 * 1000 * 1000);

    public static void meshInitially(FluidSim sim){
        //create and bind vao
        vaoPtr = GL44.glGenVertexArrays();
        GL44.glBindVertexArray(vaoPtr);
        
        //generate verts
        MeshData data = MarchingCubes.generateFluidChunkData(sim.getData());

        //
        //Buffer data to GPU
        //
        vertBufferPtr = GL44.glGenBuffers();
        GL44.glBindBuffer(GL44.GL_ARRAY_BUFFER, vertBufferPtr);
        try {
            int vertexCount = data.vertices.size();
            FloatBuffer vertData = BufferUtils.createFloatBuffer(vertexCount);
            for(float vertValue : data.vertices){
                vertData.put(vertValue);
            }
            vertData.flip();
            GL44.glBufferData(GL44.GL_ARRAY_BUFFER,vertData,GL44.GL_STATIC_DRAW);
            GL44.glVertexAttribPointer(0, 3, GL44.GL_FLOAT, false, 0, 0);
            GL44.glEnableVertexAttribArray(0);
            // MemoryUtil.memFree(vertData);
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }
        

        
        //
        //  FACES
        //
        faceBufferPtr = GL44.glGenBuffers();
        GL44.glBindBuffer(GL44.GL_ELEMENT_ARRAY_BUFFER, faceBufferPtr);
        elementCount = data.elements.size();
        try {
            IntBuffer elementArrayBufferData = BufferUtils.createIntBuffer(elementCount);
            for(int element : data.elements){
                elementArrayBufferData.put(element);
            }
            elementArrayBufferData.flip();
            GL44.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER,elementArrayBufferData,GL44.GL_STATIC_DRAW);
            // MemoryUtil.memFree(elementArrayBufferData);
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }
        
        
        
        
        //
        //  NORMALS
        //
        normBufferPtr = GL44.glGenBuffers();
        GL44.glBindBuffer(GL44.GL_ARRAY_BUFFER, normBufferPtr);
        try {
            int normalCount = data.normals.size() / 3;
            FloatBuffer NormalArrayBufferData;
            if(normalCount > 0){
                NormalArrayBufferData = BufferUtils.createFloatBuffer(normalCount * 3);
                float[] temp = new float[3];
                for(float normalValue : data.normals){
                    NormalArrayBufferData.put(normalValue);
                }
                NormalArrayBufferData.flip();
                GL44.glBufferData(GL44.GL_ARRAY_BUFFER,NormalArrayBufferData,GL44.GL_STATIC_DRAW);
                GL44.glVertexAttribPointer(1, 3, GL44.GL_FLOAT, false, 0, 0);
                GL44.glEnableVertexAttribArray(1);
                MemoryUtil.memFree(NormalArrayBufferData);
            }
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }

        String vsSrc = "";
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (BufferedReader is = new BufferedReader(new InputStreamReader(classloader.getResourceAsStream("shader.vs")))){
            String temp;
            while((temp = is.readLine())!=null){
                vsSrc = vsSrc + temp + "\n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String fsSrc = "";
        try (BufferedReader is = new BufferedReader(new InputStreamReader(classloader.getResourceAsStream("shader.fs")))){
            String temp;
            while((temp = is.readLine())!=null){
                fsSrc = fsSrc + temp + "\n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //
        //Create shader
        //
        int vertexShader = GL45.glCreateShader(GL45.GL_VERTEX_SHADER);
        GL45.glShaderSource(vertexShader, vsSrc);
        //Compiles the source for the vertex shader object
        GL45.glCompileShader(vertexShader);
        //The following tests if the vertex shader compiles successfully
        int success;
        success = GL45.glGetShaderi(vertexShader, GL45.GL_COMPILE_STATUS);
        if (success != GL45.GL_TRUE) {
            System.err.println("Vertex Shader failed to compile!");
            System.err.println("Source is: ");
            System.err.println(GL45.glGetShaderSource(vertexShader));
            System.err.println(GL45.glGetShaderInfoLog(vertexShader));
        }
        //Creates and opengl object for a fragment shader and assigns its 'pointer' to the integer fragmentShader
        int fragmentShader = GL45.glCreateShader(GL45.GL_FRAGMENT_SHADER);
        //This points the opengl shadder object to its proper source
        GL45.glShaderSource(fragmentShader, fsSrc);
        //This compiles the shader object
        GL45.glCompileShader(fragmentShader);
        //This tests for the success of the compile attempt
        success = GL45.glGetShaderi(fragmentShader, GL45.GL_COMPILE_STATUS);
        if (success != GL45.GL_TRUE) {
            System.err.println("Fragment Shader failed to compile!");
            System.err.println("Source is: ");
            System.err.println(GL45.glGetShaderSource(fragmentShader));
            System.err.println(GL45.glGetShaderInfoLog(fragmentShader));
        }
        //This creates a shader program opengl object and assigns its 'pointer' to the integer shaderProgram
        shaderProgram = GL45.glCreateProgram();
        //This attaches the vertex and fragment shaders to the program
        GL45.glAttachShader(shaderProgram, vertexShader);
        GL45.glAttachShader(shaderProgram, fragmentShader);
        //This links the program to the GPU (I think its to the GPU anyway)
        GL45.glLinkProgram(shaderProgram);
        //Tests for the success of the shader program creation
        success = GL45.glGetProgrami(shaderProgram, GL45.GL_LINK_STATUS);
        if (success != GL45.GL_TRUE) {
            throw new RuntimeException(GL45.glGetProgramInfoLog(shaderProgram));
        }        
        
        //Deletes the individual shader objects to free up memory
        GL45.glDeleteShader(vertexShader);
        GL45.glDeleteShader(fragmentShader);

        //bind shader
        GL45.glUseProgram(shaderProgram);

        viewMatrix = new Matrix4f();
        viewMatrix.setLookAt(new Vector3f(-8,20,0), new Vector3f(8,8,8), new Vector3f(0,1,0));
        projectionMatrix = new Matrix4f();
        projectionMatrix.setPerspective(90,1920.0f/1080.0f,0.0001f,1000f);
        model = new Matrix4f().identity();

        GL45.glUniformMatrix4fv(GL45.glGetUniformLocation(shaderProgram, "view"), false, viewMatrix.get(new float[16]));
        GL45.glUniformMatrix4fv(GL45.glGetUniformLocation(shaderProgram, "projection"), false, projectionMatrix.get(new float[16]));
        GL45.glUniformMatrix4fv(GL45.glGetUniformLocation(shaderProgram, "model"), false, model.get(new float[16]));
    }

    public static void remesh(FluidSim sim){
        //generate verts
        MeshData data = MarchingCubes.generateFluidChunkData(sim.getData());

        //
        //Buffer data to GPU
        //
        GL44.glBindBuffer(GL44.GL_ARRAY_BUFFER, vertBufferPtr);
        try {
            int vertexCount = data.vertices.size();
            FloatBuffer vertData = MemoryUtil.memAllocFloat(vertexCount);
            for(float vertValue : data.vertices){
                vertData.put(vertValue);
            }
            vertData.flip();
            GL44.glBufferData(GL44.GL_ARRAY_BUFFER,vertData,GL44.GL_STATIC_DRAW);
            GL44.glVertexAttribPointer(0, 3, GL44.GL_FLOAT, false, 0, 0);
            GL44.glEnableVertexAttribArray(0);
            MemoryUtil.memFree(vertData);
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }
            

        //
        //  FACES
        //
        GL44.glBindBuffer(GL44.GL_ELEMENT_ARRAY_BUFFER, faceBufferPtr);
        elementCount = data.elements.size();
        try {
            IntBuffer elementArrayBufferData = MemoryUtil.memAllocInt(elementCount);
            for(int element : data.elements){
                elementArrayBufferData.put(element);
            }
            elementArrayBufferData.flip();
            GL44.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER,elementArrayBufferData,GL44.GL_STATIC_DRAW);
            MemoryUtil.memFree(elementArrayBufferData);
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }
            
            
            
        //
        //  NORMALS
        //
        GL44.glBindBuffer(GL44.GL_ARRAY_BUFFER, normBufferPtr);
        try {
            int normalCount = data.normals.size() / 3;
            FloatBuffer NormalArrayBufferData;
            if(normalCount > 0){
                NormalArrayBufferData = MemoryUtil.memAllocFloat(normalCount * 3);
                for(float normalValue : data.normals){
                    NormalArrayBufferData.put(normalValue);
                }
                NormalArrayBufferData.flip();
                GL44.glBufferData(GL44.GL_ARRAY_BUFFER,NormalArrayBufferData,GL44.GL_STATIC_DRAW);
                GL44.glVertexAttribPointer(1, 3, GL44.GL_FLOAT, false, 0, 0);
                GL44.glEnableVertexAttribArray(1);
                MemoryUtil.memFree(NormalArrayBufferData);
            }
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    static float angle = 0;

    public static void draw(){
        GL45.glUseProgram(shaderProgram);
        GL45.glBindFramebuffer(GL45.GL_FRAMEBUFFER, 0);
        GL44.glBindVertexArray(vaoPtr);
        GL45.glUniformMatrix4fv(GL45.glGetUniformLocation(shaderProgram, "view"), false, viewMatrix.get(new float[16]));
        GL45.glUniformMatrix4fv(GL45.glGetUniformLocation(shaderProgram, "projection"), false, projectionMatrix.get(new float[16]));
        GL45.glUniformMatrix4fv(GL45.glGetUniformLocation(shaderProgram, "model"), false, model.get(new float[16]));
        GL44.glDrawElements(GL44.GL_TRIANGLES, Mesh.elementCount, GL44.GL_UNSIGNED_INT, 0);
    }
    
}
