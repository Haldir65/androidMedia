//
// Created by admin on 2018/4/4.
//

#ifndef EGLNATIVESAMPLE_GLCONTRASTIMAGEFILTER_H
#define EGLNATIVESAMPLE_GLCONTRASTIMAGEFILTER_H


#include "../GLImageFilter.h"

class GLContrastImageFilter : public GLImageFilter {
public:
    GLContrastImageFilter();

    GLContrastImageFilter(const char *vertexShader, const char *fragmentShader);

    inline void setContrast(float contrast) {
        this->contrast = contrast;
    }

protected:
    virtual void initHandle(void);

    virtual void bindValue(GLint texture, GLfloat *vertices, GLfloat *textureCoords);

    virtual const char *getFragmentShader(void);

private:
    int mContrastLoc;
    float contrast;
};


#endif //EGLNATIVESAMPLE_GLCONTRASTIMAGEFILTER_H
