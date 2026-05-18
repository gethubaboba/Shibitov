#ifndef GAMESETTINGS_H
#define GAMESETTINGS_H

#include <QObject>
#include <QString>

class QSettings;

class GameSettings : public QObject
{
    Q_OBJECT
    Q_PROPERTY(int launchCount READ launchCount NOTIFY launchCountChanged)
    Q_PROPERTY(QString applicationId READ applicationId CONSTANT)
    Q_PROPERTY(int maxAttempts READ maxAttempts WRITE setMaxAttempts NOTIFY maxAttemptsChanged)
    Q_PROPERTY(QString userName READ userName WRITE setUserName NOTIFY userNameChanged)
    Q_PROPERTY(int bestScore READ bestScore WRITE setBestScore NOTIFY bestScoreChanged)

public:
    explicit GameSettings(QObject *parent = nullptr);

    int launchCount() const;
    QString applicationId() const;
    int maxAttempts() const;
    QString userName() const;
    int bestScore() const;

    Q_INVOKABLE void setMaxAttempts(int value);
    Q_INVOKABLE void setUserName(const QString &value);
    Q_INVOKABLE void setBestScore(int value);
    Q_INVOKABLE void resetStatistics();

signals:
    void launchCountChanged();
    void maxAttemptsChanged();
    void userNameChanged();
    void bestScoreChanged();

private:
    int m_launchCount = 0;
    QString m_applicationId;
    int m_maxAttempts = 7;
    QString m_userName = "Player";
    int m_bestScore = 0;

    void load();
    void saveValue(const QString &key, const QVariant &value);
};

#endif
