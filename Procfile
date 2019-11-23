web: java $JAVA_OPTS \
    -Dserver.port=$PORT \
    -Dspring.profiles.active=heroku \
    -jar target/*.jar \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+UseCGroupMemoryLimitForHeap