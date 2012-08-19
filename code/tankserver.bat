@echo off
cd ./tank/dist
java -cp ./lib/mysql.jar;./lib/swt.jar;../../bin; org.torpidity.tank.base.TankServer
cd ../../