precision mediump float;
out vec4 FragColor;
in vec2 vTexture;
uniform sampler2D ourTexture;
uniform sampler2D ourTexture2;
void main()
{
    vec4 texture1 = texture(ourTexture,vTexture) ;
    vec4 texture2 = texture(ourTexture,vTexture2) ;
    FragColor = mix(texture1,texture2,0.5);
}