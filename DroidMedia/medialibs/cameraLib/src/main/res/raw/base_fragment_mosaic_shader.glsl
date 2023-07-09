#extension GL_OES_EGL_image_external : require
precision highp float;

varying vec2 vTextureCoord; // 纹理坐标
uniform samplerExternalOES uTextureSampler;// 输入图像纹理

uniform float imageWidthFactor; // 图像宽度
uniform float imageHeightFactor;// 图像高度
uniform float mosaicSize;       // 马赛克大小(像素值)


void main()
{
  vec2 uv  = vTextureCoord.xy;
  // 计算出马赛克的宽度
  float dx = mosaicSize * imageWidthFactor;
  // 计算出马赛克的高度
  float dy = mosaicSize * imageHeightFactor;
  // 使用floor函数计算出横坐标和纵坐标经过马赛克变换后的值
  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));
  // 计算出马赛克的颜色
  vec3 tc = texture2D(uTextureSampler, coord).xyz;
  // 输出马赛克图像
  gl_FragColor = vec4(tc, 1.0);
}
