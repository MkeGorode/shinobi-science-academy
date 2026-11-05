package com.vyriy.shinobi8464;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LiveButtonService extends AccessibilityService {
    private volatile boolean watcherRunning = false;
    private long lastLaunchTs = 0;

    private void launchShinobi() {
        long now = System.currentTimeMillis();
        if (now - lastLaunchTs < 800) return;
        lastLaunchTs = now;
        try {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            Log.i("ShinobiLiveBtn", "Launched Shinobi Science 8464 via Live TV button!");
        } catch (Throwable t) {
            Log.e("ShinobiLiveBtn", "Launch error", t);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i("ShinobiLiveBtn", "LiveButtonService connected!");
        if (!watcherRunning) {
            watcherRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (watcherRunning) {
                        Process proc = null;
                        try {
                            Runtime.getRuntime().exec("logcat -c").waitFor();
                            proc = Runtime.getRuntime().exec(new String[]{"logcat", "-T", "1", "ActivityManager:W", "GenericKeyHandler:I", "*:S"});
                            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                            String line;
                            while (watcherRunning && (line = br.readLine()) != null) {
                                if (line.contains("showLiveTVButtonContent") || line.contains("GenericKeyShortPressEvent")) {
                                    launchShinobi();
                                }
                            }
                        } catch (Throwable t) {
                            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                        } finally {
                            if (proc != null) proc.destroy();
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        watcherRunning = false;
        super.onDestroy();
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int scanCode = event.getScanCode();
        boolean isLiveBtn = (scanCode == 362 || scanCode == 644 ||
                             keyCode == 170 || keyCode == 172 || keyCode == 297);
        if (isLiveBtn) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                launchShinobi();
            }
            return true;
        }
        return super.onKeyEvent(event);
    }
}
