cmake_minimum_required(VERSION 3.23)
project(fill_polygon_impl_cpp)

set(CMAKE_CXX_STANDARD 14)

include_directories(..)
include_directories($ENV{JAVA_HOME}/include)
include_directories($ENV{JAVA_HOME}/include/darwin)

include(FetchContent)
FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
)

set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

add_compile_options(-fPIC -Wall -Werror -Wextra -Wpedantic)

enable_testing()

add_library(fill_polygon_impl_cpp SHARED
        fillpolygonjni.cpp fillpolygonjni.h line.h line.cpp
        point.h point.cpp fillpolygonimpl.cpp fillpolygonimpl.h)

add_executable(
        tests
        tests.cpp
)
target_link_libraries(
        tests
        fill_polygon_impl_cpp
        GTest::gtest_main
)

include(GoogleTest)
gtest_discover_tests(tests)