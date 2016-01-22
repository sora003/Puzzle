package com.sora.puzzle.gamelogic.impl;

import com.sora.puzzle.gamelogic.highLevelLogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sora on 2016/1/21.
 *
 * 采取广度搜索遍历，能够寻找到最短路径 使用队列实现
 *
 * 优化策略1 ： 使用int sequence 序列保存当前Map的State 取代String 更加节省空间 加快运行时间 这是highLevelLogic的最大改进
 * 优化策略2 ： 使用int[] lastD 记录上一步的运动方向   考虑到如果与上一次白块运动的方向恰好相反 那么会回到上一步运动前的状态 应避免这种情况的发生
 * 优化策略3 ： Set<Integer> set 记录已经生成过的序列 避免重复执行 极大程度提高了效率 从数学角度考虑 puzzle共有C(16,1)*C(15,8)=102960种状态 完全在可统计范围内 set的maxsize不会超过该数
 * 优化策略4 ： 取消ArrayList的使用 使用数组替代 因为数组的读取，存取快于ArrayList  从而节约一定的时间开支
 * 优化策略5 ： 使用int[] pointer 代替List<String> dQueue 存取方向 整体思路为为每一个遍历节点添加上一层节点  当找到路径时  通过pointer层层返回  获取Move Sequence  直至初始状态 节省了时间和空间
 * TODO 优化策略6 ： 删除使用Cursor遍历过的序列 节省内存 该策略考虑到手机App运行时可能出现Out Of Memory的错误 但是可能会以增加运行时间为代价 暂时不作考虑
 *
 * TODO 代码结构优化：如果不对运行时间作严格要求 可以将这若干个数组所代表的的参数 封装在一个对象中 记作node  每个节点分别记作一个node 使用List<node> 记录节点 并执行相关操作
 *
 * 相对于logic 可能由于产生了更多的临时变量 导致运行速度加快的不是非常明显
 */

public class highLevelLogicImpl implements highLevelLogic {


    private static final int CONST = (int) Math.pow(2,16);

    //方向
    private static final int LEFT = 0x01;
    private static final int RIGHT = 0x02;
    private static final int UP = 0x03;
    private static final int DOWN = 0x04;

    //记录上一次移动的方向
    private int[] lastD = new int[102960];
    //序列队列
    private int[] sQueue = new int[102960];
    //方向队列
//    private List<String> dQueue = new ArrayList<String>();
    private int[] pointer = new int[102960];
    //队列长度
    private int length;
    //记录所有产生过的序列
    private Set<Integer> set = new HashSet<Integer>();


    //为避免多次创建新的String对象 设置为全局变量
    //lSequence转字符串
    private String lString;
    //rSequence转字符串
    private String rString;


    //队列游标 指向当前执行到的节点
    private int cursor;



