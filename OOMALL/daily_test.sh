#!bin/bash
## 将文件结尾从CRLF改为LF，解决了cd 错误问题
time=$(date "+%Y-%m-%d-%H-%M-%S")
origin_dir='oomall-testreport'
daily_dir='daily-report/'$time
echo $daily_dir


cd /home/mingqiu/privilegegateway
git checkout annotation/pom.xml
git checkout privilegeservice/pom.xml
git pull

echo '-------------------initializing privilege database-------------------------'
cd /home/mingqiu/privilegegateway/sql
mysql -h 172.16.1.254 -udbuser -p12345678 -D privilege_gateway < privilegegateway-batch.sql

echo '-------------------building annotation-------------------------'
cd /home/mingqiu/privilegegateway/annotation
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean install
mvn site:site site:deploy

cd /home/mingqiu/privilegegateway
mvn install -Dmaven.test.skip=true

echo '-------------------building privilegeservice-------------------------'
cd /home/mingqiu/privilegegateway/privilegeservice
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean package
mvn site:site site:deploy
curl --user ooad_javaee:$1 -T /home/mingqiu/privilegegateway/privilegeservice/target/privilegeservice-0.1.1-SNAPSHOT.jar http://172.16.4.1/webdav/deploy/

echo '-------------------building gateway-------------------------'
cd /home/mingqiu/privilegegateway/gateway
mvn clean package
curl --user ooad_javaee:$1 -T /home/mingqiu/privilegegateway/gateway/target/gateway-0.1.1-SNAPSHOT.jar http://172.16.4.1/webdav/deploy/

cd /home/mingqiu/oomall
git checkout core/pom.xml
git checkout comment/pom.xml
git checkout freight/pom.xml
git checkout shop/pom.xml
git checkout goods/pom.xml
git checkout coupon/pom.xml
git checkout activity/pom.xml
git pull

echo '-------------------initializing oomall database-------------------------'
cd /home/mingqiu/oomall/sql
mysql -h 172.16.1.254 -udbuser -p12345678 -D oomall < goods-batch.sql

echo '-------------------building core-------------------------'
cd /home/mingqiu/oomall/core
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean install
mvn site:site site:deploy

cd /home/mingqiu/oomall/
mvn install -Dmaven.test.skip=true

echo '-------------------building comment-------------------------'
cd /home/mingqiu/oomall/comment
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean package
mvn site:site site:deploy
curl --user ooad_javaee:$1 -T /home/mingqiu/oomall/comment/target/comment-0.0.1-SNAPSHOT.jar http://172.16.4.1/webdav/deploy/

echo '-------------------building feight-------------------------'
cd /home/mingqiu/oomall/freight
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean package
mvn site:site site:deploy
curl --user ooad_javaee:$1 -T /home/mingqiu/oomall/freight/target/freight-0.0.1-SNAPSHOT.jar http://172.16.4.1/webdav/deploy/

echo '-------------------building shop-------------------------'
cd /home/mingqiu/oomall/shop
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean package
mvn site:site site:deploy
curl --user ooad_javaee:$1 -T /home/mingqiu/oomall/shop/target/shop-0.0.1-SNAPSHOT.jar http://172.16.4.1/webdav/deploy/

echo '-------------------building goods-------------------------'
cd /home/mingqiu/oomall/goods
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean package
mvn site:site site:deploy
curl --user ooad_javaee:$1 -T /home/mingqiu/oomall/goods/target/goods-0.0.1-SNAPSHOT.jar http://172.16.4.1/webdav/deploy/

echo '-------------------building coupon-------------------------'
cd /home/mingqiu/oomall/coupon
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean package
mvn site:site site:deploy
curl --user ooad_javaee:$1 -T /home/mingqiu/oomall/coupon/target/coupon-0.0.1-SNAPSHOT.jar http://172.16.4.1/webdav/deploy/

echo '-------------------building activity-------------------------'
cd /home/mingqiu/oomall/activity
sed -i 's#'''$origin_dir'''#'''$daily_dir'''#g' pom.xml
mvn clean package
mvn site:site site:deploy
curl --user ooad_javaee:$1 -T /home/mingqiu/oomall/activity/target/activity-0.0.1-SNAPSHOT.jar http://172.16.4.1/webdav/deploy/

curl --user ooad_javaee:$1 -T /home/mingqiu/logs/daily_test.log http://172.16.4.1/webdav/daily-report/$time/console.log

