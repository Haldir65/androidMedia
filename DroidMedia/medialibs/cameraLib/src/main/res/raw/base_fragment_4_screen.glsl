#extension GL_OES_EGL_image_external : require
// 仿抖音三屏特效
precision highp float;
uniform samplerExternalOES uTextureSampler;
varying highp vec2 vTextureCoord;

// https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-仿抖音四屏特效.md
// 依样画葫芦， 25屏，36屏，81屏应该都可以
void main() {
  highp vec2 uv = vTextureCoord;
  if (uv.x <= 0.5) {
    uv.x = uv.x * 2.0;
  } else {
    uv.x = (uv.x - 0.5) * 2.0;
  }
  if (uv.y <= 0.5) {
    uv.y = uv.y * 2.0;
  } else {
    uv.y = (uv.y - 0.5) * 2.0;
  }
  gl_FragColor = texture2D(uTextureSampler, uv);
}