    @Override
    public String search(int initSequence, int finalSequence) {
        //定义当前序列
        int pSequence = initSequence;
        int temp = 0;
        //队列初始长度设置为-1
        length = -1;
        //队列游标 初始值设置为0
        cursor = 0;
        //标记是否找到路径
        boolean hasFind = false;
        addQueue(initSequence);
        //初始化dQueue
//        dQueue.add("");
        pointer[length] = -1;
        lastD[length] = -1;
        //当前序列与最终序列不相同的情况下 执行循环
        while (!hasFind){
            //根据游标取得当前遍历序列
            pSequence = sQueue[cursor];
            for (int i = 0; i < 4; i++) {
                switch (i){
                    //向左移动
                    case 0 :
                        temp = move(pSequence,LEFT);
                        //排除 该移动不可行 上一步为向右移动 已经存在根据该移动得到的序列 3种情况
                        if (temp != 0 && lastD[cursor]!= RIGHT && !set.contains(temp)){
                            addQueue(temp);
//                            dQueue.add(dQueue.get(cursor) + "L");
                            //将该步的移动方向加入lastD List  便于之后调用
                            lastD[length] = LEFT;
                            //判断移动后是否为最终序列
                            if (temp == finalSequence){
                                return getPath(length);
                            }
                        }
                        break;
                    //向右移动
                    case 1 :
                        temp = move(pSequence,RIGHT);
                        //排除 该移动不可行 上一步为向左移动 已经存在根据该移动得到的序列 3种情况
                        if (temp != 0 && lastD[cursor]!= LEFT && !set.contains(temp)){
                            addQueue(temp);
//                            dQueue.add(dQueue.get(cursor) + "R");
                            lastD[length] = RIGHT;
                            if (temp == finalSequence){
                                return getPath(length);
                            }
                        }
                        break;
                    //向上移动
                    case 2 :
                        temp = move(pSequence,UP);
                        //排除 该移动不可行 上一步为向下移动 已经存在根据该移动得到的序列 3种情况
                        if (temp != 0 && lastD[cursor]!= DOWN && !set.contains(temp)){
                            addQueue(temp);
//                            dQueue.add(dQueue.get(cursor) + "U");
                            lastD[length] = UP;
                            if (temp == finalSequence){
                                return getPath(length);
                            }
                        }
                        break;
                    //向下移动
                    case 3 :
                        temp = move(pSequence,DOWN);
                        //排除 该移动不可行 上一步为向上移动 已经存在根据该移动得到的序列 3种情况
                        if (temp != 0 && lastD[cursor]!= UP && !set.contains(temp)){
                            addQueue(temp);
//                            dQueue.add(dQueue.get(cursor) + "D");
                            lastD[length] = DOWN;
                            if (temp == finalSequence){
                                return getPath(length);
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
     * 根据当前节点 通过回调 获取最短路径
     *
     * @param length 当前节点
     * @return Move Sequence
     */
    private String getPath(int length) {
        String path = "";
        int pCursor = length;
        while (pointer[pCursor] != -1){
           switch (lastD[pCursor]){
               case LEFT:
                   path = "L" + path;
                   break;
               case RIGHT:
                   path = "R" + path;
                   break;
               case UP:
                   path = "U" + path;
                   break;
               case DOWN:
                   path = "D" + path;
                   break;
           }
            pCursor = pointer[pCursor];

       }
        return path;
    }


    /**
     *
     * 将序列添加进入Queue 和 Set
     * @param sequence
     */
    private void addQueue(int sequence){
        length++;
        sQueue[length] = sequence;
        set.add(sequence);
        pointer[length] = cursor;
    }

    /**
     *
     * @param sequence  原序列
     * @param direction 移动方向
     * @return int   根据原序列和移动方向所确定的新序列
     *
     * 注意：如果根据移动方向移动将超出边界导致该移动不可行 则返回0
     */
    private int move(int sequence , int direction){
        //获取sequence前16位  该值即为白块在sequence中的位置
        int lSequence = sequence / CONST ;
        //获取sequence后16位
        int rSequence = sequence - lSequence * CONST;
        //lSequence转字符串
        lString = Integer.toBinaryString(lSequence);
        //rSequence转字符串
        rString = Integer.toBinaryString(rSequence);

        //不满16位 则补足0
        while (rString.length()<16){
            rString = "0" + rString;
        }

        //转换成对应的坐标
        int titlex = lSequence % 4;
        int titley = lSequence / 4;
        switch (direction){
            case LEFT:
                //判断白色方块是否可以向左移动
                if (titlex == 0){
                    return 0;
                }
                else {
                    int change = lSequence - 1;
                    lString =  Integer.toBinaryString(change);
                    return Integer.parseInt((lString + rString.substring(0, lSequence - 1) + "0" + rString.charAt(lSequence - 1) + rString.substring(lSequence+1,16)),2);



                }
            case RIGHT:
                //判断白色方块是否可以向右移动
                if (titlex == 3){
                    return 0;
                }
                else {
                    int change = lSequence + 1;
                    lString =  Integer.toBinaryString(change);
                    return Integer.parseInt(lString + rString.substring(0, lSequence) + rString.charAt(lSequence + 1) + "0" + rString.substring(lSequence+2,16),2);
                }
            case UP:
                //判断白色方块是否可以向上移动
                if (titley == 0){
                    return 0;
                }
                else {
                    int change = (titley - 1) * 4 + titlex;
                    lString =  Integer.toBinaryString(change);
                    return Integer.parseInt(lString + rString.substring(0, change) + "0" + rString.substring(change + 1, lSequence) + rString.charAt(change) + rString.substring(lSequence + 1, 16),2);
                }
            case DOWN:
                //判断白色方块是否可以向下移动
                if (titley == 3){
                    return 0;
                }
                else {
                    int change = (titley + 1) * 4 + titlex;
                    lString =  Integer.toBinaryString(change);
                    return Integer.parseInt(lString + rString.substring(0, lSequence) + rString.charAt(change) + rString.substring(lSequence + 1, change) + "0" + rString.substring(change + 1, 16),2);
                }

        }
        return 0;
    }

}
