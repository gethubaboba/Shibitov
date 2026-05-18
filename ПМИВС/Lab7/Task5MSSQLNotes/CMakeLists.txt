cmake_minimum_required(VERSION 3.16)

project(Task5SQLiteNotes VERSION 1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_AUTOMOC ON)

find_package(Qt6 REQUIRED COMPONENTS Quick Core Sql)
qt_standard_project_setup(REQUIRES 6.8)

qt_add_executable(appTask5SQLiteNotes
    main.cpp
    src/DatabaseManager.h
    src/DatabaseManager.cpp
    src/NotesModel.h
    src/NotesModel.cpp
)

qt_add_qml_module(appTask5SQLiteNotes
    URI Task5SQLiteNotes
    VERSION 1.0
    QML_FILES
        Main.qml
)

target_link_libraries(appTask5SQLiteNotes
    PRIVATE Qt6::Quick Qt6::Core Qt6::Sql
)

include(GNUInstallDirs)
install(TARGETS appTask5SQLiteNotes
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

option(BUILD_TESTS "Build unit tests" ON)
if(BUILD_TESTS)
    enable_testing()
    add_subdirectory(tests)
endif()
