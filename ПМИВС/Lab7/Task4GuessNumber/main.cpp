#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>

#include "src/GameSettings.h"
#include "src/GameViewModel.h"
#include "src/LanguageManager.h"

int main(int argc, char *argv[])
{
    QGuiApplication app(argc, argv);
    QCoreApplication::setOrganizationName("BSU");
    QCoreApplication::setApplicationName("Task4GuessNumber");

    QQmlApplicationEngine engine;

    GameSettings settings;
    GameViewModel gameViewModel(&settings);
    LanguageManager languageManager(&app, &engine);

    engine.rootContext()->setContextProperty("gameViewModel", &gameViewModel);
    engine.rootContext()->setContextProperty("gameSettings", &settings);
    engine.rootContext()->setContextProperty("languageManager", &languageManager);

    QObject::connect(
        &engine,
        &QQmlApplicationEngine::objectCreationFailed,
        &app,
        []() { QCoreApplication::exit(-1); },
        Qt::QueuedConnection);

    engine.loadFromModule("Task4GuessNumber", "Main");

    return app.exec();
}
