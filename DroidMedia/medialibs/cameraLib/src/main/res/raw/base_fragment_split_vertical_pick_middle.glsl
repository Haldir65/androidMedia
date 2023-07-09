#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
void main()
{
  //https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-仿抖音两屏特效.md
  // 纹理坐标 分屏特效中的两屏特效。分成上下两层，uv坐标的y轴在 0.0 ~ 0.5 和 0.5 ~ 1.0 的时候，均填充 0.25 ~ 0.75 区间的纹理图像
  vec2 uv = vTextureCoord.xy;
  float y;
  if (uv.y >= 0.0 && uv.y <= 0.5) {
    y = uv.y + 0.25;
  } else {
    y = uv.y - 0.25;
  }
  gl_FragColor = texture2D(uTextureSampler, vec2(uv.x, y));


}
