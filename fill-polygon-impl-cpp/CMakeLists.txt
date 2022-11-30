cmake_minimum_required(VERSION 3.23)
project(fill_polygon_impl_cpp)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)
include_directories($ENV{JAVA_HOME}/include)
include_directories($ENV{JAVA_HOME}/include/darwin)

add_compile_options(-fPIC)

add_library(fill_polygon_impl_cpp SHARED
        FillPolygonImpl.cpp
        FillPolygonImpl.h
        Line.h
        Point.h Line.cpp Point.cpp)
