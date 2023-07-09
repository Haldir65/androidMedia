#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;


uniform float scale;

void main()
{
    /*
       https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-仿抖音缩放滤镜.md
    */
    vec2 uv = vTextureCoord.xy;
    // 将纹理坐标中心转成(0.0, 0.0)再做缩放
    vec2 center = vec2(0.5, 0.5);
    uv -= center;
    uv = uv / scale;
    uv += center;

    gl_FragColor = texture2D(uTextureSampler, uv);
}



