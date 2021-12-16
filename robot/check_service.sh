#!/bin/bash
#CPU使用率
CPU=`top -b -n 1| grep java |awk '{print $9}'`
#内存使用率
MEM=`top -b -n 1| grep java |awk '{print $10}'`
#CPU告警
if [ ${CPU%.*} -gt 1 ];then
     echo "告警！java进程的CPU使用率已超过1%，此时占用率："${CPU}"%" >> /root/LinuxExam/robot/log.txt  
     java -jar /root/LinuxExam/robot/dingdingr/out/artifacts/dingdingr_jar/dingdingr.jar
fi
#内存告警
if [ ${MEM%.*} -gt 1 ];then
     echo "告警！java进程的内存使用率已超过1%，此时占用率："${MEM}"%" >> /root/LinuxExam/robot/log.txt
     java -jar /root/LinuxExam/robot/dingdingr/out/artifacts/dingdingr_jar/dingdingr.jar
fi
