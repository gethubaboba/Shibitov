#include "DatabaseManager.h"

#include <QSqlQuery>
#include <QSqlError>
#include <QSqlRecord>
#include <QDateTime>
#include <QVariantMap>
#include <QUuid>
#include <QDebug>

DatabaseManager::DatabaseManager(const QString &databasePath, QObject *parent)
    : QObject(parent),
      m_connectionName(QUuid::createUuid().toString(QUuid::WithoutBraces)),
      m_databasePath(databasePath)
{
}

DatabaseManager::~DatabaseManager()
{
    const QString connection = m_connectionName;
    if (QSqlDatabase::contains(connection)) {
        QSqlDatabase::database(connection).close();
        QSqlDatabase::removeDatabase(connection);
    }
}

bool DatabaseManager::openDatabase()
{
    QSqlDatabase db = QSqlDatabase::addDatabase("QSQLITE", m_connectionName);
    db.setDatabaseName(m_databasePath);

    if (!db.open()) {
        m_lastError = db.lastError().text();
        qWarning() << "Database open error:" << m_lastError;
        return false;
    }

    return true;
}

bool DatabaseManager::createTables()
{
    QSqlQuery query(QSqlDatabase::database(m_connectionName));
    const QString sql = R"(
        CREATE TABLE IF NOT EXISTS notes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            content TEXT,
            created_at TEXT NOT NULL
        )
    )";

    if (!query.exec(sql)) {
        m_lastError = query.lastError().text();
        qWarning() << "Create table error:" << m_lastError;
        return false;
    }

    return true;
}

bool DatabaseManager::addNote(const QString &title, const QString &content)
{
    if (title.trimmed().isEmpty()) {
        m_lastError = "Title must not be empty";
        qWarning() << m_lastError;
        return false;
    }

    QSqlQuery query(QSqlDatabase::database(m_connectionName));
    query.prepare("INSERT INTO notes(title, content, created_at) VALUES(:title, :content, :created_at)");
    query.bindValue(":title", title.trimmed());
    query.bindValue(":content", content.trimmed());
    query.bindValue(":created_at", QDateTime::currentDateTime().toString(Qt::ISODate));

    if (!query.exec()) {
        m_lastError = query.lastError().text();
        qWarning() << "Insert note error:" << m_lastError;
        return false;
    }

    return true;
}

bool DatabaseManager::removeNote(int id)
{
    QSqlQuery query(QSqlDatabase::database(m_connectionName));
    query.prepare("DELETE FROM notes WHERE id = :id");
    query.bindValue(":id", id);

    if (!query.exec()) {
        m_lastError = query.lastError().text();
        qWarning() << "Delete note error:" << m_lastError;
        return false;
    }

    return query.numRowsAffected() > 0;
}

QVariantList DatabaseManager::getNotes() const
{
    QVariantList notes;
    QSqlQuery query(QSqlDatabase::database(m_connectionName));

    if (!query.exec("SELECT id, title, content, created_at FROM notes ORDER BY id DESC")) {
        m_lastError = query.lastError().text();
        qWarning() << "Select notes error:" << m_lastError;
        return notes;
    }

    while (query.next()) {
        QVariantMap note;
        note["id"] = query.value("id");
        note["title"] = query.value("title");
        note["content"] = query.value("content");
        note["created_at"] = query.value("created_at");
        notes.append(note);
    }

    return notes;
}

QString DatabaseManager::lastError() const
{
    return m_lastError;
}
