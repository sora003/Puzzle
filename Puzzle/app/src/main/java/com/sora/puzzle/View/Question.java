package com.sora.puzzle.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.sora.puzzle.KeyActivity;
import com.sora.puzzle.R;


/**
 * Created by Sora on 2016/1/21.
 */
public class Question extends SurfaceView {

    //上下文环境
    Context mContext;
    //初始序列
    private static final String initSequence = "WRBBRRBBRRBBRRBB";
    //终止序列
    private static final String finalSequence = "WBRBBRBRRBRBBRBR";
    //问题文本
    private static final String QTEXT = "       There're 7 red tiles, 8 blue titles and one white title in a 4 x 4 plane. We could only move the white tile. When moving it, the white tile swaps the position with the adjacent tile. L, R, U, D are corresponding to four directions which the tile could be moved to (Left, Right, Up, Down). Now, starting from configuration (S), find the shortest way to reach configuration (T). ";
    //引用图片
//    private Bitmap KEY = null;
    //每个单元的边距
    private static int WIDTH = 5;
    //每个单元之间的间距
    private static float SPACE = 1;
    //终止状态的偏移量
    private static int OFFSET = 0;

    //定义Puzzle 4行 4列
    private static final int ROW = 4;
    private static final int COL = 4;

    //定义矩阵元素
    private static final int WHITE = 0x01;
    private static final int RED = 0x02;
    private static final int BLUE = 0x03;

    //REDRAW命令
    private static final int REDRAW = 0x04;

    //初始矩阵
    private int[][] initState = new int[4][4];
    //终止矩阵
    private int[][] finalState = new int[4][4];


    /**
     * Question界面的主函数
     * @param context
     */
    public Question(Context context) {
        super(context);
        mContext = context;
        //初始化参数
        initState = new int[][]{{WHITE, RED, BLUE, BLUE},{RED,RED,BLUE,BLUE},{RED,RED,BLUE,BLUE},{RED,RED,BLUE,BLUE}};
        finalState = new int[][]{{WHITE, BLUE, RED, BLUE},{BLUE,RED,BLUE,RED},{RED, BLUE, RED, BLUE},{BLUE,RED,BLUE,RED}};


        getHolder().addCallback(callback);
        //必须使用子线程  需要让子线程强制休眠一段时间 否则会报AndroidRuntime的错！！！！
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //强制休眠
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //向Handler发送消息 更新界面
                handler.sendEmptyMessage(REDRAW);
            }
        }).start();

        //设置点击监听
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP){
                    //如果监听到点击动作 跳转Acticity
                    mContext.startActivity(new Intent(mContext,KeyActivity.class));

                }
                return true;
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
            WIDTH = width/((COL+1)*2);
            SPACE = (float) (((float)WIDTH)/15.0);
            OFFSET = width/2;
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
        // 设置文字大小
        paint.setTextSize(50);
        //添加单元格颜色 initState
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                switch (initState[i][j]){
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
        //添加单元格颜色 finalState
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                switch (finalState[i][j]){
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

                canvas.drawRect(new RectF(j * WIDTH + SPACE + OFFSET, i * WIDTH + SPACE,
                        (j + 1) * WIDTH - SPACE + OFFSET, (i + 1) * WIDTH - SPACE), paint);
            }
        }
        // 设置画笔颜色
        paint.setColor(Color.DKGRAY);
        //设置初始矩阵序号
        canvas.drawText("(S)", (float) (1.5 * WIDTH), (float) ((COL + 0.5) * WIDTH + SPACE * 3), paint);
        //设置终止矩阵序号
        canvas.drawText("(T)", (float) (1.5 * WIDTH) + OFFSET, (float) ((COL + 0.5) * WIDTH + SPACE * 3), paint);


        //设置TextPaint 解决多行文本的换行问题
        TextPaint textPaint = new TextPaint();
        //设置颜色
        textPaint.setColor(Color.DKGRAY);
        //设置字号大小
        textPaint.setTextSize(40.0F);
        //第三个参数为文本的行宽
        StaticLayout layout = new StaticLayout(QTEXT,textPaint,WIDTH*10, Layout.Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
        //绘制起始点
        canvas.translate(SPACE * 3, (float) ((COL + 2) * WIDTH));
        //绘制文本
        layout.draw(canvas);
//        KEY = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.key);
//        canvas.drawBitmap(KEY,(float) (1.5 * WIDTH) +OFFSET,WIDTH*8,paint);


//        //绘制Key文本框
//        canvas.drawRect(new RectF((float) (1.5 * WIDTH) + OFFSET, WIDTH * 7,
//                (float) (3.5 * WIDTH) + OFFSET, WIDTH * 8), paint);


        //改变文本颜色
        paint.setColor(Color.DKGRAY);

        //绘制Key文本
        canvas.drawText("---->Click For Answer", WIDTH*3, (float) (WIDTH*9), paint);


        //取消Canvas的锁定 更新界面
        getHolder().unlockCanvasAndPost(canvas);
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
