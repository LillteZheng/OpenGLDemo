#version 300 es
precision mediump float;
out vec4 FragColor;
in vec2 vTexture;
uniform samplerExternalOES ourTexture;
void main()
{
    FragColor = texture(ourTexture,vTexture);
}