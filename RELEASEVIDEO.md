 ## 开放自定义MediaSource
 ### 1.实例化
 ```
 //实例化
  MediaSourceBuilder  mediaSourceBuilder=new MediaSourceBuilder(this,new DataSource(getApplication()));
  exoPlayerManager = new ExoUserPlayer(this,mediaSourceBuilder, videoPlayerView);
 ```
 ### 2.使用自定义MediaSource 代码如下
 * 1加载字幕文件 
 ```
 //构建视频媒体资源.
 MediaSource videoSource = new ExtractorMediaSource(videoUri, ...);
 //构建子标题媒体源
 Format subtitleFormat = Format.createTextSampleFormat(
     null, // 跟踪的标识符。可能是null。
     MimeTypes.APPLICATION_SUBRIP, // mime类型。必须正确设置
     selectionFlags, // 跑道的选择标志。
     language); // 字幕语言。可能是null。
     
 MediaSource subtitleSource = new SingleSampleMediaSource(subtitleUri, dataSourceFactory, subtitleFormat, C.TIME_UNSET);
 // 播放带有副标题的视频
 MergingMediaSource mergedSource = new MergingMediaSource(videoSource, subtitleSource);
  
      //设置
       mediaSourceBuilder.setMediaSource(mergedSource);
     
 ```
 * 2 播放的视频序列
   ```
   MediaSource firstSource = new ExtractorMediaSource(firstVideoUri, ...);
   MediaSource secondSource = new ExtractorMediaSource(secondVideoUri, ...);
   //播放第一个视频，然后是第二个视频.
   ConcatenatingMediaSource concatenatedSource =
       new ConcatenatingMediaSource(firstSource, secondSource);
   
    mediaSourceBuilder.setMediaSource(mergedSource);
   ```
 * 3.高级构成互相嵌套
   可以进一步组合使用的复合mediasources更不寻常的案件。给定两个视频A和B，下面的示例演示如何loopingmediasourceconcatenatingmediasource和可一起使用来播放所述序列(a，a，b)。
 ```
 MediaSource firstSource = new ExtractorMediaSource(firstVideoUri, ...);
 MediaSource secondSource = new ExtractorMediaSource(secondVideoUri, ...);
 // Plays the first video twice.
 LoopingMediaSource firstSourceTwice = new LoopingMediaSource(firstSource, 2);
 // Plays the first video twice, then the second video.
 ConcatenatingMediaSource concatenatedSource =
     new ConcatenatingMediaSource(firstSourceTwice, secondSource)
 ```
 或者 
 以下示例是等效的，表明可以存在一种以上的方式实现相同的结果。
 ````
 MediaSource firstSource = new ExtractorMediaSource(firstVideoUri, ...);
 MediaSource secondSource = new ExtractorMediaSource(secondVideoUri, ...);
 // Plays the first video twice, then the second video.
 ConcatenatingMediaSource concatenatedSource =
     new ConcatenatingMediaSource(firstSource, firstSource, secondSource);
 ````
### 3.[更多MediaSource使用→戳我查看](https://google.github.io/ExoPlayer/guide.html)

### 4.使用自定义MediaSource注意实现：
 * 1.调用方法 mediaSourceBuilder.setMeiDiaUri()方法，就不要使用 exoPlayerManager.setPlayerUri()方法，二者选其一就可以。
 * 2.使用自定义MediaSourceBuilder是不会自动播放视频，必须说动调用 exoPlayerManager.startPlayer()。开始播放视频。
 * 2.使用自定义MediaSourceBuilder是不会自动播放视频，必须说动调用 exoPlayerManager.startPlayer()。开始播放视频。
 * 2.使用自定义MediaSourceBuilder是不会自动播放视频，必须说动调用 exoPlayerManager.startPlayer()。开始播放视频。
 * 3.重要的事说三遍。
 
 
 
  
 