package electrosphere.render;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL44;
import org.lwjgl.system.MemoryUtil;

public class GLFWContext {
    
    static long window;

    public static void init(){
        //Initializes opengl
        GLFW.glfwInit();
        //Gives hints to glfw to control how opengl will be used
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        //Creates the window reference object
        window = GLFW.glfwCreateWindow(1920, 1080, "ORPG", GLFW.glfwGetPrimaryMonitor(), MemoryUtil.NULL);
        // Errors for failure to create window (IE: No GUI mode on linux ?)
        if (window == MemoryUtil.NULL) {
            GLFW.glfwTerminate();
        }
        //Makes the window that was just created the current OS-level window context
        GLFW.glfwMakeContextCurrent(window);
        //Maximize it
        GLFW.glfwMaximizeWindow(window);
        //grab actual framebuffer 
        IntBuffer xBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer yBuffer = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, xBuffer, yBuffer);
        MemoryUtil.memFree(xBuffer);
        MemoryUtil.memFree(yBuffer);
        
        int bufferWidth = xBuffer.get();
        int bufferHeight = yBuffer.get();

        //Creates the OpenGL capabilities for the program.
        GL.createCapabilities();

        //This enables Z-buffering so that farther-back polygons are not drawn over nearer ones
        GL44.glEnable(GL44.GL_CULL_FACE);
        GL44.glDepthFunc(GL44.GL_LEQUAL);
        GL44.glEnable(GL44.GL_DEPTH_TEST);
        
        // Support for transparency
        GL44.glEnable(GL44.GL_BLEND);
        GL44.glBlendFunc(GL44.GL_SRC_ALPHA, GL44.GL_ONE_MINUS_SRC_ALPHA);

        //this disables vsync to make game run faster
        //https://stackoverflow.com/questions/55598376/glfwswapbuffers-is-slow
        GLFW.glfwSwapInterval(0);
    }

    public static void redraw(){
        //clear screen
        GL44.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); 
        GL44.glClear(GL44.GL_COLOR_BUFFER_BIT | GL44.
        GL_DEPTH_BUFFER_BIT);
        //draw here
        Mesh.draw();
        //ending stuff
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

}
