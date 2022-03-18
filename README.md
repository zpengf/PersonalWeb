# PersonalWeb
本项目运行所需的环境及各种中间件配置：

    1.数据库 配置详见 各模块application.yml文件
    
    2.redis 配置详见 各模块application.yml系列文件
    
    3.fastDFS与ngix的结合 存一些头像图片等数据 配置文件详见各模块配置文件
    
    4.mq 做一些文章延时发布 执行下载组装html页面 配置在article article-html配置文件
    
    5.mongoDB 存放amdin人脸数据 配置详见各模块application.yml文件
    
    (注：application.yml是总配置文件 在不同的环境执行各自的配置文件 
        -dev位开发环境 -prd为生产正式环境 
      看需求改动一般需要改动ip地址 及各个 库 命名空间的名字)


注：文章模块使用了freemarker技术 静态页面 能方便搜索引擎爬取 
    1.最简单的方式 就是在管理员审核完文章后 直接freemarker输出到前端地址
    2.使用mongodb 审核完毕后组装好页面传到 gridfs保存 然后通过调接口下载相应地址
    3.使用mq mongodb 审核完毕后组装好页面传到 gridfs保存 设置好rabbitmq解藕 去下载到前端文件
    本项目三个方式都有体现 详见ArticleController.doReview() 当前应用第三种方式
    

    
项目前端文件与后端是分开运行 前端地址修改在app.js
如自己在本机玩一玩 最好使用虚拟机将数据库 redis fastDFS ngix mq 安装到不同的虚拟机 做到模拟真实公司开发环境
