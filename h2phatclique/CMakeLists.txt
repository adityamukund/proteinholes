cmake_minimum_required(VERSION 2.8)

project("phatclique")

set(CMAKE_BUILD_TYPE "Release")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fomit-frame-pointer -fexpensive-optimizations -O3 -std=c++11")

set(CMAKE_EXE_LINKER_FLAGS "-static-libgcc -static-libstdc++")

IF(${CMAKE_SYSTEM_NAME} MATCHES "Linux")
    # Linux specific code
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=native")
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static")
ENDIF(${CMAKE_SYSTEM_NAME} MATCHES "Linux")

IF(${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
    # OSX specific code
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -headerpad_max_install_names")
ENDIF(${CMAKE_SYSTEM_NAME} MATCHES "Darwin")


FIND_PACKAGE(OpenMP REQUIRED)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS}")

add_subdirectory(${phatclique_SOURCE_DIR}/cliquer-1.21)

find_package(Boost REQUIRED COMPONENTS program_options) 

set(Boost_USE_STATIC_LIBS ON)

link_directories(${Boost_LIBRARY_DIRS})

INCLUDE_DIRECTORIES(${Boost_INCLUDE_DIRS})

message(STATUS "Boost_INCLUDE_DIRS: ${Boost_INCLUDE_DIRS}")
message(STATUS "Boost_LIBRARY_DIRS: ${Boost_LIBRARY_DIRS}")

include_directories(${phatclique_SOURCE_DIR}/include)
include_directories(${phatclique_SOURCE_DIR}/cliquer-1.21)
include_directories(${phatclique_SOURCE_DIR}/phat/phat_1_4_0/include)

SET(CMAKE_FIND_LIBRARY_SUFFIXES ".a")
SET(BUILD_SHARED_LIBRARIES OFF)

add_executable (phatclique bcliques.cpp )
target_link_libraries(phatclique ${Boost_LIBRARIES} cliquer graph reorder)
