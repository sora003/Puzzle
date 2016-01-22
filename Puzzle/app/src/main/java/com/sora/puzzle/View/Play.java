package com.sora.puzzle.View;

/**
 * Created by Sora on 2016/1/21.
 *
 * TODO 可以提供让用户自己移动白块 寻找答案的交互
 * TODO 进一步考虑 可以让用户设定初始状态 和 最终状态 去进行游戏和获取答案
 * TODO 这两者均可以基于已完成模块实现具体功能
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.sora.puzzle.R;
import com.sora.puzzle.gamelogic.impl.logicImpl;
import com.sora.puzzle.gamelogic.logic;

public class Play extends SurfaceView {


    public Play(Context context) {
        super(context);
    }
}

