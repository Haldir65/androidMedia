#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
void main()
{
    /*
      vec4 vCameraColor = texture2D(uTextureSampler, vTextureCoord);
      float fGrayColor = (0.3*vCameraColor.r + 0.59*vCameraColor.g + 0.11*vCameraColor.b);
      gl_FragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
    */

  /*
     or if you hate gray color
  */
//  gl_FragColor = texture2D(uTextureSampler, vTextureCoord);

  vec2 uv = vTextureCoord;
  if (uv.y < 0.5) {
    uv.y = 1.0 - uv.y;
  }
  gl_FragColor = texture2D(uTextureSampler, fract(uv));

}
