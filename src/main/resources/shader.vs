//Vertex Shader
#version 330 core

//input buffers
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;


//coordinate space transformation matrices
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;



//output buffers
out vec3 Normal;
out vec3 FragPos;




void main() {
    //normalize posiiton and normal
    vec4 FinalVertex = vec4(aPos, 1.0);
    vec4 FinalNormal = vec4(aNormal, 1.0);


    //push frag, normal, and texture positions to fragment shader
    FragPos = vec3(model * FinalVertex);
    Normal = mat3(transpose(inverse(model))) * aNormal;

    
    //set final position with opengl space
    gl_Position = projection * view * model * FinalVertex;
}
