 ## 更新日志
 ### 1.9.93
   * 1.修复播放源为DASH类型返回错误[#62](https://github.com/yangchaojiang/yjPlay/issues/62)。
  
 ### 1.9.91
   * 1.增加列表到详情播放完美过滤。无需二次加载[#58](https://github.com/yangchaojiang/yjPlay/issues/58)。
   * 2.多线路选择增加高亮显示[#61](https://github.com/yangchaojiang/yjPlay/issues/61)。
   * 3.优化屏全屏处理。
   * 4.升级内核版本[#2.6.1](https://github.com/google/ExoPlayer/blob/release-v2/RELEASENOTES.md#261)
   * 5.修复bug[#31](https://github.com/yangchaojiang/yjPlay/issues/31)
   * 6.改进mediaSource实例化方式。
 ### 1.9.81
   * 1.新增加广告和多分辨率同时支持[#35](https://github.com/yangchaojiang/yjPlay/issues/35)。
   * 2.增加自定义预览布局设置，更容易实现自定义视频封面图UI样式。
   * 3.修改对视频链接类型判断bug[#42](https://github.com/yangchaojiang/yjPlay/issues/42)。
   * 4.优化广告可能出现没有切换视频下拖拽进度条快进，出现多次回调处理。
   * 5.修复播放完成后，可能按钮没有显示问题。
   * 6.优化视频图封面图代码控制，更简介处理。
   * 7.其他代码修复。 
   * 8.demo增加英语语言。
 ### 1.9.8
   * 1.增加离线下载助手(支持加密处理)。
   * 2.增加锁屏功能和控住布局显示和隐藏动画效果。
   * 3.升级内核版本[2.6.0](https://github.com/google/ExoPlayer/blob/release-v2/RELEASENOTES.md#260)
   * 4.增加全屏和返回按钮图标自定义。
   * 5.修复缓存问题实现真正实现离线缓存播放。
   * 6.修复bug[#31](https://github.com/yangchaojiang/yjPlay/issues/31)
   * 7.其他逻辑优化和代码分离。
   * 8.优化使用说明。
 ### 1.9.5
   * 1.分离核心处理非为精简版和完整版
   * 2.增加两种本地加密视频处理处理。
   * 3.[修复demo中全屏播放，左右滑动会导致app崩溃](https://github.com/yangchaojiang/yjPlay/issues/29) 
   * 4.全屏按钮显示不对.
 ### 1.7.0
   * 1.开放自定义MediaSource。
   * 2.开放手势布局自定义样式。
   * 3.优化播放管理类结构。 
   * 4.升级内核版本[r.2.5.4](https://github.com/google/ExoPlayer/blob/release-v2/RELEASENOTES.md#r254)
 ### 1.5.92
   * 1.优化多线路UI显示
   * 2.修复多线路可能会选择奔溃
   * 3.布局结构优化，布局层次少一级。
 ### 1.5.9
   * 1.增加广告视频处理和UI控制。
   * 2.增加视频倍数播放api。
   * 3.优化全屏按钮ui控制。
   * 4.增加手势操作设置api。
   * 5.点击开始播放视频占位图处理（提示了非wifi 提示不隐藏占位图）
   * 6.修复bug[ManualPlayer.reset();的时候提示错误](https://github.com/yangchaojiang/yjPlay/issues/23)
   * 7.修复其他已知问题.
  ### 1.5.7.2
   * 1.修复bug#20(https://github.com/yangchaojiang/yjPlay/issues/20)
   * 2.修复bug#19(https://github.com/yangchaojiang/yjPlay/issues/19)
   * 3.规范代码注释doc,增加的注解检查
  ### 1.5.7
   * 1.增加加载模式类型（下载网速模式和百分比模式）。
   * 2.删除无用string资源（减小100k左右）。
   * 3.优化 基类（ExoUserPlayer 与view）之间耦合度。
   * 4.优化视频播放时和暂停播放，手机屏幕常亮问题。
  ### 1.5.6
   * 1.增加新api方法（循环播放， 集合和数组，视频组合播放，播放状态，控制布局操作，等）。
   * 2.增加点击开始按钮自定义事件，（需求开始自己业务处理后，开始播放视频）。
   * 3.修复列表点击再次播放视频控制布局展示错误**bug**。
   * 4.优化默认数据源支持本地视频的资源。
   * 5.优化控制类和播放View的之间代码解耦,更简洁[ManualPlayer](https://github.com/yangchaojiang/yjPlay/blob/master/VideoPlayModule/src/main/java/chuangyuan/ycj/videolibrary/video/ManualPlayer.java)
   * 6.支持选择广告出现视频位置（不限于开头，也可以视频末尾）。
   * 7.修复其他已知问题(重播布局返回按钮不显示**bug**)，提高稳定性。
   ### 1.5.5
   * 1.升级内核版本[r.2.5.3](https://github.com/google/ExoPlayer/blob/release-v2/RELEASENOTES.md#r253)
   * 2.优化全屏切换代码处理，切回竖屏虚拟导航键不现实按钮问题。
   * 3.增加错误布局,重播布局，提示布局自定义，更灵活实现自己布局样式。
   * 4.优化GestureVideoPlayer 业务和view 解耦处理，更简洁，业务更清晰.。
   * 5.优化视频封面图问题，支持封面图 在控制布局下面和上面，自己选择合适.。
   * 6.增加默认封面图在控制布局上面。
   ### 1.5.4
   * 1.优化不播放的息屏
   * 2.调整返回返回键处理 exoPlayerManager.onBackPressed()和VideoPlayerManager.getInstance().onBackPressed()  返回为true 退出界面
   * 3.删除返回事件回调处理 改用 onBackPressed() 返回处理， true 正常出退出界面 false 切换到竖屏
   * 4 修复 1.5.2 和 1.5.1 无法引用问题
   ### 1.5.1
   * 1.修复列表播放缓慢滑动销毁，造成黑屏和控制布局错误等问题
   * 2.暴露获取进度条控件方法getTimeBar()
   * 3.修复其他问题等
   ### 1.4.9
   * 1.升级内核为[r.2.5.2](https://githhub.com/google/ExoPlayer/blob/release-v2/RELEASENOTES.md#r252)
   * 2.对齐android 依赖库版本对齐，移除"25.4.0"对齐"25.3.1"版本， "25.4.0"导致部分用户无法引用
   ### 1.4.8
   * 1 修改自定义加载源类，不再用单利模式,采用控件实使用进行调用
   ### 1.4.7
   * 1 修复1.4.6切换bug
   ### 1.4.6
   * 1 增加视频列表播放支持
   * 2 增加VideoPlayerManager 列表播放管理类
   * 3 增加自定义进度条控件
   * 4 修复bug
   ### 1.4.5
   * 1 增加视频缓存功能
   * 2 去掉ExoUserPlayer 构造方法设置uri
   * 3 修复已知问题
   * 4 增加自定义数据源工厂类，实现自己文件数据源类型
   * 5 升级内核版本
   ### 1.4.4
   * 1  修复线路切换文字不改变稳定
   * 2  增加线路提供方法。集合和数组
   * 3  提供布局设置水印,修复水印方法，去掉默认水印
   ### 1.4.3
   * 1  修改重新播放页面
   * 2  增加进度默认设置
   * 3  增加占位图设置
   * 4  修复全屏事问题
   ###  1.4.1
   * 1 修复其他问题,
   * 2 还原 自定义属性
   ### 1.4.0
   * 1 增加视频清晰度切换，在横屏
   * 2 修改手势类，之间业务剥离出来
   * 3 修复其他问题,
   * 4 升级内核版本，布局和业务分离处理
   * 5 重新整理项目结构，不兼容1.4.0 版本之前

   ### 1.3.0
   * 1.增加播放数据流量提醒框，增加网络变化监听
   * 2.toobar状态的隐藏和显示,  增加v7依赖
   * 3.直播隐藏进度条
   * 4.两个视频切换，广告视频，进度处理
   * 5.修复已知bug.简化处理