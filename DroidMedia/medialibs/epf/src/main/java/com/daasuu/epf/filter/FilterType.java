package com.daasuu.epf.filter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.daasuu.epf.R;
import com.daasuu.epf.custfilter.Gl4SplitFilter;
import com.daasuu.epf.custfilter.GlFlashFliter;
import com.daasuu.epf.custfilter.GlHuanJueFliter;
import com.daasuu.epf.custfilter.GlItchFilter;
import com.daasuu.epf.custfilter.GlPngFliter;
import com.daasuu.epf.custfilter.GlScaleFilter;
import com.daasuu.epf.custfilter.GlShakeFilter;
import com.daasuu.epf.custfilter.GlSoulOutFilter;
import com.daasuu.epf.filter2.AutoFixFilter;
import com.daasuu.epf.filter2.BlackAndWhiteEffectFilter;
import com.daasuu.epf.filter2.InvertColorsFilter;
import com.glfilter.advanced.adjust.GLImageBrightnessFilter;
import com.glfilter.advanced.adjust.GLImageContrastFilter;
import com.glfilter.advanced.adjust.GLImageExposureFilter;
import com.glfilter.advanced.adjust.GLImageHueFilter;
import com.glfilter.advanced.adjust.GLImageMirrorFilter;
import com.spx.egl.GLImageComplexionBeautyFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudamasayuki on 2017/05/18.
 */

public enum FilterType implements Serializable {
    DEFAULT,
    BEAUTY_CUS,
    SPX_SOULOUT,
    SPX_LUCION,
    SPX_FLASH,
    SPX_ITCH,
    SPX_SCALE,
    SPX_SHAKE,
    SPX_4SPLIT,
    PNG_CUS,
    BILATERAL_BLUR,
    BOX_BLUR,
    TONE_CURVE_SAMPLE,
    LOOK_UP_TABLE_SAMPLE,
    BULGE_DISTORTION,
    CGA_COLORSPACE,
    GAUSSIAN_FILTER,
    GRAY_SCALE,
    HAZE,
    INVERT,
    MONOCHROME,
    SEPIA,
    SHARP,
    VIGNETTE,
    FILTER_GROUP_SAMPLE,
    SPHERE_REFRACTION,
    BITMAP_OVERLAY_SAMPLE,

    // FILTERS2
    INVERT_COLORS,


    BLACK_AND_WHITE_FILTER,

    IMAGE_BRIGHTNESS_FILTER,
    IMAGE_CONTRAST_FILTER,
    IMAGE_EXPOSURE_FILTER,
    IMAGE_HueFilter,
    IMAGE_MIRROR_FILTER,

    AutoFixEffect;


    public static List<FilterType> createFilterList() {
        List<FilterType> filters = new ArrayList<>();

        filters.add(DEFAULT);
        filters.add(BEAUTY_CUS);
        filters.add(PNG_CUS);
        filters.add(SEPIA);
        filters.add(MONOCHROME);
        filters.add(TONE_CURVE_SAMPLE);
        filters.add(LOOK_UP_TABLE_SAMPLE);
        filters.add(VIGNETTE);
        filters.add(INVERT);
        filters.add(HAZE);
        filters.add(BOX_BLUR);
        filters.add(BILATERAL_BLUR);
        filters.add(GRAY_SCALE);
        filters.add(SPHERE_REFRACTION);
        filters.add(FILTER_GROUP_SAMPLE);
        filters.add(GAUSSIAN_FILTER);
        filters.add(BULGE_DISTORTION);
        filters.add(CGA_COLORSPACE);
        filters.add(SHARP);
        filters.add(BITMAP_OVERLAY_SAMPLE);
        filters.add(INVERT_COLORS);
        filters.add(BLACK_AND_WHITE_FILTER);
        filters.add(IMAGE_BRIGHTNESS_FILTER);
        filters.add(IMAGE_CONTRAST_FILTER);
        filters.add(IMAGE_EXPOSURE_FILTER);
        filters.add(IMAGE_MIRROR_FILTER);
        filters.add(AutoFixEffect);

        return filters;
    }

    public static GlFilter createGlFilter(FilterType filterType, String args, Context context) {
        switch (filterType) {
            case DEFAULT:
                return new GlFilter();
            case BEAUTY_CUS:
//                return new GlFilterGroup(new GLImageGaussPassFilter(0), new GLImageGaussPassFilter(1) );
                return new GLImageComplexionBeautyFilter(context);
//                return new GLImageComplexionBeautyFilter(context);
            case SPX_SOULOUT: return new GlSoulOutFilter(context);
            case SPX_LUCION: return new GlHuanJueFliter(context);
            case SPX_FLASH: return new GlFlashFliter(context);
            case SPX_ITCH: return new GlItchFilter(context);
            case SPX_SCALE: return new GlScaleFilter(context);
            case SPX_SHAKE: return new GlShakeFilter(context);
            case SPX_4SPLIT: return new Gl4SplitFilter(context);
            case PNG_CUS:
                return new GlPngFliter(context, args);
            case SEPIA:
                return new GlSepiaFilter();
            case GRAY_SCALE:
                return new GlGrayScaleFilter();
            case INVERT:
                return new GlInvertFilter();
            case HAZE:
                return new GlHazeFilter();
            case MONOCHROME:
                return new GlMonochromeFilter();
            case BILATERAL_BLUR:
                return new GlBilateralFilter();
            case BOX_BLUR:
                return new GlBoxBlurFilter();
            case LOOK_UP_TABLE_SAMPLE:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_beauty_face);
                return new GlLookUpTableFilter(bitmap);
            case TONE_CURVE_SAMPLE:
                try {
                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
                    return new GlToneCurveFilter(is);
                } catch (IOException e) {
                    Log.e("FilterType", "Error");
                }
                return new GlFilter();

            case SPHERE_REFRACTION:
                return new GlSphereRefractionFilter();
            case VIGNETTE:
                return new GlVignetteFilter();
            case FILTER_GROUP_SAMPLE:
                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
            case GAUSSIAN_FILTER:
                return new GlGaussianBlurFilter();
            case BULGE_DISTORTION:
                return new GlBulgeDistortionFilter();
            case CGA_COLORSPACE:
                return new GlCGAColorspaceFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(4f);
                return glSharpenFilter;
            case INVERT_COLORS:
                InvertColorsFilter filter = new InvertColorsFilter();
                return filter;
            case BLACK_AND_WHITE_FILTER:
                BlackAndWhiteEffectFilter blackAndWhiteEffectFilter = new BlackAndWhiteEffectFilter();
                return blackAndWhiteEffectFilter;
            case IMAGE_BRIGHTNESS_FILTER:
                GLImageBrightnessFilter ibs = new GLImageBrightnessFilter(context);
                ibs.setBrightness(-0.5f);
                return ibs;
            case IMAGE_CONTRAST_FILTER:
                GLImageContrastFilter cf = new GLImageContrastFilter(context);
                return cf;
            case IMAGE_EXPOSURE_FILTER:
                GLImageExposureFilter EF = new GLImageExposureFilter(context);
                EF.setExposure(8);
                return EF;
            case IMAGE_HueFilter:
                GLImageHueFilter hue = new GLImageHueFilter(context);
                hue.setHue(120);
                return hue;
            case IMAGE_MIRROR_FILTER:
                GLImageMirrorFilter mirror = new GLImageMirrorFilter(context);
                mirror.setAngle(90);
                return mirror;
            case AutoFixEffect:
                AutoFixFilter fixFilter = new AutoFixFilter(0.5f);
                return fixFilter;
            default:
                return new GlFilter();
        }
    }


}
