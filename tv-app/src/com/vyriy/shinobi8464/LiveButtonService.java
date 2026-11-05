package com.vyriy.shinobi8464;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class LiveButtonService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int scanCode = event.getScanCode();
        // Fire TV Live TV / Guide / TV buttons:
        // scanCode 362 = LIVE_TV (#GUIDE), scanCode 644 = TV (#LIVE)
        // keyCode 170 = KEYCODE_TV, 172 = KEYCODE_GUIDE, 297 = KEYCODE_LIVE_TV
        boolean isLiveBtn = (scanCode == 362 || scanCode == 644 ||
                             keyCode == 170 || keyCode == 172 || keyCode == 297);
        if (isLiveBtn) {
            Log.i("ShinobiLiveBtn", "Intercepted Live TV button! keyCode=" + keyCode + " scanCode=" + scanCode + " action=" + event.getAction());
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            return true;
        }
        return super.onKeyEvent(event);
    }
}
