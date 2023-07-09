#extension GL_OES_EGL_image_external : require
// 仿抖音三屏特效
precision highp float;
uniform samplerExternalOES uTextureSampler;
varying highp vec2 vTextureCoord;

// https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-仿抖音九屏特效.md
void main() {
  highp vec2 uv = vTextureCoord;
  if (uv.x < 1.0 / 3.0) {
    uv.x = uv.x * 3.0;
  } else if (uv.x < 2.0 / 3.0) {
    uv.x = (uv.x - 1.0 / 3.0) * 3.0;
  } else {
    uv.x = (uv.x - 2.0 / 3.0) * 3.0;
  }
  if (uv.y <= 1.0 / 3.0) {
    uv.y = uv.y * 3.0;
  } else if (uv.y < 2.0 / 3.0) {
    uv.y = (uv.y - 1.0 / 3.0) * 3.0;
  } else {
    uv.y = (uv.y - 2.0 / 3.0) * 3.0;
  }
  gl_FragColor = texture2D(uTextureSampler, uv);
}
