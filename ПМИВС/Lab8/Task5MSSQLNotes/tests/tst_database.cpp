#include <QtTest>
#include "../src/DatabaseManager.h"

class TestDatabaseManager : public QObject
{
    Q_OBJECT

private slots:
    void testCreateTables();
    void testAddNote();
    void testRemoveNote();
    void testRejectEmptyTitle();
};

void TestDatabaseManager::testCreateTables()
{
    DatabaseManager db(":memory:");
    QVERIFY(db.openDatabase());
    QVERIFY(db.createTables());
}

void TestDatabaseManager::testAddNote()
{
    DatabaseManager db(":memory:");
    QVERIFY(db.openDatabase());
    QVERIFY(db.createTables());
    QVERIFY(db.addNote("Title", "Content"));
    QCOMPARE(db.getNotes().size(), 1);
}

void TestDatabaseManager::testRemoveNote()
{
    DatabaseManager db(":memory:");
    QVERIFY(db.openDatabase());
    QVERIFY(db.createTables());
    QVERIFY(db.addNote("Title", "Content"));
    const QVariantList notes = db.getNotes();
    const int id = notes.first().toMap().value("id").toInt();
    QVERIFY(db.removeNote(id));
    QCOMPARE(db.getNotes().size(), 0);
}

void TestDatabaseManager::testRejectEmptyTitle()
{
    DatabaseManager db(":memory:");
    QVERIFY(db.openDatabase());
    QVERIFY(db.createTables());
    QVERIFY(!db.addNote("", "Content"));
}

QTEST_MAIN(TestDatabaseManager)
#include "tst_database.moc"
