#version 330 core

out vec4 FragColor;


vec3 color = vec3(0.3,0.7,0.9);

vec3 dLDirection = vec3(0.1,-0.9,0.1);
vec3 dLDiffuse = vec3(0.5,0.5,0.5);

in vec3 FragPos;
in vec3 Normal;


uniform vec3 viewPos;
// uniform DirLight dirLight;
// uniform PointLight pointLights[NR_POINT_LIGHTS];
// uniform SpotLight spotLight;

// function prototypes
vec3 CalcDirLight(vec3 normal, vec3 viewDir);

void main(){
    vec3 norm = normalize(Normal);
    vec3 viewDir = normalize(viewPos - FragPos);
    
    vec3 lightAmount = CalcDirLight(norm, viewDir);

    vec3 color = vec3(0.3,0.7,0.9) * lightAmount;

    //this final calculation is for transparency
    FragColor = vec4(color,1.0);
}

vec3 CalcDirLight(vec3 normal, vec3 viewDir){
    vec3 lightDir = normalize(-dLDirection);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 0.6);
    // combine results
    vec3 diffuse = dLDiffuse * diff;

    vec3 specular = spec * color;
    
    return diffuse + specular;
}
