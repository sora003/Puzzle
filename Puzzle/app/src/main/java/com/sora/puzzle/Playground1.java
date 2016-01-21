//package com.sora.puzzle;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.RectF;
//import android.view.MotionEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.Toast;
//
//import com.sora.catchcrazycat.Bean.Dot;
//
//import java.util.HashMap;
//import java.util.Vector;
//
///**
// * Created by Sora on 2015/12/28.
// */
//public class Playground1 extends SurfaceView{
//
//    //每个单元的边距
//    private static int WIDTH = 50;
//    private static final int ROW = 10;
//    private static final int COL = 10;
//    //默认添加的路障数量
//    private static final int BLOCKS = 10;
//    private static final int initX = 4;
//    private static final int initY = 5;
//
//    private Dot matrix[][];
//    private Dot cat;
//    private int steps;
//
//    public Playground1(Context context) {
//        super(context);
//        getHolder().addCallback(callback);
//        matrix= new Dot[ROW][COL];
//        for (int i=0;i<ROW;i++){
//            for (int j=0;j<COL;j++){
//                matrix[i][j] = new Dot(j,i);
//            }
//        }
//        setOnTouchListener(new DotOnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return super.onTouch(v, event);
//            }
//        });
//        //初始化游戏元素
//        initGame();
//    }
//
//    //绘制界面
//    private void redraw(){
//        //锁定SurfaceView对象 获取该SurfaceView上的Canvas
//        Canvas canvas = getHolder().lockCanvas();
//        //设置背景颜色
//        canvas.drawColor(Color.LTGRAY);
//        Paint paint = new Paint();
//        //抗锯齿
//        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        //添加单元格颜色
//        for (int i=0;i<ROW;i++){
//            //单元格偏移量
//            int offset = 0;
//            //偶数行右偏移
//            if (i%2 == 1){
//                offset = WIDTH/2;
//            }
//            for (int j=0;j<COL;j++){
//                Dot one = getDot(j,i);
//                switch (one.getStatus()){
//                    case Dot.STATUS_OFF:
//                        paint.setColor(0xFFEEEEEE);
//                        break;
//                    case Dot.STATUS_ON:
//                        paint.setColor(0xFFFF9900);
//                        break;
//                    case Dot.STATUS_IN:
//                        paint.setColor(0xFFFF0000);
//                        break;
//                }
//                canvas.drawOval(new RectF(one.getX()*WIDTH+offset,one.getY()*WIDTH,
//                        (one.getX()+1)*WIDTH+offset,(one.getY()+1)*WIDTH),paint);
//            }
//        }
//        //取消Canvas的锁定 更新界面
//        getHolder().unlockCanvasAndPost(canvas);
//    }
//
//    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
//        @Override
//        public void surfaceCreated(SurfaceHolder holder) {
//            redraw();
//        }
//
//        @Override
//        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            //根据屏幕宽度适配单元格大小
//            WIDTH = width/(COL+1);
//            redraw();
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder holder) {
//
//        }
//    };
//
//    //初始化游戏元素
//    private void initGame(){
//        //坐标初始化
//        for (int i=0;i<ROW;i++){
//            for (int j=0;j<COL;j++){
//                //定义所有坐标处于可走状态
//                matrix[i][j].setStatus(Dot.STATUS_OFF);
//            }
//        }
//        //步数初始化
//        steps = 0;
//        //设置猫的位置
//        cat = new Dot(initX,initY);
//        getDot(initX,initY).setStatus(Dot.STATUS_IN);
//        //设置路障位置
//        for(int i=0;i<BLOCKS;){
//            //随机获取路障坐标
//            int x = (int) ((Math.random()*1000)%COL);
//            int y = (int) ((Math.random()*1000)%ROW);
//            //确保路障的设置不重复
//            if (getDot(x,y).getStatus() == Dot.STATUS_OFF){
//                getDot(x,y).setStatus(Dot.STATUS_ON);
//                i++;
////                System.out.println(x+"      "+y);
//            }
//        }
//    }
//
//    //根据坐标获取Dot对象
//    private Dot getDot(int x,int  y){
//        return matrix[y][x];
//    }
//
//    //判断是否处于游戏边界
//    private boolean isAtEdge(Dot dot){
//        if (dot.getX()*dot.getY() == 0 || dot.getX()+1 == COL || dot.getY()+1 ==ROW){
//            return true;
//        }
//        return false;
//    }
//
//    //获取Dot的邻接单元
//    //从邻接左元素顺时针定义顺序1 2 3 4 5 6
//    private Dot getNeighbour(Dot dot,int dir){
//        switch (dir){
//            case 1:
//                return getDot(dot.getX() - 1, dot.getY());
//            case 2:
//                if (dot.getY()%2 == 0){
//                    return getDot(dot.getX() - 1, dot.getY() - 1);
//                }
//                else {
//                    return getDot(dot.getX(), dot.getY() - 1);
//                }
//            case 3:
//                if (dot.getY()%2 == 0){
//                    return getDot(dot.getX(), dot.getY() - 1);
//                }
//                else {
//                    return getDot(dot.getX() + 1, dot.getY() - 1);
//                }
//            case 4:
//                return getDot(dot.getX() + 1,dot.getY());
//            case 5:
//                if (dot.getY()%2 == 0){
//                    return getDot(dot.getX(), dot.getY() + 1);
//                }
//                else {
//                    return getDot(dot.getX() + 1, dot.getY() + 1);
//                }
//            case 6:
//                if (dot.getY()%2 == 0){
//                    return getDot(dot.getX() - 1, dot.getY() + 1);
//                }
//                else {
//                    return getDot(dot.getX(), dot.getY() + 1);
//                }
//        }
//        return null;
//    }
//
//    //返回不同方向可走长度
//    //方向计量方法与getNeighbour相同
//    private int getDistance(Dot dot,int dir){
//        int distance = 0;
//        //当点在边界时 返回距离值为0 防止数组越界报错
//        if (isAtEdge(dot)){
//            return 1;
//        }
//        //设置参考点d
//        Dot d = dot;
//        //沿指定方向移动的下一个单元
//        Dot next;
//        while (true){
//            next = getNeighbour(d,dir);
//            //遇到不可走的单元返回值
//            //返回负值
//            if (next.getStatus() == Dot.STATUS_ON){
//                return distance*-1;
//            }
//            //遇到游戏边界
//            //返回正值
//            if (isAtEdge(next)){
//                distance++;
//                return distance;
//            }
//            distance++;
//            d = next;
//        }
//    }
//
//    //猫的位置变化
//    private void moveTo(Dot dot){
//        //设置当前单元为猫所在位置
//        dot.setStatus(Dot.STATUS_IN);
//        //设置猫未移动时的位置状态为可走
//        getDot(cat.getX(),cat.getY()).setStatus(Dot.STATUS_OFF);
//        //更新猫所在的坐标
//        cat.setXY(dot.getX(),dot.getY());
//    }
//
//    //实现猫的移动
//    private void move(){
//        //判断场景边界 如果猫在边界 游戏结束 失败
//        if (isAtEdge(cat)){
//            lose();
//            return;
//        }
//        //记录猫周围可走的单元
//        Vector<Dot> available = new Vector<>();
//        //记录可以直接到达边界的路径方向
//        Vector<Dot> positive = new Vector<>();
//        //记录方向
//        HashMap<Dot,Integer> direction = new HashMap<Dot,Integer>();
//        //判断猫周围的单元是否可走
//        for (int i=1;i<=6;i++){
//            Dot d = getNeighbour(cat,i);
//            //如果有可走的单元 添加进Vector
//            if (d.getStatus() == Dot.STATUS_OFF){
//                available.add(d);
//                //更新HashMap
//                direction.put(d, i);
//                if (getDistance(d,i)>0){
//                    positive.add(d);
//                    //传入当前单元所对应的方向
//                }
//            }
//        }
//        //如果没有可走单元 游戏结束 胜利
//        if (available.size() == 0){
//            steps++;
//            win();
//        }
//        //只有一种可移动方向 向该方向移动
//        else if (available.size() == 1) {
//            moveTo(available.get(0));
//        }
//        //多余一种选择时  采取高级移动策略
//        else {
//            //记录最佳移动向单元
//            Dot best = null;
//            //存在课可以直接到达游戏边界的路径
//            if (positive.size()!=0){
//                //记录可以到达边界的最短距离
//                int min = 9999;
//                for (int i=0;i<positive.size();i++){
//                    int length = getDistance(positive.get(i), direction.get(positive.get(i)));
////                    System.out.println("方向"+direction.get(positive.get(i))+":"+length);
//                    if (length<min){
//                        min = length;
//                        best = positive.get(i);
//                    }
//                }
////                System.out.println("**********");
//            }
//            //所有方向都存在路障
//            else {
//                //取移动空间最大的
//                int max = 1;
//                for (int i=0;i<available.size();i++){
//                    int length = getDistance(available.get(i), direction.get(available.get(i)));
////                    System.out.println("方向"+direction.get(available.get(i))+":"+length);
//                    if (length<max){
//                        max = length;
//                        best = available.get(i);
//                    }
//                }
////                System.out.println("**********");
//            }
//            moveTo(best);
//        }
//        steps++;
//    }
//
//    private void lose(){
//        Toast.makeText(getContext(),"You Lose!",Toast.LENGTH_SHORT).show();
//    }
//
//    private void win(){
//        Toast.makeText(getContext(),"You Win!",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getContext(),"总计"+steps+"步",Toast.LENGTH_SHORT).show();
//    }
//
//    //触摸事件监听
//    private class DotOnTouchListener implements OnTouchListener {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//           if (event.getAction() == MotionEvent.ACTION_UP){
////               Toast.makeText(getContext(),event.getX()+":"+event.getY(),Toast.LENGTH_SHORT).show();
//               //转化为Dot下坐标
//               int x,y;
//               y = (int) (event.getY()/WIDTH);
//               if (y%2 == 0){
//                   x = (int) (event.getX()/WIDTH);
//               }
//               else {
//                   x = (int) ((event.getX()-WIDTH/2)/WIDTH);
//               }
//               //保护数组防止越界
//               //在游戏主界面外点击将初始化游戏
//               if (x+1>COL || y+1>ROW){
//                 initGame();
//               }
//               //当当前点击点处于可走状态时才可以行动
//               else if (getDot(x,y).getStatus() == Dot.STATUS_OFF){
//                   //点击后处于不可走状态
//                   getDot(x,y).setStatus(Dot.STATUS_ON);
//                   move();
//               }
//               redraw();
//           }
//            return true;
//        }
//    }
//}
