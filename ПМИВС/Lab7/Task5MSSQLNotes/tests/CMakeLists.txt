find_package(Qt6 REQUIRED COMPONENTS Test Core Sql)

qt_add_executable(tst_database
    tst_database.cpp
    ../src/DatabaseManager.h
    ../src/DatabaseManager.cpp
)

target_link_libraries(tst_database PRIVATE Qt6::Test Qt6::Core Qt6::Sql)
add_test(NAME tst_database COMMAND tst_database)
