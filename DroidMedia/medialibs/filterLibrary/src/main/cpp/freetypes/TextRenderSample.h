#pragma once
#ifndef DROIDMEDIA_TEXTRENDERSAMPLE_H
#define DROIDMEDIA_TEXTRENDERSAMPLE_H

#ifdef __cplusplus
extern "C" {
#endif
#include "ft2build.h"
#include <freetype/ftglyph.h>
#include "../common/AndroidLog.h"
#include <GLES2/gl2.h>
#ifdef __cplusplus
}
#endif
#include <iostream>
#include <string>
#include <vector>
#include <map>
#include "../glm/glm/glm.hpp"
#include "../glm/glm/gtc/matrix_transform.hpp"
#include "../glm/glm/ext.hpp"
#include "../glm/glm/detail/_noise.hpp"

#define GO_CHECK_GL_ERROR(...)   ALOGE("CHECK_GL_ERROR %s glGetError = %d, line = %d, ",  __FUNCTION__, glGetError(), __LINE__)


struct Character {
    GLuint textureID;    // ID handle of the glyph texture
    glm::ivec2 size;     // Size of glyph
    glm::ivec2 bearing;  // Offset from baseline to left/top of glyph
    GLuint advance;      // Horizontal offset to advance to next glyph
};


// https://cloud.tencent.com/developer/article/1848280

class TextRenderSample {


public:
    TextRenderSample();

    virtual ~TextRenderSample();

    int accessingFreeTypeData(std::string string);

    void LoadFacesByASCII(std::string fontfilepath);

    void RenderText(std::string text, GLfloat x, GLfloat y, GLfloat scale,
                    glm::vec3 color, glm::vec2 viewport);

private:
    FT_Library ft;
    FT_Face face;
    std::map<GLint, Character> m_Characters;


};


#endif //DROIDMEDIA_TEXTRENDERSAMPLE_H
