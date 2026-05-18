# Laboratory work 14

Distributed key-value storage based on Java sockets.

Each node stores one local key-value pair. A client may connect to any node and request a value by key. If the key is absent locally, the node forwards the request through configured peers.

Supported topologies:

- `full`: every node is connected with every other node;
- `star`: node 0 is the center;
- `tree`: binary tree links.

## Build

```powershell
javac -d build src\*.java
```

## Demo run

```powershell
java -cp build DemoLauncher full
java -cp build DemoLauncher star
java -cp build DemoLauncher tree
```

## Manual run

Start nodes in separate terminals:

```powershell
java -cp build NodeServer 5101 alpha red 5102,5103
java -cp build NodeServer 5102 beta green 5101,5103
java -cp build NodeServer 5103 gamma blue 5101,5102
```

Then start clients:

```powershell
java -cp build KeyValueClient localhost 5101 alpha
java -cp build KeyValueClient localhost 5101 gamma
```

