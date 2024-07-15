#!/bin/sh

IMAGE_TAG=$(date +%s)
IMAGE_TARGET_TAG=latest

mkdir -p ${PWD}/build/libs/cds

echo "Building the prebuild image, the tag is: $IMAGE_TAG"
./gradlew jibDockerBuild -Djib.to.image=spring-boot-aot-cds-gradle-jib-example-prebuild -Djib.to.tags=$IMAGE_TAG

echo "Running the prebuild image to prepare the CDS archive"
docker run -w /app -ti --entrypoint=/opt/java/openjdk/bin/java \
  -v ${PWD}/build/libs/cds:/cds spring-boot-aot-cds-gradle-jib-example-prebuild:$IMAGE_TAG -XX:ArchiveClassesAtExit=/cds/application.jsa \
  -Dspring.context.exit=onRefresh \
  -cp "@jib-classpath-file" io.github.artemptushkin.performance.SpringBootAotCdsGradleJibExampleApplicationKt || true

echo "Building the final image, the tag is: $IMAGE_TAG"
./gradlew jibDockerBuild \
  -Djib.to.image=spring-boot-aot-cds-gradle-jib-example \
  -Djib.container.jvmFlags="-Dspring.aot.enabled=true,-Xshare:on,-XX:SharedArchiveFile=/cds/application.jsa" \
  -Djib.to.tags=$IMAGE_TAG,$IMAGE_TARGET_TAG

echo "Image has been built, the tag is: $IMAGE_TAG"
