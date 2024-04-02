#pragma once
#include <iostream>
#include <filesystem>
#include <string_view>

#include <fmt/core.h>
#include <fmt/color.h>
#include <fmt/ranges.h>
#include <fmt/chrono.h>

#ifndef DROIDMEDIA_SIMDJSONUSECASE_H
#define DROIDMEDIA_SIMDJSONUSECASE_H



void reading_content_of_json_file(const std::string&& filepath);

#endif //DROIDMEDIA_SIMDJSONUSECASE_H
