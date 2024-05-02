package com.boha.skunk.data;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Logger;

@Service
public class PdfImageDetector {
    private static final String mm = "\uD83E\uDD66\uD83E\uDD66\uD83E\uDD66 PdfImageDetector  \uD83D\uDC9B";
    private static final Logger logger = Logger.getLogger(PdfImageDetector.class.getSimpleName());



    public boolean hasImage(PDPage page) throws IOException {
        PDResources resources = page.getResources();
        for (COSName name : resources.getXObjectNames()) {
            PDXObject pdxObject = resources.getXObject(name);
            if (pdxObject instanceof PDImageXObject) {
                logger.info(mm+"image found: " + name.getName());
                return true;
            }
        }
        return false;
    }

}