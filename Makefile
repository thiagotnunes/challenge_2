clean:
	rm -fr target

.PHONY: test
test:
	sbt test it:test

