cmake_minimum_required(VERSION 3.22.1)

project("drummaker")


# --- Konfiguracja biblioteki Oboe ---
# Ustawia ścieżkę do submodułu Oboe i go kompiluje.
set(OBOE_DIR ./oboe)
add_subdirectory(${OBOE_DIR} ./oboe/build)

set(ENABLE_TESTS OFF)
set(BUILD_EXAMPLES OFF)
add_subdirectory(./libsndfile)

add_library(${CMAKE_PROJECT_NAME} SHARED drummaker.cpp)

target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        ${OBOE_DIR}/include
        ${CMAKE_CURRENT_BINARY_DIR}/libsndfile/src
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        oboe
        sndfile
)