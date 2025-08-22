.DEFAULT_GOAL := build-run
.PHONY: build setup clean install run run-dist test lint check-deps report

setup:
	chmod +x ./gradlew && ./gradlew wrapper --gradle-version 8.8

clean:
	chmod +x ./gradlew && ./gradlew clean

build:
	chmod +x ./gradlew && ./gradlew clean build --stacktrace

install:
	chmod +x ./gradlew && ./gradlew clean installDist

run-dist:
	chmod +x ./gradlew && ./build/install/app/bin/app

run:
	chmod +x ./gradlew && ./gradlew run

test:
	chmod +x ./gradlew && ./gradlew jacocoTestReport

lint:
	chmod +x ./gradlew && ./gradlew checkstyleMain

check-deps:
	chmod +x ./gradlew && ./gradlew dependencyUpdates -Drevision=release

report:
	chmod +x ./gradlew && ./gradlew jacocoTestReport

build-run: build run