cmake_minimum_required(VERSION 3.23)
project(fill_polygon_impl_cpp)

set(CMAKE_CXX_STANDARD 14)

include_directories(..)
include_directories($ENV{JAVA_HOME}/include)
include_directories($ENV{JAVA_HOME}/include/darwin)

add_compile_options(-fPIC -Wall -Werror -Wextra -Wpedantic)

add_library(fill_polygon_impl_cpp SHARED
        fillpolygonjni.cpp fillpolygonjni.h line.h line.cpp
        point.h point.cpp fillpolygonimpl.cpp fillpolygonimpl.h)
