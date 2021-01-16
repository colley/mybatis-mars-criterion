@echo off    

echo deploy jar to depository

call mvn clean

echo clean Success

call mvn deploy -e -Dmaven.test.skip=true

echo deploy Success

pause