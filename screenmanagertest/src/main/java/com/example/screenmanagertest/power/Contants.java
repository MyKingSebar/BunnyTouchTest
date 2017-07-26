package com.example.screenmanagertest.power;

public class Contants {
	public static int num = 0;
	
	/* level */
	public static int INFO = 0;
	public static int WARN = 1;
	public static int ERROR = 2;
	/* level */
	
	/* type */
	// INFO
	/**
	 * ["消息"]
	 */
	public static int OtherInfo = 0;
	/**
	 * ["开始播出单内所有文件下载"]
	 */
	public static int ReadyDownloadFiles = 10;
	/**
	 * ["文件下载完成"]FTP下载成功时记录 <PARAM><FILE>文件路径</FILE></PARAM>
	 */
	public 	static int DownloadFile = 11;
	/**
	 * [("播出单内所有文件下载完成")]下载成功时记录
	 */
	public 	static int AllFilesReady = 12;
	/**
	 * [(""文件删除"")] <PARAM><FILE>文件路径</FILE></PARAM>
	 */
	public static int FileDeleted = 13;
	/**
	 * [(""播放节目开始"")] <PARAM><ID>节目ID</ID></PARAM>
	 */
	public 	static int PlayProgramStart = 22;
	/**
	 * [(""播放节目结束"")] <PARAM><ID>节目ID</ID></PARAM>
	 */
	public static int PlayProgramEnd = 23;
	/**
	 * [(""播放文件开始"")] <PARAM><ID>媒体ID</ID></PARAM>
	 */
	public static int PlayMediaStart = 24;
	/**
	 * [(""播放文件结束"")] <PARAM><ID>媒体ID</ID></PARAM>
	 */
	public static int PlayMediaEnd = 25;
	
	// WARN
	/** [("警告")] */
	public static int OtherWarn = 100;
	/**
	 * [(""节目XML验证失败"")] 下载时记录
	 * <PARAM><ID>节目ID</ID><CURR>当前验证号</CURR><EXPECT>期望验证号</EXPECT></PARAM>
	 */
	public static int ProgramVerifyFail = 103;
	/**
	 * [(""播放文件验证失败"")]"")]下载时记录
	 * <PARAM><ID>文件ID</ID><FILE>文件路径</FILE >
	 * <CURR>当前验证号</CURR><EXPECT>期望验证号</EXPECT ></PARAM>
	 */
	public 	static int PlayFileVerifyFail = 104;
	/**
	 * [(""播放文件解码失败"")] <PARAM><ID>文件ID</ID><FILE>文件路径</FILE>
	 * <REASON >能提供的原因(不能提供可以忽略该参数)</REASON></PARAM>
	 */
	public static int PlayFileDecodeFail = 105;
	/**
	 * [(""播放文件不支持"")] <PARAM><ID>文件ID</ID><FILE>文件路径</FILE>
	 * <REASON >能提供的原因(不能提供可以忽略该参数)</REASON>< /PARAM>
	 */
	public static int PlayFileNotSupport = 106;/*  */
	/**
	 * [(""FTP登陆失败"")]
	 * <PARAM><FTP>请求的FTP地址(格式:ftp://用户名:密码@IP:端口)/路径</FTP></PARAM>
	 */
	public static int FtpServerFail = 120;/*  */
	/**
	 * [(""FTP文件不存在"")]//FTP下载失败时记录
	 * <PARAM><FTP>请求的FTP地址(格式:ftp://用户名:密码@IP:端口)/路径</FTP></PARAM>
	 */
	public static int FtpFileNotExists = 121;
	/**
	 * [(""Url资源请求失败"")] <PARAM><URL>提供的URL地址</URL></PARAM>
	 */
	public static int UrlRequestFail = 122;
	/**
	 * 
	 */
	public 	static int StorageFull = 150;

	// ERROR
	/**
	 * [("错误")]
	 */
	public static int OtherError = 200;
	/**
	 * [("存储器错误")]
	 * <PARAM><CODE>错误号</CODE></PARAM>
	 */
	public static int StorageError = 201;
	/**
	 * [("通讯错误")] 
	 * <PARAM><CODE>错误号</CODE></PARAM>
	 */
	public static int CommunicationError = 202;
	/**
	 * [("下载错误")]
	 * <PARAM><CODE>错误号</CODE></PARAM>
	 */
	public static int DownloaderError = 203;
	/**
	 * [("播放错误")]
	 * <PARAM><CODE>错误号</CODE></PARAM>
	 */
	public static int PlayerError = 204;
	/* type */
	
	/*	remark(播放结束原因)	*/
	public static final String DOWNPAUSE="0";//按下暂停键
	public static final String DOWNMENU="1";//按下菜单
	public static final String BACKHOME="2";//返回主页面
	public static final String NOMALSTOP="3";//正常播放停止
	public static final String ERRORSTOP="4";//发生错误终止
	public static final String CHANGEPROGRAM="5";//改变节目
	/*	remark	*/
	
	public static final String POSTER_PACKAGENAME = "com.youngsee.posterdisplayer";
	
	public static final String SETTING_PACKAGENAME = "com.android.settings";
	public static final String FILEBROWSER_PACKAGENAME_DEFAULT = "com.softwinner.TvdFileManager";
	public static final String FILEBROWSER_PACKAGENAME_ES = "com.estrongs.android.pop";	
	public static final String ENVMANAGER_PACKAGENAME = "com.youngsee.envservice";
	
	public static final String UDISK_NAME_PREFIX1 = "usb";
	public static final String UDISK_NAME_PREFIX2 = "sd"; 

	public static final String TIME_ZONE_CHINA =  "Asia/Shanghai";
}
