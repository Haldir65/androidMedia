package com.glfilter.advanced.makeup;

import android.content.Context;

import com.glfilter.base.GLImageFilter;
import com.glfilter.model.FacePoints;
import com.glfilter.model.IFacePoints;
import com.glfilter.model.IMakeup;
import com.glfilter.model.Makeup;

/**
 * 彩妆滤镜
 */
public class GLImageMakeupFilter extends GLImageFilter implements IMakeup, IFacePoints {

    protected FacePoints mFacePoints;

    public GLImageMakeupFilter(Context context) {
        super(context);
    }

    public GLImageMakeupFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
    }

    @Override
    public void onMakeup(Makeup makeup) {

    }

    @Override
    public void onFacePoints(FacePoints facePoints) {
        mFacePoints = facePoints;
    }
}
