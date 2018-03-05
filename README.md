# Socket.io-FLSocketIM-Android
基于Socket.io Android即时通讯客户端 Android IM Client based on Socket.io

###### 写在前面：
iOS项目已长传数月，虽然项目还有许多问题，但是也获得一些关注，感谢朋友的支持，苦于精力有限，项目没有怎么维护。有朋友问到Android项目为何功能有限，所以花了一段时间学习了Android，重新构建了Android项目，作为Android初学者，项目中可能有许多不足的地方，希望关注的朋友能给我提出，也欢迎有人能够加入到项目中来。

##### [简书详情介绍地址](https://www.jianshu.com/p/cdb3b0301712)

###### Android代码地址:https://github.com/fengli12321/Socket.io-FLSocketIM-Android

###### iOS 代码地址:https://github.com/fengli12321/Socket.io-FLSocketIM-iOS
###### 服务器端代码实现参照:https://github.com/fengli12321/Socket.io-FLSocketIM-Server


### 实现功能
1. 视频通话
2. 文本发送
3. 图片发送（从相册选取，或者拍摄）
4. 语音发送
5. 定位发送



### 使用技术

#### 一、socket.io
[github地址](https://github.com/socketio/socket.io)



#### 二、Realm
数据库管理工具，面向对象编程，不用写复杂的SQL语句，对消息、会话等数据实现本地化

#### 三、webRTC
WebRTC，名称源自网页实时通信（Web Real-Time Communication）的缩写，简而言之它是一个支持网页浏览器进行实时语音对话或视频对话的技术。
它为我们提供了视频会议的核心技术，包括音视频的采集、编解码、网络传输、显示等功能，并且还支持跨平台：windows，linux，mac，android，iOS。
它在2011年5月开放了工程的源代码，在行业内得到了广泛的支持和应用，成为下一代视频通话的标准。

本项目视频通话逻辑完成参照自己项目的iOS端逻辑

下图为视频通话实现的流程图，具体逻辑请参照项目源码，VideoChatHelper工具类中实现

![视频通话流程图.png](./视频通话流程图.png)


### 关于服务器部分代码
该项目服务器部分是通过node.js搭建，node.js真的是一门非常强大的语言，而且简单易学，如果你有一点点js基础相信看懂服务器代码也没有太大问题！本人周末在家看了一天node.js就上手写服务器端代码，所以有时间真滴可以认真学习一下，以后写项目再也不用担心没有网络数据了，哈哈

### 项目安装

##### 1.Android
- 下载项目 编译
- 更改文件 UrlConstant 中 baseUrl 地址为服务器地址

##### 2.服务器部分
- 首先需要node.js环境
- 电脑安装MongoDB
- npm install 安装第三方
- brew install imagemagick
brew install graphicsmagick(服务器处理图片用到)

### 待实现功能
1. **群聊天** 后台已实现，Android客户端待实现
2. **短视频**发送与播放
3. **消息**详情查看
4. **用户头像**管理
5. **离线消息**拉取
6. **Android**推送和保活
