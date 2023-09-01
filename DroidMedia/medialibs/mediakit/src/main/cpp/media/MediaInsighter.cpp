
#include "MediaInsighter.h"



std::string MediaInsighter::probeBasicInfo(std::string filepath) {
    std::string version1 {"1" "." "2" "." "0"};
    std::string version2 {std::string("1") +"." + "2"};
    std::string version3  = std::string ("1") + "." + "2";



    const std::string& filename(filepath);
    FILE * f = fopen(filename.c_str(), "r");
    if (!f) {
        AVLOGE("Failed to open file %s, because %s", filename.c_str(), strerror(errno));
        return "";
    }
    int fd = fileno(f);
    long cur = ftell(f);
    fseek(f, 0, SEEK_END);
    long len = ftell(f);
    AVLOGW(" file %s has length %ld ", filename.c_str(),len);
    AMediaExtractor* extractor_ = AMediaExtractor_new();
    ssize_t ret = AMediaExtractor_setDataSourceFd(extractor_, fd, cur, len);
    fclose(f);
    if (ret < 0) {
        AVLOGE("Error when set data source fd. return %d.", ret);
        AMediaExtractor_delete(extractor_);
        return "";
    }
    int track_cnt = AMediaExtractor_getTrackCount(extractor_);
    const char *info;
    int video_track_indx_ = 0;
    AMediaFormat* format_;
    for (int i = 0; i < track_cnt; i++) {
        format_ = AMediaExtractor_getTrackFormat(extractor_, i);
        AMediaFormat_getString(format_, AMEDIAFORMAT_KEY_MIME, &info);
        std::string str(info);
        if (str.find("video") == 0) {
            AMediaExtractor_selectTrack(extractor_, i);
            video_track_indx_ = i;
            break;
        }
        AMediaFormat_delete(format_);
        format_ = nullptr;
    }
    if (video_track_indx_ < 0) {
        AVLOGE("There is no video stream in file %s.", filename.c_str());
        AMediaExtractor_delete(extractor_);
        return "";
    }
    AMediaFormat_getString(format_, AMEDIAFORMAT_KEY_MIME, &info);
    return  std::string { info };
}
