import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

ApplicationWindow {
    id: window
    width: 390
    height: 720
    visible: true
    title: "Profile App"

    Rectangle {
        anchors.fill: parent
        gradient: Gradient {
            GradientStop { position: 0.0; color: "#DDEBFF" }
            GradientStop { position: 0.55; color: "#F7FBFF" }
            GradientStop { position: 1.0; color: "#FFFFFF" }
        }

        Rectangle {
            width: parent.width * 0.88
            height: contentColumn.implicitHeight + 52
            radius: 28
            color: "#FFFFFF"
            border.color: "#D7E3F8"
            anchors.centerIn: parent

            ColumnLayout {
                id: contentColumn
                anchors.centerIn: parent
                width: parent.width * 0.86
                spacing: 18

                Rectangle {
                    width: 150
                    height: 150
                    radius: 75
                    clip: true
                    color: "#E3EEFD"
                    Layout.alignment: Qt.AlignHCenter

                    Image {
                        anchors.fill: parent
                        source: "qrc:/qt/qml/Task3ProfileApp/resources/Lab_3_Image.jpg"
                        fillMode: Image.PreserveAspectCrop
                    }
                }

                Text {
                    text: "@my_username"
                    font.pixelSize: 28
                    font.bold: true
                    color: "#1976D2"
                    horizontalAlignment: Text.AlignHCenter
                    Layout.fillWidth: true
                }

                Rectangle {
                    radius: 18
                    color: "#EEF5FF"
                    Layout.fillWidth: true
                    height: interestsText.implicitHeight + 28

                    Text {
                        id: interestsText
                        anchors.centerIn: parent
                        width: parent.width - 36
                        text: "Интересы:\n• мобильная разработка\n• UI/UX-дизайн\n• путешествия\n• музыка\n• фотография"
                        font.pixelSize: 17
                        color: "#1E2A38"
                        lineHeight: 1.18
                        wrapMode: Text.WordWrap
                    }
                }

                Button {
                    text: "Подписаться"
                    Layout.fillWidth: true
                    height: 48

                    onClicked: Qt.openUrlExternally("https://example.com/my_username")
                }
            }
        }
    }
}
