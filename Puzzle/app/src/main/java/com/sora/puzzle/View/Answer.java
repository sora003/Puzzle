package com.sora.puzzle.View;

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

/**
 * Created by Sora on 2016/1/20.
 * Answer  答案
 */
public class Answer extends SurfaceView {

    //初始序列
    private static final String initSequence = "WRBBRRBBRRBBRRBB";
    //终止序列
    private static final String finalSequence = "WBRBBRBRRBRBBRBR";
    //每个单元的边距
    private static int WIDTH = 5;
    //每个单元之间的间距
    private static float SPACE = 1;
    //定义Puzzle 4行 4列
    private static final int ROW = 4;
    private static final int COL = 4;

    //定义矩阵元素
    private static final int WHITE = 0x01;
    private static final int RED = 0x02;
    private static final int BLUE = 0x03;

    //REDRAW命令
    private static final int REDRAW = 0x04;

    //当前矩阵
    private int[][] pState = new int[4][4];

    //白块的坐标 x y
    private int whitex;
    private int whitey;

    /**
     * Answer界面的主函数
     * @param context
     */
    public Answer(Context context) {
        super(context);
        //初始化参数
        pState = new int[][]{{WHITE, RED, BLUE, BLUE},{RED,RED,BLUE,BLUE},{RED,RED,BLUE,BLUE},{RED,RED,BLUE,BLUE}};
        whitex = 0;
        whitey = 0;
        getHolder().addCallback(callback);
        getMoveSequence();
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    /**
     * SurfaceHolder.Callback 回调
     */
    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //根据屏幕宽度适配单元格大小
            WIDTH = width/((COL*2));
            SPACE = (float) (((float)WIDTH)/15.0);
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    /**
     * redraw()  重新绘制界面
     */
    private void redraw(){
        //锁定ServiceView对象 获取该SurfaceView上的Canvas
        Canvas canvas = getHolder().lockCanvas();
        //设置背景颜色
        canvas.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        //抗锯齿
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        //添加单元格颜色
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                switch (pState[i][j]){
                    case WHITE:
                        paint.setColor(getResources().getColor(R.color.WHITE));
                        break;
                    case RED:
                        paint.setColor(getResources().getColor(R.color.RED));
                        break;
                    case BLUE:
                        paint.setColor(getResources().getColor(R.color.BLUE));
                        break;
                }

                canvas.drawRect(new RectF(j * WIDTH + SPACE, i * WIDTH + SPACE,
                        (j + 1) * WIDTH - SPACE, (i + 1) * WIDTH - SPACE), paint);
            }
        }
        //取消Canvas的锁定 更新界面
        getHolder().unlockCanvasAndPost(canvas);
    }

    /**
     * 根据移动方向 更新矩阵
     * @param direction
     */
    private void parse(char direction) {

        //移动白块时使用  作为数据缓存
        int temp;
        switch (direction){
            //向左
            case 'L':
                temp = pState[whitey][whitex];
                pState[whitey][whitex] = pState[whitey][whitex-1];
                pState[whitey][whitex-1] = temp;
                whitex--;
                break;
            //向右
            case 'R':
                temp = pState[whitey][whitex];
                pState[whitey][whitex] = pState[whitey][whitex+1];
                pState[whitey][whitex+1] = temp;
                whitex++;
                break;
            //向上
            case 'U':
                temp = pState[whitey][whitex];
                pState[whitey][whitex] = pState[whitey-1][whitex];
                pState[whitey-1][whitex] = temp;
                whitey--;
                break;
            //向下
            case 'D':
                temp = pState[whitey][whitex];
                pState[whitey][whitex] = pState[whitey+1][whitex];
                pState[whitey+1][whitex] = temp;
                whitey++;
                break;
        }

    }

    /**
     * 获取移动路径和执行移动路径 都是耗时操作， 应当都放在子线程中执行
     */
    private void getMoveSequence() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取游戏逻辑接口
                logic gamelogic = new logicImpl();
                //获取移动序列
                final String moveSequence = gamelogic.search(initSequence,finalSequence);
                for (int i = 0; i < moveSequence.length(); i++) {
                    //线程强制休眠1秒 否则会报AndroidRuntime的错！！！！
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //根据移动序列 更新当前矩阵
                    parse(moveSequence.charAt(i));
                    //发送消息给Handler 进行UI更新
                    System.out.println("diection:"+moveSequence.charAt(i));
                    handler.sendEmptyMessage(REDRAW);
                }
            }
        }).start();
    }

    /**
     * Handler 接收子线程发送的REDRAW命令 在UI线程中更新界面
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REDRAW:
                    redraw();
                    break;
            }
        }
    };
}
