package co.lvdou.foundation.model;

public final class LDScreenStateModel {
    private boolean _isOffScreenNow = false;
    private long _offScreenTimeInMills = 0L;

    public boolean isOffScreenNow() {
        return _isOffScreenNow;
    }

    public void setOffScreenNow(boolean isOffScreenNow) {
        if (_isOffScreenNow != isOffScreenNow) {
            this._isOffScreenNow = isOffScreenNow;

            if (_isOffScreenNow) {
                setOffScreenTimeInMills(System.currentTimeMillis());
            }
        }
    }

    public long getOffScreenTimeInMills() {
        return _offScreenTimeInMills;
    }

    public long computeOffScreenDuration() {
        if (_isOffScreenNow) {
            return System.currentTimeMillis() - _offScreenTimeInMills;
        } else {
            return 0;
        }
    }

    private void setOffScreenTimeInMills(long offScreenTimeInMills) {
        this._offScreenTimeInMills = offScreenTimeInMills;
    }
}
