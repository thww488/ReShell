cmake_minimum_required(VERSION 4.1.2)
project("reshell")

add_library(reshell SHARED native-shell.cpp)

find_library(log-lib log)
target_link_libraries(reshell ${log-lib})