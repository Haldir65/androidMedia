precision mediump float;

uniform sampler2D u_TextureUnit; // 实际纹理数据
varying vec2 v_TextureCoordinates; // vertex传过来的纹理坐标

//  第二张图片
uniform sampler2D u_TextureUnit2; //的实际纹理数据
varying vec2 v_TextureCoordinates2; // vertex传过来的纹理坐标

float alpha;

void main(){
//    gl_FragColor = texture2D(u_TextureUnit,v_TextureCoordinates) + texture2D(u_TextureUnit2,v_TextureCoordinates2);
//    gl_FragColor = mix(texture2D(u_TextureUnit,v_TextureCoordinates),texture2D(u_TextureUnit2,v_TextureCoordinates2),0.5);

    if(v_TextureCoordinates.x >0.1 && v_TextureCoordinates.x <0.8 && v_TextureCoordinates.y >0.3 && v_TextureCoordinates.y<0.9)
    {
        alpha = 1.0f;
        gl_FragColor = texture2D(u_TextureUnit2,v_TextureCoordinates2);
    }else{
        alpha = 0.0f;
        gl_FragColor = texture2D(u_TextureUnit,v_TextureCoordinates);
    }


}