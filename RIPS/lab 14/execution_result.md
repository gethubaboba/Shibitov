# Laboratory work 14. Execution result

The project implements a Java socket-based distributed key-value storage.

Verification command:

```powershell
javac -d build src\*.java
java -cp build DemoLauncher full
java -cp build DemoLauncher star
java -cp build DemoLauncher tree
```

## Output

```text
Topology: FULL
Node 5201 started: alpha=red, peers=[5202, 5203, 5204, 5205]
Node 5202 started: beta=green, peers=[5201, 5203, 5204, 5205]
Node 5203 started: gamma=blue, peers=[5201, 5202, 5204, 5205]
Node 5204 started: delta=yellow, peers=[5201, 5202, 5203, 5205]
Node 5205 started: omega=white, peers=[5201, 5202, 5203, 5204]
Client -> node 5201, key=alpha: FOUND alpha=red at node 5201
Client -> node 5201, key=omega: FOUND omega=white at node 5205
Client -> node 5203, key=delta: FOUND delta=yellow at node 5204
Client -> node 5205, key=missing: NOT_FOUND missing

Topology: STAR
Node 5201 started: alpha=red, peers=[5202, 5203, 5204, 5205]
Node 5202 started: beta=green, peers=[5201]
Node 5203 started: gamma=blue, peers=[5201]
Node 5204 started: delta=yellow, peers=[5201]
Node 5205 started: omega=white, peers=[5201]
Client -> node 5201, key=alpha: FOUND alpha=red at node 5201
Client -> node 5201, key=omega: FOUND omega=white at node 5205
Client -> node 5203, key=delta: FOUND delta=yellow at node 5204
Client -> node 5205, key=missing: NOT_FOUND missing

Topology: TREE
Node 5201 started: alpha=red, peers=[5202, 5203]
Node 5202 started: beta=green, peers=[5201, 5204, 5205]
Node 5203 started: gamma=blue, peers=[5201]
Node 5204 started: delta=yellow, peers=[5202]
Node 5205 started: omega=white, peers=[5202]
Client -> node 5201, key=alpha: FOUND alpha=red at node 5201
Client -> node 5201, key=omega: FOUND omega=white at node 5205
Client -> node 5203, key=delta: FOUND delta=yellow at node 5204
Client -> node 5205, key=missing: NOT_FOUND missing
```

## Conclusion

The client can connect to any node. A node first checks its own key-value pair, then forwards the request to neighbors according to the selected topology. The visited-node set prevents cycles in the full and star topologies.
