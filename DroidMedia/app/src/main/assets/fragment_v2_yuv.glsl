precision mediump float;
varying vec2 v_texPosition;
uniform sampler2D sampler_y;
uniform sampler2D sampler_u;
uniform sampler2D sampler_v;
void main() {
    float y,u,v;
    y = texture2D(sampler_y,v_texPosition).r;
    u = texture2D(sampler_u,v_texPosition).r- 0.5;
    v = texture2D(sampler_v,v_texPosition).r- 0.5;

    vec3 rgb;
    rgb.r = y + 1.402 * v;
    rgb.g = y - 0.34414 * u - 0.71414 * v;
    rgb.b = y + 1.772 * u;


    if(v_texPosition.x > 0.5) {
        gl_FragColor = vec4(vec3(rgb.r*0.299 + rgb.g*0.587 + rgb.b*0.114), 1.0);
        //将输出视频帧的一半渲染成经典黑白风格的图像
    }else {
        gl_FragColor = vec4(rgb,1.0);
    }
}