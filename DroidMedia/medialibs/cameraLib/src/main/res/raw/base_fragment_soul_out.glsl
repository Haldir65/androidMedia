#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;



uniform float scale;

void main() {
    vec2 uv = vTextureCoord.xy;
    // 输入纹理
    vec4 sourceColor = texture2D(uTextureSampler, fract(uv));
    // 将纹理坐标中心转成(0.0, 0.0)再做缩放
    vec2 center = vec2(0.5, 0.5);
    uv -= center;
    uv = uv / scale;
    uv += center;
    // 缩放纹理
    vec4 scaleColor = texture2D(uTextureSampler, fract(uv));
    // 线性混合
    gl_FragColor = mix(sourceColor, scaleColor, 0.5 * (0.6 - fract(scale)));
}