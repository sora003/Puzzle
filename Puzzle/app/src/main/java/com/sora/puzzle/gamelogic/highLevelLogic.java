package com.sora.puzzle.gamelogic;

/**
 * Created by Sora on 2016/1/21.
 */
public interface highLevelLogic {
    /**
     *
     * @param initSequence   初始状态对应的序列
     * @param finalSequence  终止状态对应的序列
     * @return String        move sequence
     *
     *
     * 序列定义规则：
     *      定义：前16位 记录白块的位置    实际仅用5位 按行编号 Range：0-15  记录在12-16位 前11位记0
     *            后16位 记录Puzzle的状态  0表示红块和白块  1表示蓝块
     *
     *      根据对应颜色，按行遍历 得到Sequence
     *
     *      以下为2进制表示 实际传递中使用16进制输入
     *
     *      例如  初始状态(S)：0000 0000 0000 0000 0011 0011 0011 0011(WRBBRRBBRRBBRRBB)   ----> 0x00003333
     *            终止状态(E)：0000 0000 0000 1001 0011 0111 0001 0011(RRBBRBBBRWRBRRBB)   ----> 0x00093713
     *            终止状态(T)：0000 0000 0000 0000 0101 1010 0101 1010(WBRBBRBRRBRBBRBR)   ----> 0x00005A5A
     *
     * 调用该接口 只要提供初始，终止状态对应的序列，就可以得到对应的最短移动移动方法
     *
     */
    public String search(int initSequence , int finalSequence);
}
