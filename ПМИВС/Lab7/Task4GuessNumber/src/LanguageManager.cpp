#include "LanguageManager.h"

#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QDebug>

LanguageManager::LanguageManager(QGuiApplication *app, QQmlApplicationEngine *engine, QObject *parent)
    : QObject(parent), m_app(app), m_engine(engine)
{
}

void LanguageManager::setLanguage(const QString &languageCode)
{
    if (!m_app || !m_engine) return;

    m_app->removeTranslator(&m_translator);
    const QString path = QString(":/i18n/Task4GuessNumber_%1.qm").arg(languageCode);

    if (m_translator.load(path)) {
        m_app->installTranslator(&m_translator);
        m_engine->retranslate();
    } else {
        qWarning() << "Translation file not loaded:" << path;
    }
}
