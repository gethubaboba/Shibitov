cmake_minimum_required(VERSION 3.16)

project(Task3ProfileApp VERSION 1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(Qt6 REQUIRED COMPONENTS Quick)
qt_standard_project_setup(REQUIRES 6.8)

qt_add_executable(appTask3ProfileApp
    main.cpp
)

qt_add_qml_module(appTask3ProfileApp
    URI Task3ProfileApp
    VERSION 1.0
    QML_FILES
        Main.qml
    RESOURCES
        resources/profile.png
        icons/icon.png
        icons/icon@2x.png
        RESOURCES resources/Lab_3_Image.jpg
)

target_link_libraries(appTask3ProfileApp
    PRIVATE Qt6::Quick
)

include(GNUInstallDirs)
install(TARGETS appTask3ProfileApp
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
