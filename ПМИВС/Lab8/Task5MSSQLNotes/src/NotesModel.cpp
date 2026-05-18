#include "NotesModel.h"
#include "DatabaseManager.h"

#include <QVariantMap>

NotesModel::NotesModel(DatabaseManager *databaseManager, QObject *parent)
    : QAbstractListModel(parent), m_databaseManager(databaseManager)
{
}

int NotesModel::rowCount(const QModelIndex &parent) const
{
    if (parent.isValid()) return 0;
    return m_notes.count();
}

QVariant NotesModel::data(const QModelIndex &index, int role) const
{
    if (!index.isValid() || index.row() < 0 || index.row() >= m_notes.count()) {
        return QVariant();
    }

    const QVariantMap note = m_notes.at(index.row()).toMap();
    switch (role) {
    case IdRole: return note.value("id");
    case TitleRole: return note.value("title");
    case ContentRole: return note.value("content");
    case CreatedAtRole: return note.value("created_at");
    default: return QVariant();
    }
}

QHash<int, QByteArray> NotesModel::roleNames() const
{
    return {
        { IdRole, "noteId" },
        { TitleRole, "title" },
        { ContentRole, "content" },
        { CreatedAtRole, "createdAt" }
    };
}

void NotesModel::loadNotes()
{
    beginResetModel();
    m_notes = m_databaseManager ? m_databaseManager->getNotes() : QVariantList();
    endResetModel();
}

bool NotesModel::addNote(const QString &title, const QString &content)
{
    if (!m_databaseManager || !m_databaseManager->addNote(title, content)) {
        return false;
    }
    loadNotes();
    return true;
}

bool NotesModel::removeNote(int id)
{
    if (!m_databaseManager || !m_databaseManager->removeNote(id)) {
        return false;
    }
    loadNotes();
    return true;
}
