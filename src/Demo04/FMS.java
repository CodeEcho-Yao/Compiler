package Demo04;

/*
    状态机的接口定义
 */

public interface FMS {
    // 二位数组中的 -1, 即无效或失败状态
	public  final int STATE_FAILURE = -1;

	// 状态转换函数, 给定当前状态和输入字符, 该函数返回下一个状态的数值
    public int yy_next(int state, byte c);

    // 判断给定状态是否是接受状态
    public boolean isAcceptState(int state);
}
