#！/bin/bash
# 输出日期
hello(){
  time=$(date "+%Y-%m-%d %H:%M:%S")
  echo $time
}

# 停止tomact
stop_tomcat(){
	/root/apache-tomcat-8.5.72/bin/./shutdown.sh
}

# 打包项目
package_code(){
	# 先进入notepad的代码目录下
	cd /root/LinuxExam/notepad/
	# maven clean
	mvn clean
	# 休眠两秒
	sleep 2
	# maven package
	mvn package
}

# 删除tomact的原来的war包,并复制进去新的war包
install_war(){
	# 删除原来tomcat的war包及同名解压目录
	rm -rf /root/apache-tomcat-8.5.72/webapps/notepad.war
	rm -rf /root/apache-tomcat-8.5.72/webapps/notepad
	# 复制新的war包
	cp /root/LinuxExam/notepad/target/notepad.war /root/apache-tomcat-8.5.72/webapps/
}

# 启动tomcat
start_tomcat(){
	/root/apache-tomcat-8.5.72/bin/./startup.sh
}

# 主方法
main(){
	hello && stop_tomcat && package_code && install_war && start_tomcat && echo "success！"
}

main
