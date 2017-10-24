clean:
	rm -fr target

.PHONY: test
test:
	sbt test it:test

.PHONY: run
run:
	sbt run "com.n26.challenge.Main"

