//
// Created by harris on 2023/9/28.
//

#include "TextRenderSample.h"
#include <jni.h>
#include <GLES3/gl3.h>
#include "../glm/glm/gtc/matrix_transform.hpp"
static const int MAX_SHORT_VALUE = 65536;


TextRenderSample::TextRenderSample() {
    ALOGE("constructor of TextRenderSample called")
    face = nullptr;
    ft = nullptr;
}

TextRenderSample::~TextRenderSample() {
    if (face!= nullptr){
        FT_Done_Face(face);
        face = nullptr;
    }
    if (ft!= nullptr){
        FT_Done_FreeType(ft);
        ft = nullptr;
    }
    ALOGE("destructor of TextRenderSample called")
}

int TextRenderSample::accessingFreeTypeData(std::string string) {
    if (FT_Init_FreeType(&ft))
        ALOGE("TextRenderSample::LoadFacesByASCII FREETYPE: Could not init FreeType Library");

    if (FT_New_Face(ft, "/sdcard/fonts/Antonio-Regular.ttf", 0, &face))
        ALOGE("TextRenderSample::LoadFacesByASCII FREETYPE: Failed to load font");


    FT_Set_Pixel_Sizes(face, 0, 96);
    return 0;
}

void TextRenderSample::LoadFacesByASCII(std::string fontfilepath) {
    // FreeType
    // All functions return a value different than 0 whenever an error occurred
    if (FT_Init_FreeType(&ft))
        ALOGE("TextRenderSample::LoadFacesByASCII FREETYPE: Could not init FreeType Library");

    //"/sdcard/fonts/Antonio-Regular.ttf"
    // Load font as face
    if (FT_New_Face(ft, fontfilepath.c_str(), 0, &face))
        ALOGE("TextRenderSample::LoadFacesByASCII FREETYPE: Failed to load font");

    // Set size to load glyphs as
    FT_Set_Pixel_Sizes(face, 0, 96);

    // Disable byte-alignment restriction
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    // Load first 128 characters of ASCII set
    for (unsigned char c = 0; c < 128; c++)
    {
        // Load character glyph
        if (FT_Load_Char(face, c, FT_LOAD_RENDER))
        {
            ALOGE("TextRenderSample::LoadFacesByASCII FREETYTPE: Failed to load Glyph");
            continue;
        }
        // Generate texture
        GLuint texture;
        glGenTextures(1, &texture);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_LUMINANCE,
                face->glyph->bitmap.width,
                face->glyph->bitmap.rows,
                0,
                GL_LUMINANCE,
                GL_UNSIGNED_BYTE,
                face->glyph->bitmap.buffer
        );

        // Set texture options
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Now store character for later use
        Character character = {
                texture,
                glm::ivec2(face->glyph->bitmap.width, face->glyph->bitmap.rows),
                glm::ivec2(face->glyph->bitmap_left, face->glyph->bitmap_top),
                static_cast<GLuint>(face->glyph->advance.x)
        };
        m_Characters.insert(std::pair<GLint, Character>(c, character));
    }
    glBindTexture(GL_TEXTURE_2D, 0);
    // Destroy FreeType once we're finished
    FT_Done_Face(face);
    FT_Done_FreeType(ft);
    face = nullptr;
    ft = nullptr;
}

void
TextRenderSample::RenderText(std::string text, GLfloat x, GLfloat y, GLfloat scale, glm::vec3 color, glm::vec2 viewport) {
//    // 激活合适的渲染状态
//    glUseProgram(m_ProgramObj);
//    glUniform3f(glGetUniformLocation(m_ProgramObj, "u_textColor"), color.x, color.y, color.z);
//    glBindVertexArray(m_VaoId);
//    GO_CHECK_GL_ERROR();
//    // 对文本中的所有字符迭代
//    std::string::const_iterator c;
//    x *= viewport.x;
//    y *= viewport.y;
//    for (c = text.begin(); c != text.end(); c++)
//    {
//        Character ch = m_Characters[*c];
//
//        GLfloat xpos = x + ch.bearing.x * scale;
//        GLfloat ypos = y - (ch.size.y - ch.bearing.y) * scale;
//
//        xpos /= viewport.x;
//        ypos /= viewport.y;
//
//        GLfloat w = ch.size.x * scale;
//        GLfloat h = ch.size.y * scale;
//
//        w /= viewport.x;
//        h /= viewport.y;
//
//        ALOGE("TextRenderSample::RenderText [xpos,ypos,w,h]=[%f, %f, %f, %f]", xpos, ypos, w, h);
//
//        // 当前字符的VBO
//        GLfloat vertices[6][4] = {
//                { xpos,     ypos + h,   0.0, 0.0 },
//                { xpos,     ypos,       0.0, 1.0 },
//                { xpos + w, ypos,       1.0, 1.0 },
//
//                { xpos,     ypos + h,   0.0, 0.0 },
//                { xpos + w, ypos,       1.0, 1.0 },
//                { xpos + w, ypos + h,   1.0, 0.0 }
//        };
//
//        // 在方块上绘制字形纹理
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, ch.textureID);
//        glUniform1i(m_SamplerLoc, 0);
//        GO_CHECK_GL_ERROR();
//        // 更新当前字符的VBO
//        glBindBuffer(GL_ARRAY_BUFFER, m_VboId);
//        glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(vertices), vertices);
//        GO_CHECK_GL_ERROR();
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
//        // 绘制方块
//        glDrawArrays(GL_TRIANGLES, 0, 6);
//        GO_CHECK_GL_ERROR();
//        // 更新位置到下一个字形的原点，注意单位是1/64像素
//        x += (ch.advance >> 6) * scale; //(2^6 = 64)
//    }
//    glBindVertexArray(0);
//    glBindTexture(GL_TEXTURE_2D, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_filterlibrary_freetypes_FreeTypeHelper_loadFreeTypeIntoBitMap(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jstring text,
                                                                                 jstring font_asset_name,
                                                                                 jobject bitmap) {

    const char* displayText = env->GetStringUTFChars(text, nullptr);

    auto e = new TextRenderSample();
    e->accessingFreeTypeData(std::string{displayText});
    delete e;
    // TODO: implement loadFreeTypeIntoBitMap()
}
