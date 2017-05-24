package idv.ares.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by ares on 2017/5/19.
 */

public abstract class JarvishTask<Params, Progress, Result> {
    public interface TaskCallback<T> {
        void onCallback(T... t);
    }

    private TaskCallback<Progress> taskCallback;
    private Handler handler;
    private boolean cancel = false;
    private Thread thread;

    public JarvishTask() {
        handler = new Handler(Looper.getMainLooper());
    }

    public final void execute(Params... params) {
        thread = Thread.currentThread();
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPreExecute();
            }
        });

        final Result result = doInBackground(params);

        handler.post(new Runnable() {
            @Override
            public void run() {
                onPostExecute(result);
            }
        });

    }

    protected Result doInBackground(Params... params) {
        return null;
    }


    protected void onPreExecute() {
    }


    protected void onPostExecute(Result result) {
    }


    protected void onProgressUpdate(Progress... values) {
        if (taskCallback != null) {
            taskCallback.onCallback(values);
        }
    }


    protected void onCancelled(Result result) {
    }


    protected void onCancelled() {

    }

    protected final void publishProgress(final Progress... values) {
        if (taskCallback != null) {
            taskCallback.onCallback(values);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(values);
            }
        });
    }

    public final boolean isCancel() {
        return cancel;
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        cancel = true;
        thread.interrupt();
        return cancel;
    }
}
