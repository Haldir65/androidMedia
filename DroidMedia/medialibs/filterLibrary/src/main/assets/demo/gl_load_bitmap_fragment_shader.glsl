precision mediump float;

uniform sampler2D u_TextureUnit; // 实际纹理数据
varying vec2 v_TextureCoordinates; // vertex传过来的纹理坐标

//  第二张图片
uniform sampler2D u_TextureUnit2; //的实际纹理数据
varying vec2 v_TextureCoordinates2; // vertex传过来的纹理坐标

void main(){
//    gl_FragColor = texture2D(u_TextureUnit,v_TextureCoordinates) + texture2D(u_TextureUnit2,v_TextureCoordinates2);
    gl_FragColor = mix(texture2D(u_TextureUnit,v_TextureCoordinates),texture2D(u_TextureUnit2,v_TextureCoordinates2),0.5);
}