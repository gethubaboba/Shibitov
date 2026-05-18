find_package(Qt6 REQUIRED COMPONENTS Test Core)

qt_add_executable(tst_game
    tst_game.cpp
    ../src/GameSettings.h
    ../src/GameSettings.cpp
    ../src/GameViewModel.h
    ../src/GameViewModel.cpp
)

target_link_libraries(tst_game PRIVATE Qt6::Test Qt6::Core)
add_test(NAME tst_game COMMAND tst_game)
