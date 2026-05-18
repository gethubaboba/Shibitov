#ifndef NOTESMODEL_H
#define NOTESMODEL_H

#include <QAbstractListModel>
#include <QVariantList>

class DatabaseManager;

class NotesModel : public QAbstractListModel
{
    Q_OBJECT
public:
    enum Roles {
        IdRole = Qt::UserRole + 1,
        TitleRole,
        ContentRole,
        CreatedAtRole
    };

    explicit NotesModel(DatabaseManager *databaseManager, QObject *parent = nullptr);

    int rowCount(const QModelIndex &parent = QModelIndex()) const override;
    QVariant data(const QModelIndex &index, int role = Qt::DisplayRole) const override;
    QHash<int, QByteArray> roleNames() const override;

    Q_INVOKABLE void loadNotes();
    Q_INVOKABLE bool addNote(const QString &title, const QString &content);
    Q_INVOKABLE bool removeNote(int id);

private:
    DatabaseManager *m_databaseManager = nullptr;
    QVariantList m_notes;
};

#endif
