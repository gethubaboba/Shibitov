import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

ApplicationWindow {
    id: window
    width: 360
    height: 640
    visible: true
    title: "Task 2"

    Rectangle {
        anchors.fill: parent
        color: "#F4F7FB"

        ColumnLayout {
            anchors.centerIn: parent
            width: parent.width * 0.85
            spacing: 24

            Text {
                id: titleText
                text: "Простое мобильное приложение"
                font.pixelSize: 24
                font.bold: true
                color: "#1E2A38"
                horizontalAlignment: Text.AlignHCenter
                wrapMode: Text.WordWrap
                Layout.fillWidth: true
            }

            Text {
                id: messageText
                text: "Приложение создано с использованием Qt Creator и QML для Android."
                font.pixelSize: 17
                color: "#333333"
                horizontalAlignment: Text.AlignHCenter
                wrapMode: Text.WordWrap
                Layout.fillWidth: true
            }

            Button {
                id: actionButton
                text: "Изменить текст"
                Layout.fillWidth: true

                onClicked: {
                    messageText.text = "Кнопка нажата. Интерфейс реагирует на действие пользователя."
                }
            }

            Button {
                text: "Сбросить"
                Layout.fillWidth: true

                onClicked: {
                    messageText.text = "Приложение создано с использованием Qt Creator и QML для Android."
                }
            }
        }
    }
}