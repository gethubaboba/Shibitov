#ifndef LANGUAGEMANAGER_H
#define LANGUAGEMANAGER_H

#include <QObject>
#include <QTranslator>

class QGuiApplication;
class QQmlApplicationEngine;

class LanguageManager : public QObject
{
    Q_OBJECT
public:
    explicit LanguageManager(QGuiApplication *app, QQmlApplicationEngine *engine, QObject *parent = nullptr);

    Q_INVOKABLE void setLanguage(const QString &languageCode);

private:
    QGuiApplication *m_app = nullptr;
    QQmlApplicationEngine *m_engine = nullptr;
    QTranslator m_translator;
};

#endif
