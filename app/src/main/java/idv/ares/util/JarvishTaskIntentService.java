package idv.ares.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.os.ResultReceiver;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class JarvishTaskIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.jarvish.util.action.FOO";
    private static final String ACTION_BAZ = "com.jarvish.util.action.BAZ";

    // TODO: Rename parameters
//    private static final String EXTRA_RECEIVER = "receiver";
    private static final String EXTRA_TASKNAME = "taskName";

    public JarvishTaskIntentService() {
        super("JarvishTaskIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startTask(Context context, String taskName, Runnable runnable, ResultReceiver resultReceiver) {
        TaskFactory.getInstance().createTask(taskName, runnable);
        Intent intent = new Intent(context, JarvishTaskIntentService.class);
//        intent.setAction(ACTION_FOO);
//        intent.putExtra(EXTRA_RECEIVER, resultReceiver);
        intent.putExtra(EXTRA_TASKNAME, taskName);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, JarvishTaskIntentService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_FOO.equals(action)) {
//                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
                final String taskName = intent.getStringExtra(EXTRA_TASKNAME);
            TaskFactory taskFactory = TaskFactory.getInstance();
            Runnable task = taskFactory.getTask(taskName);
            if (task != null) {
                task.run();
                taskFactory.removeTask(taskName);
            }
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
