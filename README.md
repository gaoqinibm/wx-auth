# 授权中转站
第三方网页登陆授权，如微信网页授权,本程序实现中转站功能

### 实现过程
用户进入客户端（H5页面），进入登录页面，检测到为微信客户端，进行微信网页授权（实现自动登录或获取用户信息），首先跳转到本服务，本服务获取完微信授权信息后，将用户信息返回给客户端的登陆页。

### 使用方法
- 修改yml文件配置，假设该项目访问网址为 http://190gs90623.iok.la
- 完整的跳转网址例子，http://190gs90623.iok.la/wxlogin?targetUrl=http://preh5.psgxs.com 或 http://190gs90623.iok.la/wxlogin?targetUrl=http://preactivity.psgxs.com/index/turnTable
- targetUrl=后面为客户端地址，也就是登录页地址
- 需要对targetUrl=后面内容进行urlencode

### 前端调用
- 传入目标地址，即targetUrl

### 遇到的坑
- 公众号平台未设置
  公众号设置-功能设置-网页授权域名 是后端接口域名设置，不加http://或https://
  授权文件确保在标准域名(http://域名/文件名.txt)请求下可以打开