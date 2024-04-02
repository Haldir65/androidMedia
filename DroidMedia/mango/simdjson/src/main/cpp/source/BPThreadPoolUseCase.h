#pragma once
#ifndef DROIDMEDIA_BPTHREADPOOLUSECASE_H
#define DROIDMEDIA_BPTHREADPOOLUSECASE_H
#include <BS_thread_pool.hpp>
#include <sstream>
#include <fmt/core.h>
#include <fmt/color.h>
#include <fmt/ranges.h>
#include <fmt/chrono.h>

class BPThreadPoolUseCase {
private:
    BS::thread_pool pool;
public:


    template <typename F>
    void scheduleTask(F f){
        pool.submit_task(f);
    }

   inline void wait(){
       pool.wait();
   }

};




#endif //DROIDMEDIA_BPTHREADPOOLUSECASE_H
