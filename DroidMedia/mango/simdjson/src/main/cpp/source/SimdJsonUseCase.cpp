#include <iostream>
#include "../library/simdjson.h"
#include "SimdJsonUseCase.h"
#include "AndroidLog.h"
#include <chrono>



void reading_content_of_json_file(const std::string&& filepath){
    auto beg =  std::chrono::high_resolution_clock::now();
    // https://github.com/simdjson/simdjson/blob/master/doc/implementation-selection.md#manually-selecting-the-implementation
    constexpr auto imp = []() {
#ifdef _WIN32
        return "haswell";
#else
        return "arm64";
#endif
    }();
    auto my_implementation = simdjson::get_available_implementations()[imp];
    if (! my_implementation) { exit(1); }
    if (! my_implementation->supported_by_runtime_system()) { exit(1); }
    simdjson::get_active_implementation() = my_implementation;



#if SIMDJSON_IMPLEMENTATION_ARM64 == 1
    ALOGI("using  %s implementation ","arm64");
#endif

#if SIMDJSON_IMPLEMENTATION_ICELAKE == 1
    fmt::print(fmt::fg(fmt::color::lawn_green), "\n {0} \n", " icelake ");
#endif

#if SIMDJSON_IMPLEMENTATION_HASWELL == 1
    fmt::print(fmt::fg(fmt::color::lawn_green), "\n {0} \n", " haswell ");
#endif

    namespace fs = std::filesystem;
    if (!fs::exists(filepath)){
        ALOGE("file %s not exists , abort! ",filepath.c_str());
    } else {
        ALOGI("file %s do exists , proceed ",filepath.c_str());
        using namespace simdjson;
        ondemand::parser parser{};
        padded_string json = padded_string::load(filepath);
        ondemand::document tweets = parser.iterate(json);
        auto numOfCounts = uint64_t(tweets["search_metadata"]["count"]);
//        std::cout << numOfCounts << " results." << std::endl;
        ALOGI("num of counts is  %lu ",numOfCounts);
    }

    auto end = std::chrono::high_resolution_clock::now();
    auto dur = std::chrono::duration_cast<std::chrono::microseconds>(end - beg);
    ALOGI("time cost for parsing large json = %lld microseconds" ,dur.count());

    for (auto implementation : simdjson::get_available_implementations()) {
        if (implementation->supported_by_runtime_system()) {
            ALOGI("implementation->name  %s \n  implementation->description() %s \n",implementation->name().c_str(),implementation->description().c_str());
//
//            std::cout << implementation->name() << ": "
//                      << implementation->description() << std::endl;
        }
    }
}
