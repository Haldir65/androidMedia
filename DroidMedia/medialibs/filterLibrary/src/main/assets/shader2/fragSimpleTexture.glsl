#version 300 es
precision mediump float;
in vec2 TexCoord;
out vec4 FragColor;
//传入的纹理
uniform sampler2D ourTexture;
uniform sampler2D ourTexture1;
void main() {
    //gl_FragColor是OpenGL内置的
    FragColor = texture(ourTexture, TexCoord);
    FragColor = mix(texture(ourTexture, TexCoord), texture(ourTexture1, TexCoord), 0.5);
}