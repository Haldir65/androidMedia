precision mediump float;

uniform sampler2D u_TextureUnit; // 实际纹理数据
varying vec2 v_TextureCoordinates; // vertex传过来的纹理坐标


void main(){
    gl_FragColor = texture2D(u_TextureUnit,v_TextureCoordinates);
}