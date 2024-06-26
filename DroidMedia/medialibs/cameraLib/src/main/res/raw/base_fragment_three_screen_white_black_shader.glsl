#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;


uniform float scale;          // 黑白部分缩放倍数

void main() {
  highp vec2 uv = vTextureCoord;
  vec4 color;
  if (uv.y < 1.0 / 3.0) {
    // 缩放
    vec2 center = vec2(0.5, 0.5);
    uv -= center;
    uv = uv / scale;
    uv += center;
    color = texture2D(uTextureSampler, uv);
    // 黑白图片
    float gray = 0.3 * color.r + 0.59 * color.g + 0.11 * color.b;
    color = vec4(gray, gray, gray, 1.0);
  } else if (uv.y > 2.0 / 3.0) {
    color = texture2D(uTextureSampler, uv);
    // 缩放
    vec2 center = vec2(0.5, 0.5);
    uv -= center;
    uv = uv / scale;
    uv += center;
    color = texture2D(uTextureSampler, uv);
    // 黑白图片
    float gray = 0.3 * color.r + 0.59 * color.g + 0.11 * color.b;
    color = vec4(gray, gray, gray, 1.0);
  } else {
    color = texture2D(uTextureSampler, uv);
  }
  gl_FragColor = color;
}
