import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

ApplicationWindow {
    id: window
    width: 390
    height: 720
    visible: true
    title: "SQLite Notes"

    Rectangle {
        anchors.fill: parent
        color: "#F6F8FB"

        ColumnLayout {
            anchors.fill: parent
            anchors.margins: 18
            spacing: 12

            Text {
                text: "Заметки SQLite"
                font.pixelSize: 28
                font.bold: true
                color: "#172033"
                Layout.fillWidth: true
            }

            TextField {
                id: titleField
                Layout.fillWidth: true
                placeholderText: "Заголовок"
            }

            TextArea {
                id: contentField
                Layout.fillWidth: true
                Layout.preferredHeight: 90
                placeholderText: "Текст заметки"
                wrapMode: TextArea.Wrap
            }

            Button {
                text: "Добавить"
                Layout.fillWidth: true
                onClicked: {
                    if (notesModel.addNote(titleField.text, contentField.text)) {
                        titleField.text = ""
                        contentField.text = ""
                    } else {
                        errorDialog.open()
                    }
                }
            }

            ListView {
                id: listView
                Layout.fillWidth: true
                Layout.fillHeight: true
                spacing: 10
                clip: true
                model: notesModel

                delegate: Rectangle {
                    width: listView.width
                    height: noteColumn.implicitHeight + 24
                    radius: 16
                    color: "#FFFFFF"
                    border.color: "#DDE5F0"

                    ColumnLayout {
                        id: noteColumn
                        anchors.verticalCenter: parent.verticalCenter
                        anchors.left: parent.left
                        anchors.right: parent.right
                        anchors.margins: 14
                        spacing: 6

                        Text {
                            text: title
                            font.pixelSize: 18
                            font.bold: true
                            color: "#263238"
                            Layout.fillWidth: true
                            wrapMode: Text.WordWrap
                        }

                        Text {
                            text: content
                            font.pixelSize: 15
                            color: "#455A64"
                            Layout.fillWidth: true
                            wrapMode: Text.WordWrap
                            visible: content.length > 0
                        }

                        RowLayout {
                            Layout.fillWidth: true

                            Text {
                                text: createdAt
                                font.pixelSize: 12
                                color: "#78909C"
                                Layout.fillWidth: true
                            }

                            Button {
                                text: "Удалить"
                                onClicked: notesModel.removeNote(noteId)
                            }
                        }
                    }
                }
            }
        }
    }

    Dialog {
        id: errorDialog
        title: "Ошибка"
        modal: true
        standardButtons: Dialog.Ok
        Text { text: "Заголовок не должен быть пустым" }
    }
}
