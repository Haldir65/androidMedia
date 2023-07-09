#extension GL_OES_EGL_image_external : require
// 仿抖音三屏特效
precision highp float;
uniform samplerExternalOES uTextureSampler;
varying highp vec2 vTextureCoord;




// 在fragment shader 里面添加一个加载LookupTable的方法
vec4 getLutColor(vec4 textureColor, sampler2D lookupTexture) {
  mediump float blueColor = textureColor.b * 63.0;

  mediump vec2 quad1;
  quad1.y = floor(floor(blueColor) / 8.0);
  quad1.x = floor(blueColor) - (quad1.y * 8.0);

  mediump vec2 quad2;
  quad2.y = floor(ceil(blueColor) / 8.0);
  quad2.x = ceil(blueColor) - (quad2.y * 8.0);

  highp vec2 texPos1;
  texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
  texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

  highp vec2 texPos2;
  texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
  texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

  lowp vec4 newColor1 = texture2D(lookupTexture, texPos1);
  lowp vec4 newColor2 = texture2D(lookupTexture, texPos2);

  lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
  vec4 color = vec4(newColor.rgb, textureColor.w);
  return color;
}


// https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-仿抖音三重影像滤镜.md
void main()
{
  highp vec2 uv = vTextureCoord;
  vec4 color;
  if (uv.y >= 0.0 && uv.y <= 0.33) { // 上层
    vec2 coordinate = vec2(uv.x, uv.y + 0.33);
    vec4 textureColor = texture2D(uTextureSampler, coordinate);
    color = getLutColor(textureColor, lookupTable1);
  } else if (uv.y > 0.33 && uv.y <= 0.67) {   // 中间层
    vec4 textureColor = texture2D(uTextureSampler, uv);
    color = getLutColor(textureColor, lookupTable2);
  } else {    // 下层
    vec2 coordinate = vec2(uv.x, uv.y - 0.33);
    vec4 textureColor = texture2D(uTextureSampler, coordinate);
    color = getLutColor(textureColor, lookupTable3);
  }
  gl_FragColor = color;
}
