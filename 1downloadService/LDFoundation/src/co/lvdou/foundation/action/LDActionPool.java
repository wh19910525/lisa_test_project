package co.lvdou.foundation.action;

import android.os.Build;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 管理 {@link co.lvdou.foundation.action.LDAction} 的线程池。
 *
 * @author 郑一
 */
public class LDActionPool {
    private final LinkedList<LDAction> _waitingActionList = new LinkedList<LDAction>();
    private final LinkedHashSet<LDAction> _runningActionList = new LinkedHashSet<LDAction>();
    private final ExecutorService _threadPool = Executors.newSingleThreadExecutor();
    private boolean _isReleased = false;
    private boolean _isExecutingAction = false;

    private LDActionPool() {
    }

    /**
     * 分配一个新的线程池。
     */
    public static LDActionPool allocPool() {
        return new LDActionPool();
    }

    /**
     * 添加将要执行的 {@link co.lvdou.foundation.action.LDAction}。 <br />
     * 当线程池的持有者调用了方法 {@link #release()} 后本方法不会在执行.
     */
    public final void addAction(LDAction action) {
        if (!_isReleased && action != null) {
            _waitingActionList.add(action);
            executeNextAction();
        }
    }

    /**
     * 释放线程池。 <br/>
     * 调用本方法后，线程池所持有的所有 {@link co.lvdou.foundation.action.LDAction} 的引用将会被释放，而且方法 {@link #addAction(LDAction)} 将会无效
     */
    public final void release() {
        _isReleased = true;
        synchronized (_waitingActionList) {
            _waitingActionList.clear();
        }

        synchronized (_runningActionList) {
            for (LDAction cmd : _runningActionList) {
                cmd.interrupt();
            }
            _runningActionList.clear();
        }
    }

    private void onCommandExecuteComplete(LDAction action) {
        if (action != null) {
            synchronized (_threadPool) {
                action.setResultDelegate(null);
                _runningActionList.remove(action);
            }
        }
    }

    private void executeNextAction() {
        if (!_isExecutingAction) {
            _threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (_threadPool) {
                        _isExecutingAction = true;
                        while (!_isReleased && !_waitingActionList.isEmpty()) {
                            final LDAction nextAction;
                            if (Build.VERSION.SDK_INT >= 9) {

                                nextAction = _waitingActionList.pollFirst();
                            } else {
                                nextAction = _waitingActionList.poll();
                            }
                            _runningActionList.add(nextAction);
                            nextAction.setResultDelegate(new LDAction.LDActionResultDelegate() {
                                @Override
                                public void onActionExecuted() {
                                    onCommandExecuteComplete(nextAction);
                                }
                            });
                            nextAction.execute();
                        }
                        _isExecutingAction = false;
                    }
                }
            });
        }
    }
}