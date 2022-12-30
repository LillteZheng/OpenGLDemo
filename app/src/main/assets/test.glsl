#version 300 es
precision mediump float;
out vec4 FragColor;
in vec2 vTexture;
uniform samplerExternalOES ourTexture;
void main() {
    //FragColor = texture(ourTexture,vTexture);
    vec4 temColor = texture(ourTexture,vTexture);
    float gray = temColor.r * 0.2126 + temColor.g * 0.7152 + temColor.b * 0.0722;
    FragColor = vec4(gray,gray,gray,1.0);
}