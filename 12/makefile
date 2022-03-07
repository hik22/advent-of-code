Solve.class: Solve.java
	javac Solve.java

.PHONY: test
test: Solve.class
	java Solve input

.PHONY: test-example
test-example: Solve.class
	java Solve input-example

.PHONY: test-example2
test-example2: Solve.class
	java Solve input-example2

.PHONY: clean
clean:
	rm -rf -- Solve.class
