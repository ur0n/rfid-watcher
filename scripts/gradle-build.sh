docker build --no-cache=true -f docker-gradle/Dockerfile  --tag aiph.work:5000/mpuarch/gradle .

docker push aiph.work:5000/mpuarch/gradle
