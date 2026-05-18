#ifndef DATABASEMANAGER_H
#define DATABASEMANAGER_H

#include <QObject>
#include <QVariantList>
#include <QSqlDatabase>

class DatabaseManager : public QObject
{
    Q_OBJECT
public:
    explicit DatabaseManager(const QString &databasePath, QObject *parent = nullptr);
    ~DatabaseManager();

    bool openDatabase();
    bool createTables();
    bool addNote(const QString &title, const QString &content);
    bool removeNote(int id);
    QVariantList getNotes() const;
    QString lastError() const;

private:
    QString m_connectionName;
    QString m_databasePath;
    mutable QString m_lastError;
};

#endif
