#include <QtTest>
#include "../src/GameSettings.h"
#include "../src/GameViewModel.h"

class TestGameViewModel : public QObject
{
    Q_OBJECT

private slots:
    void initTestCase();
    void testInitialAttempts();
    void testInvalidInput();
    void testCorrectGuessFinishesGame();
    void testStartNewGameResetsAttempts();
};

void TestGameViewModel::initTestCase()
{
    QCoreApplication::setOrganizationName("BSU_TEST");
    QCoreApplication::setApplicationName("Task4GuessNumberTest");
}

void TestGameViewModel::testInitialAttempts()
{
    GameSettings settings;
    GameViewModel model(&settings);
    QCOMPARE(model.attempts(), 0);
    QVERIFY(!model.gameOver());
}

void TestGameViewModel::testInvalidInput()
{
    GameSettings settings;
    GameViewModel model(&settings);
    model.checkGuess("abc");
    QVERIFY(model.message().contains("Ошибка"));
    QCOMPARE(model.attempts(), 0);
}

void TestGameViewModel::testCorrectGuessFinishesGame()
{
    GameSettings settings;
    GameViewModel model(&settings);
    model.setTargetNumberForTest(42);
    model.checkGuess("42");
    QVERIFY(model.gameOver());
    QCOMPARE(model.attempts(), 1);
    QVERIFY(model.points() > 0);
}

void TestGameViewModel::testStartNewGameResetsAttempts()
{
    GameSettings settings;
    GameViewModel model(&settings);
    model.setTargetNumberForTest(42);
    model.checkGuess("1");
    QVERIFY(model.attempts() > 0);
    model.startNewGame();
    QCOMPARE(model.attempts(), 0);
    QVERIFY(!model.gameOver());
}

QTEST_MAIN(TestGameViewModel)
#include "tst_game.moc"
