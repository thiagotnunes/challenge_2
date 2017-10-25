# Challenge

Implementation of backend challenge.

## Local Execution

In order to run the application locally the following requirements must be met:

- `sbt >= 1.0.2` must be installed

### Running Tests

```bash
make test
```

### Running the Server

```bash
make run
```

This will bind the server to port `4567`.

#### Transaction Creation

```bash
curl -v -d '{"amount":12.3, "timestamp":1478192204000}' -H "Content-Type: application/json" -X POST http://localhost:4567/transactions
```

##### Responses

- `201` when transaction creation succeeded.
- `204` when transaction is already expired.
- `400` when json payload is malformed.
- `422` when amount is invalid (not a double or negative).
- `422` when timestamp is invalid (not a long).

#### Statistics Fetching

```bash
curl -v http://localhost:4567/statistics
```

##### Responses

- `200` with latest statistics payload.

## Containerized Execution

In order to run the application in a container form you only need to have docker installed. Please note that this will take time to start, since it has to download the docker image once, but sbt every time the container is recreated.

### Running Tests

```bash
make test-docker
```

### Running the Server

```bash
make run-docker
```

This will bind the server to port `4567` in the host machine. You can use the same commands as mentioned before.

## Design

The main challenge to be tackled was the `O(1)` statistics access for the `/statistics` endpoint. I chose to have a separate scheduler process that runs every `100` milliseconds and rebuilds an updated version of the statistics and cache it.
Since there were no requirements for the other endpoints, whenever a transaction is created, it is appended to an unsorted array. When the statistics are rebuilt, the expired transactions are discarded and the valid ones are used to calculate the statistics. This process takes `O(n)` time and space.

### Drawbacks

One drawback of the design choice made concerns the scheduled job to rebuild the cached statistics. Since it runs every `100` milliseconds, the cached version is, in the worse case, `99` milliseconds outdated.
If real time statistics consolidation is a big concern, we could reduce the interval of the scheduled job. However, this would mean more retention of resources, which could impact the endpoint's latency.

## Out of Scope

I have decided not to tackle the following problem:

- Statistics Overflow: this might happen if the sum surpasses `double` max value.

## External libraries

### [Finagle](https://twitter.github.io/finagle/guide/index.html)

I used `finagle` to implement the API endpoints. I have done any tuning of the HTTP server. This should be done if this service would run in production.

### [Play Json](https://www.playframework.com/documentation/2.6.x/ScalaJson)

I used `play-json` to serialize and deserialize json.

### [Quartz](http://www.quartz-scheduler.org/)

I used `quartz` to schedule the job to rebuild the statistics cache. No tuning has been done here as well.
