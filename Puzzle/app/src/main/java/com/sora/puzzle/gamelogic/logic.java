package com.sora.puzzle.gamelogic;

/**
 * Created by Sora on 2016/1/21.
 */
public interface logic {

    /**
     *
     * @param initSequence   初始状态对应的序列
     * @param finalSequence  终止状态对应的序列
     * @return String        move sequence
     *
     *
     * 序列定义规则：
     *      定义：W-白色    R-红色    B-蓝色
     *      根据对应颜色，按行遍历 得到Sequence
     *      例如  初始状态(S)：WRBBRRBBRRBBRRBB
     *            终止状态(E)：RRBBRBBBRWRBRRBB
     *            终止状态(T)：WBRBBRBRRBRBBRBR
     *
     * 调用该接口 只要提供初始，终止状态对应的序列，就可以得到对应的最短移动移动方法
     *
     */
    public String search(String initSequence , String finalSequence);
}
