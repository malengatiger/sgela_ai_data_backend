package com.boha.skunk.util;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;


public  class CustomImageRenderListener implements IEventListener {
    private static final String mm = "\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35" +
            " CustomImageRenderListener  \uD83D\uDC9B";
    private static final Logger logger = Logger.getLogger(CustomImageRenderListener.class.getSimpleName());

    private boolean hasImages = false;
    private final Set<EventType> eventTypes = new HashSet<>();
    private final CountDownLatch latch = new CountDownLatch(1);

    public boolean hasImages() throws InterruptedException {
        latch.await();
        return hasImages;
    }

    @Override
    public void eventOccurred(IEventData iEventData, EventType eventType) {
        if (eventType == EventType.RENDER_IMAGE) {
            logger.info(mm + "\uD83C\uDF4E \uD83C\uDF4E Handle RENDER_IMAGE event  \uD83C\uDF4E");
            hasImages = true;
            latch.countDown();
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        EventType type1 = EventType.BEGIN_TEXT;
        EventType type2 = EventType.END_TEXT;
        EventType type3 = EventType.RENDER_IMAGE;
        EventType type4 = EventType.RENDER_IMAGE;
        eventTypes.add(type1);
        eventTypes.add(type2);
        eventTypes.add(type3);
        eventTypes.add(type4);

        return eventTypes;
    }
}

