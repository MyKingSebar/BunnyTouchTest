//package com.example.screenmanagertest.screenmanager;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.example.screenmanagertest.MainActivity;
//import com.example.screenmanagertest.PosterApplication;
//import com.example.screenmanagertest.XMLpaser.XmlParser;
//import com.example.screenmanagertest.common.FileUtils;
//import com.example.screenmanagertest.common.Logger;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by admin on 2017-06-22.
// */
//
//public class ScreenManager2 {
//
//    private static final int IDLE_STATE = 0;
//    private static final int PLAYING_NORMAL_PROGRAM = 1;
//
//    private static final int NORMAL_PROGRAM = 1;
//
//    public static final String LOCAL_NORMAL_PLAYLIST_FILENAME = "playliste.xml";
//
//    private boolean mLoadProgramDone = false;
//    private boolean mStandbyScreenIsShow = false;
//
//    public Object mProgramFileLock = new Object();
//
//    // 当前播放的节目
//    private int mStatus = IDLE_STATE;
//    private ProgramInfo mNormalProgram = null;
//    private ArrayList<ProgramInfo> mNormalProgramInfoList = null;
//    private String mCurrentPgmVerifyCode = null;
//
//    // Define message Id
//    private static final int EVENT_SHOW_IDLE_PROGRAM = 0x8001;
//    private final static int EVENT_SHOW_NORMAL_PROGRAM = 0x8002;
//    private final static int EVENT_MEDIA_READY_SHOW_PROGRAM = 0x8004;
//
//    private Context mContext = null;
//    private ScreenDaemon         mScreenDaemonThread       = null;
//    private static ScreenManager2 mScreenManagerInstance = null;
//
//    // 节目信息
//    private final class ProgramInfo {
//        public String startDate = null;
//        public String endDate = null;
//        public String startTime = null;
//        public String endTime = null;
//        public String scheduleId = null;
//        public String schPri = null;
//        public String playbillId = null;
//        public String programId = null;
//        public String programName = null;
//        public String pgmFileName = null;
//        public String pgmPri = null;
//        public String verifyCode = null;
//        public long termTimePoint = 0;
//        public long breakPoint = 0;
//        public boolean playFinished = false;
//        public boolean ignoreDLLimit = false;
//        public ProgramLists programList = null;
//    }
//
//
//    private ScreenManager2(Context context) {
//        /*
//         * This Class is a single instance mode, and define a private constructor to avoid external use the 'new'
//         * keyword to instantiate a objects directly.
//         */
//        mContext = context;
//    }
//
//    public static ScreenManager2 createInstance(Context context) {
//        if (mScreenManagerInstance == null && context != null) {
//            mScreenManagerInstance = new ScreenManager2(context);
//        }
//        return mScreenManagerInstance;
//    }
//
//    public static ScreenManager2 getInstance() {
//        return mScreenManagerInstance;
//    }
//
//
//    private final class ScreenDaemon extends Thread {
//        private boolean mIsRun = false;
//
//        private String mNormalPgmFilePath = null;
//        private ArrayList<ProgramInfo> mNormalProgramInfoList = null;
//        private int mSamePriNormalPgmListIdx = 0;
//        private ArrayList<ProgramInfo> mSamePriNormalPgmList = null;
//
//        public void setRunFlag(boolean bIsRun) {
//            mIsRun = bIsRun;
//        }
//
//        public boolean getRunFlag() {
//            return mIsRun;
//        }
//
//        private String obtainNormalPgmFilePath() {
//            StringBuilder sb = new StringBuilder();
//            sb.append(PosterApplication.getProgramPath());
//            sb.append(File.separator);
//            sb.append("playlist");
//            sb.append(File.separator);
//            sb.append(LOCAL_NORMAL_PLAYLIST_FILENAME);
//            return sb.toString();
//        }
//
//
//        /*
//         * 从XML文件中获取节目清单
//         */
//        private ArrayList<ProgramInfo> getProgramScheduleFromXml(String filePath) {
//            ArrayList<ProgramInfo> programSchedule = null;
//
//            // 解析Schedule XML文件
//            ScheduleLists scheduList = null;
//            if (FileUtils.isExist(filePath)) {
//                scheduList = (ScheduleLists) XmlFileParse(filePath, ScheduleLists.class);
//            } else {
//                return null;
//            }
//
//            // 创建新的节目单
//            if (scheduList != null && scheduList.Schedule != null) {
//                programSchedule = new ArrayList<ProgramInfo>();
//                Schedules schedule = null;
//                Playbills playbill = null;
//                Programs program = null;
//                ProgramLists tmpPgmList = null;
//                ProgramInfo programInfo = null;
//                int md5Key = 0x10325476;
//                String md5Value = null;
//                String strPgmName = null;
//                StringBuilder sbPgmFileName = new StringBuilder();
//
//                // 遍历所有的Schedule
//                Log.i("jialei","scheduList.Schedule.size():"+scheduList.Schedule.size());
//                for (int i = 0; i < scheduList.Schedule.size(); i++) {
//                    // 获取schedule中的信息
//                    schedule = scheduList.Schedule.get(i);
//                    if (schedule == null || schedule.Playbill == null) {
//                        Logger.w("No schedule info, skip it.");
//                        continue;
//                    }
//
//                    // 遍历Schedule中所有的playbill
//                    Log.i("jialei","schedule.Playbill.size():"+schedule.Playbill.size());
//                    for (int j = 0; j < schedule.Playbill.size(); j++) {
//                        // 获取playbill中的信息
//                        playbill = schedule.Playbill.get(j);
//
//                        // 有效性验证
//                        if (playbill == null || playbill.Program == null) {
//                            Logger.w("No playbill info, skip it.");
//                            continue;
//                        }
//
//                        // 遍历playbill中所有的program
//                        Log.i("jialei","playbill.Program.size():"+playbill.Program.size());
//                        for (int k = 0; k < playbill.Program.size(); k++) {
//                            // 获取program中的信息
//                            program = playbill.Program.get(k);
//
//                            // 有效性验证
//                            if (program == null || program.Program == null) {
//                                Logger.w("No program info, skip it.");
//                                continue;
//                            }
//
//                            // 解析 XML文件，获取节目内容
//                            sbPgmFileName.setLength(0);
//                            sbPgmFileName.append(PosterApplication.getProgramPath());
//                            sbPgmFileName.append(File.separator);
//                            sbPgmFileName.append("template");
//                            sbPgmFileName.append(File.separator);
//                            sbPgmFileName.append(program.Program.get("name"));
//                            strPgmName = sbPgmFileName.toString();
//                            if (FileUtils.isExist(strPgmName)) {
//                                tmpPgmList = (ProgramLists) XmlFileParse(strPgmName, ProgramLists.class);
//                            } else {
//                                Logger.e("[" + strPgmName + "] didn't exist.");
//                                continue;
//                            }
//
//                            //TODO 没校验md5
//
//                            // 创建Program info
//                            programInfo = new ProgramInfo();
//                            programInfo.startDate = schedule.BeginDate;
//                            programInfo.endDate = schedule.EndDate;
//                            programInfo.schPri = schedule.PRI;
//                            programInfo.scheduleId = schedule.Schedule.get("id");
//                            programInfo.playbillId = playbill.Playbillid;
//                            programInfo.startTime = playbill.BeginTime;
//                            programInfo.endTime = playbill.EndTime;
//                            programInfo.pgmPri = playbill.PRI;
//                            programInfo.programId = program.Program.get("id");
//                            programInfo.pgmFileName = program.Program.get("name");
//                            programInfo.programName = program.Program.get("displayName");
//                            programInfo.verifyCode = program.Program.get("verify");
//                            programInfo.programList = tmpPgmList;
//                            programInfo.ignoreDLLimit = "0".equals(schedule.DLASAP) ? false : true;
//
//                            ////////////////////////////////////////////////////////////////////////////
//                            // This patch for the case of the playing program and update program are same,
//                            // it should keep the play status same.
//                            if (mStatus == PLAYING_NORMAL_PROGRAM &&
//                                    mNormalProgram != null) {
//                                programInfo.playFinished = mNormalProgram.playFinished;
//                            } else {
//                                programInfo.playFinished = false;
//                            }
//                            /////////////////////////////////////////////////////////////////////////////
//
//                            // 添加节目信息
//                            programSchedule.add(programInfo);
//                        }
//                    }
//                }
//            }else{
//                Log.i("jialei","ScheduleNULL");
//            }
//
//            return programSchedule;
//        }
//
//        // 解析xml文件
//        private Object XmlFileParse(String path, Class<?> clazz) {
//            Object obj = null;
//            FileInputStream fin;
//
//            synchronized (mProgramFileLock) {
//                try {
//                    fin = new FileInputStream(path);
//                } catch (FileNotFoundException e) {
//                    fin = null;
//                    e.printStackTrace();
//                    return null;
//                }
//
//                XmlParser parser = new XmlParser();
//                obj = parser.getXmlObject(fin, clazz);
//            }
//
//            return obj;
//        }
//
//        @Override
//        public void run() {
//            mNormalPgmFilePath = obtainNormalPgmFilePath();
//            Log.i("jialei","mNormalPgmFilePath:"+mNormalPgmFilePath);
//            mNormalProgramInfoList = getProgramScheduleFromXml(mNormalPgmFilePath);
//            Log.i("jialei","mIsRun"+mIsRun);
//            while (mIsRun) {
//                try {
//                    if (!pgmPathIsAvalible()) {
//                        Log.i("jialei","!pgmPathIsAvalible()");
//                        mNormalPgmFilePath = obtainNormalPgmFilePath();
//                        mNormalProgramInfoList = getProgramScheduleFromXml(mNormalPgmFilePath);
//                        mStatus = IDLE_STATE;
//                    }
//
//                /* 播放状态控制 */
////                    Log.i("jialei","mStatus:"+mStatus);
//                    switch (mStatus) {
//                        case IDLE_STATE:
//
//                            mNormalProgram = null;
//                            mCurrentPgmVerifyCode = null;
//                            if ((mNormalProgram = obtainNormalProgram()) != null) {
//                            /* check whether has Normal program to play */
//                                Logger.i("Start normal program name is: " + mNormalProgram.programName + " File name is: " + mNormalProgram.pgmFileName);
//                                if (loadProgramContent(EVENT_SHOW_NORMAL_PROGRAM, mNormalProgram)) {
//                                    // 节目加载成功
//                                    mStatus = PLAYING_NORMAL_PROGRAM;
//                                }
//                                continue;
//                            } else if (!mStandbyScreenIsShow) {
//                            /* Show standby screen */
//                                Logger.i("Start standby screen.");
//                                loadProgramContent(EVENT_SHOW_IDLE_PROGRAM, null);
//                            }
//                            break;
//
//                        case PLAYING_NORMAL_PROGRAM:
//
//                            if (normalPgmIsValid()) {
//                            /* 有相同优先级的节目，则循环播放节目 */
//                                if (mNormalProgram.playFinished && mSamePriNormalPgmList != null) {
//                                    if (++mSamePriNormalPgmListIdx >= mSamePriNormalPgmList.size()) {
//                                        mSamePriNormalPgmListIdx = 0;
//                                    }
//                                    mNormalProgram = mSamePriNormalPgmList.get(mSamePriNormalPgmListIdx);
//                                    Logger.i("change normal program is: " + mNormalProgram.programName + " File name is: " + mNormalProgram.pgmFileName);
//                                    loadProgramContent(EVENT_SHOW_NORMAL_PROGRAM, mNormalProgram);
//                                }
//                            } else {
//                                mNormalProgram = null;
//                                mCurrentPgmVerifyCode = null;
//                                mStatus = IDLE_STATE;
//                                Logger.i("Normal program is invalid, go to IDLE status.");
//                                continue;
//                            }
//
//                            break;
//
//
//                    }
//                } catch (InterruptedException e) {
//                    Logger.i("ScreenDaemon Thread sleep over, and safe exit, the Thread id is: " + currentThread().getId());
//                    return;
//                } catch (Exception e) {
//                    Logger.e("ScreenDaemon Thread Catch a error");
//                    e.printStackTrace();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e1) {
//                        Logger.i("ScreenDaemon Thread sleep over, and safe exit, the Thread id is: " + currentThread().getId());
//                        return;
//                    }
//                }
//
//
//            }
//        }
//
//        private boolean normalPgmIsValid() {
//            /* 若节目超时, 则该节目失效 */
//            return (mNormalProgram != null);
//        }
//
//        /*
// * 重新获取下一组需要播放的普通节目
// */
//        private ProgramInfo obtainNormalProgram() {
//            // 重新获取节目
//            return (getPlayProgram(mNormalProgramInfoList, NORMAL_PROGRAM));
//        }
//
//        /*
// * 从节目清单中获取将要播放的节目
// */
//        private ProgramInfo getPlayProgram(ArrayList<ProgramInfo> pgmSchedule, int nProgramType) {
//            if (pgmSchedule == null) {
//                return null;
//            }
//            // 清空相同优先级的节目列表
//            if (nProgramType == NORMAL_PROGRAM) {
//                mSamePriNormalPgmList = null;
//                mSamePriNormalPgmListIdx = 0;
//            }
//            // 初始化临时变量
//            ProgramInfo currentPgmInfo = null;
//            ArrayList<ProgramInfo> currentPgmSchedule = null;
//            ArrayList<ProgramInfo> samePgmSchedule = new ArrayList<ProgramInfo>();
//
//            // 遍历节目单，找出在当前时间之前开始播放但没有结束的节目单
//            for (ProgramInfo tempPgmInfo : pgmSchedule) {
//                // 节目当天的开始时间在当前时间之前，结束时间在当前时间之后，则该节目为当前可播放节目
//                if (currentPgmSchedule == null) {
//                    currentPgmSchedule = new ArrayList<ProgramInfo>();
//                }
//                currentPgmSchedule.add(tempPgmInfo);
//            }
//            // 遍历当前播放的节目单，找出优先级最高的节目为当前播放节目
//            if (currentPgmSchedule != null) {
//                int nMaxSchPri = -1;
//                for (ProgramInfo tempPgmInfo : currentPgmSchedule) {
//                    if (Integer.parseInt(tempPgmInfo.schPri) > nMaxSchPri) {
//                        nMaxSchPri = Integer.parseInt(tempPgmInfo.schPri);
//                        currentPgmInfo = tempPgmInfo;
//                    } else if (currentPgmInfo != null &&
//                            Integer.parseInt(currentPgmInfo.schPri) == nMaxSchPri &&
//                            Integer.parseInt(tempPgmInfo.pgmPri) > Integer.parseInt(currentPgmInfo.pgmPri)) {
//                        currentPgmInfo = tempPgmInfo;
//                    }
//                }
//            }
//            // 找出和当前节目拥有相同播放时间、相同scedule、相同playbill，相同优先级的其他节目
//            if (currentPgmSchedule != null && currentPgmSchedule.size() > 1) {
//                for (ProgramInfo tempPgmInfo : currentPgmSchedule) {
//                    if (tempPgmInfo.scheduleId.equals(currentPgmInfo.scheduleId) &&
//                            tempPgmInfo.playbillId.equals(currentPgmInfo.playbillId) &&
//                            tempPgmInfo.schPri.equals(currentPgmInfo.schPri) &&
//                            tempPgmInfo.pgmPri.equals(currentPgmInfo.pgmPri)
//                            ) {
//                        tempPgmInfo.breakPoint = currentPgmInfo.breakPoint;
//                        samePgmSchedule.add(tempPgmInfo);
//                    }
//                }
//            }
//            // 保存拥有相同播放时间、相同scedule、相同playbill，相同优先级的节目
//            if (samePgmSchedule.size() > 1) {
//                mSamePriNormalPgmList = samePgmSchedule;
//            }
//
//            return currentPgmInfo;
//
//        }
//
//        /*
//         * 加载节目内容
//         */
//        private boolean loadProgramContent(int msgId, ProgramInfo pgmInfo) throws InterruptedException {
//            if (!pgmPathIsAvalible()) {
//                /*
//                 * 服务器通知节目改变的原因之一
//                 * 有可能是因为本地的节目列表丢失了，所以需要检查外部存储是否被拔走
//                 */
//                mNormalPgmFilePath = obtainNormalPgmFilePath();
//                mNormalProgramInfoList = getProgramScheduleFromXml(mNormalPgmFilePath);
//                Logger.i("loadProgramContent(): Stroge has been lost, can't load program.");
//                return false;
//            } else if (msgId == EVENT_SHOW_IDLE_PROGRAM) {
//                Logger.i("loadProgramContent(): idle program is playing.");
//                return true;
//            }
//            // 获取窗口信息
//            ArrayList<SubWindowInfoRef> subWndList = null;
//            if (msgId == EVENT_SHOW_IDLE_PROGRAM) {
//                subWndList = getStandbyWndInfoList();
//            } else {
//                subWndList = getSubWindowCollection(pgmInfo);
//            }
//            if (subWndList == null) {
//                Logger.e("loadProgramContent(): No subwindow info.");
//                return false;
//            }
//
//            // 清空标志
//            mLoadProgramDone = false;
//            if (pgmInfo != null) {
//                pgmInfo.playFinished = false;
//            }
//
//            // 准备参数
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("subwindowlist", (Serializable) subWndList);
//
//            // 发送消息
//            Message msg = mHandler.obtainMessage();
//            msg.what = msgId;
//            msg.setData(bundle);
//            mHandler.sendMessage(msg);
//
//            // 等待完成
//            while (!mLoadProgramDone) {
//                Thread.sleep(100);
//            }
//
//            // 更新当前节目的VerifyCode
//            if (msgId == EVENT_SHOW_NORMAL_PROGRAM) {
//                mStandbyScreenIsShow = false;
//                mCurrentPgmVerifyCode = pgmInfo.verifyCode;
//            } else if (msgId == EVENT_SHOW_IDLE_PROGRAM) {
//                mStandbyScreenIsShow = true;
//                mCurrentPgmVerifyCode = null;
//            }
//
//            return true;
//
//
//        }
//
//        private boolean pgmPathIsAvalible() {
//            String extPath = PosterApplication.getProgramPath();
//            if (!TextUtils.isEmpty(mNormalPgmFilePath) && !mNormalPgmFilePath.startsWith(extPath)) {
//                return false;
//            }
//
//            return true;
//        }
//
//
//        // 创建待机画面节目信息
//        private ArrayList<SubWindowInfoRef> getStandbyWndInfoList() {
//            ArrayList<SubWindowInfoRef> subWndInfoList = new ArrayList<SubWindowInfoRef>();
//            List<MediaInfoRef> mediaList = new ArrayList<MediaInfoRef>();
//
//            // 创建新的playMediaInfo
//            MediaInfoRef playMediaInfo = new MediaInfoRef();
//            playMediaInfo.filePath = PosterApplication.getStandbyScreenImgPath();
//            playMediaInfo.md5Key = 0;
//            playMediaInfo.verifyCode = "";
//            playMediaInfo.remotePath = "";
//            playMediaInfo.mid = "0";
//            playMediaInfo.vType = "Image";
//            playMediaInfo.mediaType = "Image";
//            playMediaInfo.source = "File";
//            playMediaInfo.duration = 60 * 1000;
//            playMediaInfo.times = 0;
//            playMediaInfo.mode = 0;
//            playMediaInfo.aspect = 0;
//            playMediaInfo.speed = 0;
//            playMediaInfo.vol = 0;
//            playMediaInfo.durationPerPage = 60 * 1000;
//            playMediaInfo.playlistmode = "loop";
//            playMediaInfo.timetype = "absolute";
//            playMediaInfo.starttime = null;
//            playMediaInfo.endtime = null;
//            playMediaInfo.containerwidth = PosterApplication.getScreenWidth();
//            playMediaInfo.containerheight = PosterApplication.getScreenHeight();
//            playMediaInfo.isIgnoreDlLimit = false;
//            mediaList.add(playMediaInfo);
//
//            SubWindowInfoRef subWndInfo = new SubWindowInfoRef();
//            subWndInfo.setSubWindowType("StandbyScreen");
//            subWndInfo.setSubWindowName("StandbyScreenImage");
//            subWndInfo.setWidth(PosterApplication.getScreenWidth());
//            subWndInfo.setHeight(PosterApplication.getScreenHeight());
//            subWndInfo.setSubWndMediaList(mediaList);
//            subWndInfoList.add(subWndInfo);
//
//            return subWndInfoList;
//        }
//
//        /*
//        * 从节目信息中获取所有窗体信息
//        */
//        private ArrayList<SubWindowInfoRef> getSubWindowCollection(ProgramInfo pgmInfo) {
//            ArrayList<SubWindowInfoRef> subWndCollection = null;
//
//            if (pgmInfo != null && pgmInfo.programList != null && pgmInfo.programList.Area != null) {
//                // 初始化变量
//                Areas area = null;
//                PlayLists playlist = null;
//                Medias media = null;
//                PlayFiles playFile = null;
//                MediaInfoRef playMediaInfo = null;
//                List<MediaInfoRef> mediaList = null;
//                String strFilePathName = null;
//                String strFileSavePath = null;
//                SubWindowInfoRef tmpSubWndInfo = null;
//                StringBuilder sb = new StringBuilder();
//                List<Areas> areaList = pgmInfo.programList.Area;
//                int nPgmScreenWidth = 0;
//                int nPgmScreenHeight = 0;
//
//
//                // 获取节目所对应的屏尺寸信息
//                if (pgmInfo.programList.Screen != null) {
//                    if (pgmInfo.programList.Screen.get("width") != null) {
//                        nPgmScreenWidth = Integer.parseInt(pgmInfo.programList.Screen.get("width"));
//                    }
//
//                    if (pgmInfo.programList.Screen.get("height") != null) {
//                        nPgmScreenHeight = Integer.parseInt(pgmInfo.programList.Screen.get("height"));
//                    }
//                }
//
//                // 遍历Area，解析节目的窗体信息
//                for (int i = 0; i < areaList.size(); i++) {
//                    area = areaList.get(i);
//                    mediaList = null;
//
//                    // 创建新的子窗体信息
//                    tmpSubWndInfo = new SubWindowInfoRef();
//
//                    // ID、Type
//                    if (area.Area != null) {
//                        tmpSubWndInfo.setSubWindowName(area.Area.get("id"));
//                        tmpSubWndInfo.setSubWindowType(area.Area.get("type"));
//                    }
//
//                    // Location
//                    if (area.Location != null) {
//                        // 按比例自适应屏幕大小
//                        tmpSubWndInfo.setWidth(calculateWidthScale(Integer.parseInt(area.Location.get("w")), nPgmScreenWidth));
//                        tmpSubWndInfo.setHeight(calculateHeightScale(Integer.parseInt(area.Location.get("h")), nPgmScreenHeight));
//                        tmpSubWndInfo.setXPos(calculateWidthScale(Integer.parseInt(area.Location.get("x")), nPgmScreenWidth));
//                        tmpSubWndInfo.setYPos(calculateHeightScale(Integer.parseInt(area.Location.get("y")), nPgmScreenHeight));
//                    }
//
//                    // Play List
//                    if (area.Playlist != null) {
//                        for (int j = 0; j < area.Playlist.size(); j++) {
//                            playlist = area.Playlist.get(j);
//
//                            // Media List
//                            if (playlist.Media != null) {
//                                for (int k = 0; k < playlist.Media.size(); k++) {
//                                    media = playlist.Media.get(k);
//                                    if (media.Media != null && media.Media.get("type").endsWith("BroadcastVideo")) {
//                                        Logger.i("can't support broadcast Video type.");
//                                        continue;
//                                    }
//
//                                    // Play File List
//                                    if (media.PlayFile != null) {
//                                        for (int l = 0; l < media.PlayFile.size(); l++) {
//                                            /***** 窗口的所有素材 ******/
//                                            playFile = media.PlayFile.get(l);
//
//                                            if (playFile.PlayFile != null &&
//                                                    playFile.PlayFile.get("type") != null &&
//                                                    "File".equals(playFile.PlayFile.get("type"))) {
//                                                sb.setLength(0);
//                                                sb.append(PosterApplication.getProgramPath());
//                                                sb.append(File.separator);
//                                                sb.append(media.Media.get("ptype"));
//                                                sb.append(File.separator);
//                                                sb.append(FileUtils.getFilename(playFile.FileName));
//                                                strFilePathName = sb.toString();
//
//                                            } else {
//                                                strFilePathName = playFile.Url;
//                                            }
//
//                                            // 创建新的playMediaInfo
//                                            playMediaInfo = new MediaInfoRef();
//                                            playMediaInfo.filePath = strFilePathName;
//                                            playMediaInfo.verifyCode = playFile.VerifyCode;
//                                            playMediaInfo.remotePath = playFile.FileName;
//                                            if (media.Media != null) {
//                                                playMediaInfo.mid = media.Media.get("id");
//                                                playMediaInfo.vType = media.Media.get("type");
//                                                playMediaInfo.mediaType = media.Media.get("ptype");
//                                            }
//                                            if (playFile.PlayFile != null) {
//                                                playMediaInfo.source = playFile.PlayFile.get("type");
//                                            }
//                                            if (media.Duration != null) {
//                                                playMediaInfo.duration = Integer.parseInt(media.Duration) * 1000;
//                                            }
//                                            if (media.Times != null) {
//                                                playMediaInfo.times = Integer.parseInt(media.Times);
//                                            }
//                                            if (media.Mode != null) {
//                                                playMediaInfo.mode = Integer.parseInt(media.Mode);
//                                            }
//                                            if (media.Aspect != null) {
//                                                playMediaInfo.aspect = Integer.parseInt(media.Aspect);
//                                            }
//                                            if (media.Speed != null) {
//                                                playMediaInfo.speed = Integer.parseInt(media.Speed);
//                                            }
//                                            if (media.Vol != null) {
//                                                playMediaInfo.vol = Integer.parseInt(media.Vol);
//                                            }
//                                            if (media.Font != null) {
//                                                playMediaInfo.fontName = media.Font.get("name");
//                                                playMediaInfo.fontSize = media.Font.get("size");
//                                                playMediaInfo.fontColor = media.Font.get("color");
//                                            }
//                                            if (media.DurationPerPage != null) {
//                                                playMediaInfo.durationPerPage = Integer.parseInt(media.DurationPerPage) * 1000;
//                                            }
//                                            if (playlist.Playlist != null) {
//                                                playMediaInfo.playlistmode = playlist.Playlist.get("mode");
//                                                playMediaInfo.timetype = playlist.Playlist.get("timetype");
//                                                playMediaInfo.starttime = playlist.Playlist.get("stime");
//                                                playMediaInfo.endtime = playlist.Playlist.get("etime");
//                                            }
//                                            playMediaInfo.containerwidth = tmpSubWndInfo.getWidth();
//                                            playMediaInfo.containerheight = tmpSubWndInfo.getHeight();
//                                            playMediaInfo.isIgnoreDlLimit = pgmInfo.ignoreDLLimit;
//
//                                            // 添加到Media list中
//                                            if (mediaList == null) {
//                                                mediaList = new ArrayList<MediaInfoRef>();
//                                            }
//                                            mediaList.add(playMediaInfo);
//                                        }
//                                    } else if ("Clock".equals(tmpSubWndInfo.getSubWindowType())) {
//                                        /***** Clock 只有参数，没有素材 ******/
//                                        playMediaInfo = new MediaInfoRef();
//                                        playMediaInfo.format = media.Format;
//                                        playMediaInfo.fontName = media.Font.get("name");
//                                        playMediaInfo.fontSize = media.Font.get("size");
//                                        playMediaInfo.fontColor = media.Font.get("color");
//                                        playMediaInfo.containerwidth = tmpSubWndInfo.getWidth();
//                                        playMediaInfo.containerheight = tmpSubWndInfo.getHeight();
//
//                                        // 添加到Media list中
//                                        if (mediaList == null) {
//                                            mediaList = new ArrayList<MediaInfoRef>();
//                                        }
//                                        mediaList.add(playMediaInfo);
//                                    } else if ("Timer".equals(tmpSubWndInfo.getSubWindowType())) {
//                                        playMediaInfo = new MediaInfoRef();
//                                        playMediaInfo.format = media.Format;
//                                        playMediaInfo.fontName = media.Font.get("name");
//                                        playMediaInfo.fontSize = media.Font.get("size");
//                                        playMediaInfo.fontColor = media.Font.get("color");
//                                        if ("Countdown".equals(media.Mode)) {
//                                            playMediaInfo.mode = 0;
//                                        } else if ("Elapse".equals(media.Mode)) {
//                                            playMediaInfo.mode = 1;
//                                        } else {
//                                            playMediaInfo.mode = -1;
//                                        }
//                                        playMediaInfo.deadline = media.Deadline;
//                                        playMediaInfo.containerwidth = tmpSubWndInfo.getWidth();
//                                        playMediaInfo.containerheight = tmpSubWndInfo.getHeight();
//
//                                        // 添加到Media list中
//                                        if (mediaList == null) {
//                                            mediaList = new ArrayList<MediaInfoRef>();
//                                        }
//                                        mediaList.add(playMediaInfo);
//                                    }
//                                }
//                            }
//                        }
//
//                        if (mediaList != null) {
//                            tmpSubWndInfo.setSubWndMediaList(mediaList);
//                        }
//                    }
//
//                    // 添加子窗体信息到窗口集合
//                    if (subWndCollection == null) {
//                        subWndCollection = new ArrayList<SubWindowInfoRef>();
//                    }
//                    subWndCollection.add(tmpSubWndInfo);
//                }
//            }
//            return subWndCollection;
//        }
//
//
//        // 按屏幕实际大小进行比例缩放(横向)
//        private int calculateWidthScale(int nValue, int pgmWidth) {
//            int ret = nValue;
//            int nScreenWidth = PosterApplication.getScreenWidth();
//            if (pgmWidth > 0 && nScreenWidth > 0) {
//                ret = (int) (nValue * nScreenWidth / pgmWidth + 0.5f);
//            }
//            return ret;
//            //return PosterApplication.px2dip(mContext, ret);
//        }
//
//        // 按屏幕实际大小进行比例缩放(纵向)
//        private int calculateHeightScale(int nValue, int pgmHeight) {
//            int ret = nValue;
//            int nScreenHeight = PosterApplication.getScreenHeight();
//            if (pgmHeight > 0 && nScreenHeight > 0) {
//                ret = (int) (nValue * nScreenHeight / pgmHeight + 0.5f);
//            }
//            return ret;
//            //return PosterApplication.px2dip(mContext, ret);
//        }
//    }
//
//
//    @SuppressLint("HandlerLeak")
//    final Handler mHandler = new Handler() {
//        @SuppressWarnings("unchecked")
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case EVENT_SHOW_NORMAL_PROGRAM:
//                    if (mContext instanceof MainActivity) {
//                        ((MainActivity) mContext).loadNewProgram((ArrayList<SubWindowInfoRef>) msg.getData().getSerializable("subwindowlist"));
//                    }
//                    mLoadProgramDone = true;
//                    return;
//
//                case EVENT_SHOW_IDLE_PROGRAM:
//                case EVENT_MEDIA_READY_SHOW_PROGRAM:
//
//                    if (mContext instanceof MainActivity) {
//                        ((MainActivity) mContext).loadNewProgram((ArrayList<SubWindowInfoRef>) msg.getData().getSerializable("subwindowlist"));
//                    }
//                    mLoadProgramDone = true;
//                    return;
//                default:
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//
//    /**********************************************
//     * Start Screen Daemon Thread *
//     **********************************************/
//    public void startRun()
//    {
//        stopRun();
//        mStatus = IDLE_STATE;
//        mScreenDaemonThread = new ScreenDaemon();
//        mScreenDaemonThread.setRunFlag(true);
//        mScreenDaemonThread.start();
//    }
//
//    /**********************************************
//     * Stop Screen Daemon Thread *
//     **********************************************/
//    public void stopRun()
//    {
//        if (mScreenDaemonThread != null)
//        {
//            mScreenDaemonThread.setRunFlag(false);
//            mScreenDaemonThread.interrupt();
//            mScreenDaemonThread = null;
//        }
//
//
//        mHandler.removeMessages(EVENT_SHOW_IDLE_PROGRAM);
//        mHandler.removeMessages(EVENT_SHOW_NORMAL_PROGRAM);
//        mHandler.removeMessages(EVENT_MEDIA_READY_SHOW_PROGRAM);
//    }
//}
