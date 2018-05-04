 ### 三.列表
 
   1.列表播放，只能使用ManualPlayer,在你的VideoHolder
   *  1.在列表控件使用属性 ”app:controller_layout_id="@layout/simple_exo_playback＿list_view"“  //提供默列表控制布局
  
   *  2.player_list="true" 设置为true 开启列表模式
  
   *  3.设置列表item 没有播放完成当前视频播放进度,不然不会保存播放进度---> userPlayer.setTag(getAdapterPosition());
   
   *  3.设置列表item 没有播放完成当前视频播放进度,不然不会保存播放进度---> userPlayer.setTag(getAdapterPosition());
  
   *  3.设置列表item 没有播放完成当前视频播放进度,不然不会保存播放进度---> userPlayer.setTag(getAdapterPosition());
  
   *  4.demo:
       
       
              public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
              .......
              @Override
              public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                  View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video1, parent, false);
                  return new VideoViewHolder(itemView);
              }
              @Override
              public void onBindViewHolder(VideoViewHolder holder, int position) {
                  String video = mVideoList.get(position);
                  holder.bindData(video);
              }

              public class VideoViewHolder extends RecyclerView.ViewHolder {
                  ManualPlayer userPlayer;
                  VideoPlayerView playerView;
                  public VideoViewHolder(View itemView) {
                      super(itemView);
                      //初始化控件
                      playerView = (VideoPlayerView) itemView.findViewById(R.id.item_exo_player_view);
                      userPlayer = new ManualPlayer((Activity) mContext, playerView);
                  }
                 /***绑定数据源***/
                  public void bindData(String videoBean) {
                      userPlayer.setTitles("" + getAdapterPosition());
                      userPlayer.setPlayUri(videoBean);
                      //设置列表item播放当前视频播放进度.不然不会保存视频播放进度
                      userPlayer.setTag(helper.getAdapterPosition());
                      Glide.with(mContext) .load("....") .into(playerView.getPreviewImage());
                  }
              }
              
              
   >>注意 [更多adapter 实例请参考demo程序](https://github.com/yangchaojiang/yjPlay/tree/master/app/src/main/java/chuangyuan/ycj/yjplay/adapter)
              
  2.列表播放周期方法 列表在Activity或者Fragment  实现相应周期方法
  >> 在viewPager使用，不要在实现 Fragment onDestroy（）方法周期， onPause()也会释放资源。
  >> onDestroy 用户页面销毁处理,不是释放资源.
  >> onDestroy 用户页面销毁处理,不是释放资源.
  >> onDestroy 用户页面销毁处理,不是释放资源.
  
                      @Override
                      protected void onPause() {
                          super.onPause();
                          VideoPlayerManager.getInstance().onPause();
                      }
                      @Override
                       protected void onResume() {
                          super.onResume();
                          VideoPlayerManager.getInstance().onResume();
                      }
                      @Override
                       public void onConfigurationChanged(Configuration newConfig) {
                          //横竖屏切换
                           VideoPlayerManager.getInstance().onConfigurationChanged(newConfig);
                           super.onConfigurationChanged(newConfig);
                       }    
                                  
                      @Override
                      protected void onDestroy() {
                          super.onDestroy();
                          VideoPlayerManager.getInstance().onDestroy();
                      }
                      @Override
                      public void onBackPressed() {
                          //返回监听类
                          if (VideoPlayerManager.getInstance().onBackPressed()){
                              finish();
                          }
                      }

  
 
 
 
  
 