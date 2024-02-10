precision mediump float;//精度 为float

varying vec2      texCoord;//纹理位置  接收于vertex_shader

//这些是shaderToy的参数
uniform vec3      iResolution;// viewport resolution (in pixels)
uniform float     iTime;// shader playback time (in seconds)
uniform int       iFrame;// shader playback frame
uniform vec4      iMouse;// mouse pixel coords. xy: current (if MLB down), zw: click
uniform sampler2D iChannel0;// input channel. XX = 2D/Cube
uniform sampler2D iChannel1;// input channel. XX = 2D/Cube
uniform sampler2D iChannel2;// input channel. XX = 2D/Cube


//https://www.shadertoy.com/view/llGXWc

//https://iquilezles.org/articles/palettes/
vec3 palette( float t ) {
    vec3 a = vec3(0.5, 0.5, 0.5);
    vec3 b = vec3(0.5, 0.5, 0.5);
    vec3 c = vec3(1.0, 1.0, 1.0);
    vec3 d = vec3(0.263,0.416,0.557);

    return a + b*cos( 6.28318*(c*t+d) );
}

//https://www.shadertoy.com/view/mtyGWy
void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
    vec2 uv = (fragCoord * 2.0 - iResolution.xy) / iResolution.y;
    vec2 uv0 = uv;
    vec3 finalColor = vec3(0.0);

    for (float i = 0.0; i < 4.0; i++) {
        uv = fract(uv * 1.5) - 0.5;

        float d = length(uv) * exp(-length(uv0));

        vec3 col = palette(length(uv0) + i*.4 + iTime*.4);

        d = sin(d*8. + iTime)/8.;
        d = abs(d);

        d = pow(0.01 / d, 1.2);

        finalColor += col * d;
    }

    fragColor = vec4(finalColor, 1.0);
}

void main() {
    mainImage(gl_FragColor, texCoord * iResolution.xy);
}
