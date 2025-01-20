# Variables for directories and files
CLASS_INPUT = ./src/main/java/org/domain/classes/*.java \
              ./src/main/java/org/domain/exceptions/*.java \
              ./src/main/java/org/domain/controllers/*.java \
              ./src/main/java/org/domain/types/*.java \
              ./src/main/java/org/domain/algorithms/*.java \
              ./src/main/java/org/persistence/controllers/*.java \
              ./src/main/java/org/presentation/controllers/*.java \
              ./src/main/java/org/presentation/utils/*.java \
              ./src/main/java/org/presentation/views/*.java

DRIVER_INPUT = ./src/main/java/org/presentation/drivers/*.java
CLASS_OUTPUT = ../EXE/
JAR_OUTPUT = ../EXE/jars/
JUNIT_JARS = lib/mockito-core-4.9.0.jar:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:lib/junit-4.13.2-javadoc.jar:lib/byte-buddy-1.12.8.jar:lib/objenesis-3.2.jar

# Files for JUnit
JUNIT_TESTS = ./src/test/java/org/domain/classes/*.java \
              ./src/test/java/org/domain/algorithms/*.java

# Main rules
all: compile_classes compile_tests

# Create output directories if they do not exist
create_dirs:
	mkdir -p $(CLASS_OUTPUT)
	mkdir -p $(JAR_OUTPUT)

# Compile classes
compile_classes: create_dirs
	javac -d $(CLASS_OUTPUT) $(CLASS_INPUT) $(DRIVER_INPUT)

# Compile tests with JUnit 4
compile_tests: compile_classes
	javac -cp $(JUNIT_JARS):$(CLASS_OUTPUT) -d $(CLASS_OUTPUT) $(JUNIT_TESTS)

# Create jar for Driver principal
jarDomain: compile_classes
	javac -d $(CLASS_OUTPUT) $(CLASS_INPUT) ./src/main/java/org/drivers/DriverControllerDomain.java
	jar cvf $(JAR_OUTPUT)/DriverControllerDomain.jar -C $(CLASS_OUTPUT) .

jarPresentation: compile_classes
	javac -d $(CLASS_OUTPUT) $(CLASS_INPUT) ./src/main/java/org/presentation/drivers/DriverPresentation.java
	jar cvf $(JAR_OUTPUT)/DriverPresentation.jar -C $(CLASS_OUTPUT) .

# Run the driver of the presentation layer
runDriver: jarPresentation
	java -cp $(JAR_OUTPUT)/DriverPresentation.jar org.presentation.drivers.DriverPresentation

jars: jarDomain jarPresentation

# Run the driver of the controller domain
runDomainDriver: jarDomain
	java -cp $(JAR_OUTPUT)/DriverControllerDomain.jar org.drivers.DriverControllerDomain

# Execute unitary tests with JUnit 4 and Mockito 4.9.0
test: compile_tests
	java -cp $(JUNIT_JARS):$(CLASS_OUTPUT) org.junit.runner.JUnitCore \
	org.domain.classes.ProductListTest \
	org.domain.classes.ProductTest \
	org.domain.classes.ShelfIntegrationTest \
	org.domain.classes.ShelfTest \
	org.domain.classes.SimilarityTest \
	org.domain.algorithms.BruteForceAlgorithmTest \
	org.domain.algorithms.HillClimbingAlgorithmTest

# Clean classes
clean:
	rm -rf $(CLASS_OUTPUT)* $(JAR_OUTPUT)*

# Clean .class files
classclean:
	find $(CLASS_OUTPUT) -name "*.class" -type f -delete

# Clean all (classes and jars)
distclean: clean
	rm -rf $(JAR_OUTPUT)*

# Documentations
.PHONY: all compile_classes compile_tests jarDomain jarPresentation runDriver runDomainDriver test clean distclean