#include "GameSettings.h"

#include <QSettings>
#include <QUuid>
#include <QVariant>

GameSettings::GameSettings(QObject *parent)
    : QObject(parent)
{
    load();
    m_launchCount++;
    saveValue("launchCount", m_launchCount);
}

void GameSettings::load()
{
    QSettings settings;
    m_applicationId = settings.value("applicationId").toString();
    if (m_applicationId.isEmpty()) {
        m_applicationId = QUuid::createUuid().toString(QUuid::WithoutBraces);
        settings.setValue("applicationId", m_applicationId);
    }

    m_launchCount = settings.value("launchCount", 0).toInt();
    m_maxAttempts = settings.value("maxAttempts", 7).toInt();
    m_userName = settings.value("userName", "Player").toString();
    m_bestScore = settings.value("bestScore", 0).toInt();
}

void GameSettings::saveValue(const QString &key, const QVariant &value)
{
    QSettings settings;
    settings.setValue(key, value);
}

int GameSettings::launchCount() const { return m_launchCount; }
QString GameSettings::applicationId() const { return m_applicationId; }
int GameSettings::maxAttempts() const { return m_maxAttempts; }
QString GameSettings::userName() const { return m_userName; }
int GameSettings::bestScore() const { return m_bestScore; }

void GameSettings::setMaxAttempts(int value)
{
    if (value < 3) value = 3;
    if (value > 20) value = 20;
    if (m_maxAttempts == value) return;
    m_maxAttempts = value;
    saveValue("maxAttempts", m_maxAttempts);
    emit maxAttemptsChanged();
}

void GameSettings::setUserName(const QString &value)
{
    const QString normalized = value.trimmed().isEmpty() ? "Player" : value.trimmed();
    if (m_userName == normalized) return;
    m_userName = normalized;
    saveValue("userName", m_userName);
    emit userNameChanged();
}

void GameSettings::setBestScore(int value)
{
    if (value < 0) value = 0;
    if (m_bestScore == value) return;
    m_bestScore = value;
    saveValue("bestScore", m_bestScore);
    emit bestScoreChanged();
}

void GameSettings::resetStatistics()
{
    setBestScore(0);
}
