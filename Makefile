DOCKER_DIR := /challenge
DOCKER_RUN := docker run -v `pwd`:$(DOCKER_DIR) -w $(DOCKER_DIR) -p 8080:8080 --rm hseeberger/scala-sbt

clean:
	rm -fr target

.PHONY: test
test:
	sbt test it:test

.PHONY: test-docker
test-docker:
	$(DOCKER_RUN) sbt test it:test

.PHONY: run
run:
	sbt run

.PHONY: run-docker
run-docker:
	$(DOCKER_RUN) sbt run
