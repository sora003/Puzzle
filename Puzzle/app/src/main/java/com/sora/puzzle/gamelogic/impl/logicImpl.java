package com.sora.puzzle.gamelogic.impl;

import com.sora.puzzle.gamelogic.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by Sora on 2016/1/20.
 *
 * 优化策略1 ： 使用String sequence 序列保存当前Map的State 而不使用二维数组 从而极大的节省了空间  这也是最基本的优化策略
 * 优化策略2 ： 使用List<Integer> lastD 记录上一步的运动方向   考虑到如果与上一次白块运动的方向恰好相反 那么会回到上一步运动前的状态 应避免这种情况的发生
 * 优化策略3 ： Set<String> set 记录已经生成过的序列 避免重复执行 极大程度提高了效率 从数学角度考虑 puzzle共有C(16,1)*C(15,8)=102960种状态 完全在可统计范围内 set的maxsize不会超过该数
 * TODO 优化策略4 ： 删除使用Cursor遍历过的序列 节省内存 该策略考虑到手机App运行时可能出现Out Of Memory的错误 但是可能会以增加运行时间为代价 暂时不作考虑
 */
public class logicImpl implements logic {

    //方向
    private static final int LEFT = 0x01;
    private static final int RIGHT = 0x02;
    private static final int UP = 0x03;
    private static final int DOWN = 0x04;

    //记录上一次移动的方向
    private List<Integer> lastD = new ArrayList<Integer>();
    //序列队列
    private List<String> sQueue = new ArrayList<String>();
    //方向队列
    private List<String> dQueue = new ArrayList<String>();
    //队列长度
    private int length;
    //记录所有产生过的序列
    private Set<String> set = new HashSet<String>();



    @Override
    public String search(String initSequence, String finalSequence) {
        String psequence = initSequence;
        String temp = null;
        //队列初始长度设置为-1
        length = -1;
        //队列游标 初始值设置为0
        int cursor = 0;
        //标记是否找到路径
        boolean hasFind = false;
        addQueue(initSequence);
        //初始化dQueue
        dQueue.add("");
        lastD.add(-1);
        //当前序列与最终序列不相同的情况下 执行循环
        while (!hasFind){
            //根据游标取得当前遍历序列
            psequence = sQueue.get(cursor);
            for (int i = 0; i < 4; i++) {
                switch (i){
                    //向左移动
                    case 0 :
                        temp = move(psequence,LEFT);
                        //排除 该移动不可行 上一步为向右移动 已经存在根据该移动得到的序列 3种情况
                        if (temp != null && lastD.get(cursor)!= RIGHT && !set.contains(temp)){
                            addQueue(temp);
                            dQueue.add(dQueue.get(cursor) + "L");
                            //将该步的移动方向加入lastD List  便于之后调用
                            lastD.add(LEFT);
                            //判断移动后是否为最终序列
                            if (temp.equals(finalSequence)){
                                return dQueue.get(length);
                            }
                        }
                        break;
                    //向右移动
                    case 1 :
                        temp = move(psequence,RIGHT);
                        //排除 该移动不可行 上一步为向左移动 已经存在根据该移动得到的序列 3种情况
                        if (temp != null && lastD.get(cursor)!= LEFT && !set.contains(temp)){
                            addQueue(temp);
                            dQueue.add(dQueue.get(cursor) + "R");
                            lastD.add(RIGHT);
                            if (temp.equals(finalSequence)){
                                return dQueue.get(length);
                            }
                        }
                        break;
                    //向上移动
                    case 2 :
                        temp = move(psequence,UP);
                        //排除 该移动不可行 上一步为向下移动 已经存在根据该移动得到的序列 3种情况
                        if (temp != null && lastD.get(cursor)!= DOWN && !set.contains(temp)){
                            addQueue(temp);
                            dQueue.add(dQueue.get(cursor) + "U");
                            lastD.add(UP);
                            if (temp.equals(finalSequence)){
                                return dQueue.get(length);
                            }
                        }
                        break;
                    //向下移动
                    case 3 :
                        temp = move(psequence,DOWN);
                        //排除 该移动不可行 上一步为向上移动 已经存在根据该移动得到的序列 3种情况
                        if (temp != null && lastD.get(cursor)!= UP && !set.contains(temp)){
                            addQueue(temp);
                            dQueue.add(dQueue.get(cursor) + "D");
                            lastD.add(DOWN);
                            if (temp.equals(finalSequence)){
                                return dQueue.get(length);
                            }
                        }
                        break;
                }
            }
            //当前序列的可移动情况执行完成 游标向后移动一位
            cursor++;
        }
        return null;
    }



    /**
     *
     * 将序列添加进入Queue 和 Set
     * @param sequence
     */
    private void addQueue(String sequence){
        length++;
        sQueue.add(sequence);
        set.add(sequence);
    }

    /**
     *
     * @param sequence  原序列
     * @param direction 移动方向
     * @return String   根据原序列和移动方向所确定的新序列
     *
     * 注意：如果根据移动方向移动将超出边界导致该移动不可行 则返回null
     */
    private String move(String sequence , int direction){
        String newsequence = null;
        //返回W在sequence中的位置
        int white = sequence.indexOf("W");
        //转换成对应的坐标
        int titlex = white % 4;
        int titley = white / 4;
        switch (direction){
            case LEFT:
                //判断白色方块是否可以向左移动
                if (titlex == 0){
                    return null;
                }
                else {
                    int change = white - 1;
                    newsequence = sequence.substring(0,change) + "W" + sequence.charAt(change) + sequence.substring(white+1,16);
                }
                break;
            case RIGHT:
                //判断白色方块是否可以向右移动
                if (titlex == 3){
                    return null;
                }
                else {
                    int change = white + 1;
                    newsequence = sequence.substring(0,white) + sequence.charAt(change) + "W" + sequence.substring(change+1,16);
                }
                break;
            case UP:
                //判断白色方块是否可以向上移动
                if (titley == 0){
                    return null;
                }
                else {
                    int change = (titley - 1) * 4 + titlex;
                    newsequence = sequence.substring(0,change) + "W" + sequence.substring(change+1,white) + sequence.charAt(change) + sequence.substring(white+1,16);
                }
                break;
            case DOWN:
                //判断白色方块是否可以向下移动
                if (titley == 3){
                    return null;
                }
                else {
                    int change = (titley + 1) * 4 + titlex;
                    newsequence = sequence.substring(0,white) + sequence.charAt(change) + sequence.substring(white+1,change) + "W" + sequence.substring(change+1,16);
                }
                break;

        }
        return newsequence;
    }

}
