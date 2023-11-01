cmake_minimum_required(VERSION 3.22.1)

message(------CMake begin-------)
message(${CMAKE_CURRENT_SOURCE_DIR})

set(target musicplayer)
project(${target})

# 引入soundtouch的头文件与库
include_directories(soundtouch/include)
include_directories(soundtouch/SoundTouch)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/include)
include_directories(libcommon)
include_directories(libmedia)

# 引入ffmpeg头文件目录
#include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# 把所有hpp文件和h文件都放入名称为hdrs的集合中
#file(GLOB hdrs ./*.h libcommon/*.h libmedia/*.h libmusicdecoder/*.h )
# 把所有cpp文件和c文件都放入名称为srcs的集合中
#file(GLOB srcs ./*.cpp libcommon/*.cpp libmedia/*.cpp libmusicdecoder/*.cpp )

# 设置ffmpeg库的查找目录 (目的是下面可以直接写ffmpeg库的名字)
# CMAKE_CXX_FLAGS用于存储 C++ 编译器的标志
# 作用是将 JNI 库文件所在的路径添加到 C++ 编译器的标志中，以便编译器在链接阶段能够正确地查找和链接 JNI 库
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_CURRENT_SOURCE_DIR}/jniLibs/${ANDROID_ABI}")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}")

aux_source_directory(. SOURCE)
aux_source_directory(./soundtouch/SoundTouch SOURCE_SOUNDTOUCH)
aux_source_directory(./libmedia SOURCE_MEIDA)
aux_source_directory(./libcommon SOURCE_COMMMON)
aux_source_directory(./libmusicdecoder SOURCE_DECODER)

# 生成动态库
add_library(
        ${target}

        SHARED

        ${SOURCE_DECODER}
        ${SOURCE_MEIDA}
        ${SOURCE_COMMMON}
        ${SOURCE_SOUNDTOUCH}
        ${SOURCE})

# 作用是在指定的搜索路径中查找名为 log 的库文件，并将找到的库文件的路径存储在 log-lib 变量中
# 一旦找到 log 库，就可以在下面直接使用 log-lib 变量来引用该库文件。
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

#target_link_libraries(
#        ${target}
#
#        x264
#        opus
#        mp3lame
#        fdk-aac
#
#        avdevice
#        postproc
#        avcodec
#        avformat
#        avutil
#        avfilter
#        swresample
#        swscale
#
#        android
#        OpenSLES
#        # Links the target library to the log library
#        # included in the NDK.
#        ${log-lib})

# 设置静态库，必须这样吗？
target_link_libraries(${target} log z OpenSLES
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libx264.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libswscale.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libswresample.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libpostproc.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libopus.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libmp3lame.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libfdk-aac.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavutil.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavformat.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavfilter.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavdevice.a
        ${CMAKE_SOURCE_DIR}/3rdparty/lib/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.a
        ) # 指定要链接哪些库。log表示日志库，z表示libz，librtmp需要。
message(------CMake end-------)
