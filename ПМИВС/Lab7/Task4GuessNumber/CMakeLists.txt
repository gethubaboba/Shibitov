cmake_minimum_required(VERSION 3.16)

project(Task4GuessNumber VERSION 1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_AUTOMOC ON)

find_package(Qt6 REQUIRED COMPONENTS Quick Core LinguistTools)
qt_standard_project_setup(REQUIRES 6.8)

qt_add_executable(appTask4GuessNumber
    main.cpp
    src/GameSettings.h
    src/GameSettings.cpp
    src/GameViewModel.h
    src/GameViewModel.cpp
    src/LanguageManager.h
    src/LanguageManager.cpp
)

qt_add_qml_module(appTask4GuessNumber
    URI Task4GuessNumber
    VERSION 1.0
    QML_FILES
        Main.qml
)

qt_add_translations(appTask4GuessNumber
    TS_FILES
        translations/Task4GuessNumber_ru.ts
        translations/Task4GuessNumber_en.ts
        translations/Task4GuessNumber_be.ts
    RESOURCE_PREFIX "/i18n"
)

target_link_libraries(appTask4GuessNumber
    PRIVATE Qt6::Quick Qt6::Core
)

include(GNUInstallDirs)
install(TARGETS appTask4GuessNumber
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

option(BUILD_TESTS "Build unit tests" ON)
if(BUILD_TESTS)
    enable_testing()
    add_subdirectory(tests)
endif()
