#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include <QStandardPaths>
#include <QDir>

#include "src/DatabaseManager.h"
#include "src/NotesModel.h"

int main(int argc, char *argv[])
{
    QGuiApplication app(argc, argv);
    QCoreApplication::setOrganizationName("BSU");
    QCoreApplication::setApplicationName("Task5SQLiteNotes");

    const QString dataDir = QStandardPaths::writableLocation(QStandardPaths::AppDataLocation);
    QDir().mkpath(dataDir);
    const QString dbPath = dataDir + "/notes.sqlite";

    DatabaseManager databaseManager(dbPath);
    databaseManager.openDatabase();
    databaseManager.createTables();

    NotesModel notesModel(&databaseManager);
    notesModel.loadNotes();

    QQmlApplicationEngine engine;
    engine.rootContext()->setContextProperty("notesModel", &notesModel);

    QObject::connect(
        &engine,
        &QQmlApplicationEngine::objectCreationFailed,
        &app,
        []() { QCoreApplication::exit(-1); },
        Qt::QueuedConnection);

    engine.loadFromModule("Task5SQLiteNotes", "Main");

    return app.exec();
}
