Play WS Fakes
=============
[![Build Status](https://travis-ci.org/servicerocket-labs/play-ws-fakes.svg?branch=master)](https://travis-ci.org/servicerocket-labs/play-ws-fakes)
![GitHub Action](https://github.com/servicerocket-labs/play-ws-fakes/actions/workflows/ci.yml/badge.svg)

Fake Play WS request and responses for testing purposes.
### Usage
```
libraryDependencies += "com.servicerocket" %% "play-ws-fakes" % version
libraryDependencies += "com.typesafe.play" %% "play-ws" % version
```
Make sure to add a Play WS dependency explicitly,
if not already in the scope of your project, as none will be
provided transitively (provided scope).

Use in your tests:
```scala
import com.servicerocket.play.ws.fakes._
```
### Compatibility Matrix
|  Play WS Fakes | Play WS   | Scala  |
| -------------- | --------- | ------ |
| `0.1.0`        | `2.15.18` | `2.11` |
| `0.1.1`        | `2.6.11`  | `2.12` |
| `0.1.2`        | `2.6.11`  | `2.12` |
| `0.1.3`        | `2.6.11`  | `2.12` |
| `0.1.4`        | `2.7.3`   | `2.12` |