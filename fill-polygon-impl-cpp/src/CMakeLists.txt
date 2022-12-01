cmake_minimum_required(VERSION 3.23)
project(fill_polygon_impl_cpp)

set(CMAKE_CXX_STANDARD 14)

include_directories(..)
include_directories($ENV{JAVA_HOME}/include)
include_directories($ENV{JAVA_HOME}/include/darwin)

add_compile_options(-fPIC)

add_library(fill_polygon_impl_cpp SHARED
        FillPolygonJNI.cpp FillPolygonJNI.h Line.h Line.cpp
        Point.h  Point.cpp FillPolygonImpl.cpp FillPolygonImpl.h)
