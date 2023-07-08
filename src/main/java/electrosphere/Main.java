package electrosphere;

import java.util.concurrent.TimeUnit;

import electrosphere.render.GLFWContext;
import electrosphere.render.Mesh;

public class Main {
    
    public static void main(String args[]){
        try {
        GLFWContext.init();
        FluidSim sim = new FluidSim();
        Mesh.meshInitially(sim);
        int i = 0;


        while(true){
            
            Mesh.remesh(sim);
            GLFWContext.redraw();


            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            System.out.println(i);
        }


    } catch (Throwable t){
        t.printStackTrace();
    }
    }

}
