#ifndef GAMEVIEWMODEL_H
#define GAMEVIEWMODEL_H

#include <QObject>
#include <QString>

class GameSettings;

class GameViewModel : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QString message READ message NOTIFY messageChanged)
    Q_PROPERTY(int attempts READ attempts NOTIFY attemptsChanged)
    Q_PROPERTY(int points READ points NOTIFY pointsChanged)
    Q_PROPERTY(bool gameOver READ gameOver NOTIFY gameOverChanged)

public:
    explicit GameViewModel(GameSettings *settings, QObject *parent = nullptr);

    QString message() const;
    int attempts() const;
    int points() const;
    bool gameOver() const;

    Q_INVOKABLE void startNewGame();
    Q_INVOKABLE void checkGuess(const QString &value);

    // Метод для unit-тестов: позволяет задать число без случайности.
    void setTargetNumberForTest(int value);

signals:
    void messageChanged();
    void attemptsChanged();
    void pointsChanged();
    void gameOverChanged();
    void gameFinished(QString result);

private:
    GameSettings *m_settings = nullptr;
    int m_targetNumber = 1;
    int m_attempts = 0;
    int m_points = 0;
    bool m_gameOver = false;
    QString m_message;

    void setMessage(const QString &message);
    void setGameOver(bool value);
};

#endif
