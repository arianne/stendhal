#include <jni.h>
#include <X11/XKBlib.h>
#include "games_stendhal_client_gui_X11KeyConfig.h"

/*
 * Class:     games_stendhal_client_gui_X11KeyConfig
 * Method:    SetDetectableAutoRepeat
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_games_stendhal_client_gui_X11KeyConfig_SetDetectableAutoRepeat
  (JNIEnv * a, jclass b)
{
    /*int xkb_major = XkbMajorVersion;
    int xkb_minor = XkbMinorVersion;
    if (XkbLibraryVersion (&xkb_major, &xkb_minor))
    {
        xkb_major = XkbMajorVersion;
        xkb_minor = XkbMinorVersion;

        if (XkbQueryExtension (display_x11->xdisplay, 
			       NULL, &display_x11->xkb_event_type, NULL,
                               &xkb_major, &xkb_minor))
        {
            Bool detectable_autorepeat_supported;
            XkbSelectEvents (display_x11->xdisplay,
                             XkbUseCoreKbd,
                             XkbNewKeyboardNotifyMask | XkbMapNotifyMask | XkbStateNotifyMask,
                             XkbNewKeyboardNotifyMask | XkbMapNotifyMask | XkbStateNotifyMask);
            XkbSetDetectableAutoRepeat (display_x11->xdisplay,
					1,
					&detectable_autorepeat_supported);

            return detectable_autorepeat_supported ? JNI_TRUE : JNI_FALSE;
        }
    }*/
	printf("In native code");
	return JNI_FALSE;
}

// gcc --shared -I/usr/lib/j2sdk1.5-sun/include -I/usr/lib/j2sdk1.5-sun/include/linux games_stendhal_client_gui_X11KeyConfig.c -o libX11KeyConfig.so

