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
    // mix为OpenGL内置的函数，表示对2个数进行按比例混合叠加，这里就是对当前片段从2纹理采样得到的颜色值进行按照0.5的比例混合。
    // 假如改成0.1就是左边取0.1
    // https://thebookofshaders.com/glossary/?search=mix
}