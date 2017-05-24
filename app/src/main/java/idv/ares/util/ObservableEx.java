package idv.ares.util;

import java.util.Observable;

/**
 * Created by ares on 2017/4/19.
 */

public class ObservableEx<ObservableT, ValueT> extends Observable {
    private ObservableT observable;

    public ObservableEx() {

    }

    public ObservableEx(ObservableT observable) {
        this.observable = observable;
    }

    public void notifyObserversEx(ValueT arg) {
        super.notifyObservers(arg);
    }

    public synchronized void addObserver(ObserverEx<ObservableT, ValueT> o) {
        super.addObserver(o);
    }

    public ObservableT getObservable() {
        return observable;
    }

    public void setObservable(ObservableT observable) {
        this.observable = observable;
    }

    @Override
    public synchronized void setChanged() {
        super.setChanged();
    }
}
