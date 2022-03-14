# PersonalWeb
本项目运行所需的环境及各种中间件：

    1.数据库 配置详见 service-user模块application.yml文件
    
    2.redis 配置详见 service-user模块application.yml系列文件
    (注：application.yml是总配置文件 在不同的环境执行各自的配置 -dev位开发环境 -prd为生产正式环境 -test为测试环境)
    
    3.fastDFS文件传输及ngix配置 配置文件详见files模块配置文件 主要修改对应的ip地址
    
    4.mq 
    
    

注：项目前端文件与后端是分开运行 前端地址修改在app.js
 如自己在本机玩一玩 最好通过虚拟机将数据库 redis fastDFS ngix mq 安装到不同的虚拟机 做到模拟真实公司开发环境
