#include "GameViewModel.h"
#include "GameSettings.h"

#include <QDebug>
#include <QRandomGenerator>

GameViewModel::GameViewModel(GameSettings *settings, QObject *parent)
    : QObject(parent),
      m_settings(settings)
{
    startNewGame();
}

QString GameViewModel::message() const { return m_message; }
int GameViewModel::attempts() const { return m_attempts; }
int GameViewModel::points() const { return m_points; }
bool GameViewModel::gameOver() const { return m_gameOver; }

void GameViewModel::startNewGame()
{
    m_targetNumber = QRandomGenerator::global()->bounded(1, 101);
    m_attempts = 0;
    m_points = 0;
    setGameOver(false);
    setMessage(tr("Введите число от 1 до 100"));
    emit attemptsChanged();
    emit pointsChanged();
}

void GameViewModel::setTargetNumberForTest(int value)
{
    if (value < 1) value = 1;
    if (value > 100) value = 100;
    m_targetNumber = value;
}

void GameViewModel::checkGuess(const QString &value)
{
    if (m_gameOver) {
        setMessage(tr("Игра завершена. Начните новую игру."));
        return;
    }

    bool ok = false;
    const int guess = value.toInt(&ok);

    if (!ok) {
        qWarning() << "Invalid input:" << value;
        setMessage(tr("Ошибка: введите целое число"));
        return;
    }

    if (guess < 1 || guess > 100) {
        qWarning() << "Value out of range:" << guess;
        setMessage(tr("Ошибка: число должно быть от 1 до 100"));
        return;
    }

    m_attempts++;
    emit attemptsChanged();

    if (guess < m_targetNumber) {
        setMessage(tr("Загаданное число больше"));
    } else if (guess > m_targetNumber) {
        setMessage(tr("Загаданное число меньше"));
    } else {
        m_points = qMax(1, (m_settings ? m_settings->maxAttempts() : 7) - m_attempts + 1) * 10;
        emit pointsChanged();

        if (m_settings && (m_settings->bestScore() == 0 || m_attempts < m_settings->bestScore())) {
            m_settings->setBestScore(m_attempts);
        }

        setGameOver(true);
        setMessage(tr("Вы угадали число"));
        emit gameFinished(tr("Победа за %1 попыток. Баллы: %2").arg(m_attempts).arg(m_points));
        return;
    }

    if (m_settings && m_attempts >= m_settings->maxAttempts()) {
        setGameOver(true);
        setMessage(tr("Попытки закончились"));
        emit gameFinished(tr("Игра окончена. Загаданное число: %1").arg(m_targetNumber));
    }
}

void GameViewModel::setMessage(const QString &message)
{
    if (m_message == message) return;
    m_message = message;
    emit messageChanged();
}

void GameViewModel::setGameOver(bool value)
{
    if (m_gameOver == value) return;
    m_gameOver = value;
    emit gameOverChanged();
}
