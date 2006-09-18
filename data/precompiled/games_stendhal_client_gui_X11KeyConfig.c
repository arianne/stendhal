#include <X11/XKBlib.h>
#include <jni.h>
#include <jawt_md.h>
#include "games_stendhal_client_gui_X11KeyConfig.h"

#ifdef __cplusplus
extern "C" {
#endif

jboolean successful = JNI_FALSE;
jboolean inited = JNI_FALSE;

JNIEXPORT jboolean JNICALL Java_games_stendhal_client_gui_X11KeyConfig_getSetDetectableAutoRepeat
  (JNIEnv * env, jclass cl)
{
	return successful;
}

void keyX(Display* xdisplay)
{
	if (!inited)
	{
		inited = JNI_TRUE;
		int xkb_major = XkbMajorVersion;
		int xkb_minor = XkbMinorVersion;
		if (XkbLibraryVersion (&xkb_major, &xkb_minor))
		{
			xkb_major = XkbMajorVersion;
			xkb_minor = XkbMinorVersion;

			Bool detectable_autorepeat_supported;
			XkbSetDetectableAutoRepeat (xdisplay,
				1,
				&detectable_autorepeat_supported);
			successful = detectable_autorepeat_supported ? JNI_TRUE : JNI_FALSE;
		}
	}
}

/*
 * Class:     MyCanvas
 * Method:    paint
 * Signature: (Ljava/awt/Graphics;)V
 */
JNIEXPORT void JNICALL Java_games_stendhal_client_gui_X11KeyConfig_paint
(JNIEnv* env, jobject canvas, jobject graphics)
{
    JAWT awt;
    JAWT_DrawingSurface* ds;
    JAWT_DrawingSurfaceInfo* dsi;
    JAWT_X11DrawingSurfaceInfo* dsi_x11;
    jboolean result;
    jint lock;
    GC gc;
    
    short	i;
    char	*testString = "^^^ rendered from native code ^^^";

    /* Get the AWT */
    awt.version = JAWT_VERSION_1_3;
    if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
        printf("AWT Not found\n");
        return;
    }

    /* Get the drawing surface */
    ds = awt.GetDrawingSurface(env, canvas);
    if (ds == NULL) {
        printf("NULL drawing surface\n");
        return;
    }

    /* Lock the drawing surface */
    lock = ds->Lock(ds);
    if((lock & JAWT_LOCK_ERROR) != 0) {
        printf("Error locking surface\n");
        awt.FreeDrawingSurface(ds);
        return;
    }

    /* Get the drawing surface info */
    dsi = ds->GetDrawingSurfaceInfo(ds);
    if (dsi == NULL) {
        printf("Error getting surface info\n");
        ds->Unlock(ds);
        awt.FreeDrawingSurface(ds);
        return;
    }

    /* Get the platform-specific drawing info */
    dsi_x11 = (JAWT_X11DrawingSurfaceInfo*)dsi->platformInfo;


    /* Now paint */
	
	keyX(dsi_x11->display);

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    gc = XCreateGC(dsi_x11->display, dsi_x11->drawable, 0, 0);
    XSetBackground(dsi_x11->display, gc, 0);
    for (i=0; i<36;i++)
    {
	XSetForeground(dsi_x11->display, gc, 10*i);
   	XFillRectangle(dsi_x11->display, dsi_x11->drawable, gc,
                   	10*i, 5, 90, 90);
    }
    XSetForeground(dsi_x11->display, gc, 155);
    XDrawImageString(dsi_x11->display, dsi_x11->drawable, gc,
    			100, 110, testString, strlen(testString));
    XFreeGC(dsi_x11->display, gc);



// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    /* Free the drawing surface info */
    ds->FreeDrawingSurfaceInfo(dsi);

    /* Unlock the drawing surface */
    ds->Unlock(ds);

    /* Free the drawing surface */
    awt.FreeDrawingSurface(ds);
}

#ifdef __cplusplus
}
#endif
// i386
// gcc --shared -L/usr/lib/j2sdk1.5-sun/jre/lib/i386 -ljawt -I/usr/lib/j2sdk1.5-sun/include -I/usr/lib/j2sdk1.5-sun/include/linux games_stendhal_client_gui_X11KeyConfig.c -o libX11KeyConfig.so

// AMD64
// gcc -fPIC --shared -L/usr/lib/j2sdk1.5-sun/jre/lib/amd64/ -ljawt -I/usr/lib/j2sdk1.5-sun/include -I/usr/lib/j2sdk1.5-sun/include/linux games_stendhal_client_gui_X11KeyConfig.c -o libX11KeyConfig64.so
