package idv.ares.util;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by ares on 2017/4/19.
 */

public abstract class ObserverEx<ObservableT, ValueT> implements Observer {

    @Override
    public void update(Observable observable, Object o) {
        update((ObservableEx<ObservableT, ValueT>)observable, (ValueT)o);
    }

    public abstract void update(ObservableEx<ObservableT, ValueT> observableEx, ValueT t);
}
