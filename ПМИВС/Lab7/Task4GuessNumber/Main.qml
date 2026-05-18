import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

ApplicationWindow {
    id: window
    width: 390
    height: 720
    visible: true
    title: qsTr("Угадай число")

    Rectangle {
        anchors.fill: parent
        color: "#F5F7FA"

        ColumnLayout {
            anchors.centerIn: parent
            width: parent.width * 0.88
            spacing: 14

            Text {
                text: qsTr("Угадай число")
                font.pixelSize: 30
                font.bold: true
                color: "#172033"
                horizontalAlignment: Text.AlignHCenter
                Layout.fillWidth: true
            }

            ComboBox {
                Layout.fillWidth: true
                textRole: "name"
                valueRole: "code"
                model: [
                    { name: "Русский", code: "ru" },
                    { name: "English", code: "en" },
                    { name: "Беларуская", code: "be" }
                ]
                onActivated: languageManager.setLanguage(currentValue)
            }

            TextField {
                id: nameField
                Layout.fillWidth: true
                placeholderText: qsTr("Имя игрока")
                text: gameSettings.userName
                onEditingFinished: gameSettings.setUserName(text)
            }

            SpinBox {
                id: attemptsBox
                from: 3
                to: 20
                value: gameSettings.maxAttempts
                editable: true
                Layout.fillWidth: true
                onValueModified: gameSettings.setMaxAttempts(value)
            }

            Text {
                text: gameViewModel.message
                font.pixelSize: 18
                color: "#263238"
                horizontalAlignment: Text.AlignHCenter
                wrapMode: Text.WordWrap
                Layout.fillWidth: true
            }

            TextField {
                id: inputField
                Layout.fillWidth: true
                placeholderText: qsTr("Введите число")
                inputMethodHints: Qt.ImhDigitsOnly
                enabled: !gameViewModel.gameOver
            }

            Button {
                id: checkButton
                text: qsTr("Проверить")
                Layout.fillWidth: true
                enabled: !gameViewModel.gameOver

                onClicked: {
                    gameViewModel.checkGuess(inputField.text)
                    pulseAnimation.start()
                }
            }

            Button {
                text: qsTr("Новая игра")
                Layout.fillWidth: true
                onClicked: {
                    inputField.text = ""
                    gameViewModel.startNewGame()
                }
            }

            Rectangle {
                Layout.fillWidth: true
                height: stats.implicitHeight + 24
                radius: 16
                color: "#FFFFFF"
                border.color: "#DDE5F0"

                Text {
                    id: stats
                    anchors.centerIn: parent
                    width: parent.width - 28
                    font.pixelSize: 15
                    color: "#37474F"
                    text: qsTr("Попытки: ") + gameViewModel.attempts
                          + "\n" + qsTr("Баллы: ") + gameViewModel.points
                          + "\n" + qsTr("Лучший результат: ") + gameSettings.bestScore
                          + "\n" + qsTr("Запусков приложения: ") + gameSettings.launchCount
                          + "\nID: " + gameSettings.applicationId
                    wrapMode: Text.WordWrap
                }
            }
        }
    }

    NumberAnimation {
        id: pulseAnimation
        target: checkButton
        property: "scale"
        from: 1.0
        to: 1.06
        duration: 120
        easing.type: Easing.InOutQuad
        onFinished: checkButton.scale = 1.0
    }

    Dialog {
        id: resultDialog
        title: qsTr("Результат")
        modal: true
        standardButtons: Dialog.Ok

        Text {
            id: resultText
            width: 260
            wrapMode: Text.WordWrap
        }
    }

    Connections {
        target: gameViewModel
        function onGameFinished(result) {
            resultText.text = result
            resultDialog.open()
        }
    }
}
