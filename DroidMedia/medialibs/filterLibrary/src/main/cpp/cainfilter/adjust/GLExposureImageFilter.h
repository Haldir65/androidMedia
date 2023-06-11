//
// Created by admin on 2018/4/4.
//

#ifndef EGLNATIVESAMPLE_GLEXPOSUREIMAGEFILTER_H
#define EGLNATIVESAMPLE_GLEXPOSUREIMAGEFILTER_H


#include "../GLImageFilter.h"

class GLExposureImageFilter : public GLImageFilter {
public:
    GLExposureImageFilter();

    GLExposureImageFilter(const char *vertexShader, const char *fragmentShader);

    inline void setExposure(float exposure) {
        this->exposure = exposure;
    }

protected:
    virtual void initHandle(void);

    virtual void bindValue(GLint texture, GLfloat *vertices, GLfloat *textureCoords);

    virtual const char *getFragmentShader(void);

private:
    int mExposureLoc;
    float exposure;
};


#endif //EGLNATIVESAMPLE_GLEXPOSUREIMAGEFILTER_H
