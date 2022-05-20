아직 등록된 정오표가 없습니다.

78 페이지  컨테이너 이미지 생성 부분에서 오자가 있습니다.  
. 이 빠져서 빌드가 안됩니다. 
. 을 추가해주어야 합니다.

(원문)
sudo docker build -t k8sbook/backend-app:1.0.0  --build-arg \
JAR_FILE=build/libs/backend-app-1.0.0.jar

(수정)
sudo docker build -t k8sbook/backend-app:1.0.0 . --build-arg \
JAR_FILE=build/libs/backend-app-1.0.0.jar

