package com.magicrepokit.chat.constant;

public interface KnowledgeConstant {
    //-------------------------------文件夹类型---------------------------
    //1.文件夹
    Integer FOLDER = 1;
    //2.文件
    Integer FILE = 2;

    //-------------------------------文件任务状态---------------------------
    //1.未开始
    Integer NOT_STARTED = 1;
    //2.文件分隔中
    Integer FILE_SPLITTING = 2;
    //3.训练
    Integer TRAINING = 3;
    //4.完成
    Integer COMPLETE = 4;
    //5.失败
    Integer FAIL = 5;
}
